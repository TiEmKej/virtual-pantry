package com.example.virtualpantry

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.virtualpantry.adapters.PantryAdapter
import com.example.virtualpantry.dataclass.NotifityData
import com.example.virtualpantry.fragments.FragmentAdd
import com.example.virtualpantry.fragments.FragmentEdit
import com.example.virtualpantry.fragments.FragmentPantry
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Math.abs


class MainActivity : AppCompatActivity(){
    val TAG = "MainActivityDebug"
    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101
    var notificationNextId = 0
    var mainHandler: Handler? = null
    lateinit var pantryADP: PantryAdapter
    lateinit var sound: Uri
    var is_first_thread_run: Boolean = true
    var i = 0

    protected fun shouldAskPermissions(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    @TargetApi(23)
    protected fun askPermissions() {
        val permissions = arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        val requestCode = 200
        requestPermissions(permissions, requestCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (shouldAskPermissions()) {
            askPermissions();
        }

        createNotificationChannel()
        //one hour schedule task
        pantryADP = PantryAdapter()

        mainHandler = Handler(mainLooper)
        sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        Thread{
            while (true) {
                try {
                    if(is_first_thread_run) {
                        Thread.sleep(1 * 60 * 1000) //run every 1 hour
                        is_first_thread_run = false
                    }
                    else Thread.sleep(60 * 60 * 1000) //run every 1 hour

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                mainHandler!!.post{
                val x: ArrayList<NotifityData>
                x = pantryADP.check_product_validity(applicationContext)
                Log.i(TAG, "onCreate: $x")

                val bmOptions = BitmapFactory.Options().apply {
                    Log.i(TAG, "setPic: bmOptions")
                    inJustDecodeBounds = true
                    inJustDecodeBounds = false
                    inSampleSize = 1
                    inPurgeable = true
                }

                for (i in x){
                    if(i.days <= 2)
                    {
                        var bitmap: Bitmap = BitmapFactory.decodeFile(i.img_path, bmOptions)
                        if(i.days == 0) sendNotificationImage(i.name, "Produkt ${i.name} się dzisiaj przeterminuje", bitmap, i.id, true)
                        else if(i.days == 1) sendNotificationImage(i.name, "Produkt ${i.name} jutro się przeterminuje", bitmap, i.id, true)
                        else if(i.days == -1) sendNotificationImage(i.name, "Produkt ${i.name} jest przeterminowany od ${abs(i.days)} dnia", bitmap, i.id, true)
                        else if (i.days < -1) sendNotificationImage(i.name, "Produkt ${i.name} jest przeterminowany od ${abs(i.days)} dni", bitmap, i.id, true)
                    }
                }

//                    var y = FragmentAdd()
//                    val test_names = arrayOf<String>("agrest", "ananas", "arbuz", "aronia", "awokado", "bakłażan", "banan",
//                        "batat", "borówka", "brokuł", "brokuły", "brukiew", "brukselka", "brzoskwinia", "burak", "bułka tarta",
//                        "bób", "cebula", "chleb", "chrzan", "ciecierzyca", "cukier", "cukinia", "cykoria", "cytryna", "czarny bez",
//                        "czereśnia", "czosnek", "daktyl", "dereń", "drożdże", "dynia", "dzika róża", "fasola", "fasolka szparagowa",
//                        "figa", "granat", "grejpfrut", "groch", "groszek", "gruszka", "guawa", "jabłko", "jagoda", "jaja", "jarmuż", "jeżyna",
//                        "jogurt", "kaki", "kalafior", "kalarepa", "kapar", "kapusta", "karczoch", "kasze", "keczup", "kefir", "kiwi", "kokos",
//                        "koper", "kukurydza", "kumkwat", "liczi", "limonka", "majonez", "makarony", "malina", "mandarynka", "mango",
//                        "marchew", "marchewka", "masło", "melon", "miechunka", "miód", "morela", "mąka", "nektarynka", "ogórek",
//                        "oliwka", "opuncja", "orzech brazylijski", "orzech laskowy", "orzech piniowy", "orzech pistacjowy", "orzech włoski",
//                        "orzech ziemny", "papaja", "papryka", "pasternak", "patison", "pieczarki", "pieczywo", "pietruszka", "pigwa",
//                        "pomarańcza", "pomidor", "por", "porzeczka", "poziomka", "rabarbar", "renkloda", "rokitnik", "roszponka",
//                        "rukola", "ryż", "rzepa", "rzeżucha", "rzodkiew", "rzodkiewka", "sałata", "seler", "ser żółty", "soczewica",
//                        "soda", "soja", "szalotka", "szczaw", "szczypiorek", "szparag", "szpinak", "tarnina", "truskawka", "twaróg",
//                        "winogrono", "wiśnia", "ziemniak", "śliwka", "śmietana", "żurawina"
//                    )
//                    y.sendImgUsingPostReq("/storage/emulated/0/Android/data/com.example.virtualpantry/files/Pictures/Tests/$i.jpg", test_names[i], true)
//                    i += 1
//                    if(i==130) i = 0
                }
            }
        }.start()

//        Fragmenty
        val fragmentPantry = FragmentPantry()
        val fragmentAdd = FragmentAdd()
        val fragmentEdit = FragmentEdit()

//        Ustawienie pierwszego ekranu
        replaceCurrentFragment(fragmentPantry)

//        Pasek nawigacyjny
        val navigationBarView = findViewById<BottomNavigationView>(R.id.bnvMenuBar)

        navigationBarView.setOnItemSelectedListener { item ->
            var image: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.product1)
            //sendNotificationImage("Notification with img", "Data ważności tego produktu minie za 1 dzień", )
            when(item.itemId) {
                R.id.pantryMenu -> {
                    replaceCurrentFragment(fragmentPantry)
                    true
                }
                R.id.addMenu -> {
                    replaceCurrentFragment(fragmentAdd)
                    true
                }
                R.id.editMenu -> {
                    replaceCurrentFragment(fragmentEdit)
                    true
                }
                else -> {
                    false
                }
            }
        }

    }

    private fun replaceCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flContainer, fragment)
            commit()
        }

    private fun createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification_title = "Virtual Pantry Notification"
            val notification_context = "notification content"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, notification_title, importance).apply {
                description = notification_context
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, content: String){
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_for_test)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationNextId, builder.build())
            notificationNextId += 1
        }
    }

    private fun sendNotificationImage(title: String, content: String, image: Bitmap, id: Int, rotate_img: Boolean) {
        //val vibrations_1: LongArray(0, 500, 110, 500, 110, 450, 110, 200, 110, 170, 40, 450, 110, 200, 110, 170, 40, 500)
        var image_bitmap = image
        if(rotate_img) {
            val matrix = Matrix()
            matrix.postRotate(90F)
            var final_rotatedBitmap: Bitmap = Bitmap.createBitmap(
                image_bitmap,
                0,
                0,
                image.getWidth(),
                image.getHeight(),
                matrix,
                true
            )
            image_bitmap = final_rotatedBitmap
        }

        var notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_for_test)
            .setContentTitle(title)
            .setContentText(content)
            .setLargeIcon(image_bitmap)
            //.setSound(sound)

        with(NotificationManagerCompat.from(this)){
            notify(id, notification.build())

        }
    }
}