package com.ivanfwc.myownapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
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
import com.google.firebase.auth.UserProfileChangeRequest

class CreateAccountActivity : BaseActivity(), View.OnClickListener {

    internal lateinit var field_fullname: TextView
    internal lateinit var field_email: TextView
    internal lateinit var field_password: TextView
    internal var create_account_button: Button? = null
    internal var cancel_create_account_button: Button? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createaccount)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        (findViewById<View>(R.id.toolbar_title) as TextView).text = "New Account"
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        field_fullname = findViewById(R.id.field_fullname)
        field_email = findViewById(R.id.field_email)
        field_password = findViewById(R.id.field_password)

        findViewById<View>(R.id.create_account_button).setOnClickListener(this)
        findViewById<View>(R.id.cancel_create_account_button).setOnClickListener(this)

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

    private fun createAccount(email: String, password: String) {

        if (!validateForm()) {
            return
        }

        showProgressDialog()

        // [START create_user_with_email]
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    userProfile()
                    Toast.makeText(
                        this@CreateAccountActivity, "Account created successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(applicationContext, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    //userProfile();
                    Toast.makeText(
                        this@CreateAccountActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // [START_EXCLUDE]
                hideProgressDialog()
                // [END_EXCLUDE]
            }
        // [END create_user_with_email]
    }

    private fun userProfile() {
        val user = mAuth!!.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(field_fullname.text.toString().trim { it <= ' ' })
                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg")) // here you can set image link also.
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("TESTING", "User profile updated.")
                    }
                }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = field_fullname.text.toString()
        if (TextUtils.isEmpty(name)) {
            field_fullname.error = "Required."
            valid = false
        } else {
            field_fullname.error = null
        }

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
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
                sDialog.dismiss()
            }
            .show()
    }

    override fun onClick(view: View) {
        val i = view.id
        if (i == R.id.create_account_button) {

            createAccount(field_email.text.toString(), field_password.text.toString())

        } else if (i == R.id.cancel_create_account_button) {
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
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                    sDialog.dismiss()
                }
                .show()
        }

    }
}
