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
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualpantry.R
import com.example.virtualpantry.dataclass.PantryItem
import kotlinx.android.synthetic.main.fragment_add.*
import java.text.SimpleDateFormat

class PantryAdapter() : RecyclerView.Adapter<PantryAdapter.ViewHolder>() {
    private var pantryList: ArrayList<PantryItem> = ArrayList()
    private var onClickItem: ((PantryItem)->Unit)? = null
    private var onClickDeleteItem: ((PantryItem)->Unit)? = null
    private var onClickPackageOpen: ((PantryItem)->Unit)? = null

    /******************************************************************************************************************/

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

        var quantity_btn_up: ImageButton
        var quantity_btn_down: ImageButton
        var remove_btn: ImageButton

        var open: CheckBox

        var TAG: String = "PantryAdapter"

        init {
            image = view.findViewById(R.id.ivPFIItemPreview)
            exp_image = view.findViewById(R.id.ivPFIPantryItemDayImage)

            name = view.findViewById(R.id.tvPFIPantryItemName)
            quantity = view.findViewById(R.id.tvPFIPantryItemQuantity)
            days = view.findViewById(R.id.tvPFIPantryItemDayText)

            quantity_btn_up = view.findViewById(R.id.quantity_up)
            quantity_btn_down = view.findViewById(R.id.quantity_down)
            remove_btn = view.findViewById(R.id.RemoveButton)

            open = view.findViewById(R.id.is_product_open)
        }

        fun getExpImage(end_date: String): Int
        {
            Log.i(TAG, "getExpImage: entered")
            val STATES = listOf(R.drawable.smile, R.drawable.neutral, R.drawable.sad)

            val today = "02/10/2022"
            val mDateFormat = SimpleDateFormat("dd/MM/yyyy")
            val Today = mDateFormat.parse(today)
            Log.i(TAG, "getExpImage: end_date = $end_date")

            val End_date = mDateFormat.parse(end_date)
            val mDifference = kotlin.math.abs(End_date.time - Today.time)
            val days = mDifference / (24 * 60 * 60 * 1000)

            if(days > 3) return STATES[0]
             else if(days in (1..3)) return STATES[1]
                 else if(days < 1) return STATES[2]
                    else return R.drawable.noimageavailable
        }

        fun setPic(path: String, width:Int, height:Int):Bitmap{
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

            var bitmap: Bitmap = BitmapFactory.decodeFile(path, bmOptions)
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

            var final_bitmap = Bitmap.createScaledBitmap(dstBmp, width, height, true)
            val matrix = Matrix()
            matrix.postRotate(90F)
            var final_rotatedBitmap: Bitmap = Bitmap.createBitmap(final_bitmap, 0, 0, final_bitmap.getWidth(), final_bitmap.getHeight(), matrix, true)
            return final_rotatedBitmap
        }

        fun bindView(std: PantryItem) {
            Log.i(TAG, "bindView: ${std}")
            image.setImageBitmap(setPic(std.img, 100, 100))

            if(std.productOpen == "OPEN")
            {
                exp_image.setImageResource(getExpImage(std.dateEnd))
                days.text = std.dateEnd //todo różnica dni
                open.isChecked = true
                open.isEnabled = false
            }
            else if(std.productOpen == "CLOSED")
            {
                exp_image.setImageResource(getExpImage(std.dateEndAfterOpen))
                days.text = std.dateEndAfterOpen //todo różnica dni
                open.isChecked = false
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