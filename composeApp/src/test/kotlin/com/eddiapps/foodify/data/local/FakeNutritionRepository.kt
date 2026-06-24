package com.eddiapps.foodify.data.local

import com.eddiapps.foodify.domain.model.NutritionInterface
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeNutritionRepository(
    initialEntries: List<NutritionEntry> = emptyList()
) : NutritionInterface {

    private val _entries = MutableStateFlow(initialEntries)
    override val entries: StateFlow<List<NutritionEntry>> = _entries.asStateFlow()

    var clearAllCached = false
        private set

    override fun addEntry(entry: NutritionEntry) {
        _entries.value += entry
    }

    override fun removeEntry(entry: NutritionEntry) {
        _entries.value = _entries.value.filter { it.id != entry.id }
    }

    override fun updateEntry(updated: NutritionEntry) {
        _entries.value = _entries.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    override fun clearAll() {
        _entries.value = emptyList()
        clearAllCached = true
    }
}