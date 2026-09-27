package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherCacheDao {
    @Query("SELECT * FROM teacher_cache WHERE cacheKey = :key LIMIT 1")
    suspend fun getCacheByKey(key: String): TeacherCacheEntity?

    @Query("SELECT * FROM teacher_cache ORDER BY timestamp DESC")
    fun getAllCaches(): Flow<List<TeacherCacheEntity>>

    @Query("SELECT * FROM teacher_cache WHERE toolType = :toolType ORDER BY timestamp DESC")
    fun getCachesByTool(toolType: String): Flow<List<TeacherCacheEntity>>

    @Query("SELECT * FROM teacher_cache WHERE queryTitle LIKE '%' || :query || '%' OR responseContent LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchCaches(query: String): Flow<List<TeacherCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: TeacherCacheEntity): Long

    @Query("DELETE FROM teacher_cache WHERE id = :id")
    suspend fun deleteCacheById(id: Long)

    @Query("DELETE FROM teacher_cache WHERE isPreloaded = 0")
    suspend fun clearUserCaches()

    @Query("SELECT COUNT(*) FROM teacher_cache")
    suspend fun getCacheCount(): Int

    @Query("SELECT COALESCE(SUM(tokensSaved), 0) FROM teacher_cache")
    fun getTotalTokensSaved(): Flow<Int>
}
