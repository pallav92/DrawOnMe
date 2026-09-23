package com.draw.onme.data.repository

import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import com.draw.onme.domain.repository.BoardDraftRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * File-based implementation of BoardDraftRepository storing drawing drafts
 * as JSON files in app-private storage.
 *
 * Provides instant in-memory lookups via ConcurrentHashMap while asynchronously
 * writing to disk to survive app lifecycle events and process death.
 */
class FileBoardDraftRepository(
    private val storageDir: File
) : BoardDraftRepository {

    private val cache: ConcurrentHashMap<String, List<Stroke>> = ConcurrentHashMap()

    init {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
    }

    override suspend fun getBoardStrokes(boardId: String): List<Stroke> {
        val cached = cache[boardId]
        if (cached != null) {
            return cached
        }

        return withContext(Dispatchers.IO) {
            val file = getBoardFile(boardId)
            if (!file.exists()) {
                val empty = emptyList<Stroke>()
                cache[boardId] = empty
                return@withContext empty
            }

            try {
                val jsonString = file.readText()
                val strokes = deserializeStrokes(JSONArray(jsonString))
                cache[boardId] = strokes
                strokes
            } catch (_: Exception) {
                val empty = emptyList<Stroke>()
                cache[boardId] = empty
                empty
            }
        }
    }

    override suspend fun saveBoardStrokes(boardId: String, strokes: List<Stroke>): Unit = withContext(Dispatchers.IO) {
        cache[boardId] = strokes
        val file = getBoardFile(boardId)

        if (strokes.isEmpty()) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val jsonArray = serializeStrokes(strokes)
            file.writeText(jsonArray.toString())
        }
    }

    override suspend fun clearBoard(boardId: String): Unit = withContext(Dispatchers.IO) {
        cache[boardId] = emptyList()
        val file = getBoardFile(boardId)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun getBoardFile(boardId: String): File {
        // Sanitize boardId to prevent illegal path characters
        val sanitized = boardId.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return File(storageDir, "$sanitized.json")
    }

    companion object {
        fun serializeStrokes(strokes: List<Stroke>): JSONArray {
            val jsonArray = JSONArray()
            for (stroke in strokes) {
                val strokeObj = JSONObject()
                strokeObj.put("id", stroke.id)
                strokeObj.put("color", stroke.color.argb)
                strokeObj.put("strokeWidth", stroke.strokeWidth.toDouble())
                strokeObj.put("tool", stroke.tool.name)

                val pointsArray = JSONArray()
                for (p in stroke.points) {
                    val pObj = JSONObject()
                    pObj.put("x", p.x.toDouble())
                    pObj.put("y", p.y.toDouble())
                    pointsArray.put(pObj)
                }
                strokeObj.put("points", pointsArray)
                jsonArray.put(strokeObj)
            }
            return jsonArray
        }

        fun deserializeStrokes(jsonArray: JSONArray): List<Stroke> {
            val strokes = ArrayList<Stroke>(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val sObj = jsonArray.getJSONObject(i)
                val sId = sObj.optString("id", UUID.randomUUID().toString())
                val color = StrokeColor(sObj.getLong("color"))
                val strokeWidth = sObj.getDouble("strokeWidth").toFloat()
                val tool = try {
                    DrawingTool.valueOf(sObj.optString("tool", "PEN"))
                } catch (_: Exception) {
                    DrawingTool.PEN
                }

                val pointsArray = sObj.getJSONArray("points")
                val points = ArrayList<Point>(pointsArray.length())
                for (j in 0 until pointsArray.length()) {
                    val pObj = pointsArray.getJSONObject(j)
                    points.add(Point(pObj.getDouble("x").toFloat(), pObj.getDouble("y").toFloat()))
                }

                strokes.add(
                    Stroke(
                        id = sId,
                        points = points,
                        color = color,
                        strokeWidth = strokeWidth,
                        tool = tool
                    )
                )
            }
            return strokes
        }
    }
}
