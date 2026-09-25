package com.draw.onme.data.repository

import com.draw.onme.domain.model.ClosedAreaFill
import com.draw.onme.domain.model.ColoringDraft
import com.draw.onme.domain.model.FillSpan
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.StrokeColor
import com.draw.onme.domain.repository.ColoringDraftRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * File-based implementation of [ColoringDraftRepository] storing in-progress
 * coloring drafts as JSON in app-private storage.
 */
class FileColoringDraftRepository(
    private val storageDir: File
) : ColoringDraftRepository {

    private val cache: ConcurrentHashMap<String, ColoringDraft> = ConcurrentHashMap()

    init {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
    }

    override suspend fun getDraft(pageId: String): ColoringDraft? {
        val cached = cache[pageId]
        if (cached != null) return cached

        return withContext(Dispatchers.IO) {
            val file = getDraftFile(pageId)
            if (!file.exists()) return@withContext null

            try {
                val json = JSONObject(file.readText())
                val parsedPageId = json.getString("pageId")

                val fillsObj = json.getJSONObject("fills")
                val fills = mutableMapOf<String, StrokeColor>()
                val keys = fillsObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    fills[k] = StrokeColor(fillsObj.getLong(k))
                }

                val strokesArray = json.getJSONArray("strokes")
                val strokes = FileBoardDraftRepository.deserializeStrokes(strokesArray)

                val customFills = mutableListOf<ClosedAreaFill>()
                val customFillsArray = json.optJSONArray("customFills")
                if (customFillsArray != null) {
                    for (i in 0 until customFillsArray.length()) {
                        val fillObj = customFillsArray.getJSONObject(i)
                        val id = fillObj.getString("id")
                        val color = StrokeColor(fillObj.getLong("color"))
                        val seedX = fillObj.getDouble("seedX").toFloat()
                        val seedY = fillObj.getDouble("seedY").toFloat()
                        val regionId = fillObj.optString("regionId").takeIf { it.isNotEmpty() }

                        val spansArray = fillObj.getJSONArray("spans")
                        val spans = mutableListOf<FillSpan>()
                        for (j in 0 until spansArray.length()) {
                            val spanObj = spansArray.getJSONObject(j)
                            spans.add(
                                FillSpan(
                                    y = spanObj.getDouble("y").toFloat(),
                                    x1 = spanObj.getDouble("x1").toFloat(),
                                    x2 = spanObj.getDouble("x2").toFloat()
                                )
                            )
                        }
                        customFills.add(
                            ClosedAreaFill(
                                id = id,
                                color = color,
                                spans = spans,
                                seedPoint = Point(seedX, seedY),
                                regionId = regionId
                            )
                        )
                    }
                }

                val draft = ColoringDraft(
                    pageId = parsedPageId,
                    fills = fills,
                    strokes = strokes,
                    customFills = customFills
                )
                cache[pageId] = draft
                draft
            } catch (_: Exception) {
                null
            }
        }
    }

    override suspend fun saveDraft(draft: ColoringDraft): Unit = withContext(Dispatchers.IO) {
        cache[draft.pageId] = draft
        val file = getDraftFile(draft.pageId)

        if (draft.fills.isEmpty() && draft.strokes.isEmpty() && draft.customFills.isEmpty()) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val json = JSONObject()
            json.put("pageId", draft.pageId)

            val fillsObj = JSONObject()
            for ((regionId, color) in draft.fills) {
                fillsObj.put(regionId, color.argb)
            }
            json.put("fills", fillsObj)

            val strokesArray = FileBoardDraftRepository.serializeStrokes(draft.strokes)
            json.put("strokes", strokesArray)

            val customFillsArray = JSONArray()
            for (fill in draft.customFills) {
                val fillObj = JSONObject()
                fillObj.put("id", fill.id)
                fillObj.put("color", fill.color.argb)
                fillObj.put("seedX", fill.seedPoint.x.toDouble())
                fillObj.put("seedY", fill.seedPoint.y.toDouble())
                fill.regionId?.let { fillObj.put("regionId", it) }

                val spansArray = JSONArray()
                for (span in fill.spans) {
                    val spanObj = JSONObject()
                    spanObj.put("y", span.y.toDouble())
                    spanObj.put("x1", span.x1.toDouble())
                    spanObj.put("x2", span.x2.toDouble())
                    spansArray.put(spanObj)
                }
                fillObj.put("spans", spansArray)
                customFillsArray.put(fillObj)
            }
            json.put("customFills", customFillsArray)

            file.writeText(json.toString())
        }
    }

    override suspend fun clearDraft(pageId: String): Unit = withContext(Dispatchers.IO) {
        cache.remove(pageId)
        val file = getDraftFile(pageId)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun getDraftFile(pageId: String): File {
        val sanitized = pageId.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return File(storageDir, "coloring_$sanitized.json")
    }
}
