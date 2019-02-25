package com.ivanfwc.myownapp.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ivanfwc.myownapp.CheckConnectivity.getConnectivityStatusString
import com.ivanfwc.myownapp.LoginActivity
import com.ivanfwc.myownapp.R
import com.ivanfwc.myownapp.SetLogoutClearFCM
import org.json.JSONException
import org.json.JSONObject

import java.util.Objects

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var fragprofile_imageView: ImageView
    internal lateinit var profile_tvName: TextView
    internal lateinit var profile_tvEmail: TextView
    internal lateinit var bAbout: Button
    internal lateinit var bResetPin: Button
    internal lateinit var bSignOut: Button
    internal lateinit var mAuth: FirebaseAuth
    private var linearLayout: LinearLayout? = null
    private var internetConnected = true

    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = getConnectivityStatusString(context)
            setSnackbarMessage(status!!, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        mAuth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        linearLayout = findViewById(R.id.linearLayout)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        (findViewById<View>(R.id.toolbar_title) as TextView).text = "Profile"
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        fragprofile_imageView = findViewById(R.id.fragprofile_imageView)
        profile_tvName = findViewById(R.id.profile_tvName)
        profile_tvEmail = findViewById(R.id.profile_tvEmail)

        val user = mAuth.currentUser

        if (user != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .apply(RequestOptions.circleCropTransform()
                    .placeholder(R.mipmap.ic_launcher_round))
                .into(fragprofile_imageView)
        }

        //imageView.setImageBitmap(BitmapFactory.decodeStream(photoUrl));
        if (user != null) {
            profile_tvName.text = user.displayName
        }
        if (user != null) {
            profile_tvEmail.text = user.email
        }

        bResetPin = findViewById(R.id.bResetPin)
        bResetPin.setOnClickListener(this)

        bAbout = findViewById(R.id.bSettings)
        bAbout.setOnClickListener(this)

        bSignOut = findViewById(R.id.bSignOut)
        bSignOut.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {

        if (v.id == R.id.bSignOut) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SweetAlertDialog(Objects.requireNonNull(this), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.signout_app))
                    .setContentText(getString(R.string.main_activity_confirmation))
                    .setCancelText(getString(R.string.button_no))
                    .setConfirmText(getString(R.string.button_yes))
                    .showCancelButton(true)
                    .setCancelClickListener { sDialog -> sDialog.cancel() }
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismiss()
                        val user = mAuth.currentUser
                        userLogoutRequest(user!!.email)
                    }
                    .show()
            }
        }
    }

    private fun userLogoutRequest(userEmail: String?) {
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()

        val responseListener = Response.Listener<String> { response ->
            pDialog.dismiss()
            try {
                val jsonResponse = JSONObject(response)
                val success = jsonResponse.getBoolean("success")

                if (success) {

                    AuthUI.getInstance()
                        .signOut(applicationContext)
                        .addOnCompleteListener {
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }

                } else {

                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val logout = SetLogoutClearFCM(userEmail!!, responseListener)
        val queue = Volley.newRequestQueue(applicationContext)
        queue.add(logout)
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    public override fun onResume() {
        super.onResume()
        //Log.i(TAG, "onResume()");
        registerInternetCheckReceiver()
    }

    /**
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private fun registerInternetCheckReceiver() {
        val internetFilter = IntentFilter()
        internetFilter.addAction("android.net.wifi.STATE_CHANGE")
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(broadcastReceiver, internetFilter)
    }

    private fun setSnackbarMessage(status: String, showBar: Boolean) {
        var internetStatus = ""
        if (status.equals(getString(R.string.Wifi_enabled), ignoreCase = true) || status.equals(getString(R.string.Mobile_data_enabled), ignoreCase = true)) {
            internetStatus = getString(R.string.Internet_Connected)
        } else {
            internetStatus = getString(R.string.Lost_Internet_Connection)
        }

        val snackbar: Snackbar
        if (internetStatus.equals(getString(R.string.Lost_Internet_Connection), ignoreCase = true)) {
            if (internetConnected) {
                snackbar = Snackbar
                    .make(linearLayout!!, internetStatus, Snackbar.LENGTH_INDEFINITE)
                /*.setAction("X", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });*/
                snackbar.setActionTextColor(Color.WHITE)
                val sbView = snackbar.view
                val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
                textView.setTextColor(Color.WHITE)
                snackbar.show()
                internetConnected = false
            }
        } else {
            if (!internetConnected) {
                snackbar = Snackbar
                    .make(linearLayout!!, internetStatus, Snackbar.LENGTH_LONG)
                /*.setAction("X", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });*/
                snackbar.setActionTextColor(Color.WHITE)
                val sbView = snackbar.view
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.green_500))
                val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
                textView.setTextColor(Color.WHITE)
                internetConnected = true
                snackbar.show()
            }
        }
    }
}
