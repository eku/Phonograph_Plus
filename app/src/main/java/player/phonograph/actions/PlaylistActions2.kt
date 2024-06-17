/*
 *  Copyright (c) 2022~2024 chr_56
 */

package player.phonograph.actions

import player.phonograph.mechanism.playlist2.PlaylistProcessors
import player.phonograph.model.Song
import player.phonograph.model.playlist2.Playlist
import player.phonograph.service.MusicPlayerRemote
import player.phonograph.service.queue.ShuffleMode
import player.phonograph.ui.modules.playlist2.dialogs.AddToPlaylistDialog
import player.phonograph.ui.modules.playlist2.dialogs.ClearPlaylistDialog
import player.phonograph.ui.modules.playlist2.dialogs.RenamePlaylistDialog
import androidx.fragment.app.FragmentActivity
import android.content.Context
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun Playlist.actionPlay(context: Context): Boolean = runBlocking {
    songs(context).let { songs ->
        if (songs.isNotEmpty())
            songs.actionPlay(ShuffleMode.NONE, 0)
        else
            false
    }
}

fun Playlist.actionShuffleAndPlay(context: Context) = runBlocking {
    songs(context).let { songs ->
        if (songs.isNotEmpty())
            songs.actionPlay(ShuffleMode.SHUFFLE, Random.nextInt(songs.size))
        else
            false
    }
}

fun Playlist.actionPlayNext(context: Context): Boolean = runBlocking {
    MusicPlayerRemote.playNext(ArrayList(songs(context)))
}

fun Playlist.actionAddToCurrentQueue(context: Context): Boolean = runBlocking {
    MusicPlayerRemote.enqueue(ArrayList(songs(context)))
}

fun Playlist.actionAddToPlaylist(activity: FragmentActivity) = runBlocking {
    AddToPlaylistDialog.create(songs(activity))
        .show(activity.supportFragmentManager, "ADD_PLAYLIST")
}

fun Playlist.actionRenamePlaylist(activity: FragmentActivity) {
    RenamePlaylistDialog.create(this).show(activity.supportFragmentManager, "RENAME_PLAYLIST")
}

fun Playlist.actionDeletePlaylist(activity: FragmentActivity) {
    ClearPlaylistDialog.create(listOf(this)).show(activity.supportFragmentManager, "CLEAR_PLAYLIST")
}

fun List<Playlist>.actionDeletePlaylists(context: Context): Boolean = fragmentActivity(context) { activity ->
    ClearPlaylistDialog.create(this).show(activity.supportFragmentManager, "CLEAR_PLAYLIST")
    true
}

fun Playlist.actionSavePlaylist(activity: FragmentActivity) {
    CoroutineScope(Dispatchers.Default).launch {
        PlaylistProcessors.duplicate(activity, this@actionSavePlaylist)
    }
}

fun List<Playlist>.actionSavePlaylists(activity: Context) {
    CoroutineScope(Dispatchers.Default).launch {
        PlaylistProcessors.duplicate(activity, this@actionSavePlaylists)
    }
}

private suspend fun Playlist.songs(context: Context): List<Song> =
    PlaylistProcessors.of(this).allSongs(context)