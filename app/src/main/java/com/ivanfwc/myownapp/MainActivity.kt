package com.ivanfwc.myownapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.ivanfwc.myownapp.CheckConnectivity.getConnectivityStatusString
import com.ivanfwc.myownapp.activities.MapsActivity
import com.ivanfwc.myownapp.activities.ProfileActivity

class MainActivity : AppCompatActivity() {

    private var drawerLayout: DrawerLayout? = null
    //private var toolbar: Toolbar? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    internal lateinit var mAuth: FirebaseAuth

    private var linearLayout: LinearLayout? = null
    private var internetConnected = true
    internal lateinit var snackbar: Snackbar
    lateinit var toolbar: ActionBar

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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen_buyer)
        linearLayout = findViewById(R.id.linearLayout)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        drawerLayout = findViewById(R.id.drawerLayout)
        //toolbar = findViewById(R.id.toolbar)
        //val navigationView = findViewById<NavigationView>(R.id.navigationDrawer)

        // since, NoActionBar was defined in theme, we set toolbar as our action bar.
        //setSupportActionBar(toolbar)
        toolbar = supportActionBar!!
        supportActionBar?.hide()

        /*//this basically defines on click on each menu item.
        setUpNavigationView(navigationView)

        //This is for the Hamburger icon.
        drawerToggle = setupDrawerToggle()
        drawerLayout!!.addDrawerListener(drawerToggle!!)

        //Inflate the first fragment,this is like home fragment before user selects anything.
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frameContent, MainTabLayout()).commit()
        title = "Home"

        val header = navigationView.getHeaderView(0)

        val navprofile_imageView = header.findViewById<ImageView>(R.id.navprofile_imageView)
        val nav_name = header.findViewById<TextView>(R.id.nav_name)
        val nav_email = header.findViewById<TextView>(R.id.nav_email)

        if (user != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .apply(
                    RequestOptions.circleCropTransform()
                        .placeholder(R.mipmap.ic_launcher_round))
                .into(navprofile_imageView)
        }

        navprofile_imageView.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            drawerLayout!!.closeDrawers()
        }

        if (user != null) {
            nav_name.text = user.displayName
        }
        if (user != null) {
            nav_email.text = user.email
        }*/

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)

    }

    /**
     * Inflate the fragment according to item clicked in navigation drawer.
     */
    /*private fun setUpNavigationView(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            //replace the current fragment with the new fragment.
            *//*Fragment selectedFragment = selectDrawerItem(menuItem);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frameContent, selectedFragment).commit();
                        // the current menu item is highlighted in navigation tray.
                        navigationView.setCheckedItem(menuItem.getItemId());
                        setTitle(menuItem.getTitle());
                        //close the drawer when user selects a nav item.
                        drawerLayout.closeDrawers();*//*

            val id = menuItem.itemId

            if (id == R.id.nav_item_home) {
                val fragment = MainTabLayout()
                val fragmentTransaction1 = supportFragmentManager.beginTransaction()
                fragmentTransaction1.addToBackStack(null)
                fragmentTransaction1.replace(R.id.frameContent, fragment).commit()
                snackbar = Snackbar.make(linearLayout!!, menuItem.title.toString() + " selected", Snackbar.LENGTH_SHORT)
                snackbar.setActionTextColor(Color.WHITE)
                val sbView = snackbar.view
                val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
                textView.setTextColor(Color.WHITE)
                snackbar.show()

            } else if (id == R.id.nav_item_additional) {
                val intent = Intent(applicationContext, MapsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }

            navigationView.setCheckedItem(menuItem.itemId)
            title = menuItem.title
            //close the drawer when user selects a nav item.
            drawerLayout!!.closeDrawers()

            true
        }
    }*/

    /*public Fragment selectDrawerItem(MenuItem menuItem){
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_item_home:
                fragment = new MainTabLayout();
                break;
            case R.id.nav_item_additional:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return fragment;
    }*/

    /**
     * This is to setup our Toggle icon. The strings R.string.drawer_open and R.string.drawer close, are for accessibility (generally audio for visually impaired)
     * use only. It is now showed on the screen. While the remaining parameters are required initialize the toggle.
     */
    /*private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
    }*/

    /**
     * This makes sure that the action bar home button that is the toggle button, opens or closes the drawer when tapped.
     */
    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        *//*when (item.itemId) {
            android.R.id.home -> {
                drawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)*//*
    }*/

    /**
     * This synchronizes the drawer icon that rotates when the drawer is swiped left or right.
     * Called inside onPostCreate so that it can synchronize the animation again when the Activity is restored.
     */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        //drawerToggle!!.syncState()
    }

    /**
     * This is to handle generally orientation changes of your device. It is mandatory to include
     * android:configChanges="keyboardHidden|orientation|screenSize" in your activity tag of the manifest for this to work.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //drawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(baseContext, getString(R.string.double_pressed_back), Toast.LENGTH_SHORT).show()
            back_pressed = System.currentTimeMillis()
            //finish();
        }
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

    companion object {
        private var back_pressed: Long = 0
    }
}
