package com.ivanfwc.myownapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import org.json.JSONException
import org.json.JSONObject

import java.util.Collections

class LoginActivity : BaseActivity(), View.OnClickListener {

    private var mFacebookSignInButton: LoginButton? = null
    private var mFacebookCallbackManager: CallbackManager? = null
    //creating a GoogleSignInClient object
    internal var mGoogleSignInClient: GoogleSignInClient
    //And also a Firebase Auth object
    private var mAuth: FirebaseAuth? = null
    internal var signInButton: SignInButton
    private val mProfileTracker: ProfileTracker? = null
    private var mAccessToken: AccessToken? = null
    internal var email_sign_in_button: Button? = null

    internal var errorDialog: Dialog? = null

    internal var Name: String? = null
    internal var Email: String? = null
    internal var UserId: String? = null
    internal var fcm_token: String? = null

    protected fun onCreate(savedInstanceState: Bundle) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAccessToken = AccessToken.getCurrentAccessToken()

        //facebook
        mFacebookCallbackManager = CallbackManager.Factory.create()
        mFacebookSignInButton = findViewById(R.id.login_button)
        mFacebookSignInButton!!.setReadPermissions(listOf(EMAIL))
        mFacebookSignInButton!!.registerCallback(mFacebookCallbackManager,
            object : FacebookCallback<LoginResult>() {

                fun onSuccess(loginResult: LoginResult) {
                    //TODO: Use the Profile class to get information about the current user.

                    handleFacebookAccessToken(loginResult.getAccessToken())

                    /*String userLoginId = loginResult.getAccessToken().getUserId();
                        Intent facebookIntent = new Intent(LoginActivity.this, MainActivity.class);
                        Profile mProfile = Profile.getCurrentProfile();
                        String firstName = mProfile.getFirstName();
                        String lastName = mProfile.getLastName();
                        String userId = mProfile.getId().toString();
                        String profileImageUrl = mProfile.getProfilePictureUri(96, 96).toString();
                        facebookIntent.putExtra(PROFILE_USER_ID, userId);
                        facebookIntent.putExtra(PROFILE_FIRST_NAME, firstName);
                        facebookIntent.putExtra(PROFILE_LAST_NAME, lastName);
                        facebookIntent.putExtra(PROFILE_IMAGE_URL, profileImageUrl);
                        startActivity(facebookIntent);
                        finish();*/

                    /*if(Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                    Log.v("facebook - profile", currentProfile.getFirstName());
                                    mProfileTracker.stopTracking();
                                }
                            };
                            // no need to call startTracking() on mProfileTracker
                            // because it is called by its constructor, internally.
                        }
                        else {
                            Profile profile = Profile.getCurrentProfile();
                            Log.v("facebook - profile", profile.getFirstName());
                        }
                        handleSignInResult(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                LoginManager.getInstance().logOut();
                                return null;
                            }
                        });*/
                }

                fun onCancel() {
                    //handleSignInResult(null);
                }

                fun onError(error: FacebookException) {
                    Log.d(MainActivity::class.java.canonicalName, error.getMessage())
                    //handleSignInResult(null);
                }
            }
        )

        checkPlayServices()

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        signInButton = findViewById(R.id.sign_in_button)

        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener(this)

