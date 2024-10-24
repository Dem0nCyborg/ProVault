package com.example.provault.Files
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PinataApi {
    @Multipart
    @POST("pinning/pinFileToIPFS")
    fun uploadFile(
        @Header("pinata_api_key") apiKey: String,
        @Header("pinata_secret_api_key") secretApiKey: String,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}