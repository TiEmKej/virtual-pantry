package com.example.virtualpantry.database

import com.example.virtualpantry.dataclass.ApiData
import com.example.virtualpantry.dataclass.RequestModel
import com.example.virtualpantry.dataclass.ResponseModel

import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("productimg")
    fun sendReq(@Body requestModel: RequestModel) : Call<ResponseModel>
}