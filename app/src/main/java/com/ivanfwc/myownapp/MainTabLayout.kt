package com.ivanfwc.myownapp

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.ivanfwc.myownapp.Fragment.HomeFragment
import com.ivanfwc.myownapp.Fragment.ToBeAddedFragment

import java.util.ArrayList
import java.util.Objects

class MainTabLayout : Fragment() {

    internal lateinit var dialog: ProgressDialog
    internal lateinit var mContext: MainTabLayout
    internal lateinit var mAuth: FirebaseAuth

    internal var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    private val tabIcons = intArrayOf(R.drawable.ic_home_white, R.drawable.ic_account_circle_white)

    private val tabIcons2 = intArrayOf(R.drawable.ic_home_black, R.drawable.ic_account_circle_black)

    internal lateinit var tabsTitles: Array<String>

    internal var isChecked: Boolean = false
    internal var oldTheme: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        isChecked = sharedPref.getBoolean("caps_pref", false)
        val lister = sharedPref.getString("list_preference", "1")
        oldTheme = Integer.parseInt(lister!!)

        val rootView = inflater.inflate(R.layout.activity_tablayout, container, false)

        tabsTitles = arrayOf(getString(R.string.home_fragment_title), "To be Added")

        dialog = ProgressDialog(context)
        dialog.setMessage(getString(R.string.loading_app))

        mAuth = FirebaseAuth.getInstance()

        val user: FirebaseUser?

        mContext = this

        user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            for (profile in user.providerData) {
                // Id of the provider (ex: google.com)
                val providerId = profile.providerId

                // UID specific to the provider
                //String uid = profile.getUid();

                // Name, email address, and profile photo Url
                val photoUrl = profile.photoUrl
                val currentUser = mAuth.currentUser
                currentUser?.photoUrl
                currentUser?.email
                currentUser?.displayName
            }
        }

        viewPager = rootView.findViewById(R.id.viewpager)
        viewPager!!.offscreenPageLimit = 2

        setupViewPager(viewPager!!)

        tabLayout = rootView.findViewById(R.id.tabs)
        tabLayout!!.setupWithViewPager(viewPager)

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                viewPager!!.setCurrentItem(tab.position, true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (tab.icon != null) {
                        tab.icon!!.setColorFilter(
                            Objects.requireNonNull<Context>(context).getColor(R.color.colorPrimary),
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                } else {
                    var drawableProgress: Drawable? = null
                    if (tab.icon != null) {
                        drawableProgress = DrawableCompat.wrap(tab.icon!!)
                    }
                    if (drawableProgress != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            DrawableCompat.setTint(
                                drawableProgress,
                                ContextCompat.getColor(Objects.requireNonNull<Context>(context), R.color.colorPrimary)
                            )
                        }
                    }
                    //tab.getIcon().setColorFilter();
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull<Drawable>(Objects.requireNonNull<TabLayout.Tab>(tabLayout!!.getTabAt(0)).getIcon())
                        .clearColorFilter()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull<Drawable>(Objects.requireNonNull<TabLayout.Tab>(tabLayout!!.getTabAt(1)).getIcon())
                        .clearColorFilter()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        setupTabIcons(lister)

        viewPager!!.currentItem = 1
        viewPager!!.currentItem = 0

        return rootView
    }

    private fun setupTabIcons(checkTheme: String) {

        val checkTabIcons: IntArray
        if (Integer.parseInt(checkTheme) == 1) {
            checkTabIcons = tabIcons
        } else {
            checkTabIcons = tabIcons2
        }
        for (i in checkTabIcons.indices) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull<TabLayout.Tab>(tabLayout!!.getTabAt(i)).setIcon(checkTabIcons[i])
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(this.fragmentManager!!)
        viewPager.adapter = adapter
        adapter.addFrag(HomeFragment(), getString(R.string.home_fragment_title))
        adapter.addFrag(ToBeAddedFragment(), "To be Added")
        adapter.notifyDataSetChanged()
    }

    private inner class ViewPagerAdapter internal constructor(manager: FragmentManager) :
        FragmentStatePagerAdapter(manager) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE  // This will get invoke as soon as you call notifyDataSetChanged on viewPagerAdapter.
        }

        override fun getItem(position: Int): Fragment {

            when (position) {
                0 -> return HomeFragment()

                1 -> return ToBeAddedFragment()
            }

            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        internal fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
            //return null;
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(POSITION, tabLayout!!.selectedTabPosition)
    }

    companion object {
        var POSITION = "POSITION"
    }

}
