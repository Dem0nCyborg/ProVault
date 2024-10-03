package com.example.provault.files

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class FileItem(val uri: Uri, val name: String)

class FileViewModel : ViewModel() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val _fileItems = MutableStateFlow<List<FileItem>>(emptyList())
    val fileItems: StateFlow<List<FileItem>> = _fileItems

    init {
        retrieveFiles()
    }

    fun uploadFile(uri: Uri) {
        viewModelScope.launch {
            try {
                val fileRef = storageRef.child("uploads/${System.currentTimeMillis()}")
                fileRef.putFile(uri).await()
                retrieveFiles()
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun retrieveFiles() {
        viewModelScope.launch {
            try {
                val listResult = storageRef.child("uploads").listAll().await()
                val fileItemList = listResult.items.map { item ->
                    val downloadUri = item.downloadUrl.await()
                    FileItem(downloadUri, item.name)
                }
                _fileItems.value = fileItemList
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }
}