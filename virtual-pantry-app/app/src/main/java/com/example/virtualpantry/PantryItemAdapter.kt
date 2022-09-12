package com.example.virtualpantry

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_pantry.view.*

class PantryItemAdapter(private val pantryItems: MutableList<PantryItem>) : RecyclerView.Adapter<PantryItemAdapter.PantryItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryItemViewHolder {
        return PantryItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pantry, parent, false))
    }

    override fun onBindViewHolder(holder: PantryItemViewHolder, position: Int) {
        val curPantryItem = pantryItems[position]
        holder.itemView.apply{
            tvPantryItemTitle.text = curPantryItem.itemName
            tvPantryItemQuantity.text = curPantryItem.itemQuantity.toString()
        }
    }

    override fun getItemCount(): Int {
        return pantryItems.size
    }

    class PantryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}