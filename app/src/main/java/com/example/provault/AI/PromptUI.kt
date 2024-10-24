package com.example.provault.AI

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.ai.client.generativeai.GenerativeModel


import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PromptUI() {
    var promptInput by remember { mutableStateOf("") }
    var generatedResponse by remember { mutableStateOf("No response yet") }
    val coroutineScope = rememberCoroutineScope()


    val generativeModel =
        GenerativeModel(
            // Specify a Gemini model appropriate for your use case
            modelName = "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = "AIzaSyCUjvYCucJjamkqXlSz0nH6a180RzQos9Y")

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
                runBlocking {
                    val prompt = promptInput+"In about 100-200 words if possible"
        val response = generativeModel.generateContent(prompt)
        generatedResponse = response.text!!
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "GENERATE")
        }

        LazyColumn {
        item {
            Text(text = generatedResponse)
        }

    }
    }



}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewPromptUI() {
    PromptUI()
}