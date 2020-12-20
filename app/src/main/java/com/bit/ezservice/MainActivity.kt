package com.bit.ezservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log.d
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.ui.*
import com.bit.ezservice.login.SelectionActivity
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
//    var username = "faryz"
//    var dId = "8F8ofMWH0ZpqwlsBVmLb"
    var username = ""
    var dId = ""
    private val db = Firebase.firestore

    private val CHANNEL_ID = "channel_id_ez_service"
    private val notificationId = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        centerTitle()

        username = intent.getStringExtra("username").toString()
        dId = intent.getStringExtra("dId").toString()

        Toast.makeText(applicationContext, "Welcome $username", Toast.LENGTH_LONG).show()

        createNotificationChannel()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val logout = findViewById<TextView>(R.id.logout)
        logout.setOnClickListener {
            // LOGOUT
            val intent = Intent(this, SelectionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fun checkNotify() {
            db.collection("Profile").document("Liked Ads").collection(dId)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (result in it) {
                            val likedName = result.getField<String>("Name").toString()
                            val notifyStatus= result.getField<Boolean>("Notify")

                            if (!notifyStatus!!) {
                                sendNotification(likedName)
                                val data = hashMapOf(
                                    "Notify" to true
                                )
                                db.collection("Profile").document("Liked Ads").collection(dId)
                                    .document(result.id)
                                    .set(data, SetOptions.merge())
                                    .addOnSuccessListener {
                                        d("bomoh", "alert:notify")
                                    }
                            }
                        }
                }
                }
        }


        val docRef = db.collection("Profile").document("Liked Ads").collection(dId)
        docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                checkNotify()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        findViewById<TextView>(R.id.nav_name).text = username
        db.collection("Account").document(dId)
            .get()
            .addOnSuccessListener {
                val user_img = it.getField<String>("Photo Link").toString()
                if (user_img != "null"){
                    Picasso.get().load(it.getField<String>("Photo Link").toString()).into(findViewById<ImageView>(R.id.nav_image))
                    d("bomoh", "userImg: not null")
                } else {
                    findViewById<ImageView>(R.id.nav_image).setImageResource(R.mipmap.user_acc)
                }

            }

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(likedName: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Ez Service")
            .setContentText("$likedName just liked your ads")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    private fun centerTitle() {
        val textViews = ArrayList<View>()
        window.decorView.findViewsWithText(textViews, title, View.FIND_VIEWS_WITH_TEXT)
        if (textViews.size > 0) {
            var appCompatTextView: AppCompatTextView? = null
            if (textViews.size == 1)
                appCompatTextView = textViews[0] as AppCompatTextView
            else {
                for (v in textViews) {
                    if (v.parent is Toolbar) {
                        appCompatTextView = v as AppCompatTextView
                        break
                    }
                }
            }
            if (appCompatTextView != null) {
                val params = appCompatTextView.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                appCompatTextView.layoutParams = params
                appCompatTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                appCompatTextView.setTextColor(Color.BLACK)
            }
        }
    }

}