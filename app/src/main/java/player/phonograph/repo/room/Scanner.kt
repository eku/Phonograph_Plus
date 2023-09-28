/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.repo.room

import player.phonograph.notification.DatabaseUpdateNotification
import player.phonograph.repo.mediastore.loaders.SongLoader
import player.phonograph.repo.room.entity.SongEntity
import player.phonograph.util.debug
import player.phonograph.util.text.currentTimestamp
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import player.phonograph.model.Song as SongModel

object Scanner {

    private const val TAG = "DatabaseScanner"
    private val scope by lazy { CoroutineScope(Dispatchers.IO) }

    fun refreshDatabase(context: Context, force: Boolean = false) {
        Log.i(TAG, "Start refreshing")
        var latestSongTimestamp = -1L
        var databaseUpdateTimestamp = -1L

        // check latest music files
        val latestSong = SongLoader.latest(context)
        if (latestSong != null && latestSong.dateModified > 0) latestSongTimestamp = latestSong.dateModified
        // check database timestamps
        databaseUpdateTimestamp = MusicDatabase.Metadata.lastUpdateTimestamp

        debug {
            Log.i(TAG, "latestSongTimestamp    :$latestSongTimestamp")
            Log.i(TAG, "databaseUpdateTimestamp:$databaseUpdateTimestamp")
        }

        // compare
        scope.launch {
            val songEntities: List<SongEntity>
            if (force) {
                songEntities = SongLoader.all(context).map(Converters::fromSongModel)
                importFromMediaStore(context, songEntities)
            } else if (latestSongTimestamp > databaseUpdateTimestamp || databaseUpdateTimestamp == -1L) {
                songEntities = SongLoader.since(context, databaseUpdateTimestamp).map(Converters::fromSongModel)
                importFromMediaStore(context, songEntities)
                MusicDatabase.Metadata.lastUpdateTimestamp = currentTimestamp() / 1000
            }
        }
    }

    private fun importFromMediaStore(context: Context, songEntities: List<SongEntity>) = withNotification(context) {
        val songDataBase = MusicDatabase.songsDataBase
        val relationShipDao = songDataBase.RelationShipDao()
        for (song in songEntities) {
            relationShipDao.register(song)
        }
    }

    fun refreshSingleSong(context: Context, songId: Long) =
        refreshSingleSong(context, SongLoader.id(context, songId))

    fun refreshSingleSong(context: Context, song: SongModel) =
        refreshSingleSong(context, Converters.fromSongModel(song))

    fun refreshSingleSong(context: Context, songEntity: SongEntity) {
        scope.launch {
            withNotification(context) {
                val relationShipDao = MusicDatabase.songsDataBase.RelationShipDao()
                relationShipDao.register(songEntity)
            }
        }
    }

    private inline fun withNotification(context: Context, block: (Context) -> Unit) {
        Log.d(TAG, "Start importing")
        DatabaseUpdateNotification.send(context)

        block(context)

        Log.d(TAG, "End importing")
        DatabaseUpdateNotification.cancel(context)
    }

}