package com.example.virtualpantry.dataclass

/*data class PantryItem(var id: Int, var image: Int, val name: String, var quantity: String, var expDays: String,
                      var expImage: Int){

}*/

data class PantryItem(val id: Int, var name: String, var img: String, var quantity: Float,
                      var unit:String, var dateEnd: String, var dateEndAfterOpen: String, var dateOfAdd: String,
                      var productOpen: String, var productTag: String, var inFridge: String, var stateAtAdd: String,
                      var stateNow: String){
}



