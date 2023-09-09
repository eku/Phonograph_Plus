/*
 *  Copyright (c) 2022~2023 chr_56
 */

package player.phonograph.ui.compose.tag

import org.jaudiotagger.tag.FieldKey
import player.phonograph.model.RawTag
import player.phonograph.model.SongInfoModel
import player.phonograph.model.TagData
import player.phonograph.model.TagData.TextData
import player.phonograph.model.TagFormat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TagInfoTableViewModel(state: TagInfoTableState) : ViewModel() {

    private val _viewState: MutableStateFlow<TagInfoTableState> = MutableStateFlow(state)
    val viewState get() = _viewState.asStateFlow()

    fun process(event: TagInfoTableEvent) {
        when (event) {
            is TagInfoTableEvent.UpdateTag -> editTag(EditAction.Update(event.fieldKey, event.newValue))

            is TagInfoTableEvent.AddNewTag -> {
                val newState = appendNewTagToView(event.fieldKey)
                _viewState.update { newState }
                editTag(EditAction.Update(event.fieldKey, ""))
            }

            is TagInfoTableEvent.RemoveTag -> {
                val newState = removeTagFromView(event.fieldKey)
                _viewState.update { newState }
                editTag(EditAction.Delete(event.fieldKey))
            }
        }
    }

    //region Edit
    private var _pendingEditRequests: MutableList<EditAction> = mutableListOf()
    val pendingEditRequests: List<EditAction> get() = _pendingEditRequests.toList()

    val hasEdited: Boolean get() = pendingEditRequests.isNotEmpty()

    fun mergeActions() {
        _pendingEditRequests = EditAction.merge(_pendingEditRequests)
    }

    private fun editTag(action: EditAction): Boolean {
        return if (_viewState.value.editable) {
            _pendingEditRequests.add(action)
            true
        } else {
            false
        }
    }

    private fun appendNewTagToView(fieldKey: FieldKey): TagInfoTableState {
        val tmp = _viewState.value.tagFields + (fieldKey to TextData(""))
        return _viewState.value.copy(tagFields = tmp)
    }

    private fun removeTagFromView(fieldKey: FieldKey): TagInfoTableState {
        val tmp = _viewState.value.tagFields.toMutableMap().apply { remove(fieldKey) }
        return _viewState.value.copy(tagFields = tmp)
    }
    //endregion


    private val _prefillsMap: MutableStateFlow<MutableMap<FieldKey, List<String>>> = MutableStateFlow(mutableMapOf())
    val prefillsMap get() = _prefillsMap.asStateFlow()

    fun insertPrefill(key: FieldKey, value: String) {
        val newList = (_prefillsMap.value[key] ?: listOf()) + value
        val newMap = _prefillsMap.value.also { it[key] = newList }
        _prefillsMap.tryEmit(newMap)
    }

    fun insertPrefill(key: FieldKey, values: List<String>) {
        val newList = (_prefillsMap.value[key] ?: listOf()) + values
        val newMap = _prefillsMap.value.also { it[key] = newList }
        _prefillsMap.tryEmit(newMap)
    }
}

data class TagInfoTableState(
    val editable: Boolean,
    val tagFormat: TagFormat,
    val tagFields: Map<FieldKey, TagData>,
    val allTags: Map<String, RawTag>,
) {
    companion object {
        fun from(songInfoModel: SongInfoModel, editable: Boolean): TagInfoTableState {
            val tagFields =
                songInfoModel.tagFields.filter { it.value.content is TextData }.mapValues { it.value.content }
            return TagInfoTableState(editable, songInfoModel.tagFormat, tagFields, songInfoModel.allTags)
        }
    }
}

sealed interface TagInfoTableEvent {
    data class UpdateTag(val fieldKey: FieldKey, val newValue: String) : TagInfoTableEvent
    data class AddNewTag(val fieldKey: FieldKey) : TagInfoTableEvent
    data class RemoveTag(val fieldKey: FieldKey) : TagInfoTableEvent
}


