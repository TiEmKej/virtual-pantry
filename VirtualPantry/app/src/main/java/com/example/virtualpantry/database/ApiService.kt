package com.example.virtualpantry.database

import com.example.virtualpantry.dataclass.ApiData
import com.example.virtualpantry.dataclass.RequestModel
import com.example.virtualpantry.dataclass.ResponseModel

import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @GET("/todos")
    fun getPosts(): Call<MutableList<ApiData>>

    @GET("test1")
    fun test(): Call<MutableList<ApiData>>

    @POST("users")
    fun sendReq(@Body requestModel: RequestModel) : Call<ResponseModel>
}