package com.ivanfwc.myownapp.Fragment

import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivanfwc.myownapp.Config.MY_SERVER_URL
import com.ivanfwc.myownapp.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

class DataFragment : Fragment(), View.OnClickListener {

    internal lateinit var buttonUnsortedlist: Button
    internal lateinit var buttonSortedbybrand: Button
    internal lateinit var buttonSortedbyprice: Button
    internal lateinit var buttonFilterresults: Button
    internal lateinit var progressDialog: ProgressDialog
    internal var textView: TextView? = null
    internal lateinit var sharedPreferences: SharedPreferences
    internal var URL = MY_SERVER_URL + "getData.php"
    internal var mn: String? = null
    //LaptopAdapter laptopAdapter;
    //ArrayList<Laptop> laptopList= new ArrayList<Laptop>();
    internal lateinit var jsonString: String
    internal var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_data, container, false)

        progressDialog = ProgressDialog(activity)
        buttonFilterresults = rootView.findViewById(R.id.buttonFilterresults)
        buttonSortedbybrand = rootView.findViewById(R.id.buttonSortedbybrand)
        buttonSortedbyprice = rootView.findViewById(R.id.buttonSortedbyprice)
        buttonUnsortedlist = rootView.findViewById(R.id.buttonUnsortedlist)

        buttonSortedbybrand.setOnClickListener(this)
        buttonUnsortedlist.setOnClickListener(this)
        buttonFilterresults.setOnClickListener(this)
        buttonSortedbyprice.setOnClickListener(this)
        getData()
        sharedPreferences = mContext!!.getSharedPreferences("SHARED_PREF_NAME", Context.MODE_PRIVATE)

        return rootView
    }

    override fun onClick(v: View) {
        /*if(v==buttonUnsortedlist){
            startActivity(new Intent(this,UnsortedActivity.class));
        }
        if(v==buttonSortedbybrand){

            startActivity(new Intent(this,Sortedbybrand.class));
        }
        if(v==buttonSortedbyprice){

            startActivity(new Intent(this,Sortedbyprice.class));
        }
        if(v==buttonFilterresults){

            startActivity(new Intent(this,Filterresults.class));
        }*/
    }

    fun getData() {
        progressDialog.setMessage("Fetching data from the Server...")
        progressDialog.show()

        val stringRequest = StringRequest(Request.Method.POST, URL,

            Response.Listener { response ->
                progressDialog.dismiss()

                Toast.makeText(activity, "Data Successfully Fetched", Toast.LENGTH_SHORT).show()
                try {
                    val js = JSONObject(response)

                    val jsonArray = js.getJSONArray("laptops")

                    jsonString = jsonArray.toString()


                    val editor = sharedPreferences.edit()

                    editor.putString("jsonString", jsonString)
                    editor.apply()


                    val sortedJsonArray = JSONArray()
                    val jsonValues = ArrayList<JSONObject>()
                    for (i in 0 until jsonArray.length()) {
                        jsonValues.add(jsonArray.getJSONObject(i))
                    }
                    Collections.sort(jsonValues, object : Comparator<JSONObject> {
                        //You can change "Name" with "ID" if you want to sort by ID
                        private val KEY_NAME = "modelname"

                        override fun compare(a: JSONObject, b: JSONObject): Int {
                            var valA = String()
                            var valB = String()

                            try {
                                valA = a.get(KEY_NAME) as String
                                valB = b.get(KEY_NAME) as String
                            } catch (e: JSONException) {
                                //do something
                            }

                            return valA.compareTo(valB)
                            //if you want to change the sort order, simply use the following:
                            //return -valA.compareTo(valB);
                        }
                    })

                    for (i in 0 until jsonArray.length()) {
                        sortedJsonArray.put(jsonValues[i])
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { })

        val request = Volley.newRequestQueue(activity)
        request.add(stringRequest)
    }
}// Required empty public constructor
