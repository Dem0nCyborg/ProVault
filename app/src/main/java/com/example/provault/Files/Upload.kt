package com.example.provault.files

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
 import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.provault.ConnectRoute

@Composable
fun FileUploaderScreen(
    navController: NavController,
    viewModel: FileViewModel
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.uploadFile(uri)
                Toast.makeText(context, "File Uploaded Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                filePickerLauncher.launch("*/*") // You can specify a specific MIME type like "image/*"
            }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Upload File")
            }
        },
        bottomBar = {
            BottomAppBar {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("profile")
                        }

                    ) {
                        Icon(Icons.Default.AccountBox, contentDescription = "Profile")
                    }
                    IconButton(onClick = { navController.navigate(ConnectRoute) }
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Profile")
                    }
                }

            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FileGrid(viewModel = viewModel, navController = navController)
        }
    }
}

@Composable
fun FileGrid(viewModel: FileViewModel, navController: NavController) {
    val fileItems by viewModel.fileItems.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(fileItems) { fileItem ->
            FileItemView(fileItem, navController)
        }
    }
}

@Composable
fun FileItemView(fileItem: FileItem, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
            .clickable {
                openFile(fileItem.uri, navController.context)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilePreview(uri = fileItem.uri)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = fileItem.name,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun FilePreview(uri: Uri) {
    // Replace with the appropriate icon for file preview
    val icon: ImageVector = Icons.Default.Lock

    Icon(
        imageVector = icon,
        contentDescription = "File Icon",
        modifier = Modifier
            .size(80.dp)
            .padding(8.dp)
    )
}

fun openFile(uri: Uri, context: android.content.Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, context.contentResolver.getType(uri))
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(Intent.createChooser(intent, "Open with"))
}