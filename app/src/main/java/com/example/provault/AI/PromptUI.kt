package com.example.provault.AI

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.provault.ConnectRoute
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

data class BottomNavRail(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PromptUI(navController: NavHostController) {
    val selectedIndex = remember { mutableStateOf(2) }
    val items = listOf(
        BottomNavRail("Projects", Icons.Default.Lock, "fileUploader"),
        BottomNavRail("VideoCall", Icons.Default.Face, ""),
        BottomNavRail("AI Bot", Icons.Default.Search, "AI"),
        BottomNavRail("To-Do", Icons.Default.List, "TODO")
    )

    var promptInput by remember { mutableStateOf("") }
    var generatedResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = com.example.provault.BuildConfig.Gemini_Api_Key
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("projects") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
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
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 12.sp) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Talk to your personal AI helper ✨",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Chat / Output area
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    } else if (generatedResponse.isNotEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.95f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = generatedResponse,
                                    modifier = Modifier.padding(16.dp),
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }


                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    label = { Text("Type your prompt") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    coroutineScope.launch {
                                        try {
                                            focusManager.clearFocus()
                                            isLoading = true
                                            val prompt = "${promptInput.trim()} " +
                                                    "(Be precise and give a short, clear answer.)"
                                            val response = generativeModel.generateContent(prompt)
                                            generatedResponse = response.text ?: "No response generated."
                                            promptInput = ""

                                            scrollState.animateScrollToItem(0)
                                        } catch (e: Exception) {
                                            generatedResponse = "Error: ${e.message}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}