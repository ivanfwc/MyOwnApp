package com.ivanfwc.myownapp.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ivanfwc.myownapp.R

class HomeFragment : Fragment() {

    internal lateinit var mAuth: FirebaseAuth
    internal var Name: String? = null
    internal var Email: String? = null
    internal var UserId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        //setMatricNumber();

        return rootView
    }

    /*public void setMatricNumber() {

        *//*Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        SetFirstTimeRequest confirmMatric = new SetFirstTimeRequest(Name, Email, UserId, responseListener);
        //Toast.makeText(LoginActivity.this, user.getEmail().toString().trim(), Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(confirmMatric);*//*
    }*/
}// Required empty public constructor
