package com.decisionhelper.ai

import android.app.Application
import com.decisionhelper.ai.data.local.DecisionDatabase

/**
 * AI 决策小助理 - 应用程序类
 *
 * 帮助用户在生活中做出简单但频繁的选择
 */
class DecisionHelperApp : Application() {

    // 懒加载数据库实例
    val database: DecisionDatabase by lazy {
        DecisionDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: DecisionHelperApp
            private set
    }
}
