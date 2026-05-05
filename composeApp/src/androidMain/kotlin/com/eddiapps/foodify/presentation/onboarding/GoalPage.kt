package com.eddiapps.foodify.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eddiapps.foodify.R

@Composable
fun GoalPage(
    vm: OnboardingViewModel,
    currentStep: Int,
    totalSteps: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(
                R.string.step_indicator,
                currentStep, totalSteps
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.goal_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.goal_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // BMR Card im BMI-Stil
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.calorie_base),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = vm.bmr.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "kcal/Tag",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Section Header
        Text(
            text = stringResource(R.string.weekly_goal),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        WeightGoalSelector(
            selected = vm.weightGoal,
            onSelect = { vm.weightGoal = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tagesziel-Resultat im BMI-Stil
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.daily_goal),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = vm.dailyCalorieLimit.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.kcal_day),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Spacer(modifier = Modifier.height(14.dp))

                // Aufschlüsselung
                GoalDetailRow(
                    label = stringResource(R.string.deficit),
                    value = "${vm.weightGoal?.dailyDeficit ?: 0} kcal/Tag"
                )
                Spacer(modifier = Modifier.height(8.dp))
                GoalDetailRow(
                    label = stringResource(R.string.speed),
                    value = stringResource(
                        R.string.kg_week,
                        vm.weightGoal?.kgPerWeek ?: 0.0
                    )
                )
            }
        }
    }
}

@Composable
private fun GoalDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}