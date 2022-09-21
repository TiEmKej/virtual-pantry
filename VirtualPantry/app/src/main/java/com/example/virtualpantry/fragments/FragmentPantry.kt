package com.example.virtualpantry.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualpantry.R
import com.example.virtualpantry.adapters.PantryAdapter
import com.example.virtualpantry.dataclass.PantryItem

class FragmentPantry : Fragment(R.layout.fragment_pantry) {
    var pantryItemList = ArrayList<PantryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataInitList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pantry, container, false)
        val recycleView = view.findViewById<RecyclerView>(R.id.rvPantryFragment)
        recycleView.layoutManager = LinearLayoutManager(activity)
        recycleView.adapter = PantryAdapter(pantryItemList)

        return view
    }
    private fun dataInitList()
    {
        val itemImage = arrayOf(
            R.drawable.baguette,
            R.drawable.baguette,
            R.drawable.baguette,
            R.drawable.baguette,
            R.drawable.baguette,
            R.drawable.baguette,
            R.drawable.baguette,
            R.drawable.baguette,
            R.drawable.baguette,
            R.drawable.baguette
        )

        val itemName = arrayOf(
            "Produkt 1",
            "Produkt 2",
            "Produkt 3",
            "Produkt 4",
            "Produkt 5",
            "Produkt 6",
            "Produkt 7",
            "Produkt 8",
            "Produkt 9",
            "Produkt 10"
        )

        val itemExpImage = arrayOf(
            R.drawable.smile,
            R.drawable.smile,
            R.drawable.smile,
            R.drawable.smile,
            R.drawable.smile,
            R.drawable.smile,
            R.drawable.smile,
            R.drawable.smile,
            R.drawable.smile,
            R.drawable.smile
        )

        val itemQuantiy = arrayOf(
            10,
            1,
            2,
            5,
            2,
            7,
            3,
            1,
            6,
            9
        )

        val itemExpDay = arrayOf(
            4,
            6,
            2,
            9,
            1,
            5,
            6,
            3,
            1,
            6
        )

        for (i in itemImage.indices){
            val product = PantryItem(itemImage[i],itemName[i],itemQuantiy[i],itemExpDay[i],itemExpImage[i])
            pantryItemList.add(product)
        }
    }
}