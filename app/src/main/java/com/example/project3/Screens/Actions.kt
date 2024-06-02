package com.example.project3.Screens

import com.example.project3.database.Note

sealed interface Actions {
    object SortNotes: Actions

    data class DeleteNote(val note: Note): Actions

    data class SaveNote(
        val title: String,
        val description: String
    ): Actions
}