package com.draw.onme.presentation.fridge.export

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.print.PrintHelper
import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.SavedArtwork
import com.draw.onme.domain.model.Stroke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Geometric bounds for a collection of drawing strokes.
 */
data class ArtworkBounds(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float
) {
    val width: Float get() = (maxX - minX).coerceAtLeast(1f)
    val height: Float get() = (maxY - minY).coerceAtLeast(1f)
    val aspectRatio: Float get() = width / height
}

/**
 * Utility responsible for rendering [SavedArtwork] into high-resolution bitmap images,
 * saving them to temporary cache storage, and integrating with Android system sharing
 * and printing mechanisms.
 */
object ArtworkImageExporter {

    const val DEFAULT_BITMAP_WIDTH = 1200
    const val FOOTER_HEIGHT = 160f
    const val PADDING_PERCENTAGE = 0.08f

    /**
     * Calculates the bounding box of all visible (non-eraser) strokes in [strokes].
     * Returns null if there are no visible points.
     */
    fun calculateBounds(strokes: List<Stroke>): ArtworkBounds? {
        val visibleStrokes = strokes.filter { it.tool != DrawingTool.ERASER && it.points.isNotEmpty() }
        if (visibleStrokes.isEmpty()) return null

        var minX = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE

        for (stroke in visibleStrokes) {
            val halfWidth = stroke.strokeWidth / 2f
            for (p in stroke.points) {
                if (p.x - halfWidth < minX) minX = p.x - halfWidth
                if (p.x + halfWidth > maxX) maxX = p.x + halfWidth
                if (p.y - halfWidth < minY) minY = p.y - halfWidth
                if (p.y + halfWidth > maxY) maxY = p.y + halfWidth
            }
        }
        return ArtworkBounds(minX, maxX, minY, maxY)
    }

    /**
     * Renders [artwork] into a high-resolution [Bitmap] on a white canvas.
     *
     * @param artwork The saved artwork to render.
     * @param targetWidth The width of the exported bitmap in pixels.
     * @param includeFooter Whether to include a keepsake caption footer with title, date, and magnet.
     */
    fun renderToBitmap(
        artwork: SavedArtwork,
        targetWidth: Int = DEFAULT_BITMAP_WIDTH,
        includeFooter: Boolean = true
    ): Bitmap {
        val bounds = calculateBounds(artwork.strokes)
        val boundsAspectRatio = bounds?.aspectRatio?.coerceIn(0.70f, 1.45f) ?: 1.0f

        val drawingHeight = (targetWidth / boundsAspectRatio).roundToInt()
        val totalHeight = if (includeFooter) drawingHeight + FOOTER_HEIGHT.roundToInt() else drawingHeight

        val bitmap = Bitmap.createBitmap(targetWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Clean white paper background
        canvas.drawColor(Color.WHITE)

        // Draw drawing content
        if (bounds != null) {
            val availableW = targetWidth * (1f - 2 * PADDING_PERCENTAGE)
            val availableH = drawingHeight * (1f - 2 * PADDING_PERCENTAGE)
            val scale = minOf(availableW / bounds.width, availableH / bounds.height)

            val contentWidth = bounds.width * scale
            val contentHeight = bounds.height * scale
            val offsetX = (targetWidth - contentWidth) / 2f - bounds.minX * scale
            val offsetY = (drawingHeight - contentHeight) / 2f - bounds.minY * scale

            // Render strokes to an offscreen bitmap to enable true PorterDuff clear masking for erasers
            val strokeBitmap = Bitmap.createBitmap(targetWidth, drawingHeight, Bitmap.Config.ARGB_8888)
            val strokeCanvas = Canvas(strokeBitmap)

            val penPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }

            val eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }

            for (stroke in artwork.strokes) {
                if (stroke.points.isEmpty()) continue

                val isEraser = stroke.tool == DrawingTool.ERASER
                val paint = if (isEraser) eraserPaint else penPaint
                if (!isEraser) {
                    paint.color = stroke.color.argb.toInt()
                }
                paint.strokeWidth = (stroke.strokeWidth * scale).coerceIn(2.5f, 36f)

                if (stroke.points.size == 1) {
                    val pt = stroke.points[0]
                    strokeCanvas.drawCircle(
                        pt.x * scale + offsetX,
                        pt.y * scale + offsetY,
                        paint.strokeWidth / 2f,
                        paint
                    )
                } else {
                    val path = Path()
                    val first = stroke.points[0]
                    path.moveTo(first.x * scale + offsetX, first.y * scale + offsetY)

                    for (i in 1 until stroke.points.size) {
                        val prev = stroke.points[i - 1]
                        val curr = stroke.points[i]
                        val prevX = prev.x * scale + offsetX
                        val prevY = prev.y * scale + offsetY
                        val currX = curr.x * scale + offsetX
                        val currY = curr.y * scale + offsetY
                        val midX = (prevX + currX) / 2f
                        val midY = (prevY + currY) / 2f
                        path.quadTo(prevX, prevY, midX, midY)
                    }
                    val last = stroke.points.last()
                    path.lineTo(last.x * scale + offsetX, last.y * scale + offsetY)

                    strokeCanvas.drawPath(path, paint)
                }
            }

            canvas.drawBitmap(strokeBitmap, 0f, 0f, null)
            strokeBitmap.recycle()
        }

        // Render keepsake footer
        if (includeFooter) {
            val dividerY = drawingHeight.toFloat()
            val dividerPaint = Paint().apply {
                color = Color.parseColor("#E2E8F0")
                strokeWidth = 2f
            }
            canvas.drawLine(40f, dividerY, targetWidth - 40f, dividerY, dividerPaint)

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#1E293B")
                textSize = 38f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#64748B")
                textSize = 24f
            }

            val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#94A3B8")
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }

            val titleY = dividerY + 62f
            val dateY = dividerY + 110f
            val fullTitle = "${artwork.magnetEmoji} ${artwork.title}"
            canvas.drawText(fullTitle, 48f, titleY, titlePaint)

            val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            val dateText = "Created on ${formatter.format(Date(artwork.createdAt))}"
            canvas.drawText(dateText, 48f, dateY, datePaint)

            canvas.drawText("DrawOnMe 🎨", targetWidth - 48f, dateY, brandPaint)
        }

        return bitmap
    }

    /**
     * Saves rendered artwork to application cache directory and returns a secure content [Uri].
     */
    suspend fun saveToCache(context: Context, artwork: SavedArtwork): Uri = withContext(Dispatchers.IO) {
        val cacheDir = File(context.cacheDir, "shared_artworks").apply { mkdirs() }
        val file = File(cacheDir, "artwork_${artwork.id}.png")

        val bitmap = renderToBitmap(artwork)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /**
     * Prepares and launches the Android System Share Sheet to share [artwork] as a PNG image
     * to any installed social media, messaging, or cloud application.
     */
    suspend fun shareArtwork(context: Context, artwork: SavedArtwork) {
        val contentUri = saveToCache(context, artwork)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, artwork.title)
            putExtra(Intent.EXTRA_TEXT, "Look what was drawn with DrawOnMe! 🎨")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share Artwork").apply {
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(chooserIntent)
    }

    /**
     * Launches Android's system [PrintHelper] to print [artwork] or save it as a PDF.
     */
    fun printArtwork(context: Context, artwork: SavedArtwork) {
        val printHelper = PrintHelper(context).apply {
            scaleMode = PrintHelper.SCALE_MODE_FIT
        }
        val bitmap = renderToBitmap(artwork, includeFooter = true)
        printHelper.printBitmap("${artwork.title} - DrawOnMe", bitmap)
    }
}
