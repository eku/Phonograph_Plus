/*
 * Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.mechanism.backup

import okio.BufferedSink
import player.phonograph.mechanism.event.MediaStoreTracker
import player.phonograph.model.Song
import player.phonograph.model.playlist.FilePlaylist
import player.phonograph.repo.database.FavoritesStore
import player.phonograph.repo.database.PathFilterStore
import player.phonograph.repo.mediastore.loaders.PlaylistLoader
import player.phonograph.repo.mediastore.loaders.SongLoader
import player.phonograph.service.queue.QueueStore
import player.phonograph.util.reportError
import player.phonograph.util.warning
import androidx.annotation.Keep
import android.content.Context
import kotlin.LazyThreadSafetyMode.NONE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.IOException
import java.io.InputStream

object DatabaseDataManger {

    const val VERSION = "version"
    const val VERSION_CODE = 0

    private const val WHITE_LIST = "whitelist"
    private const val BLACK_LIST = "blacklist"

    fun exportPathFilter(sink: BufferedSink, context: Context): Boolean {
        return writeJson(sink, "PathFilter", exportPathFilter(context))
    }

    private fun exportPathFilter(context: Context): JsonObject? {
        val db = PathFilterStore.getInstance(context)
        val wl = db.whitelistPaths.map { JsonPrimitive(it) }
        val bl = db.blacklistPaths.map { JsonPrimitive(it) }
        return if (wl.isNotEmpty() || bl.isNotEmpty()) {
            JsonObject(
                mapOf(
                    VERSION to JsonPrimitive(VERSION_CODE),
                    WHITE_LIST to JsonArray(wl),
                    BLACK_LIST to JsonArray(bl),
                )
            )
        } else {
            null
        }
    }

    fun importPathFilter(context: Context, inputStream: InputStream): Boolean {
        val rawString = inputStream.reader().use { it.readText() }
        return parseJson(rawString, "PathFilter") { json ->
            importPathFilter(context, json, true)
        }
    }

    private fun importPathFilter(context: Context, json: JsonObject, override: Boolean): Boolean {

        val wl = json[WHITE_LIST] as? JsonArray
        val bl = json[BLACK_LIST] as? JsonArray


        val db = PathFilterStore.getInstance(context)

        return if (!(wl == null && bl == null)) {

            bl?.map { (it as JsonPrimitive).content }?.let {
                if (override) db.clearBlacklist()
                db.addBlacklistPath(it)
            }

            wl?.map { (it as JsonPrimitive).content }?.let {
                if (override) db.clearWhitelist()
                db.addWhitelistPath(it)
            }
            true
        } else {
            warning(TAG, "PathFilter: Nothing to import")
            false
        }

    }

    private const val PLAYING_QUEUE = "playing_queue"
    private const val ORIGINAL_PLAYING_QUEUE = "original_playing_queue"

    fun exportPlayingQueues(sink: BufferedSink, context: Context): Boolean {
        return writeJson(sink, "PlayingQueues", exportPlayingQueues(context))
    }

    private fun exportPlayingQueues(context: Context): JsonObject? {
        val db = QueueStore.getInstance(context)
        val oq = db.savedOriginalPlayingQueue(context).map(DatabaseDataManger::persistentSong)
        val pq = db.savedPlayingQueue(context).map(DatabaseDataManger::persistentSong)
        return if (oq.isNotEmpty() || pq.isNotEmpty()) {
            JsonObject(
                mapOf(
                    VERSION to JsonPrimitive(VERSION_CODE),
                    PLAYING_QUEUE to JsonArray(pq),
                    ORIGINAL_PLAYING_QUEUE to JsonArray(oq),
                )
            )
        } else {
            null
        }
    }

    fun importPlayingQueues(context: Context, inputStream: InputStream): Boolean {
        val rawString = inputStream.reader().use { it.readText() }
        return parseJson(rawString, "PlayingQueues") { json ->
            importPlayingQueues(context, json)
        }
    }

    private fun importPlayingQueues(context: Context, json: JsonObject): Boolean {
        val oq = json[ORIGINAL_PLAYING_QUEUE] as? JsonArray
        val pq = json[PLAYING_QUEUE] as? JsonArray


        val db = QueueStore.getInstance(context)

        val originalQueue = recoverSongs(context, oq)
        val currentQueueQueue = recoverSongs(context, pq)

        return if (!(originalQueue == null && currentQueueQueue == null)) {

            // todo: report imported queues

            db.save(
                currentQueueQueue ?: originalQueue ?: emptyList(),
                originalQueue ?: currentQueueQueue ?: emptyList(),
            )
            MediaStoreTracker.notifyAllListeners()
            true
        } else {
            warning(TAG, "PlayingQueues: Nothing to import")
            false
        }
    }

    private const val FAVORITE_SONG = "favorite"
    private const val PINED_PLAYLIST = "pined_playlists"


    fun exportFavorites(sink: BufferedSink, context: Context): Boolean {
        return writeJson(sink, "Favorites", exportFavorites(context))
    }

    private fun exportFavorites(context: Context): JsonObject? {
        val db = FavoritesStore.instance
        val songs = db.getAllSongs(context).map(DatabaseDataManger::persistentSong)
        val playlists = db.getAllPlaylists(context).map(DatabaseDataManger::persistentPlaylist)
        return if (songs.isNotEmpty()) {
            JsonObject(
                mapOf(
                    VERSION to JsonPrimitive(VERSION_CODE),
                    FAVORITE_SONG to JsonArray(songs),
                    PINED_PLAYLIST to JsonArray(playlists),
                )
            )
        } else {
            null
        }
    }

    fun importFavorites(context: Context, inputStream: InputStream): Boolean {
        val rawString = inputStream.reader().use { it.readText() }
        return parseJson(rawString, "Favorites") { json ->
            importFavorites(context, json, true)
        }
    }

    private fun importFavorites(context: Context, json: JsonObject, override: Boolean): Boolean {
        val s = json[FAVORITE_SONG] as? JsonArray
        val p = json[PINED_PLAYLIST] as? JsonArray

        val db = FavoritesStore.instance

        val songs = recoverSongs(context, s)
        val playlists = recoverPlaylists(context, p)

        val r1 = if (!songs.isNullOrEmpty()) {
            if (override) db.clearAllSongs()
            db.addSongs(songs.asReversed())
        } else {
            false
        }

        val r2 = if (!playlists.isNullOrEmpty()) {
            if (override) db.clearAllPlaylists()
            db.addPlaylists(playlists.asReversed())
        } else {
            false
        }

        // todo: report the imported
        MediaStoreTracker.notifyAllListeners()

        return r1 || r2
    }

    private fun persistentSong(song: Song): JsonElement =
        parser.encodeToJsonElement(PersistentSong.serializer(), PersistentSong.from(song))

    private fun recoverSongs(context: Context, array: JsonArray?): List<Song>? =
        array?.map { parser.decodeFromJsonElement(PersistentSong.serializer(), it) }
            ?.mapNotNull { it.getMatchingSong(context) }

    private fun persistentPlaylist(playlist: FilePlaylist): JsonElement =
        parser.encodeToJsonElement(PersistentPlaylist.serializer(), PersistentPlaylist.from(playlist))

    private fun recoverPlaylists(context: Context, array: JsonArray?): List<FilePlaylist>? =
        array?.map { parser.decodeFromJsonElement(PersistentPlaylist.serializer(), it) }
            ?.mapNotNull { it.getMatchingPlaylist(context) }


    @Keep
    @Serializable
    class PersistentSong(
        @SerialName("path") val path: String,
        @SerialName("title") val title: String,
        @SerialName("album") val album: String?,
        @SerialName("artist") val artist: String?,
    ) {
        companion object {
            fun from(song: Song): PersistentSong =
                PersistentSong(song.data, song.title, song.albumName, song.artistName)
        }

        fun getMatchingSong(context: Context): Song? =
            SongLoader.searchByPath(context, path).firstOrNull()
    }
    @Keep
    @Serializable
    class PersistentPlaylist(
        @SerialName("path") val path: String,
        @SerialName("title") val name: String,
    ) {
        companion object {
            fun from(filePlaylist: FilePlaylist): PersistentPlaylist =
                PersistentPlaylist(filePlaylist.associatedFilePath, filePlaylist.name)
        }

        fun getMatchingPlaylist(context: Context): FilePlaylist? =
            PlaylistLoader.searchByPath(context, path)
    }

    private fun parseJson(rawString: String, name: String, block: (JsonObject) -> Boolean): Boolean {
        val json = parser.parseToJsonElement(rawString) as? JsonObject
        return if (json != null) {
            try {
                block(json)
            } catch (e: Exception) {
                reportError(e, TAG, "Failed to import $name!")
                false
            }
        } else {
            warning(TAG, "$name: Nothing to import")
            false
        }
    }

    /**
     * write [json] to [sink]
     */
    private fun writeJson(sink: BufferedSink, name: String, json: JsonObject?): Boolean {
        return try {
            if (json != null) {
                val content = parser.encodeToString(json)
                sink.writeString(content, Charsets.UTF_8)
                true
            } else {
                warning(TAG, "$name: Nothing to export")
                false
            }
        } catch (e: IOException) {
            reportError(e, TAG, "Failed to export $name!")
            false
        }
    }

    private val parser by lazy(NONE) {
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    private const val TAG = "DatabaseBackupManger"
}