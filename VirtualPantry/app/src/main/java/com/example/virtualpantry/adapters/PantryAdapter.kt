package com.example.virtualpantry.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualpantry.R
import com.example.virtualpantry.dataclass.PantryItem

class PantryAdapter(private val itemList: ArrayList<PantryItem>) : RecyclerView.Adapter<PantryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView
        val tvName: TextView
        val tvQuantity: TextView
        val ivExpImage: ImageView
        val tvExpDay: TextView

        init {
            // Define click listener for the ViewHolder's View.
            ivImage = view.findViewById(R.id.ivPFIItemPreview)
            tvName = view.findViewById(R.id.tvPFIPantryItemName)
            tvQuantity = view.findViewById(R.id.tvPFIPantryItemQuantity)
            ivExpImage = view.findViewById(R.id.ivPFIPantryItemDayImage)
            tvExpDay = view.findViewById(R.id.tvPFIPantryItemDayText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cv_pantry_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivImage.setImageResource(itemList[position].image)
        holder.tvName.text = itemList[position].name
        holder.tvQuantity.text = itemList[position].quantity.toString() + " sztuk"
        holder.ivExpImage.setImageResource((itemList[position].expImage))
        holder.tvExpDay.text = itemList[position].expDays.toString() + " Dni"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}