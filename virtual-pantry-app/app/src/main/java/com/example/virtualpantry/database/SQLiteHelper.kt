package com.example.virtualpantry.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.virtualpantry.dataclass.PantryItem

class SQLiteHelper(context:Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "product.db"
        private const val TBL_PRODUCTS = "tbl_products"

        private const val ID = "id"
        private const val NAME = "name"
        private const val IMG = "IMG"
        private const val QUANTITY = "quantity"
        private const val UNIT = "unit"
        private const val DATE_END = "date_end"
        private const val DAYS_END_AFTER_OPEN = "date_end_after_open"
        private const val DATE_OF_PRODUCT_OPEN = "date_of_product_open"
        private const val DATE_OF_ADD = "date_of_add"
        private const val PRODUCT_OPEN = "is_open"
        private const val PRODUCT_TAG = "tag"
        private const val IN_FRIDGE = "in_fridge"
        private const val STATE_AT_ADD = "state_at_add" //good middle bad
        private const val STATE_NOW = "state_now"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblStudent = ("CREATE TABLE " + TBL_PRODUCTS + "(" +
                ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT, " +
                IMG + " TEXT, " +
                QUANTITY + " FLOAT, " +
                UNIT + " TEXT, " +
                DATE_END + " TEXT, " +
                DAYS_END_AFTER_OPEN + " TEXT, " +
                DATE_OF_PRODUCT_OPEN + " TEXT, " +
                DATE_OF_ADD + " TEXT, " +
                PRODUCT_OPEN + " TEXT, " +
                PRODUCT_TAG + " Text," +
                IN_FRIDGE + " Text," +
                STATE_AT_ADD + " Text," +
                STATE_NOW + " Text" +
                ")")
        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE TBL_STUDEN$TBL_PRODUCTS")
        onCreate(db)
    }

    fun insertProduct(std:PantryItem): Long{
        var db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, std.name)
        contentValues.put(IMG, std.img)
        contentValues.put(QUANTITY, std.quantity)

        contentValues.put(UNIT, std.unit)
        contentValues.put(DATE_END, std.dateEnd)
        contentValues.put(DAYS_END_AFTER_OPEN, std.daysEndAfterOpen)
        contentValues.put(DATE_OF_PRODUCT_OPEN, std.date_of_product_open)
        contentValues.put(DATE_OF_ADD, std.dateOfAdd)

        contentValues.put(PRODUCT_OPEN, std.productOpen)
        contentValues.put(PRODUCT_TAG, std.productTag)
        contentValues.put(IN_FRIDGE, std.inFridge)
        contentValues.put(STATE_AT_ADD, std.stateAtAdd)

        contentValues.put(STATE_NOW, std.stateNow)

        val success = db.insert(TBL_PRODUCTS, null, contentValues)
        db.close()

        return success
    }

    @SuppressLint("Range")
    fun getALLProducts(): ArrayList<PantryItem>{
        val stdList: ArrayList<PantryItem> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_PRODUCTS"
        val db = this.readableDatabase

        val cursor:  Cursor?
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch(e:Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var img: String
        var quantity: Float
        var unit: String
        var date_end: String
        var days_end_after_open: Int
        var date_of_product_open: String
        var date_of_add: String
        var product_open: String
        var product_tag: String
        var in_fridge: String
        var state_at_add: String
        var state_now: String

        if(cursor.moveToFirst())
        {
            do{
                id = cursor.getInt(cursor.getColumnIndex(ID))
                name = cursor.getString(cursor.getColumnIndex(NAME))
                img = cursor.getString(cursor.getColumnIndex(IMG))
                quantity = cursor.getFloat(cursor.getColumnIndex(QUANTITY))
                unit = cursor.getString(cursor.getColumnIndex(UNIT))
                date_end = cursor.getString(cursor.getColumnIndex(DATE_END))
                days_end_after_open = cursor.getInt(cursor.getColumnIndex(DAYS_END_AFTER_OPEN))
                date_of_product_open = cursor.getString(cursor.getColumnIndex(DATE_OF_PRODUCT_OPEN))
                date_of_add = cursor.getString(cursor.getColumnIndex(DATE_OF_ADD))
                product_open = cursor.getString(cursor.getColumnIndex(PRODUCT_OPEN))
                product_tag = cursor.getString(cursor.getColumnIndex(PRODUCT_TAG))
                in_fridge = cursor.getString(cursor.getColumnIndex(IN_FRIDGE))
                state_at_add = cursor.getString(cursor.getColumnIndex(STATE_AT_ADD))
                state_now = cursor.getString(cursor.getColumnIndex(STATE_NOW))

                val std = PantryItem(id = id, name = name, img = img, quantity = quantity, unit = unit, dateEnd = date_end,
                    daysEndAfterOpen = days_end_after_open, date_of_product_open = date_of_product_open, dateOfAdd = date_of_add, productOpen = product_open,
                productTag = product_tag, inFridge = in_fridge, stateAtAdd = state_at_add, stateNow = state_now)
                stdList.add(std)
            }while(cursor.moveToNext())
        }
        return stdList
    }

    fun updateProduct(std: PantryItem): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NAME, std.name)
        contentValues.put(IMG, std.img)
        contentValues.put(QUANTITY, std.quantity)

        contentValues.put(UNIT, std.unit)
        contentValues.put(DATE_END, std.dateEnd)
        contentValues.put(DAYS_END_AFTER_OPEN, std.daysEndAfterOpen)
        contentValues.put(DATE_OF_PRODUCT_OPEN, std.date_of_product_open)
        contentValues.put(DATE_OF_ADD, std.dateOfAdd)

        contentValues.put(PRODUCT_OPEN, std.productOpen)
        contentValues.put(PRODUCT_TAG, std.productTag)
        contentValues.put(IN_FRIDGE, std.inFridge)
        contentValues.put(STATE_AT_ADD, std.stateAtAdd)

        contentValues.put(STATE_NOW, std.stateNow)


        val success = db.update(TBL_PRODUCTS, contentValues, "id=" + std.id, null)
        db.close()
        return success
    }

    fun deleteProductById(id:Int): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = db.delete(TBL_PRODUCTS, "id=$id", null)
        db.close()
        return success
    }
}