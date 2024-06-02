package com.example.project3


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project3.Screens.Actions
import com.example.project3.Screens.States
import com.example.project3.database.Note
import com.example.project3.database.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotePadViewModel(
    private val dao: NoteDao
) : ViewModel() {

    private val isSortedByDateAdded = MutableStateFlow(true)

    private var notes =
        isSortedByDateAdded.flatMapLatest { sort ->
            if (sort) {
                dao.getNotesOrderdByDateAdded()
            } else {
                dao.getNotesOrderdByTitle()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(States())
    val state =
        combine(_state, isSortedByDateAdded, notes) { state, isSortedByDateAdded, notes ->
            state.copy(
                notes = notes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), States())

    fun onEvent(event: Actions) {
        when (event) {
            is Actions.DeleteNote -> {
                viewModelScope.launch {
                    dao.deleteNote(event.note)
                }
            }

            is Actions.SaveNote -> {
                val note = Note(
                    title = state.value.title.value,
                    description = state.value.description.value,
                    dateAdded = System.currentTimeMillis()
                )

                viewModelScope.launch {
                    dao.upsertNote(note)
                }

                _state.update {
                    it.copy(
                        title = mutableStateOf(""),
                        description = mutableStateOf("")
                    )
                }
            }

            Actions.SortNotes -> {
                isSortedByDateAdded.value = !isSortedByDateAdded.value
            }
        }
    }

}