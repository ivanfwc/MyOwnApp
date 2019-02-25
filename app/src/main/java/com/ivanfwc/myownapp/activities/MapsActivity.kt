package com.ivanfwc.myownapp.activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.*
import com.ivanfwc.myownapp.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, com.google.android.gms.location.LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    internal lateinit var mMap: GoogleMap
    internal var markerOptions = MarkerOptions()
    internal var cameraPosition: CameraPosition? = null
    internal var center: LatLng? = null
    internal lateinit var latLng: LatLng
    internal var title: String? = null
    internal var snippet: String? = null
    internal var locationManager: LocationManager? = null
    internal var mCurrLocationMarker: Marker? = null
    internal lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest

    private var mGoogleApiClient: GoogleApiClient? = null
    internal var placeAutoComplete: PlaceAutocompleteFragment? = null

    /**
     * Keeps track of the selected marker.
     */
    private val mSelectedMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        checkLocationPermission()

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        (findViewById(R.id.toolbar_title) as TextView).text = "Maps"
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager != null) {
            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show()
            } else {
                showGPSDisabledAlertToUser()
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //getMarkers();

        mMap.setOnMapClickListener(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            buildGoogleApiClient()

            mMap.isMyLocationEnabled = true
            mMap.isTrafficEnabled = true

            val uiSettings = googleMap.uiSettings
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMapToolbarEnabled = true
            //return;
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        //mGoogleApiClient.connect();
    }

    /*private void addMarker(LatLng latlng, final String title, final String snippet) {

        int height = 100;
        int width = 80;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.parkingicon);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        markerOptions.position(latlng);
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        gMap.addMarker(markerOptions);

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MapsActivity.this, ActivateParking.class);
                startActivity(intent);
            }
        });
    }*/

    /*private void getMarkers() {
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String getObject = jObj.getString("location");
                    JSONArray jsonArray = new JSONArray(getObject);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        title = jsonObject.getString(TITLE);
                        snippet = "Current charge rate is: RM " + jsonObject.getString(SNIPPET) +"\n"+ "Parking area: " + jsonObject.getString(SNIPPET2);
                        latLng = new LatLng(Double.parseDouble(jsonObject.getString(LAT)), Double.parseDouble(jsonObject.getString(LNG)));

                        addMarker(latLng, title, snippet);
                        //String url = "http://maps.google.com/maps?f=d&daddr="+ new LatLng(Double.parseDouble(jsonObject.getString(LAT)), Double.parseDouble(jsonObject.getString(LNG)));
                        //Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                        //intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        //startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error" , error.getMessage());
                Toast.makeText(getApplicationContext(),"Unable to connect to the internet, please check your connection and try again.",Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

        gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                // Getting reference to the TextView to set title
                TextView tvTitle = (TextView) v.findViewById(R.id.tv_titlee);

                // Getting reference to the TextView to set snippet
                TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

                // Setting the title
                tvTitle.setText(marker.getTitle());

                // Setting the snippet
                tvSnippet.setText(marker.getSnippet());

                return v;
            }
        });
    }*/

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Goto Settings Page To Enable GPS"
            ) { dialog, id ->
                val callGPSSettingIntent = Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(callGPSSettingIntent)
            }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, id -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onConnected(bundle: Bundle?) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            mMap.clear()
            latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Position")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            mCurrLocationMarker = mMap.addMarker(markerOptions)
            //mCurrLocationMarker = mLastLocation;
        }

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 5000 //5 seconds
        mLocationRequest.fastestInterval = 5000 //5 seconds
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationRequest.smallestDisplacement = 0.1f //1/10 meter

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show()

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST)
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (e: IntentSender.SendIntentException) {
                // Log the error
                e.printStackTrace()
            }

        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.errorCode)
        }

    }

    override fun onLocationChanged(location: Location) {

        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
    }

    override fun onInfoWindowClick(marker: Marker) {

    }

    override fun onMapClick(latLng: LatLng) {
        //mSelectedMarker = null;

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        mCurrLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true).visible(true)
        )
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK") { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MapsActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()

                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            //return false;
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mMap.isMyLocationEnabled = true
                        mMap.uiSettings.isZoomControlsEnabled = true
                        mMap.uiSettings.isCompassEnabled = true
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    companion object {

        val ID = "id"
        val TITLE = "name"
        val SNIPPET = "charge_rate"
        val SNIPPET2 = "slotNum"
        val LAT = "lat"
        val LNG = "lng"

        val MY_PERMISSIONS_REQUEST_LOCATION = 99

        val TAG = MapsActivity::class.java.simpleName

        /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
        private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    }
}
