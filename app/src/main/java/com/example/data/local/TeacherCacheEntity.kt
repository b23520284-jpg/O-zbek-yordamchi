package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "teacher_cache",
    indices = [
        Index(value = ["cacheKey"], unique = true),
        Index(value = ["toolType"])
    ]
)
data class TeacherCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cacheKey: String,
    val toolType: String,
    val queryTitle: String,
    val promptSent: String,
    val responseContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tokensSaved: Int = 0,
    val isPreloaded: Boolean = false
)
