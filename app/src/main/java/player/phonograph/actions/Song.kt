/*
 * Copyright (c) 2022 chr_56
 */

package player.phonograph.actions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.util.Pair
import com.github.chr56.android.menu_dsl.attach
import com.github.chr56.android.menu_dsl.menuItem
import com.github.chr56.android.menu_dsl.submenu
import player.phonograph.R
import player.phonograph.interfaces.PaletteColorHolder
import player.phonograph.model.Song
import player.phonograph.service.MusicPlayerRemote
import player.phonograph.util.BlacklistUtil
import player.phonograph.util.NavigationUtil
import player.phonograph.util.RingtoneManager
import util.phonograph.tageditor.AbsTagEditorActivity
import util.phonograph.tageditor.AbsTagEditorActivity.EXTRA_PALETTE
import util.phonograph.tageditor.SongTagEditorActivity

fun applyToPopupMenu(
    context: Context,
    menu: Menu,
    song: Song,
    showPlay: Boolean,
    index: Int,
    transitionView: View?,
) = context.run {
    attach(menu) {
        if (showPlay) menuItem(title = getString(R.string.action_play)) { // id = R.id.action_play_
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
            onClick {
                MusicPlayerRemote.playNow(song)
                true
            }
        }
        menuItem(title = getString(R.string.action_play_next)) { // id = R.id.action_play_next
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
            onClick {
                MusicPlayerRemote.playNext(song)
                true
            }
        }
        if (index >= 0) {
            menuItem(title = getString(R.string.action_remove_from_playing_queue)) {
                showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
                onClick {
                    MusicPlayerRemote.removeFromQueue(index)
                    true
                }
            }
        } else {
            menuItem(title = getString(R.string.action_add_to_playing_queue)) { // id = R.id.action_add_to_current_playing
                showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
                onClick {
                    MusicPlayerRemote.enqueue(song)
                    true
                }
            }
        }
        menuItem(title = getString(R.string.action_add_to_playlist)) { // id = R.id.action_add_to_playlist
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
            onClick {
                actionAddToPlaylist(listOf(song))
            }
        }
        menuItem(title = getString(R.string.action_go_to_album)) { // id = R.id.action_go_to_album
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
            onClick {
                activity(context) {
                    if (transitionView != null) {
                        NavigationUtil.goToAlbum(
                            it,
                            song.albumId,
                            Pair(transitionView, context.resources.getString(R.string.transition_album_art))
                        )
                    } else {
                        NavigationUtil.goToAlbum(it, song.albumId)
                    }
                    true
                }
            }
        }
        menuItem(title = getString(R.string.action_go_to_artist)) { // id = R.id.action_go_to_artist
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
            onClick {
                activity(context) {
                    if (transitionView != null) {
                        NavigationUtil.goToArtist(
                            it,
                            song.artistId,
                            Pair(transitionView, context.resources.getString(R.string.transition_artist_image))
                        )
                    } else {
                        NavigationUtil.goToArtist(it, song.artistId)
                    }
                    true
                }
            }
        }
        menuItem(title = getString(R.string.action_details)) { // id = R.id.action_details
            showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
            onClick {
                fragmentActivity(context) { gotoDetail(it, song) }
                true
            }
        }
        submenu(context.getString(R.string.more_actions)) {
            menuItem(title = getString(R.string.action_share)) { // id = R.id.action_share
                showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
                onClick { share(context, song) }
            }
            menuItem(title = getString(R.string.action_tag_editor)) { // id = R.id.action_tag_editor
                showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
                onClick { tagEditor(context, song) }
            }
            menuItem(title = getString(R.string.action_set_as_ringtone)) { // id = R.id.action_set_as_ringtone
                showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
                onClick {
                    if (RingtoneManager.requiresDialog(context)) {
                        RingtoneManager.showDialog(context)
                    } else {
                        RingtoneManager.setRingtone(context, song.id)
                    }
                    true
                }
            }
            menuItem(title = getString(R.string.action_add_to_black_list)) { // id = R.id.action_add_to_black_list
                showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
                onClick {
                    BlacklistUtil.addToBlacklist(context, song)
                    true
                }
            }
            menuItem(title = getString(R.string.action_delete_from_device)) { // id = R.id.action_delete_from_device
                showAsActionFlag = MenuItem.SHOW_AS_ACTION_NEVER
                onClick {
                    actionDelete(listOf(song))
                    true
                }
            }
        }
    }
}

private inline fun activity(context: Context, block: (Activity) -> Boolean): Boolean = if (context is Activity) {
    block(context)
} else {
    false
}

fun tagEditor(context: Context, song: Song): Boolean {
    context.startActivity(Intent(context, SongTagEditorActivity::class.java).apply {
        putExtra(AbsTagEditorActivity.EXTRA_ID, song.id)
        (context as? PaletteColorHolder)?.let {
            putExtra(EXTRA_PALETTE, it.paletteColor)
        }
    })
    return true
}