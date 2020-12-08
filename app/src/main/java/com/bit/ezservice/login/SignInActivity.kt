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

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_signin_activity)

        val buttonSignIn : Button = findViewById(R.id.buttonSignIn)
        val signInLayout : ConstraintLayout = findViewById(R.id.signin_layout)

        buttonSignIn.setOnClickListener(this)
        signInLayout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSignIn -> signIn(v)
            R.id.signin_layout -> closeKeyBoard(v)
        }
    }

    private fun signIn(v: View) {
        val email : TextView = findViewById(R.id.inputEmail2)
        val password : TextView = findViewById(R.id.inputPassword2)

        if (!valid(email, password)) {
            return
        } else {
            Snackbar.make(v, "Welcome ..", Snackbar.LENGTH_SHORT).show()
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