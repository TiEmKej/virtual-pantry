package com.example.virtualpantry.database

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataApi {
    var retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.31.244:3565/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> buildService(service: Class<T>):T{
        return retrofit.create(service)
    }
}

object DataApi2{
    private val client = OkHttpClient.Builder().build()

    val retrofit2 = Retrofit.Builder()
        .baseUrl("http://192.168.31.244:3565/") //
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit2.create(service)
    }
}