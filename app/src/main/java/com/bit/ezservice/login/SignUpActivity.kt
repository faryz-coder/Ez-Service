package com.bit.ezservice.login

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bit.ezservice.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_signup_activity)

        val buttonSignUp : Button = findViewById(R.id.buttonSignUp)
        val signupLayout : ConstraintLayout = findViewById(R.id.signup_layout)

        buttonSignUp.setOnClickListener(this)
        signupLayout.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSignUp -> signUp(v)
            R.id.signup_layout -> closeKeyBoard(v)
        }
    }

    private fun signUp(v: View) {
        val username : TextView = findViewById(R.id.inputUsername)
        val email : TextView = findViewById(R.id.inputEmail)
        val phoneNumber : TextView = findViewById(R.id.inputPhoneNumber)
        val password : TextView = findViewById(R.id.inputPassword)

        if (!valid(username, email, phoneNumber, password) ) {
            return
        }

        // when valid then register
        addToDb(v, username, email, phoneNumber, password)



    }

    private fun addToDb(v: View, username: TextView, email: TextView, phoneNumber: TextView, password: TextView) {
        // Check if the email already exist
        var valid = true
        db.collection("Account")
                .get()
                .addOnSuccessListener {
                    for (result in it) {
                        val fEmail = result.getField<String>("Username").toString()
                        if (fEmail == email.text.toString()) {
                            valid = false
                            email.error = "Email already Used"
                            Snackbar.make(v, "Email already Used", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    // Add to db if the email is not used
                    if (valid) {
                        val data = hashMapOf(
                                "Username" to username.text.toString(),
                                "Email" to email.text.toString(),
                                "Phone Number" to phoneNumber.text.toString(),
                                "Password" to password.text.toString()
                        )
                        db.collection("Account")
                                .add(data)
                                .addOnSuccessListener {
                                    Snackbar.make(v, "Register Complete!", Snackbar.LENGTH_SHORT).show()
                                }
                    }
                }
    }

    private fun valid(username: TextView, email: TextView, phoneNumber: TextView, password: TextView): Boolean {
        var valid = true
        if (username.text.isNullOrEmpty()) {
            valid = false
            username.error = "Enter Username"
        } else {
            username.error = null
        }

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

        if (phoneNumber.text.isNullOrEmpty()) {
            valid = false
            phoneNumber.error = "Enter Phone No"
        } else {
            phoneNumber.error = null
        }

        return valid
    }

    private fun closeKeyBoard(v : View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}