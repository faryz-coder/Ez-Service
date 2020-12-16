package com.bit.ezservice.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bit.ezservice.R

class SelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selection_main)

        val signup: Button = findViewById(R.id.buttonChooseSignUp)
        val login: Button = findViewById(R.id.buttonChooseLogin)
        val splash: ConstraintLayout = findViewById(R.id.splashScreen)

        signup.setOnClickListener {
            val intent = Intent (this, SignUpActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val intent = Intent (this, SignInActivity::class.java)
            startActivity(intent)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            splash.isVisible = false
            login.isVisible = true
            signup.isVisible = true
        }, 2000)
    }
}