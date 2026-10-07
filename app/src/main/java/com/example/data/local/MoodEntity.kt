package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntity(
    @PrimaryKey val id: String,
    val emoji: String,
    val moodName: String,
    val timestamp: String,
    val note: String,
    val microTensionScore: Int,
    val createdAtMillis: Long = System.currentTimeMillis()
)
