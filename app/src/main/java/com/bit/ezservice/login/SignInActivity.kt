package com.bit.ezservice.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bit.ezservice.MainActivity
import com.bit.ezservice.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_signin_activity)

        val buttonSignIn : Button = findViewById(R.id.buttonSignIn)
        val signInLayout : ConstraintLayout = findViewById(R.id.signin_layout)
        val buttonBack : Button = findViewById(R.id.button_back2)
        val signUpNow : TextView = findViewById(R.id.textSignUp)
        val progressBar : ProgressBar = findViewById(R.id.progressBarSignIn)

        buttonSignIn.setOnClickListener(this)
        signInLayout.setOnClickListener(this)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
        signUpNow.setOnClickListener {
            val intent = Intent (this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSignIn -> signIn(v, findViewById(R.id.progressBarSignIn))
            R.id.signin_layout -> closeKeyBoard(v)
        }
    }

    private fun signIn(v: View, pb: ProgressBar) {
        pb.isVisible = true
        val email : TextView = findViewById(R.id.inputEmail2)
        val password : TextView = findViewById(R.id.inputPassword2)

        if (!valid(email, password)) {
//            pb.isVisible = false
            return
        }

        checkUser(v, email, password, pb)

    }

    private fun checkUser(v: View, email: TextView, password: TextView, pb: ProgressBar) {

        db.collection("Account")
            .get()
            .addOnSuccessListener {
                for (result in it) {
                    val em = result.getField<String>("Email").toString()
                    val pass = result.getField<String>("Password").toString()
                    val username = result.getField<String>("Username").toString()
                    val dID = result.id // database id

                    if (email.text.toString() == em && password.text.toString() == pass) {
                        Snackbar.make(v, "Welcome $username", Snackbar.LENGTH_SHORT).show()
                        /* return@addOnSuccessListener */
//                        pb.isVisible = false
                        // INSERT HERE TO NAVIGATE TO MAIN ACTIVITY
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("username", username)
                        intent.putExtra("dId", dID)
                        startActivity(intent)
                        finish()
                    }
                }
                Snackbar.make(v, "Wrong Email or Password", Snackbar.LENGTH_SHORT).show()
            }

    }

    private fun valid(email: TextView, password: TextView): Boolean {
        var valid = true

        if (email.text.isNullOrEmpty()) {
            valid = false
            email.error = "Enter Email"
        } else {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
                email.error = null
            } else {
                email.error = "Email is not Valid"
                valid = false
            }
        }

        if (password.text.isNullOrEmpty()) {
            valid = false
            password.error = "Enter Password"
        } else {
            password.error = null
        }

        return valid
    }

    private fun closeKeyBoard(v : View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}