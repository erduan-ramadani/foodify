package com.ercoding.foodify.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun ProteinInputSection(
    onClick: (String) -> Unit,
    isLoading: Boolean
) {
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var userTextInput by remember { mutableStateOf("") }

    Column() {
        Text(
            text = "Mahlzeit hinzufügen",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            OutlinedTextField(
                value = userTextInput,
                onValueChange = { userTextInput = it },
                placeholder = { Text("z.B. 200g Hähnchen") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .weight(0.80f)
                    .fillMaxHeight(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onClick(userTextInput)
                        focusRequester.freeFocus()
                        keyboardController?.hide()
                        userTextInput = ""
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    onClick(userTextInput)
                    localFocusManager.clearFocus()
                    userTextInput = ""
                },
//                enabled = userTextInput.isNotBlank()
//                        && userTextInput.length >= 3
//                        && userTextInput.length < 40,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f),
                shape = RoundedCornerShape(20.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
        Spacer(modifier = Modifier.padding(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "KI schätzt Proteine automatisch",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}