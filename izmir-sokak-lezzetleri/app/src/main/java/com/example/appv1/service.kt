package com.example.appv1

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class Service(private val context: Context, private val onResult: (String) -> Unit) {
    private val apiKey = "GjvsRR7Z5FneW2QjD0Bm"
    private val modelEndpoint = "test-zgbxm/4"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun uploadImage(bitmap: Bitmap) {
        Thread {
            try {
                // Prepare the file for upload
                val file = File.createTempFile("image", ".jpg", context.cacheDir)
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                file.writeBytes(outputStream.toByteArray())

                // Create the request
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.name,
                        RequestBody.create("image/jpeg".toMediaTypeOrNull(), file))
                    .build()

                val request = Request.Builder()
                    .url("https://detect.roboflow.com/$modelEndpoint?api_key=$apiKey")
                    .post(requestBody)
                    .build()

                // Execute the request
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }

                    val responseBody = response.body?.string()
                    responseBody?.let {
                        try {
                            // Parse the JSON response
                            val jsonResponse = JSONObject(it)

                            // Extract the predictions array
                            val predictionsArray = jsonResponse.getJSONArray("predictions")

                            // Initialize the detected class variable
                            var detectedClass = "Class not found"

                            // Loop through the predictions to find the 'class'
                            for (i in 0 until predictionsArray.length()) {
                                val prediction = predictionsArray.getJSONObject(i)
                                if (prediction.has("class")) {
                                    detectedClass = prediction.getString("class")
                                    break // Stop after the first class is found
                                }
                            }

                            // Invoke the callback function with the detected class
                            (context as? MainActivity)?.runOnUiThread {
                                onResult(detectedClass)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            (context as? MainActivity)?.runOnUiThread {
                                Toast.makeText(context, "Yanıt ayrıştırma hatası: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } ?: (context as? MainActivity)?.runOnUiThread {
                        Toast.makeText(context, "Boş yanıt", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                (context as? MainActivity)?.runOnUiThread {
                    Toast.makeText(context, "Yükleme başarısız: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
