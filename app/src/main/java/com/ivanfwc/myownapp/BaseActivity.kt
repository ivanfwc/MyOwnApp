package com.ivanfwc.myownapp

/*
 * Created by Ivanf on 12/12/2017.
 */
import android.app.ProgressDialog
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    @VisibleForTesting
    var mProgressDialog: ProgressDialog? = null
    @VisibleForTesting
    var mProgressDialog2: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage(getString(R.string.loading_app))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun showProgressDialog2() {
        if (mProgressDialog2 == null) {
            mProgressDialog2 = ProgressDialog(this)
            mProgressDialog2!!.setMessage(getString(R.string.welcomeing_user))
            mProgressDialog2!!.isIndeterminate = true
        }

        mProgressDialog2!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}
