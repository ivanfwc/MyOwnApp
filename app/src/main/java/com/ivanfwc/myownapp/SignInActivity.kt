package com.ivanfwc.myownapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignInActivity : BaseActivity(), View.OnClickListener {

    internal lateinit var field_email: TextView
    internal lateinit var field_password: TextView
    internal var email_sign_in_button: Button? = null
    internal var create_account_sign_in_button: Button? = null
    internal var reset_password_sign_in_button: Button? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signinwithemail)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        (findViewById<View>(R.id.toolbar_title) as TextView).text = "Sign In"
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        field_email = findViewById(R.id.field_email)
        field_password = findViewById(R.id.field_password)

        findViewById<View>(R.id.email_sign_in_button).setOnClickListener(this)
        findViewById<View>(R.id.create_account_sign_in_button).setOnClickListener(this)
        findViewById<View>(R.id.reset_password_sign_in_button).setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    /*@Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }*/

    private fun signIn(email: String, password: String) {

        if (!validateForm()) {
            return
        }

        showProgressDialog()

        // [START sign_in_with_email]
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser

                    val i = Intent(this@SignInActivity, MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@SignInActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                hideProgressDialog()
            }
        // [END sign_in_with_email]
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = field_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            field_email.error = "Required."
            valid = false
        } else {
            field_email.error = null
        }

        val password = field_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            field_password.error = "Required."
            valid = false
        } else {
            field_password.error = null
        }

        return valid
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.login_activity_closing_application))
            .setContentText(getString(R.string.login_activity_confirmation))
            .setCancelText(getString(R.string.button_no))
            .setConfirmText(getString(R.string.button_yes))
            .showCancelButton(true)
            .setCancelClickListener { sDialog ->
                // reuse previous dialog instance, keep widget user state, reset them if you need
                sDialog.cancel()
            }
            .setConfirmClickListener { sDialog ->
                val intent = Intent(this@SignInActivity, LoginActivity::class.java)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                sDialog.dismiss()
            }
            .show()
    }

    override fun onClick(view: View) {
        val i = view.id
        if (i == R.id.email_sign_in_button) {

            signIn(field_email.text.toString(), field_password.text.toString())

        } else if (i == R.id.create_account_sign_in_button) {

            val intent = Intent(applicationContext, CreateAccountActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()

        } else if (i == R.id.reset_password_sign_in_button) {

            val intent = Intent(applicationContext, ResetPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()

        }
    }
}
