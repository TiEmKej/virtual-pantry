package com.example.virtualpantry.dataclass

data class ApiData(
    var title: String? = null
)


data class ResponseModel(
    val message: String
)

data class RequestModel(
    val name: String,
    val data: String
)