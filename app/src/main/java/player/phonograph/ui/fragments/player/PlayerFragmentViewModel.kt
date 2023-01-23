/*
 * Copyright (c) 2022 chr_56
 */

package player.phonograph.ui.fragments.player

import android.app.Application
import android.view.MenuItem
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import player.phonograph.model.Song
import player.phonograph.model.lyrics.AbsLyrics
import player.phonograph.model.lyrics.LyricsList
import player.phonograph.mediastore.LyricsLoader
import player.phonograph.notification.ErrorNotification
import player.phonograph.util.FavoriteUtil.isFavorite
import androidx.lifecycle.viewModelScope
import android.content.Context

class PlayerFragmentViewModel(application: Application) : AndroidViewModel(application) {

    val applicationContext get() = getApplication<Application>()

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        ErrorNotification.postErrorNotification(throwable)
    }

    var currentSong: Song = Song.EMPTY_SONG
        private set

    fun updateCurrentSong(song: Song, context: Context?) {
        currentSong = song
        loadLyrics(song)
        updateFavoriteState(song, context)
    }

    var lyricsMenuItem: MenuItem? = null

    private var _lyricsList: MutableStateFlow<LyricsList> = MutableStateFlow(LyricsList())
    val lyricsList get() = _lyricsList.asStateFlow()

    var currentLyrics: AbsLyrics? = null
        private set

    fun forceReplaceLyrics(lyrics: AbsLyrics) {
        currentLyrics = lyrics
    }

    private var loadLyricsJob: Job? = null
    fun loadLyrics(song: Song) {
        // cancel old song's lyrics after switching
        loadLyricsJob?.cancel()
        currentLyrics = null
        _lyricsList.value = LyricsList()
        lyricsMenuItem?.isVisible = false
        // load new lyrics
        loadLyricsJob = viewModelScope.launch {
            if (song == Song.EMPTY_SONG) return@launch
            _lyricsList.emit(
                LyricsLoader.loadLyrics(File(song.data), song)
            )
            currentLyrics = _lyricsList.value.getAvailableLyrics()
        }
    }

    private var _favoriteState: MutableStateFlow<Pair<Song, Boolean>> =
        MutableStateFlow(Song.EMPTY_SONG to false)
    val favoriteState = _favoriteState.asStateFlow()

    private var loadFavoriteStateJob: Job? = null
    fun updateFavoriteState(song: Song, context: Context?) {
        loadFavoriteStateJob?.cancel()
        _favoriteState.value = Song.EMPTY_SONG to false
        loadFavoriteStateJob = viewModelScope.launch(exceptionHandler) {
            if (song == Song.EMPTY_SONG) return@launch
            _favoriteState.emit(song to isFavorite(context ?: applicationContext, song))
        }
    }

    companion object {
        fun from(application: Application): ViewModelProvider.Factory {
            return ViewModelProvider.AndroidViewModelFactory(application)
        }
    }
}
