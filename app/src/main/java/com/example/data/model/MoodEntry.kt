package com.example.data.model

data class MoodEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val emoji: String,
    val moodName: String,
    val timestamp: String,
    val note: String = "",
    val microTensionScore: Int = 15
)
