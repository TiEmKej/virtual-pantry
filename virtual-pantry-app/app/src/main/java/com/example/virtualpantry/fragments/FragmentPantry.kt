package com.example.virtualpantry.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.chaquo.python.PyObject
//import com.chaquo.python.Python
//import com.chaquo.python.android.AndroidPlatform
import com.example.virtualpantry.R
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

    fun showDialog1()
    {
        val listItems = arrayOf("Wykorzystałem produkt", "Produkt jest popsuty")
        var checkedItems: Int = 0
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Dlaczego usuwasz ten produkt?")
        builder.setIcon(R.drawable.ic_baseline_no_food_24)

        builder.setSingleChoiceItems(listItems, 0){ dialogInterface, i ->
            Log.i(TAG, "showDialog1: ${listItems[i]}")
            checkedItems = i
        }

        builder.setCancelable(false)
        builder.setPositiveButton("Zatwierdź") { dialog, which ->
            Log.i(TAG, "showDialog1 wybrano${listItems[checkedItems]}")
            data.dialog1_opt = checkedItems
            when(checkedItems){
                0->showDialog2() //produkt wykorzystany
                1->showDialog4() //produkt popsuty
            }
        }

        builder.create()
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showDialog2()
    {
        val listItems = arrayOf("bardzo dobry", "dobry", "zły", "nie zdatny do spożycia")
        var checkedItems: Int = 0
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Jak oceniasz końcowy stan usuwanego produktu?")
        builder.setIcon(R.drawable.ic_baseline_no_food_24)

        builder.setSingleChoiceItems(listItems, 0){ dialogInterface, i ->
            Log.i(TAG, "showDialog1: ${listItems[i]}")
            checkedItems = i
        }

        builder.setCancelable(false)
        builder.setPositiveButton("Zatwierdź") { dialog, which ->
            Log.i(TAG, "showDialog2 wybrano${listItems[checkedItems]}")
            data.dialog2_opt = checkedItems
            when(checkedItems){
                0, 1, 2->showDialog3()
                3->showDialog4()
            }
            showDialog3()
        }

        builder.create()
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showDialog3()
    {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Za ile dni według ciebie nie byłby zdatny do spożycia?")
        builder.setIcon(R.drawable.ic_baseline_no_food_24)

        val input = EditText(requireContext())
        input.setHint("Dni")
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setCancelable(false)
        builder.setPositiveButton("Zatwierdź") { dialog, which ->
            Log.i(TAG, "showDialog3 wybrano${input.text.toString()}")
            dataAnalyseDialogsEnd = 1
            data.dialog3_opt = input.text.toString().toInt()
        }

        builder.create()
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showDialog4()
    {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Od ilu dni produkt nie jest zdatny do spożycia?")
        builder.setIcon(R.drawable.ic_baseline_no_food_24)

        val input = EditText(requireContext())
        input.setHint("Dni")
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setCancelable(false)
        builder.setPositiveButton("Zatwierdź") { dialog, which ->
            data.dialog4_opt = input.text.toString().toInt()
            Log.i(TAG, "showDialog3 wybrano${input.text.toString()}")
            dataAnalyseDialogsEnd = 1

        }

        builder.create()
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun send_data_to_analyse(){
        //to use in api
        data.name = "test"
        data.product_tag = 1
    }

    fun deleteProduct(id:Int)
    {
        //todo ocena stanu produktu, powód usunięcia produktu
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Czy na pewno chcesz usunąć ten produkt?")
        builder.setCancelable(true)
        builder.setPositiveButton("Tak") { dialog, _ ->
            showDialog1()
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



