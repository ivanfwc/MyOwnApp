package com.ivanfwc.myownapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : BaseActivity(), View.OnClickListener {

    internal lateinit var field_email_forgot_password: TextView
    internal var reset_password_button: Button? = null
    internal var cancel_reset_password: Button? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        (findViewById<View>(R.id.toolbar_title) as TextView).text = "Reset Password"
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        field_email_forgot_password = findViewById(R.id.field_email_forgot_password)

        findViewById<View>(R.id.reset_password_button).setOnClickListener(this)
        findViewById<View>(R.id.cancel_reset_password).setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth!!.currentUser != null) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private fun resetPassword() {

        showProgressDialog()

        val email = field_email_forgot_password.text.toString().trim { it <= ' ' }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Enter your email!", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth!!.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Check email to reset your password!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(applicationContext, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Fail to send reset password email!", Toast.LENGTH_SHORT)
                        .show()
                }

                hideProgressDialog()
            }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        val intent = Intent(applicationContext, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onClick(view: View) {
        val i = view.id
        if (i == R.id.reset_password_button) {

            resetPassword()

        } else if (i == R.id.cancel_reset_password) {

            val intent = Intent(applicationContext, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()

        }
    }
}
