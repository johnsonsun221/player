package com.decisionhelper.ai.data.repository

import com.decisionhelper.ai.data.local.DecisionDao
import com.decisionhelper.ai.data.model.Decision
import kotlinx.coroutines.flow.Flow

/**
 * 决策数据仓库
 */
class DecisionRepository(private val decisionDao: DecisionDao) {

    /**
     * 获取所有决策记录
     */
    fun getAllDecisions(): Flow<List<Decision>> = decisionDao.getAllDecisions()

    /**
     * 获取最近的决策记录
     */
    fun getRecentDecisions(limit: Int = 20): Flow<List<Decision>> =
        decisionDao.getRecentDecisions(limit)

    /**
     * 根据 ID 获取决策
     */
    suspend fun getDecisionById(id: Long): Decision? = decisionDao.getDecisionById(id)

    /**
     * 保存决策记录
     */
    suspend fun saveDecision(decision: Decision): Long = decisionDao.insertDecision(decision)

    /**
     * 更新决策记录
     */
    suspend fun updateDecision(decision: Decision) = decisionDao.updateDecision(decision)

    /**
     * 删除决策记录
     */
    suspend fun deleteDecision(decision: Decision) = decisionDao.deleteDecision(decision)

    /**
     * 清空所有记录
     */
    suspend fun clearAllDecisions() = decisionDao.deleteAllDecisions()

    /**
     * 获取记录数量
     */
    suspend fun getDecisionCount(): Int = decisionDao.getDecisionCount()
}