        //findViewById(R.id.email_sign_in_button).setOnClickListener(this);

    }

    /*private void checkInitialized() {
    }
    private void handleSignInResult(Object o) {
        //Handle sign result here
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }*/

    // [START auth_with_facebook]
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = FacebookAuthProvider.getCredential(token.getToken())
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult>() {
                fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        //FirebaseUser user = mAuth.getCurrentUser();

                        ResponseCreator()

                        /*// Facebook Email address
                            GraphRequest request = GraphRequest.newMeRequest(
                                    mAccessToken,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.v("LoginActivity Response ", response.toString());
                                            try {
                                                String Name = object.getString("name");
                                                String FEmail = object.getString("email");
                                                Log.v("Email = ", " " + FEmail);
                                                Toast.makeText(getApplicationContext(), "Name " + Name, Toast.LENGTH_LONG).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "name,email");
                            request.setParameters(parameters);
                            request.executeAsync();*/

                        val request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            object : GraphRequest.GraphJSONObjectCallback() {
                                fun onCompleted(`object`: JSONObject, response: GraphResponse) {
                                    // Application code

                                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(i)
                                    finish()

                                    /*String Email = object.getString("email");
                                String Name = object.getString("name");*/

                                }
                            })
                        val parameters = Bundle()
                        parameters.putString("fields", "email,name")
                        request.setParameters(parameters)
                        request.executeAsync()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException())
                        Toast.makeText(
                            this@LoginActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // [START_EXCLUDE]
                    hideProgressDialog()
                    // [END_EXCLUDE]
                }
            })
    }
    // [END auth_with_facebook]

    protected fun onStart() {
        super.onStart()

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth!!.getCurrentUser() != null) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        mFacebookCallbackManager!!.onActivityResult(requestCode, resultCode, data)

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                //authenticating with firebase
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(getApplicationContext(), R.string.login_activity_failed, Toast.LENGTH_SHORT).show()
                //Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                //updateUI(null);
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        showProgressDialog2()

        //getting the auth credential
        val credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null)

        //Now using firebase we are signing in the user here
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult>() {
                fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        ResponseCreator()
                        //Log.d(TAG, "signInWithCredential:success");
                        //FirebaseUser user = mAuth.getCurrentUser();
                        //updateUI(user);
                        //Toast.makeText(LoginActivity.this, user.getDisplayName() + " " + getString(R.string.signed_in), Toast.LENGTH_SHORT).show();

                        //sendRequest();

                        val i = Intent(this@LoginActivity, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        //Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(
                            this@LoginActivity,
                            R.string.login_activity_authentication_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                        //updateUI(null);
                    }
                    hideProgressDialog()
                    // ...
                }
            })
    }

    private fun ResponseCreator() {
        val responseListener = object : Response.Listener<String>() {
            fun onResponse(response: String) {
                try {

                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")

                    if (success) {

                    } else {

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }

        val confirmMatric = SetFirstTimeRequest(Name, Email, UserId, fcm_token, responseListener)
        //Toast.makeText(LoginActivity.this, user.getEmail().toString().trim(), Toast.LENGTH_SHORT).show();
        val queue = Volley.newRequestQueue(getApplicationContext())
        queue.add(confirmMatric)
    }

    private fun checkPlayServices(): Boolean {

        val googleApiAvailability = GoogleApiAvailability.getInstance()

        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {

                if (errorDialog == null) {
                    errorDialog = googleApiAvailability.getErrorDialog(this, resultCode, 2404)
                    errorDialog!!.setCancelable(false)
                }

                if (!errorDialog!!.isShowing)
                    errorDialog!!.show()

            }
        }

        return resultCode == ConnectionResult.SUCCESS
    }

    //this method is called on click
    private fun signIn() {
        //getting the google signin intent
        val signInIntent = mGoogleSignInClient.getSignInIntent()

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    fun onPause() {
        super.onPause()

        if (mProgressDialog2 != null) {
            mProgressDialog2.dismiss()
            mProgressDialog2 = null
        }
    }

    fun onDestroy() {
        super.onDestroy()

        if (mProgressDialog2 != null) {
            mProgressDialog2.dismiss()
            mProgressDialog2 = null
        }
    }

    fun onBackPressed() {

        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.login_activity_closing_application))
            .setContentText(getString(R.string.login_activity_confirmation))
            .setCancelText(getString(R.string.button_no))
            .setConfirmText(getString(R.string.button_yes))
            .showCancelButton(true)
            .setCancelClickListener(object : SweetAlertDialog.OnSweetClickListener() {
                fun onClick(sDialog: SweetAlertDialog) {
                    // reuse previous dialog instance, keep widget user state, reset them if you need
                    sDialog.cancel()

                }
            })
            .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener() {
                fun onClick(sDialog: SweetAlertDialog) {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    sDialog.dismiss()
                }
            })
            .show()
    }

    override fun onClick(view: View) {
        val i = view.id
        if (i == R.id.sign_in_button) {

            val cm = getApplicationContext().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

            var activeNetwork: NetworkInfo? = null
            if (cm != null) {
                activeNetwork = cm.activeNetworkInfo
            }
            //if (activeNetwork != null && activeNetwork.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) { // connected to the internet
            if (activeNetwork != null && activeNetwork.isConnected) { // connected to the internet

                signIn()

            } else {

                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.something_went_wrong))
                    .setContentText(getString(R.string.no_internet_connection))
                    .setCancelText(getString(R.string.exit_app))
                    .setConfirmText(getString(R.string.open_settings))
                    .setNeutralText(getString(R.string.try_again_dialog))
                    .showCancelButton(true)
                    .setCancelClickListener(object : SweetAlertDialog.OnSweetClickListener() {
                        fun onClick(sDialog: SweetAlertDialog) {
                            this@LoginActivity.finish()
                            sDialog.dismiss()

                        }
                    })
                    .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener() {
                        fun onClick(sDialog: SweetAlertDialog) {
                            startActivityForResult(Intent(android.provider.Settings.ACTION_SETTINGS), 0)
                            sDialog.dismiss()
                        }
                    })
                    .setNeutralClickListener(object : SweetAlertDialog.OnSweetClickListener() {
                        fun onClick(sDialog: SweetAlertDialog) {

                            val cm =
                                this@LoginActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                            var activeNetwork: NetworkInfo? = null
                            if (cm != null) {
                                activeNetwork = cm.activeNetworkInfo
                            }
                            //if (activeNetwork != null && activeNetwork.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) { // connected to the internet
                            if (activeNetwork != null && activeNetwork!!.isConnected) { // connected to the internet
                                mAuth = FirebaseAuth.getInstance()
                                //FirebaseUser user = mAuth.getCurrentUser();
                                getApplicationContext()
                                sDialog.dismiss()

                            } else {
                                mAuth = FirebaseAuth.getInstance()
                                //FirebaseUser user = mAuth.getCurrentUser();
                                getApplicationContext()
                                sDialog.dismiss()
                            }
                        }
                    })
                    .show()
            }
        } else if (i == R.id.email_sign_in_button) {
            val intent = Intent(this, SignInActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            this.finish()
        }
    }

    companion object {
        //a constant for detecting the login intent result
        private val RC_SIGN_IN = 9001
        //Tag for the logs optional
        private val TAG = "DataMining"
        private val EMAIL = "email"
    }
}