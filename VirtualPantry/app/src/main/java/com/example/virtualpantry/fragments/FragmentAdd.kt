package com.example.virtualpantry.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import androidx.fragment.app.Fragment
import com.example.virtualpantry.BuildConfig
import com.example.virtualpantry.R
import com.example.virtualpantry.adapters.ApiAdapter
import com.example.virtualpantry.database.ApiService
import com.example.virtualpantry.database.DataApi2
import com.example.virtualpantry.database.SQLiteHelper
import com.example.virtualpantry.dataclass.PantryItem
import com.example.virtualpantry.dataclass.RequestModel
import com.example.virtualpantry.dataclass.ResponseModel
import kotlinx.android.synthetic.main.fragment_add.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FragmentAdd : Fragment(R.layout.fragment_add) {
    private lateinit var addsqliteHelper: SQLiteHelper
    lateinit var isopen_checkbox: CheckBox
    lateinit var infridge_checkbox: CheckBox
    lateinit var image_view: ImageView

    lateinit var spinner: Spinner

    private val cameraRequestId  = 1222
    var currentPhotoPath: String = ""
    var was_photo_taken: Int = 0

    var TAG: String = "FragmentAdd_INFO"
    var NULL: Int = 0
    var NULL_STR: String = "NAN"

    /*****************************************************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        //addTestProduct(10)
        super.onCreate(savedInstanceState)
        was_photo_taken = 0

        Log.i(TAG, "Trying to init databases")
        addsqliteHelper = SQLiteHelper(context)

        Log.i(TAG, "Enter to onCreate FragmentAdd function ")

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), cameraRequestId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        
        var v =  inflater.inflate(R.layout.fragment_add, null)

        isopen_checkbox = v.findViewById<CheckBox>(R.id.add_product_is_open)
        infridge_checkbox = v.findViewById<CheckBox>(R.id.add_product_is_outside_fridge)
        image_view = v.findViewById<ImageView>(R.id.take_photo)

        image_view?.setOnClickListener() {
            Toast.makeText(requireContext(), "img clicked", Toast.LENGTH_SHORT).show()
            dispatchTakePictureIntent()
        }
        was_photo_taken = 0

        //apiImagSend = ApiAdapter(R.id.product_name, requireContext())
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated: entered")

        was_photo_taken = 0

        super.onViewCreated(view, savedInstanceState)
        add_product_add_button.setOnClickListener {view->
           addProduct()
       }

        image_view.setImageResource(R.drawable.ic_baseline_camera_alt_24)
        ArrayAdapter.createFromResource(requireContext() ,R.array.product_unit, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner = unit_spinner
            spinner.adapter = adapter
            spinner.onItemClickListener
        }

        val datePicker = datePicker1
        val today = Calendar.getInstance()

        //zabroń podanie daty wcześniejszej niż dzisiaj (użytkownik nie doda przeterminowanego produktu)
        //datePicker.setMinDate(System.currentTimeMillis() - 1000);

        var todYear = today.get(Calendar.YEAR)
        var todMonth = today.get(Calendar.MONTH)
        var todDay = today.get(Calendar.DAY_OF_MONTH)

        datePicker.init(todYear, todMonth,todDay) { view, year, month, day ->
            val month = month + 1
            //szybka konwersja z błędem (nie uwzględnia dni przestępnych)
            //todo dokładna konwersja dni
            var validateDays : Int = ((year-todYear)*366) + (((month-1) - todMonth)*31) + (day - todDay)
            add_product_end_date.setText(validateDays.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == cameraRequestId && resultCode == Activity.RESULT_OK) {
            setPic()
            was_photo_taken = 1
            //ret = apiImagSend.sendImgUsingPostReq(currentPhotoPath)
            sendImgUsingPostReq(currentPhotoPath)
        }
    }

    /*****************************************************************************************/

    fun sendImgUsingPostReq(imgPath: String)
    {
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
        var final_bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)

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
                    product_name.setText(response.body()?.message
                    )
                    Log.i("FragmentEdit", "Success response value passed")
                }
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Log.i("FragmentEdit", "Fail response :t.toString()")
                }
            }
        )
    }

    private fun addTestProduct(ile: Int)
    {
        var i: Int = 0

        while(i < ile)
        {
            val units = arrayOf("sztuk" + "kg" + "gram" + "litrów" + "mililitrów")

            var name = "Test Produkt$i"
            var quantity: Float = i.toFloat()
            var end_date = "$i/10/2022"
            var end_date_after_open = "${i-2}/10/2022"
            var unit = units[i%units.size]

            var today: String
            val aptoday = Calendar.getInstance()
            var todYear = aptoday.get(Calendar.YEAR)
            var todMonth = aptoday.get(Calendar.MONTH)
            var todDay = aptoday.get(Calendar.DAY_OF_MONTH)
            today = "$todDay/${todMonth+1}/$todYear"

            var isProductOpen: String
            if(i%2 == 1) isProductOpen = "OPEN"
            else isProductOpen = "CLOSED"

            var isInFridge: String
            if(i%2 == 0) isInFridge = "YES"
            else isInFridge = "NO"

            var product_image: String = "/storage/emulated/0/Android/data/com.malkinfo.opencamera/files/Pictures/JPEG_test.jpg"
            //todo cut path from: /storage/emulated/0/Android/data/com.malkinfo.opencamera/files/Pictures/JPEG_20221002_193423_2686587610948885184.jpg to JPEG_20221002_193423_2686587610948885184.jpg

            val std = PantryItem(id = NULL, name = name, img = product_image, quantity = quantity, unit = unit, dateEnd = "$end_date/01/2022",
                dateEndAfterOpen = "$end_date_after_open/01/2022", dateOfAdd = today, productOpen = isProductOpen,
                productTag = "product_tag", inFridge = isInFridge, stateAtAdd = "state_at_add", stateNow = NULL_STR)

            val status = addsqliteHelper.insertProduct(std)
            if(status > -1)
            {
                Toast.makeText(requireContext(), "Dodano produkt...", Toast.LENGTH_SHORT).show()
                clearEditText()
            }else{
                Toast.makeText(requireContext(), "Coś poszło nie tak :/", Toast.LENGTH_SHORT).show()
            }
            i++
        }
    }

    private fun addProduct()
    {
        Log.i(TAG, "addProduct: ${currentPhotoPath.isEmpty()} / $currentPhotoPath")
        if( was_photo_taken == 0 || product_name.text.isEmpty() || add_product_quanity.text.isEmpty() || add_product_end_date.text.isEmpty() || add_product_open_end_date.text.isEmpty()) {
            Toast.makeText(activity, "Please enter required fields", Toast.LENGTH_SHORT).show()
            return
        }

        var name = product_name.text.toString()
        var quantity: Float = add_product_quanity.text.toString().toFloat()

        var dt = Date()
        var c = Calendar.getInstance()
        c.time = dt
        c.add(Calendar.DATE, add_product_end_date.text.toString().toInt())
        dt = c.time
        var end_date = "04/01/2022"
        Log.i(TAG, "end date: $end_date")

        dt = Date()
        c = Calendar.getInstance()
        c.time = dt
        c.add(Calendar.DATE, add_product_open_end_date.text.toString().toInt())
        dt = c.time
        var end_date_after_open = "01/01/2022"
        Log.i(TAG, "end date: $end_date_after_open")

        var unit = spinner.selectedItem.toString()

        var today: String
        val aptoday = Calendar.getInstance()
        var todYear = aptoday.get(Calendar.YEAR)
        var todMonth = aptoday.get(Calendar.MONTH)
        var todDay = aptoday.get(Calendar.DAY_OF_MONTH)
        today = "$todDay/${todMonth+1}/$todYear"

        var isProductOpen: String
        if(isopen_checkbox.isChecked()) isProductOpen = "OPEN"
        else isProductOpen = "CLOSED"

        var isInFridge: String
        if(infridge_checkbox.isChecked()) isInFridge = "YES"
        else isInFridge = "NO"

        var product_image: String = currentPhotoPath
        //todo cut path from: /storage/emulated/0/Android/data/com.malkinfo.opencamera/files/Pictures/JPEG_20221002_193423_2686587610948885184.jpg to JPEG_20221002_193423_2686587610948885184.jpg

        val std = PantryItem(id = NULL, name = name, img = product_image, quantity = quantity, unit = unit, dateEnd = end_date,
            dateEndAfterOpen = end_date_after_open, dateOfAdd = today, productOpen = isProductOpen,
            productTag = "product_tag", inFridge = isInFridge, stateAtAdd = "state_at_add", stateNow = NULL_STR)

        val status = addsqliteHelper.insertProduct(std)
        if(status > -1)
        {
            Toast.makeText(requireContext(), "Dodano produkt...", Toast.LENGTH_SHORT).show()
            clearEditText()
        }else{
            Toast.makeText(requireContext(), "Coś poszło nie tak :/", Toast.LENGTH_SHORT).show()
        }
     }

    private fun clearEditText(){
        product_name.setText("")
        add_product_quanity.setText("")
        add_product_end_date.setText("")
        add_product_open_end_date.setText("")
        add_product_is_open.isChecked=false
        add_product_is_outside_fridge.isChecked=false
        image_view.setImageResource(R.drawable.ic_baseline_camera_alt_24)
        was_photo_taken = 0

        product_name.requestFocus()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getActivity()?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.i(TAG, "createImageFile: ${Environment.DIRECTORY_PICTURES}")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            val packageManager = requireActivity().packageManager
            takePictureIntent.resolveActivity( packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.i(TAG, "dispatchTakePictureIntent: ")
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, cameraRequestId)
                }
            }
        }
    }

    private fun setPic() {
        Log.i(TAG, "setPic: Enter")
        //val targetH: Int = take_photo.height
        //val targetW: Int = take_photo.width

        val bmOptions = BitmapFactory.Options().apply {
            Log.i(TAG, "setPic: bmOptions")
            inJustDecodeBounds = true
            //val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))
            inJustDecodeBounds = false
            inSampleSize = 1
            inPurgeable = true
        }
        Log.i(TAG, "setPic: ${currentPhotoPath}")

        var bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        Log.i(TAG, "width = ${bitmap.width} / height = ${bitmap.height}")

        lateinit var dstBmp: Bitmap
        Log.i(TAG, "bitmap.getWidth() = ${bitmap.getWidth()} / bitmap.getHeight() = ${bitmap.getHeight()}")
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
        var final_bitmap = Bitmap.createScaledBitmap(dstBmp, 500, 500, true) //for better resolution
        val matrix = Matrix()
        Log.i(TAG, "setPic: bitmap.getWidth() = ${final_bitmap.getWidth()} / bitmap.getHeight() = ${final_bitmap.getHeight()}")
        matrix.postRotate(90F)
        var final_rotatedBitmap: Bitmap = Bitmap.createBitmap(final_bitmap, 0, 0, final_bitmap.getWidth(), final_bitmap.getHeight(), matrix, true)
        take_photo.setImageBitmap(final_rotatedBitmap)
    }
}