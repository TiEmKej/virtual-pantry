package com.example.virtualpantry.database

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private var server_ip: String = "77.65.19.238"
private var server_port: String = "3565"
private var base_url: String = "http://$server_ip:$server_port/"


object DataApi {
    private val client = OkHttpClient.Builder().build()

    val retrofit2 = Retrofit.Builder()
        .baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit2.create(service)
    }
}