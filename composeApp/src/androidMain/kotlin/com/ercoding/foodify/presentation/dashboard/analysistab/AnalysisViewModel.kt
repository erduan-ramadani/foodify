package com.ercoding.foodify.presentation.dashboard.analysistab

import androidx.lifecycle.ViewModel
import com.ercoding.foodify.presentation.dashboard.analysistab.components.DayData

class AnalysisViewModel : ViewModel() {
    var range: Int = 0// 7, 30, oder 90
    val estimatedKg: Double = 0.0   // positiv = abgenommen
    val netDeficit: Int = 0         // kcal Defizit/Überschuss
    val goalProgress: Float = 0f    // 0f..100f
    val avgConsumed: Int = 0
    val avgBurned: Int = 0
    val totalBurned: Int = 0
    val totalConsumed: Int = 0
    val trackedDays: Int = 0
    val bestDay: DayData? = null
    val weekData: List<DayData> = emptyList()
    val dailyLimit: Int = 0
}