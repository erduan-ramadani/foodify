package com.ercoding.foodify.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    onConfirmClick: () -> Unit
) {
    val viewModel: OnboardingViewModel = koinViewModel()
    var proteinGoal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Täglicher Eiweißbedarf?",
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = proteinGoal,
            onValueChange = {
                if (it.all(Char::isDigit)) {
                    proteinGoal = it
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Empfohlene Menge: 2gr pro KG Körpergewicht",
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            onClick = {
                viewModel.setProteinGoal(proteinGoal.toInt())
                onConfirmClick()
            },
            enabled = proteinGoal.isNotBlank()
        ) {
            Text("Bestätigen")
        }
    }
}