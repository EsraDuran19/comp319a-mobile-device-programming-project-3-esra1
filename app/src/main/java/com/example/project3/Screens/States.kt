package com.example.project3.Screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.project3.database.Note

data class States(

    val notes: List<Note> = emptyList(),
    val title: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf("")

)