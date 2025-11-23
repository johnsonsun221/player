package com.decisionhelper.ai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 决策选项数据类
 */
data class DecisionOption(
    val title: String,
    val description: String = "",
    val score: Float = 0f,      // AI 推荐分数 (0-1)
    val reason: String = ""      // AI 给的理由
)

/**
 * 用户输入的条件
 */
data class DecisionConditions(
    val budget: String = "",        // 预算
    val location: String = "",      // 地点/范围
    val restrictions: String = ""   // 额外限制
)

/**
 * 决策记录实体 - 存储到本地数据库
 */
@Entity(tableName = "decisions")
@TypeConverters(DecisionConverters::class)
data class Decision(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,                    // 用户的问题
    val conditions: DecisionConditions,      // 条件设定
    val options: List<DecisionOption>,       // 候选选项列表
    val selectedOption: DecisionOption?,     // 最终选择
    val aiExplanation: String = "",          // AI 解释
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Room 类型转换器
 */
class DecisionConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromOptionsList(options: List<DecisionOption>): String {
        return gson.toJson(options)
    }

    @TypeConverter
    fun toOptionsList(json: String): List<DecisionOption> {
        val type = object : TypeToken<List<DecisionOption>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromOption(option: DecisionOption?): String {
        return gson.toJson(option)
    }

    @TypeConverter
    fun toOption(json: String): DecisionOption? {
        return if (json.isBlank() || json == "null") null
        else gson.fromJson(json, DecisionOption::class.java)
    }

    @TypeConverter
    fun fromConditions(conditions: DecisionConditions): String {
        return gson.toJson(conditions)
    }

    @TypeConverter
    fun toConditions(json: String): DecisionConditions {
        return gson.fromJson(json, DecisionConditions::class.java)
            ?: DecisionConditions()
    }
}
