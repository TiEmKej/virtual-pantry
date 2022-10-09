package com.example.virtualpantry.dataclass

import com.google.gson.annotations.SerializedName

//data class ApiData(
//    var userid: Int? = null,
//    var id: Int? = null,
//    var title: String? = null,
//    var completed: Boolean? = null
//)

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