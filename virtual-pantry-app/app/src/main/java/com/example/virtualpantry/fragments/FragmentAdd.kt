package com.example.virtualpantry.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
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
import com.example.virtualpantry.database.ApiService
import com.example.virtualpantry.database.DataApi
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
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class FragmentAdd : Fragment(R.layout.fragment_add) {
    private lateinit var addsqliteHelper: SQLiteHelper
    lateinit var isopen_checkbox: CheckBox
    lateinit var infridge_checkbox: CheckBox
    lateinit var image_view: ImageView
    lateinit var attach_photo: ImageButton

    lateinit var spinner: Spinner

    private val cameraRequestId  = 1000
    private val galeryRequestId = 1001
    private var imageUri: Uri? = null

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
        attach_photo = v.findViewById<ImageButton>(R.id.add_product_attach_photo_button)

        image_view?.setOnClickListener() {
            Toast.makeText(requireContext(), "img clicked", Toast.LENGTH_SHORT).show()
            dispatchTakePictureIntent()
        }
        was_photo_taken = 0

        attach_photo.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, galeryRequestId)
        }
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

    fun getMediaAbsolutePath(ctx: Context, uri: Uri?): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = ctx.getContentResolver().query(uri!!, filePathColumn, null, null, null)
        if (cursor == null || cursor.getCount() === 0) return null
        cursor.moveToFirst()
        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
        val picturePath: String = cursor.getString(columnIndex)
        cursor.close()
        return picturePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == cameraRequestId && resultCode == Activity.RESULT_OK) {
            setPic(currentPhotoPath, true)
            was_photo_taken = 1
            sendImgUsingPostReq(currentPhotoPath, "camera_name", true)
        }
        else if(requestCode == galeryRequestId  && resultCode == Activity.RESULT_OK)
        {
            imageUri = data?.data
            //Log.i(TAG, imageUri.toString())

            val selected_img_path = getMediaAbsolutePath(requireContext(), imageUri)
            Log.i(TAG, "${selected_img_path}")

            if (selected_img_path != null) {
                sendImgUsingPostReq(selected_img_path, "gallery_img", false)
                setPic(selected_img_path, false)
            }
        }
    }



    /*****************************************************************************************/

    fun sendImgUsingPostReq(imgPath: String, name: String, rotate_img: Boolean)
    {
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            inJustDecodeBounds = false
            inSampleSize = 1
            inPurgeable = true
        }

        Log.i(TAG, "sendImgUsingPostReq: ${imgPath}")
        var bitmap: Bitmap = BitmapFactory.decodeFile(imgPath, bmOptions)
        Log.i(TAG, "width = ${bitmap.width} / height = ${bitmap.height}")

        val byteArrayOutputStream = ByteArrayOutputStream()
        var final_bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)

        bitmap = final_bitmap
        Log.i(TAG, "width = ${bitmap.width} / height = ${bitmap.height}")

        if(rotate_img) {
            val matrix = Matrix()
            matrix.postRotate(90F)
            var final_rotatedBitmap: Bitmap = Bitmap.createBitmap(
                final_bitmap,
                0,
                0,
                final_bitmap.getWidth(),
                final_bitmap.getHeight(),
                matrix,
                true
            )
            bitmap = final_rotatedBitmap
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        val imageString: String = Base64.encodeToString(imageBytes, Base64.URL_SAFE)

        var response = DataApi.buildService(ApiService::class.java)
        val obj = RequestModel(name, imageString)
        response.sendReq(obj).enqueue(
            object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    Log.i("FragmentEdit", "Success response : ${response.message()} / ${response.body()}")
                    product_name.setText(response.body()?.message )
                    Log.i("FragmentEdit", "Success response value passed")
                }
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Log.i("FragmentEdit", "Fail response :t.toString()")
                }
            }
        )
    }

    fun return_date(year: Int,month: Int, day: Int): String{
        var endDate: String = ""
        endDate += "${year}-"
        if(month < 10) endDate += "0${month}-"
        else endDate += "${month}-"
        if(day < 10) endDate += "0${day}-"
        else endDate += "${day}"

        return endDate
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
        var unit = spinner.selectedItem.toString()



        var days_end_after_open: Int = add_product_open_end_date.text.toString().toInt()

        var date_of_product_open: String = "2000-01-01"
        var today = LocalDateTime.now()
        var end_date = today.plusDays(add_product_end_date.text.toString().toLong())
        Log.i(TAG, "End date = $today + ${add_product_end_date.text.toString().toLong()} = $end_date")

        var isProductOpen: String
        if(isopen_checkbox.isChecked()) {
            isProductOpen = "OPEN"
            date_of_product_open = return_date(today.year,today.monthValue,today.dayOfMonth)
        }
        else isProductOpen = "CLOSED"

        var isInFridge: String
        if(infridge_checkbox.isChecked()) isInFridge = "YES"
        else isInFridge = "NO"

        var product_image: String = currentPhotoPath
        //todo cut path from: /storage/emulated/0/Android/data/com.malkinfo.opencamera/files/Pictures/JPEG_20221002_193423_2686587610948885184.jpg to JPEG_20221002_193423_2686587610948885184.jpg

        var endDate: String = return_date(end_date.year,end_date.monthValue,end_date.dayOfMonth)

        val std = PantryItem(id = NULL, name = name, img = product_image, quantity = quantity, unit = unit, dateEnd = endDate,
            daysEndAfterOpen = days_end_after_open, date_of_product_open = date_of_product_open, dateOfAdd = return_date(today.year,today.monthValue,today.dayOfMonth), productOpen = isProductOpen,
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
        add_product_end_date.setText("0")
        add_product_open_end_date.setText("0")
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

    private fun setPic(path: String, rotate_img: Boolean) {
        Log.i(TAG, "setPic: Enter")

        val bmOptions = BitmapFactory.Options().apply {
            Log.i(TAG, "setPic: bmOptions")
            inJustDecodeBounds = true
            //val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))
            inJustDecodeBounds = false
            inSampleSize = 1
            inPurgeable = true
        }
        Log.i(TAG, "setPic: ${path}")

        var bitmap: Bitmap = BitmapFactory.decodeFile(path, bmOptions)
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

        if(rotate_img) {
            val matrix = Matrix()
            Log.i(TAG, "setPic: bitmap.getWidth() = ${final_bitmap.getWidth()} / bitmap.getHeight() = ${final_bitmap.getHeight()}")
            matrix.postRotate(90F)
            var final_rotatedBitmap: Bitmap = Bitmap.createBitmap(final_bitmap, 0, 0, final_bitmap.getWidth(), final_bitmap.getHeight(), matrix, true)
            final_bitmap = final_rotatedBitmap
        }

        take_photo.setImageBitmap(final_bitmap)
    }
}
