package com.example.provault.AI

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.provault.ConnectRoute
import com.example.provault.Files.BottomNavRail
import com.google.ai.client.generativeai.GenerativeModel


import kotlinx.coroutines.runBlocking


data class BottomNavRail(
    val title : String,
    val icon : ImageVector,
    val route: String
)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun PromptUI(navController : NavHostController) {



    val selectedIndex = remember { mutableStateOf(2) }
    val items = listOf(
        BottomNavRail("projects", Icons.Default.Lock, "fileUploader"),
        BottomNavRail("VideoCall", Icons.Default.Face, ""),
        BottomNavRail("AI Bot", Icons.Default.Search,"AI"),
        BottomNavRail("AI Bot", Icons.Filled.List, "TODO")
    )

    var promptInput by remember { mutableStateOf("") }
    var generatedResponse by remember { mutableStateOf("No response yet") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("AI helper") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("projects") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigation Icon",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )},
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex.value == index,
                        onClick = {
                            if (index != 1) {
                                selectedIndex.value = index
                                navController.navigate(item.route)
                            } else {
                                navController.navigate(ConnectRoute)
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.route) }
                    )
                }
            }
        },

    ) { }


    val generativeModel =
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = "AIzaSyCUjvYCucJjamkqXlSz0nH6a180RzQos9Y")

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

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
