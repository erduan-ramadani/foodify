package com.eddiapps.foodify.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eddiapps.foodify.domain.calculation.UnitConverter.convertCmToFeetInches
import com.eddiapps.foodify.domain.calculation.UnitConverter.convertKgToLb
import com.eddiapps.foodify.domain.calculation.UnitConverter.convertLbToKg
import com.eddiapps.foodify.domain.calculation.UnitConverter.toDecimal
import com.eddiapps.foodify.domain.calculation.calculateBMR
import com.eddiapps.foodify.domain.calculation.calculateTDEE
import com.eddiapps.foodify.domain.model.UnitSystem
import com.eddiapps.foodify.domain.model.onboarding.OnboardingData
import com.eddiapps.foodify.domain.model.onboarding.WeightGoal
import com.eddiapps.foodify.presentation.picker.PickerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow
import kotlin.math.roundToInt

class OnboardingViewModel(
) : ViewModel() {
    var unitSystem: UnitSystem by mutableStateOf(UnitSystem.METRIC)
        private set
    private val _pickerState = MutableStateFlow(
        PickerState(
            age = 36,
            heightCm = 180,
            weightKg = 80.0,
            heightFt = 3,
            heightIn = 5,
            weightLb = 242
        )
    )
    val pickerState = _pickerState.asStateFlow()
    var isMale: Boolean? by mutableStateOf(null)
    val ageRange: ClosedFloatingPointRange<Float> = 10f..99f

    // HEIGHT
    val displayHeightText: String
        get() = if (unitSystem == UnitSystem.METRIC) {
            "${_pickerState.value.heightCm} $heightUnit"
        } else {
            "${_pickerState.value.heightFt}'${_pickerState.value.heightIn}\" ft/in"
        }
    val heightUnit: String
        get() = if (unitSystem == UnitSystem.METRIC) "cm" else "ft/in"

    // WEIGHT
    val displayWeightText: String
        get() = if (unitSystem == UnitSystem.METRIC) {
            "${"%.1f".format(_pickerState.value.weightKg)} $weightUnit"
        } else {
            "${_pickerState.value.weightLb} $weightUnit"
        }
    val weightUnit: String
        get() = if (unitSystem == UnitSystem.METRIC) "kg" else "lb"
    var weightGoal: WeightGoal? by mutableStateOf(WeightGoal.NORMAL)

    private val bmiRange: Pair<Double, Double>
        get() = when (activityLevel) {
            ActivityLevel.SEDENTARY -> 22.0 to 24.0
            ActivityLevel.LIGHT -> 23.0 to 25.0
            ActivityLevel.ACTIVE -> 23.0 to 26.0
            ActivityLevel.VERY_ACTIVE -> 24.0 to 27.0
            null -> 23.0 to 25.0
        }

    val idealWeightMin: Int
        get() {
            val minBmi = bmiRange.first
            val idealWeightMinKg = minBmi * (_pickerState.value.heightCm / 100.0).pow(2)
            return if (unitSystem == UnitSystem.METRIC) {
                idealWeightMinKg.roundToInt()
            } else {
                (idealWeightMinKg * 2.20462).roundToInt()
            }
        }

    val idealWeightMax: Int
        get() {
            val maxBmi = bmiRange.second
            val idealWeightMaxKg = maxBmi * (_pickerState.value.heightCm / 100.0).pow(2)
            return if (unitSystem == UnitSystem.METRIC) {
                idealWeightMaxKg.roundToInt()
            } else {
                (idealWeightMaxKg * 2.20462).roundToInt()
            }
        }

    val dailyCalorieLimit: Int
        get() = bmr - (weightGoal?.dailyDeficit ?: 0)
    val bmr: Int
        get() = calculateBMR(
            isMale == true,
            _pickerState.value.weightKg,
            _pickerState.value.heightCm,
            _pickerState.value.age
        ).toInt()
    var activityLevel: ActivityLevel? by mutableStateOf(null)
    val tdee: Int
        get() = calculateTDEE(bmr, activityLevel)
    val weightGoalDisplayValue: Double
        get() {
            return if (unitSystem == UnitSystem.METRIC) {
                weightGoal?.kgPerWeek ?: 0.5
            } else {
                when (weightGoal) {
                    WeightGoal.SLOW -> 0.5
                    WeightGoal.NORMAL -> 1.0
                    WeightGoal.FAST -> 1.5
                    WeightGoal.AGGRESSIVE -> 2.0
                    else -> {
                        1.0
                    }
                }
            }
        }

    fun getOnboardingData(
    ): OnboardingData {
        return OnboardingData(
            isMale = isMale ?: false,
            unitSystem = unitSystem,
            pickerState = _pickerState.value,
            dailyCalorieLimit = dailyCalorieLimit,
            weightGoal = weightGoal,
            tdee = tdee
        )
    }

    fun canProceed(currentPage: Int): Boolean = when (currentPage) {
        0 -> isMale != null
        1 -> activityLevel != null
        2 -> true
        else -> false
    }

    fun onUnitSystemChange(system: UnitSystem) {
        unitSystem = system
    }

    fun onWeightPicked(firstPickerValue: Int, secondPickerValue: Int?) {
        if (unitSystem == UnitSystem.METRIC) {
            val newWeightKg = toDecimal(firstPickerValue, secondPickerValue ?: 0)
            _pickerState.update { current ->
                current.copy(
                    weightKg = newWeightKg,
                    weightLb = convertKgToLb(newWeightKg)
                )
            }
        } else {
            _pickerState.update { current ->
                current.copy(
                    weightLb = firstPickerValue,
                    weightKg = convertLbToKg(firstPickerValue)
                )
            }
        }
    }

    fun onHeightPicked(firstPickerValue: Int, secondPickerValue: Int?) {
        _pickerState.update { current ->
            if (unitSystem == UnitSystem.METRIC) {
                val heightFtIn = convertCmToFeetInches(firstPickerValue)
                current.copy(
                    heightCm = firstPickerValue,
                    heightFt = heightFtIn.first,
                    heightIn = heightFtIn.second
                )
            } else {
                current.copy(
                    heightFt = firstPickerValue,
                    heightIn = secondPickerValue ?: 0,
                    heightCm = ((firstPickerValue * 12 + (secondPickerValue ?: 0)) * 2.54).toInt()
                )
            }
        }
    }

    fun onAgePicked(newAge: Int) {
        _pickerState.update { current ->
            current.copy(age = newAge)
        }
    }
}
