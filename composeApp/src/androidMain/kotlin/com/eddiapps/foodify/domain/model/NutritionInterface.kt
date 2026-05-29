package com.eddiapps.foodify.domain.model

import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.flow.StateFlow

interface NutritionInterface {
    val entries: StateFlow<List<NutritionEntry>>
    fun addEntry(entry: NutritionEntry)
    fun removeEntry(entry: NutritionEntry)
    fun updateEntry(updated: NutritionEntry)
}