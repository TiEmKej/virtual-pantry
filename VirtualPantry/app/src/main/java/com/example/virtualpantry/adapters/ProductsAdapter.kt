package com.example.virtualpantry.adapters


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualpantry.dataclass.AvailableProductsItem


class ProductsAdapter() /*: RecyclerView.Adapter<ProductsAdapter.ViewHolder>()*/  {
    /*private var ProductsList: ArrayList<AvailableProductsItem> = ArrayList()
    private var onClickItem: ((AvailableProductsItem)->Unit)? = null
    lateinit var ImgIdList: Array<Int>
    var selected_position: Int = -1

    fun setImgIdArray(imgIdArray: MutableList<Int>)
    {
        ImgIdList = imgIdArray.toTypedArray()
        for(i in ImgIdList)
        {
            Log.i("ProductAdapter", i.toString())
        }
    }

    fun setOnClickItem(callback: (AvailableProductsItem) -> Unit){
        this.onClickItem = callback
    }

    fun addItems(items:ArrayList<AvailableProductsItem>){
        this.ProductsList = items
        notifyDataSetChanged()
    }

    fun setPosition(pos: Int){
        selected_position = pos
        Log.i("ProductsAdapter", "setPosition: " + pos.toString())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        LayoutInflater.from(parent.context).inflate(com.example.virtualpantry.R.layout.available_product_rv_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val std = ProductsList[position]
        holder.bindView(std, ImgIdList, selected_position, position)
        holder.itemView.setOnClickListener{onClickItem?.invoke(std)}

    }

    override fun getItemCount(): Int {
        return ProductsList.size
    }


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private var ivImage: ImageView
        private var tvName: TextView

        init {
            ivImage = view.findViewById(com.example.virtualpantry.R.id.available_product_img)
            tvName = view.findViewById(com.example.virtualpantry.R.id.available_product_name)
        }


        fun bindView(std: AvailableProductsItem, imgidlist: Array<Int>, selected_pos: Int, item_position: Int) {
            Log.i("ProductAdapter", "std.image = "+std.image.toString()+" / finaly = " + imgidlist[(std.image-1)].toString())
            ivImage.setImageResource(imgidlist[(std.image-1)])
            tvName.text = std.name

            Log.i("ProductsAdapter", "item_position: $item_position selected_position: $selected_pos")
            if((item_position+1) == selected_pos)
                view.setBackgroundColor(Color.parseColor("#64a832"))
            else
                view.setBackgroundColor(Color.parseColor("#3d4f30"))
        }

    }*/
}