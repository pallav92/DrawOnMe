package com.pallav.drawonme.data.repository

import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.domain.model.Point
import com.pallav.drawonme.domain.model.SavedArtwork
import com.pallav.drawonme.domain.model.Stroke
import com.pallav.drawonme.domain.model.StrokeColor
import com.pallav.drawonme.domain.repository.ArtworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/**
 * File-based implementation of ArtworkRepository storing drawings as JSON in private app storage.
 */
class FileArtworkRepository(
    private val storageDir: File
) : ArtworkRepository {

    private val _artworks = MutableStateFlow<List<SavedArtwork>>(emptyList())

    init {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        loadAllFromDisk()
    }

    override fun observeArtworks(): Flow<List<SavedArtwork>> {
        return _artworks.asStateFlow()
    }

    override suspend fun saveArtwork(artwork: SavedArtwork): Unit = withContext(Dispatchers.IO) {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val file = File(storageDir, "${artwork.id}.json")
        val jsonString = artworkToJson(artwork).toString(2)
        file.writeText(jsonString)

        _artworks.update { current ->
            (listOf(artwork) + current.filterNot { it.id == artwork.id }).sortedByDescending { it.createdAt }
        }
    }

    override suspend fun deleteArtwork(id: String): Unit = withContext(Dispatchers.IO) {
        val file = File(storageDir, "$id.json")
        if (file.exists()) {
            file.delete()
        }
        _artworks.update { current ->
            current.filterNot { it.id == id }
        }
    }

    override suspend fun getArtworkById(id: String): SavedArtwork? {
        return _artworks.value.firstOrNull { it.id == id }
    }

    private fun loadAllFromDisk() {
        val files = storageDir.listFiles { file -> file.extension == "json" } ?: return
        val loaded = files.mapNotNull { file ->
            try {
                jsonToArtwork(JSONObject(file.readText()))
            } catch (_: Exception) {
                null
            }
        }.sortedByDescending { it.createdAt }

        _artworks.value = loaded
    }

    companion object {
        fun artworkToJson(artwork: SavedArtwork): JSONObject {
            val json = JSONObject()
            json.put("id", artwork.id)
            json.put("title", artwork.title)
            json.put("createdAt", artwork.createdAt)
            json.put("stencilId", artwork.stencilId ?: JSONObject.NULL)
            json.put("magnetEmoji", artwork.magnetEmoji)

            val strokesArray = JSONArray()
            for (stroke in artwork.strokes) {
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
                strokesArray.put(strokeObj)
            }
            json.put("strokes", strokesArray)
            return json
        }

        fun jsonToArtwork(json: JSONObject): SavedArtwork {
            val id = json.getString("id")
            val title = json.getString("title")
            val createdAt = json.getLong("createdAt")
            val stencilId = if (json.isNull("stencilId")) null else json.getString("stencilId")
            val magnetEmoji = json.optString("magnetEmoji", "⭐️")

            val strokesArray = json.getJSONArray("strokes")
            val strokes = mutableListOf<Stroke>()
            for (i in 0 until strokesArray.length()) {
                val sObj = strokesArray.getJSONObject(i)
                val sId = sObj.optString("id", UUID.randomUUID().toString())
                val color = StrokeColor(sObj.getLong("color"))
                val strokeWidth = sObj.getDouble("strokeWidth").toFloat()
                val tool = DrawingTool.valueOf(sObj.optString("tool", "PEN"))

                val pointsArray = sObj.getJSONArray("points")
                val points = mutableListOf<Point>()
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

            return SavedArtwork(
                id = id,
                title = title,
                createdAt = createdAt,
                strokes = strokes,
                stencilId = stencilId,
                magnetEmoji = magnetEmoji
            )
        }
    }
}
