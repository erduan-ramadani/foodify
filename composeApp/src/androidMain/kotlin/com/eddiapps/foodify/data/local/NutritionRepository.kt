package com.eddiapps.foodify.data.local

import com.eddiapps.foodify.domain.PreferencesInterface
import com.eddiapps.foodify.domain.model.NutritionInterface
import com.eddiapps.foodify.domain.model.sheet.NutritionEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NutritionRepository(
    private val prefRepository: PreferencesInterface,
    private val scope: CoroutineScope
) : NutritionInterface {
    private val _entries = MutableStateFlow<List<NutritionEntry>>(emptyList())
    override val entries: StateFlow<List<NutritionEntry>> = _entries.asStateFlow()

    init {
        scope.launch {
            _entries.value = prefRepository.getNutritionEntries()
        }
    }

    override fun addEntry(entry: NutritionEntry) {
        _entries.value = _entries.value + entry
        persist()
    }

    override fun removeEntry(entry: NutritionEntry) {
        _entries.value = _entries.value.filterNot { it.id == entry.id }
        persist()
    }

    override fun updateEntry(updated: NutritionEntry) {
        _entries.value = _entries.value.map {
            if (it.id == updated.id) updated else it
        }
        persist()
    }

    private fun persist() {
        scope.launch {
            prefRepository.setNutritionEntries(_entries.value)
        }
    }
}