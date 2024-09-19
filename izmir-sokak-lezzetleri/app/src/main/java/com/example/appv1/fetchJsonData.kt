package com.example.appv1

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException


suspend fun fetchJsonData(): List<Place> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://raw.githubusercontent.com/ramazandmrr/host_api/main/kotlin.json") // Güncel URL
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonArray = JSONArray(it)
                    val placesList = mutableListOf<Place>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val name = jsonObject.getString("Ad")
                        val location = jsonObject.getString("konum")
                        val image = jsonObject.getString("image") // image alanı
                        placesList.add(Place(name, location, image))
                    }
                    placesList
                } ?: emptyList()
            } else {
                println("Error: ${response.code}")
                emptyList() // Hata durumunda boş liste döndür
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList() // Hata durumunda boş liste döndür
        }
    }
}
suspend fun fetchJsonData2(): List<Place> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://raw.githubusercontent.com/ramazandmrr/host_api/main/deneme.json") // Güncel URL
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonData = response.body?.string()
                jsonData?.let {
                    val jsonArray = JSONArray(it)
                    val placesList = mutableListOf<Place>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val name = jsonObject.getString("Ad")
                        val location = jsonObject.getString("konum")
                        val image = jsonObject.getString("image") // image alanı
                        placesList.add(Place(name, location, image))
                    }
                    placesList
                } ?: emptyList()
            } else {
                println("Error: ${response.code}")
                emptyList() // Hata durumunda boş liste döndür
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList() // Hata durumunda boş liste döndür
        }
    }
}


