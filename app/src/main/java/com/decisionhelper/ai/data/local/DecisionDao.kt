package com.decisionhelper.ai.data.local

import androidx.room.*
import com.decisionhelper.ai.data.model.Decision
import kotlinx.coroutines.flow.Flow

/**
 * 决策记录数据访问对象
 */
@Dao
interface DecisionDao {

    /**
     * 获取所有决策记录（按时间倒序）
     */
    @Query("SELECT * FROM decisions ORDER BY createdAt DESC")
    fun getAllDecisions(): Flow<List<Decision>>

    /**
     * 获取最近 N 条记录
     */
    @Query("SELECT * FROM decisions ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentDecisions(limit: Int): Flow<List<Decision>>

    /**
     * 根据 ID 获取单条记录
     */
    @Query("SELECT * FROM decisions WHERE id = :id")
    suspend fun getDecisionById(id: Long): Decision?

    /**
     * 插入新记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecision(decision: Decision): Long

    /**
     * 更新记录
     */
    @Update
    suspend fun updateDecision(decision: Decision)

    /**
     * 删除记录
     */
    @Delete
    suspend fun deleteDecision(decision: Decision)

    /**
     * 删除所有记录
     */
    @Query("DELETE FROM decisions")
    suspend fun deleteAllDecisions()

    /**
     * 获取记录总数
     */
    @Query("SELECT COUNT(*) FROM decisions")
    suspend fun getDecisionCount(): Int
}
