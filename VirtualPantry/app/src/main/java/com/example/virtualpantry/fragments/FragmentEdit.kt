package com.example.virtualpantry.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.RemoteCallbackList
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.virtualpantry.R
import com.example.virtualpantry.database.ApiService
import com.example.virtualpantry.database.DataApi
import com.example.virtualpantry.database.DataApi2
import com.example.virtualpantry.dataclass.ApiData
import com.example.virtualpantry.dataclass.ResponseModel

import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.virtualpantry.dataclass.RequestModel
import java.io.ByteArrayOutputStream

class FragmentEdit : Fragment(R.layout.fragment_edit) {
    val TAG = "FragmentEdit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //send_test()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //sendImgUsingPostReq("test")
    }

}

