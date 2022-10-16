package com.example.virtualpantry.dataclass

data class PantryItem(val id: Int, var name: String, var img: String, var quantity: Float,
                      var unit:String, var dateEnd: String, var daysEndAfterOpen: Int, var date_of_product_open: String, var dateOfAdd: String,
                      var productOpen: String, var productTag: String, var inFridge: String, var stateAtAdd: String,
                      var stateNow: String){
}



