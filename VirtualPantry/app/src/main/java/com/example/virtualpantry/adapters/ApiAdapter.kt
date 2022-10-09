package com.example.virtualpantry.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import android.widget.EditText
import com.example.virtualpantry.database.ApiService
import com.example.virtualpantry.database.DataApi
import com.example.virtualpantry.database.DataApi2
import com.example.virtualpantry.dataclass.ApiData
import com.example.virtualpantry.dataclass.RequestModel
import com.example.virtualpantry.dataclass.ResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class ApiAdapter(product_name: Int, content: Context) {
    var rec_name: Int
    var rec_context: Context

    private val TAG = "ApiAdapter"

    init {
        Log.i(TAG, "First initializer block that prints ${product_name}")
        rec_name = product_name
        rec_context = content
    }

    fun largeLog(tag: String, content: String) {
        if (content.length > 4000) {
            Log.i(tag, content.substring(0, 4000))
            largeLog(tag, content.substring(4000))
        } else {
            Log.i(tag, content);
        }
    }

    fun send_test() {
        val serviceGenerator = DataApi.buildService(ApiService::class.java)
        val call = serviceGenerator.test()

        call.enqueue(object : Callback<MutableList<ApiData>> {
            override fun onResponse(
                call: Call<MutableList<ApiData>>,
                response: Response<MutableList<ApiData>>
            ) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Recived: ${response.body().toString()}")
                }
            }

            override fun onFailure(call: Call<MutableList<ApiData>>, t: Throwable) {
                t.printStackTrace()
                Log.e(TAG, t.message.toString())
            }
        })
    }

    fun sendImgUsingPostReq(imgPath: String): String
    {
        var ret: String = "fail"

        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            inJustDecodeBounds = false
            inSampleSize = 1
            inPurgeable = true
        }


        var bitmap: Bitmap = BitmapFactory.decodeFile(imgPath, bmOptions)
        Log.i(TAG, "width = ${bitmap.width} / height = ${bitmap.height}")

        lateinit var dstBmp: Bitmap
        if (bitmap.getWidth() >= bitmap.getHeight()){
            dstBmp = Bitmap.createBitmap(
                bitmap,
                bitmap.getWidth()/2 - bitmap.getHeight()/2,
                0,
                bitmap.getHeight(),
                bitmap.getHeight()
            );
        }else{
            dstBmp = Bitmap.createBitmap(
                bitmap,
                0,
                bitmap.getHeight()/2 - bitmap.getWidth()/2,
                bitmap.getWidth(),
                bitmap.getWidth()
            );
        }

        bitmap = dstBmp
        val byteArrayOutputStream = ByteArrayOutputStream()
        var final_bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)

        val matrix = Matrix()
        matrix.postRotate(90F)
        var final_rotatedBitmap: Bitmap = Bitmap.createBitmap(final_bitmap, 0, 0, final_bitmap.getWidth(), final_bitmap.getHeight(), matrix, true)
        bitmap = final_rotatedBitmap

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        val imageString: String = Base64.encodeToString(imageBytes, Base64.URL_SAFE)

        //largeLog(TAG, imageString)
        //Log.i(TAG, "->${imageString}<-")
        var response = DataApi2.buildService(ApiService::class.java)
        val obj = RequestModel("Test_image_123.png", imageString)
        response.sendReq(obj).enqueue(
            object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    Log.i("FragmentEdit", "Success response : ${response.message()} / ${response.body()}")
                    var x: EditText
                    x = EditText(rec_context)
                    x.findViewById<EditText>(rec_name)
                    x.setText("test")

                    Log.i("FragmentEdit", "Success response value passed")
                }
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Log.i("FragmentEdit", "Fail response :t.toString()")
                }
            }
        )
        return ret
    }
}