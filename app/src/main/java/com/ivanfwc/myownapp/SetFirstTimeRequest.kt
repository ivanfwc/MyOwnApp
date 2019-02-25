package com.ivanfwc.myownapp

import android.os.Build
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.ivanfwc.myownapp.Config.MY_SERVER_URL

import java.util.HashMap
import java.util.Objects

class SetFirstTimeRequest(
    Name: String,
    Email: String,
    UserId: String,
    fcm_token: String?,
    listener: Response.Listener<String>
) : StringRequest(Request.Method.POST, LOGIN_REQUEST_URL, listener, null) {
    private val params: MutableMap<String, String>
    private val mAuth: FirebaseAuth

    init {
        var fcm_token = fcm_token
        fcm_token = FirebaseInstanceId.getInstance().token
        mAuth = FirebaseAuth.getInstance()
        params = HashMap()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params["Name"] =
                Objects.requireNonNull<String>(Objects.requireNonNull<FirebaseUser>(mAuth.currentUser).getDisplayName())
                    .trim { it <= ' ' }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params["Email"] = Objects.requireNonNull<String>(mAuth.currentUser!!.email).trim { it <= ' ' }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params["UserId"] = Objects.requireNonNull<FirebaseUser>(mAuth.currentUser).getUid().trim({ it <= ' ' })
        }
        if (fcm_token != null) {
            params["fcm_token"] = fcm_token.trim { it <= ' ' }
        }
        //params.put("User_Name", MainActivity.global_Username);
    }

    public override fun getParams(): Map<String, String> {
        return params
    }

    companion object {

        private val LOGIN_REQUEST_URL = MY_SERVER_URL + "user_login2.php"
    }
}
