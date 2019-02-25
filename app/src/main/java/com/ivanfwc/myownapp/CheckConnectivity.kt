package com.ivanfwc.myownapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object CheckConnectivity {

    private val TYPE_WIFI = 1
    private val TYPE_MOBILE = 2
    private val TYPE_NOT_CONNECTED = 0

    private fun getConnectivityStatus(context: Context): Int {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork: NetworkInfo? = null
        if (cm != null) {
            activeNetwork = cm.activeNetworkInfo
        }
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI

            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context): String? {
        val conn = getConnectivityStatus(context)
        var status: String? = null
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled"
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled"
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet"
        }
        return status
    }
}
