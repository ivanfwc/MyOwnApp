package com.ivanfwc.myownapp

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.ivanfwc.myownapp.Config.MY_SERVER_URL

import java.util.HashMap

class SetLogoutClearFCM(Email: String, listener: Response.Listener<String>) :
    StringRequest(Request.Method.POST, LOGIN_REQUEST_URL, listener, null) {
    private val params: MutableMap<String, String>

    init {
        params = HashMap()
        params["Email"] = Email.trim { it <= ' ' }
    }

    public override fun getParams(): Map<String, String> {
        return params
    }

    companion object {

        private val LOGIN_REQUEST_URL = MY_SERVER_URL + "setLogoutClearFCM.php"
    }
}
