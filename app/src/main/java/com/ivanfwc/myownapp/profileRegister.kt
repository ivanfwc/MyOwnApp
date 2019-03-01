package com.ivanfwc.myownapp

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.ivanfwc.myownapp.R
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.sim_register_main.*

class profileRegister: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sim_register_main)
        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show()
        supportActionBar?.hide()
        view_pager_register.adapter = CustomPagerAdapter(this)
        btn_next.setOnClickListener {
            view_pager_register.currentItem = view_pager_register.currentItem + 1
        }
    }
}

enum class CustomPagerEnum constructor(val titleResId: Int, val layoutResId: Int) {

    RED(1, R.layout.activity_profile_buyer),
    BLUE(2, R.layout.activity_profile_buyer2);
    //GREEN(3, R.layout.sim_register_get_name);

    private var mTitleResId: Int = titleResId
    private var mLayoutResId: Int = layoutResId

    fun getTitleResId() : String{
        return mTitleResId.toString()
    }

    fun getLayoutResId() : String {
        return mLayoutResId.toString()
    }

}

class CustomPagerAdapter(private val mContext: Context) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val customPagerEnum = CustomPagerEnum.values()[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(customPagerEnum.getLayoutResId().toInt(), collection, false) as ViewGroup
        collection.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return CustomPagerEnum.values().size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val customPagerEnum = CustomPagerEnum.values()[position]
        return mContext.getString(customPagerEnum.getTitleResId().toInt())
    }

}