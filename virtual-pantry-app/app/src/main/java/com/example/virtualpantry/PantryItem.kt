package com.example.virtualpantry

import android.media.Image
import java.sql.Date

data class PantryItem(val itemName: String, val itemQuantity: Int, val itemPhoto: Image, val itemRegDate: Date)