package com.eddiapps.foodify.presentation.dashboard.daytab.components

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eddiapps.foodify.R
import java.util.Locale

@Composable
fun rememberSpeechLauncher(onResult: (String) -> Unit): () -> Unit {
    val locale = Locale.getDefault().toLanguageTag()
    val prompt = stringResource(R.string.speech_prompt)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
        if (!spokenText.isNullOrBlank()) onResult(spokenText)
    }

    return {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
        launcher.launch(intent)
    }
}