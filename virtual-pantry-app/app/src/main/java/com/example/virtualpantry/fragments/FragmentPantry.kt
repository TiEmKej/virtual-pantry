package com.example.virtualpantry.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualpantry.adapters.PantryAdapter
import com.example.virtualpantry.database.SQLiteHelper
import com.example.virtualpantry.dataclass.PantryItem
import com.example.virtualpantry.dataclass.RemovedProductAnaliseData


class FragmentPantry : Fragment(com.example.virtualpantry.R.layout.fragment_pantry) {
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: PantryAdapter? = null
    lateinit var v: View
    val TAG: String = "FragmentPantry"
    var dataAnalyseDialogsEnd: Int = 0
    lateinit var data: RemovedProductAnaliseData


    /****************************************************************************************************************/
    fun getProducts(){
        val stdList = sqliteHelper.getALLProducts()
        adapter?.addItems(stdList)
    }

    fun deleteProduct(id:Int)
    {
        //todo ocena stanu produktu, powód usunięcia produktu
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Czy na pewno chcesz usunąć ten produkt?")
        builder.setCancelable(true)
        builder.setPositiveButton("Tak") { dialog, _ ->
            sqliteHelper.deleteProductById(id)
            getProducts()
            dialog.dismiss()
        }
        builder.setNegativeButton("Nie"){dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun ChangeProductToOpen(std: PantryItem)
    {
        adapter?.something_change()
        std.productOpen = "OPEN"
        std.date_of_product_open = "2022-10-10" //TODO today date
        sqliteHelper.updateProduct(std)
    }
    /****************************************************************************************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        v = inflater.inflate(com.example.virtualpantry.R.layout.fragment_pantry, null)
        recyclerView = v.findViewById<RecyclerView>(com.example.virtualpantry.R.id.rvPantryFragment)

        adapter = PantryAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        getProducts()

        adapter?.setOnClickDeleteItem {
            deleteProduct(it.id)
        }

        adapter?.setOnClickPackageOpen {
            ChangeProductToOpen(it)
        }
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sqliteHelper = SQLiteHelper(context)
    }
}



