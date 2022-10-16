package com.example.virtualpantry.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualpantry.R
import com.example.virtualpantry.database.SQLiteHelper
import com.example.virtualpantry.dataclass.NotifityData
import com.example.virtualpantry.dataclass.PantryItem
import kotlinx.android.synthetic.main.fragment_add.*
import java.lang.Math.abs
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PantryAdapter() : RecyclerView.Adapter<PantryAdapter.ViewHolder>() {
    private var pantryList: ArrayList<PantryItem> = ArrayList()
    private var onClickItem: ((PantryItem)->Unit)? = null
    private var onClickDeleteItem: ((PantryItem)->Unit)? = null
    private var onClickPackageOpen: ((PantryItem)->Unit)? = null
    private var TAG = "PantryAdapter"
    lateinit var sqlhelper: SQLiteHelper

    /******************************************************************************************************************/

    fun return_date(year: Int,month: Int, day: Int): String{
        var endDate: String = ""
        endDate += "${year}-"
        if(month < 10) endDate += "0${month}-"
        else endDate += "${month}-"
        if(day < 10) endDate += "0${day}-"
        else endDate += "${day}"

        return endDate
    }

    fun open_product_get_days(days_after_open: Int, end_date_after_open: String, end_date: String): Int
    {
        //oblicz datę koca przydatności po otwarciu opakowania (data_otwarcia+ważność_po_otwarciu)
        //jeżeli data jest większa niż data_przydatności zamkniętego opakowania, ustaw datę zamkniętego opakowania

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        var End_date_after_open = end_date_after_open
        End_date_after_open += " 00:00"
        var open_end_date = LocalDateTime.parse(End_date_after_open, formatter)

        var end_date_after_open = open_end_date.plusDays(days_after_open.toLong())
        Log.i(TAG, "open_product_set_image: end_date_after_open = $end_date_after_open")

        var LDTtoday = LocalDateTime.now()
        var strtoday:String = return_date(LDTtoday.year, LDTtoday.monthValue, LDTtoday.dayOfMonth)
        strtoday += " 00:00"
        var today = LocalDateTime.parse(strtoday, formatter)

        var End_date= end_date
        End_date += " 00:00"
        var end_date = LocalDateTime.parse(End_date, formatter)

        if(end_date_after_open.toEpochSecond(ZoneOffset.UTC) > end_date.toEpochSecond(ZoneOffset.UTC)){
            end_date_after_open = today
        }

        var mDifference: Long = 0
        mDifference = end_date_after_open.toEpochSecond(ZoneOffset.UTC) - today.toEpochSecond(ZoneOffset.UTC)
        var ddays = mDifference / (24 * 60 * 60)
        Log.i(TAG, "open_product_set_image: end_date_after_open = ${end_date_after_open.toEpochSecond(ZoneOffset.UTC)} / today = ${today.toEpochSecond(ZoneOffset.UTC)} - difference: $ddays")

        return ddays.toInt()
    }

    fun closed_product_get_days(end_date: String): Int
    {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        var LDTtoday = LocalDateTime.now()
        var strtoday:String = return_date(LDTtoday.year, LDTtoday.monthValue, LDTtoday.dayOfMonth)
        strtoday += " 00:00"
        var today = LocalDateTime.parse(strtoday, formatter)

        var strend = "$end_date 00:00"
        var end = LocalDateTime.parse(strend, formatter)

        var mDifference: Long = 0
        mDifference = end.toEpochSecond(ZoneOffset.UTC) - today.toEpochSecond(ZoneOffset.UTC)
        var ddays = mDifference / (24 * 60 * 60)


        return ddays.toInt()
    }


    fun check_product_validity(context: Context): ArrayList<NotifityData>
    {
        var stdlist: ArrayList<PantryItem> = ArrayList()
        sqlhelper = SQLiteHelper(context)
        stdlist = sqlhelper.getALLProducts()
        var notifData: ArrayList<NotifityData> = ArrayList()

        for(i in stdlist){
            var data = NotifityData(0, "", "", 0.0F,0)
            var days: Int

            if(i.productOpen == "OPEN")
            {
                var x = open_product_get_days(i.daysEndAfterOpen, i.date_of_product_open, i.dateEnd)

                data = NotifityData(i.id, i.name, i.img, i.quantity, x)
            }
            else if(i.productOpen == "CLOSED")
            {
                var x = closed_product_get_days(i.dateEnd)

                data = NotifityData(i.id, i.name, i.img, i.quantity, x)
            }
            else Log.i(TAG, "check_product_validity: error productOpen value: ${i.productOpen}")
            notifData.add(data)
        }
        return notifData
    }

    fun addItems(items:ArrayList<PantryItem>){
        this.pantryList = items
        notifyDataSetChanged()
    }

    fun something_change()
    {
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (PantryItem) -> Unit){
        this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback: (PantryItem) -> Unit){
        this.onClickDeleteItem = callback
    }

    fun setOnClickPackageOpen(callback: (PantryItem) -> Unit){
        this.onClickPackageOpen = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.cv_pantry_fragment_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val std = pantryList[position]
        holder.bindView(std)
        holder.remove_btn.setOnClickListener{onClickDeleteItem?.invoke(std)}
        holder.open.setOnClickListener{onClickPackageOpen?.invoke(std)}
    }

    override fun getItemCount(): Int {
        return pantryList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView
        var exp_image: ImageView

        var name: TextView
        var quantity: TextView
        var days: TextView

        var remove_btn: ImageButton

        var open: CheckBox

        var TAG: String = "PantryAdapter"
        val STATES = listOf(R.drawable.smile, R.drawable.neutral, R.drawable.sad)

        fun return_date(year: Int,month: Int, day: Int): String{
            var endDate: String = ""
            endDate += "${year}-"
            if(month < 10) endDate += "0${month}-"
            else endDate += "${month}-"
            if(day < 10) endDate += "0${day}-"
            else endDate += "${day}"

            return endDate
        }

        init {
            image = view.findViewById(R.id.ivPFIItemPreview)
            exp_image = view.findViewById(R.id.ivPFIPantryItemDayImage)

            name = view.findViewById(R.id.tvPFIPantryItemName)
            quantity = view.findViewById(R.id.tvPFIPantryItemQuantity)
            days = view.findViewById(R.id.tvPFIPantryItemDayText)

            remove_btn = view.findViewById(R.id.RemoveButton)

            open = view.findViewById(R.id.is_product_open)
        }

        fun open_product_get_days(days_after_open: Int, end_date_after_open: String, end_date: String, setImage: Boolean): Int
        {
            //oblicz datę koca przydatności po otwarciu opakowania (data_otwarcia+ważność_po_otwarciu)
            //jeżeli data jest większa niż data_przydatności zamkniętego opakowania, ustaw datę zamkniętego opakowania

            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            var End_date_after_open = end_date_after_open
            End_date_after_open += " 00:00"
            var open_end_date = LocalDateTime.parse(End_date_after_open, formatter)

            var end_date_after_open = open_end_date.plusDays(days_after_open.toLong())
            Log.i(TAG, "open_product_set_image: end_date_after_open = $end_date_after_open")

            var LDTtoday = LocalDateTime.now()
            var strtoday:String = return_date(LDTtoday.year, LDTtoday.monthValue, LDTtoday.dayOfMonth)
            strtoday += " 00:00"
            var today = LocalDateTime.parse(strtoday, formatter)

            var End_date= end_date
            End_date += " 00:00"
            var end_date = LocalDateTime.parse(End_date, formatter)

            if(end_date_after_open.toEpochSecond(ZoneOffset.UTC) > end_date.toEpochSecond(ZoneOffset.UTC)){
                end_date_after_open = today
            }

            var mDifference: Long = 0
            mDifference = end_date_after_open.toEpochSecond(ZoneOffset.UTC) - today.toEpochSecond(ZoneOffset.UTC)
            var ddays = mDifference / (24 * 60 * 60)
            Log.i(TAG, "open_product_set_image: end_date_after_open = ${end_date_after_open.toEpochSecond(ZoneOffset.UTC)} / today = ${today.toEpochSecond(ZoneOffset.UTC)} - difference: $ddays")

            if(setImage) set_status_image_and_validity_text(ddays)

            return ddays.toInt()
        }

        fun closed_product_get_days(end_date: String, setImage: Boolean): Int
        {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            var LDTtoday = LocalDateTime.now()
            var strtoday:String = return_date(LDTtoday.year, LDTtoday.monthValue, LDTtoday.dayOfMonth)
            strtoday += " 00:00"
            var today = LocalDateTime.parse(strtoday, formatter)

            var strend = "$end_date 00:00"
            var end = LocalDateTime.parse(strend, formatter)

            var mDifference: Long = 0
            mDifference = end.toEpochSecond(ZoneOffset.UTC) - today.toEpochSecond(ZoneOffset.UTC)
            var ddays = mDifference / (24 * 60 * 60)

            if(setImage) set_status_image_and_validity_text(ddays)

            return ddays.toInt()
        }


        fun set_status_image_and_validity_text(ddays: Long)
        {
            var final_text: String

            if (ddays < 0) {
                final_text = abs(ddays).toString() + " "
                if(ddays == -1L) final_text += "dzień po"
                else final_text += "dni po"
            }
            else if(ddays == 0L) final_text = "dzisiaj"
            else{
                if(ddays == 1L) final_text = "ostatni dzień"
                else final_text = "jeszcze " + ddays.toString() + " dni"
            }
            days.text = final_text
            if(ddays > 3) exp_image.setImageResource(STATES[0])
            else if(ddays in (1..3)) exp_image.setImageResource(STATES[1])
            else if(ddays < 1) exp_image.setImageResource(STATES[2])
            else exp_image.setImageResource(R.drawable.noimageavailable)
        }

        fun setPic(path: String, width:Int, height:Int):Bitmap{
            val bmOptions = BitmapFactory.Options().apply {
                Log.i(TAG, "setPic: bmOptions")
                inJustDecodeBounds = true
                inJustDecodeBounds = false
                inSampleSize = 1
                inPurgeable = true
            }

            var bitmap: Bitmap = BitmapFactory.decodeFile(path, bmOptions)
            lateinit var dstBmp: Bitmap
            if (bitmap.getWidth() >= bitmap.getHeight()){
                dstBmp = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
                )
            }else{
                dstBmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
                )
            }

            var final_bitmap = Bitmap.createScaledBitmap(dstBmp, width, height, true)
            val matrix = Matrix()
            matrix.postRotate(90F)
            var final_rotatedBitmap: Bitmap = Bitmap.createBitmap(final_bitmap, 0, 0, final_bitmap.getWidth(), final_bitmap.getHeight(), matrix, true)
            return final_rotatedBitmap
        }



        fun bindView(std: PantryItem) {
            Log.i(TAG, "bindView: ${std}")
            image.setImageBitmap(setPic(std.img, 100, 100))


            if(std.productOpen == "CLOSED")
            {
                closed_product_get_days(std.dateEnd, true)
                open.isChecked = false
            }
            else if(std.productOpen == "OPEN")
            {
                open_product_get_days(std.daysEndAfterOpen, std.date_of_product_open, std.dateEnd, true)
                open.isChecked = true
                open.isEnabled = false
            }
            else
            {
                Log.i(TAG, "bindView: std.productOpen value is incorrect")
                return
            }

            name.text = std.name
            quantity.text = std.quantity.toString() + " " + std.unit
        }

    }
}