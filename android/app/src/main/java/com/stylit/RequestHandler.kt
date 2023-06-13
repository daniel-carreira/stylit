package com.stylit

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class RequestHandler {
    private val client = OkHttpClient()

    fun getRequest(url: String, onResponse: (Response) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .build()

        val thread = Thread {
            val response = client.newCall(request).execute()
            onResponse(response)
        }
        thread.start()
    }

    fun postRequest(url: String, data: String, onResponse: (Response) -> Unit) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), data)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val thread = Thread {
            val response = client.newCall(request).execute()
            onResponse(response)
        }
        thread.start()
    }
}
