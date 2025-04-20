package com.example.provault.Files

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.provault.ConnectRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONObject
import java.io.File

data class BottomNavRail(
    val title : String,
    val icon : ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UploadAndRetrieve(
    navController : NavHostController
) {



    val selectedIndex = remember { mutableStateOf(0) }
    val items = listOf(
        BottomNavRail("projects", Icons.Default.Lock, "fileUploader"),
        BottomNavRail("VideoCall", Icons.Default.Face, ""),
        BottomNavRail("AI Bot", Icons.Default.Search,"AI"),
        BottomNavRail("AI Bot", Icons.Filled.List, "TODO")
    )

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedFiles by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } // store file names and URLs

    // JWT token for Piñata API
    val jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiI4YjczZmNmZS01YmYyLTRhODUtOGU4NC1mOTA1YTJhMzE4NDQiLCJlbWFpbCI6ImNoYW5kYW5iaG9waTE2MDdAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBpbl9wb2xpY3kiOnsicmVnaW9ucyI6W3siZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiRlJBMSJ9LHsiZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiTllDMSJ9XSwidmVyc2lvbiI6MX0sIm1mYV9lbmFibGVkIjpmYWxzZSwic3RhdHVzIjoiQUNUSVZFIn0sImF1dGhlbnRpY2F0aW9uVHlwZSI6InNjb3BlZEtleSIsInNjb3BlZEtleUtleSI6IjU1OTI2NmJiNWJiODFjZjVlYzFiIiwic2NvcGVkS2V5U2VjcmV0IjoiZWJlYmU4MGNmMjIxM2ZkNDI1ZDYxYmVjZDgwMTgyM2ZjZjUyMWI3NjQ3MWJjMjkxZDUzOTMwMGE1ZWRlNmE4NSIsImV4cCI6MTc2MTI5NjcxOH0.4kKwOxqhioZ5K2Gm67gq5t2SNAmSuW8kIH7_NvBXWjY"

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            fileUri = uri
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = { Text("File Uploader") },
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
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex.value == index,
                        onClick = {if (index != 1){
                            selectedIndex.value = index
                            navController.navigate(item.route)}
                            else{ navController.navigate(ConnectRoute)
                        }
                        },
                        icon = { Icon(item.icon, contentDescription = item.route) }
                    )
                }
            }
        }
    ) {

    }

    // Trigger file retrieval on startup
    LaunchedEffect(Unit) {
        retrieveFilesFromPinata(jwt, scope) { files ->
            uploadedFiles = files
        }
    }

    // UI Elements
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Button to select file
        Button(onClick = { filePickerLauncher.launch("*/*") }) {
            Text("Select File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upload button if file is selected
        fileUri?.let { uri ->
            val file = uriToFile(uri, context)
            file?.let {
                Button(onClick = { uploadFileToPinata(it, jwt, scope) }) {
                    Text("Upload to Pinata")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Grid of uploaded files
        if (uploadedFiles.isNotEmpty()) {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(uploadedFiles.size) { index ->
                    val (fileName, fileUrl) = uploadedFiles[index]
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                openFile(context, fileUrl) // Open the file when clicked
                            }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Lock, contentDescription = "File Icon")
                            BasicText(text = fileName)
                        }
                    }
                }
            }
        }

        }



}





fun uploadFileToPinata(file: File, jwt: String, scope: CoroutineScope) {
    val client = OkHttpClient()
    val formBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", file.name, RequestBody.create("text/plain".toMediaTypeOrNull(), file))
        .addFormDataPart("pinataMetadata", "{\"name\":\"${file.name}\"}") // Set file name in metadata
        .build()

    val request = Request.Builder()
        .url("https://api.pinata.cloud/pinning/pinFileToIPFS")
        .addHeader("Authorization", "Bearer $jwt")
        .post(formBody)
        .build()

    scope.launch(Dispatchers.IO) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println("File uploaded successfully")
                } else {
                    println("Upload failed: ${response.message}")
                }
            }
        })
    }
}

fun retrieveFilesFromPinata(jwt: String, scope: CoroutineScope, onSuccess: (List<Pair<String, String>>) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.pinata.cloud/data/pinList")
        .addHeader("Authorization", "Bearer $jwt")
        .build()

    scope.launch(Dispatchers.IO) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "")
                    val items = jsonResponse.getJSONArray("rows")
                    val files = mutableListOf<Pair<String, String>>()
                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val fileName = item.getJSONObject("metadata").getString("name")
                        val ipfsHash = item.getString("ipfs_pin_hash")
                        val fileUrl = "https://gateway.pinata.cloud/ipfs/$ipfsHash"
                        files.add(Pair(fileName, fileUrl))
                    }
                    // Post the result on the main thread
                    scope.launch(Dispatchers.Main) {
                        onSuccess(files)
                    }
                } else {
                    println("Failed to retrieve files: ${response.message}")
                }
            }
        })
    }
}
fun uriToFile(uri: Uri, context: Context): File? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "tempFile")
    file.outputStream().use { inputStream?.copyTo(it) }
    return file
}

fun openFile(context: Context, fileUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}