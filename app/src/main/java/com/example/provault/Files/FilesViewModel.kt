package com.example.provault.files

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import okio.IOException
import java.io.File

class FileViewModel : ViewModel() {

    // Function to upload file to Piñata
    fun uploadFileToPinata(uri: Uri, context: Context) {
        // Get file path from the URI
        val file = File(getFilePathFromUri(uri, context))

        // Upload to Piñata
        uploadToPinata(file)
    }

    private fun uploadToPinata(file: File) {
        val client = OkHttpClient()
        val mediaType = "multipart/form-data".toMediaTypeOrNull()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody(mediaType)
            )
            .build()

        val request = Request.Builder()
            .url("https://api.pinata.cloud/pinning/pinFileToIPFS")
            .addHeader("Authorization", "Bearer YOUR_JWT_TOKEN")  // Replace with your Piñata JWT token
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    println("File uploaded successfully: ${response.body?.string()}")
                } else {
                    println("Upload failed: ${response.body?.string()}")
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("Error uploading file: ${e.message}")
            }
        })
    }

    private fun getFilePathFromUri(uri: Uri, context: Context): String {
        var filePath = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            filePath = cursor.getString(nameIndex)
        }
        return filePath
    }
}