package com.example.provault.Files

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.provault.PIDGlobal
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONObject
import java.io.File
import java.util.UUID
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

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

    val files = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(Unit) {
        val fetchedFiles = fetchFilesForSelectedProject()
        files.clear()
        files.addAll(fetchedFiles)
    }

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
                title = { Text(
                    PIDGlobal.selectedProjectName.toString() + ": ${PIDGlobal.selectedProjectId}",
                ) },
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

    LaunchedEffect(Unit) {
        retrieveFilesFromPinata(jwt, scope) { files ->
            uploadedFiles = files
        }
    }

    // UI Elements
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 70.dp),
        horizontalAlignment = Alignment.End,

    ) {

        LazyColumn {
            items(files) { file ->
                FileCard(fileName = file.first, context = context, fileLink = file.second)
            }
        }

        FloatingActionButton(onClick = { filePickerLauncher.launch("*/*") }) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Select File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        fileUri?.let { uri ->
            val file = uriToFile(uri, context)
            file?.let {
                Button(onClick = {
                    uploadFileToPinata(it, jwt, scope)
                    //TODO : Change this if files doesn't go to Pinata
                    uploadFileToFirebase(
                        navController,
                        context = context,
                        fileUri = fileUri,
                        onSuccess = { /* maybe refresh your list */ },
                        onFailure = { e -> Log.e("Upload", "Failed: ${e.message}") }
                    )
                    navController.navigate("fileUploader"){
                        popUpTo("fileUploader") {
                            inclusive = true
                        }
                    }

                }
                    ) {
                    Text("Upload to Pinata")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

fun uploadFileToFirebase(
    navController: NavHostController,
    context: Context,
    fileUri: Uri?,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    if (fileUri == null) {
        Toast.makeText(context, "No file selected!", Toast.LENGTH_SHORT).show()
        return
    }

    val storageRef = Firebase.storage.reference
    val folderName = PIDGlobal.selectedProjectId
    val folderRef = storageRef.child("uploads/$folderName")

    // Auto-create folder by referencing file path
    val fileName = UUID.randomUUID().toString() + "-" + (fileUri.lastPathSegment ?: "file")
    val fileRef = folderRef.child(fileName)

    fileRef.putFile(fileUri)
        .addOnSuccessListener {
            Toast.makeText(context, "File uploaded successfully!", Toast.LENGTH_SHORT).show()
            navController.navigate("fileUploader") {
                popUpTo("fileUploader") {
                    inclusive = true
                }
            }
            onSuccess()
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Upload failed: ${exception.message}", Toast.LENGTH_LONG).show()
            onFailure(exception)
        }
}




@Composable
fun FileCard(fileName: String, context: Context, fileLink: String) {
    val isImage = fileName.endsWith(".jpg", true) || fileName.endsWith(".png", true)
    val fileIcon = when {
        fileName.endsWith(".pdf", true) -> Icons.Default.Lock
        isImage -> Icons.Default.Lock
        else -> Icons.Default.Lock
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { openFile1(context, fileLink) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = fileIcon,
                contentDescription = fileName,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = fileName)
        }
    }
}

fun openFile1(context: Context, fileUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(fileUrl)
        type = "*/*"
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val chooser = Intent.createChooser(intent, "Open file with")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(chooser)
    } else {
        Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
    }
}


suspend fun fetchFilesForSelectedProject(): List<Pair<String, String>> {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val selectedProjectId = PIDGlobal.selectedProjectId
    val projectFolderRef = storageRef.child("uploads/$selectedProjectId")

    return try {
        val result = projectFolderRef.listAll().await()
        result.items.map { item ->
            val fileName = item.name
            val downloadUrl = item.downloadUrl.await().toString()
            Pair(fileName, downloadUrl)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}