package com.example.provault.AI
/*
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.ai.client.generativeai.GenerativeModel

import kotlinx.coroutines.launch
/*
@Composable
fun PromptUI() {
    var promptInput by remember { mutableStateOf("") }
    var generatedResponse by remember { mutableStateOf("No response yet") }
    val coroutineScope = rememberCoroutineScope()

    // Setup the GenerativeModel
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = ""  //removed for security reasons

    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = promptInput,
            onValueChange = { promptInput = it },
            label = { Text(text = "Prompt") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        Log.d("PromptUI", "Sending prompt: $promptInput")
                        val response = generativeModel.generateContent(promptInput)

                        // Logging response to check if it's valid
                        Log.d("PromptUI", "Response: $response")

                        if (response != null) {
                            generatedResponse = response.toString()
                        } else {
                            generatedResponse = "No response received"
                        }
                    } catch (e: Exception) {
                        Log.e("PromptUI", "Error generating content: ${e.message}")
                        generatedResponse = "Error: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "GENERATE")
        }

        // Display the generated response or error
        Text(
            text = generatedResponse,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewPromptUI() {
    PromptUI()
}*/