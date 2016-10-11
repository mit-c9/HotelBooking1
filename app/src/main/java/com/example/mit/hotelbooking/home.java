package com.example.mit.hotelbooking;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class home extends AppCompatActivity {


    ListView lv;
    Spinner sp,sp2;
    CustAdapter adapter;
    ArrayList<Item> data;
    ArrayList<String> cityname,list;
    private RequestQueue mqueue;
    private boolean isloaded = false;
    private String catsel= "All",citysel="All";
    private String url = "http://localhost/list.php";
    private String curl = "http://localhost/city.php";
    private String furl = "http://localhost/filter.php";

    private ArrayAdapter<String> arrayAdapter,catAdapter;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lv = (ListView) findViewById(R.id.listView);
        sp = (Spinner) findViewById(R.id.spinner);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               // Toast.makeText(getApplicationContext(),"position is "+position,Toast.LENGTH_SHORT).show();
                citysel = cityname.get(position);
                getFilterData(cityname.get(position),catsel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp2 = (Spinner) findViewById(R.id.spinner2);

        mqueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        fetchHotelList();
        getCityList();
        setsp2data();
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    catsel = list.get(position);
                    Log.e("cat:", catsel);
                    getFilterData(citysel, list.get(position));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startProgress(){
        pDialog = new ProgressDialog(home.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void stopProgress(){
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    public void fetchHotelList(){
        startProgress();
        JsonArrayRequest ja = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                    if(jsonArray.length()>0){
                        try {
                        JSONObject jo;
                            String name,cat;
                            int price;
                            Item item;
                            data = new ArrayList<>(jsonArray.length());
                        for(int i=0;i<jsonArray.length();i++){

                                jo = jsonArray.getJSONObject(i);
                                name = jo.getString("H_name");
                                cat = jo.getString("category");
                                price = jo.getInt("price");
                               item = new Item(name,cat,price);
                               data.add(item);

                        }
                            stopProgress();
                            adapter = new CustAdapter(home.this,R.layout.custrow,data);
                            lv.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                  stopProgress();
                Toast.makeText(getApplicationContext(),"Error avi",Toast.LENGTH_SHORT).show();
            }
        });
        mqueue.add(ja);
    }

    public void getCityList(){
        JsonArrayRequest ja = new JsonArrayRequest(curl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                  if(jsonArray.length()>0){
                      JSONObject jo;
                      String city;
                      cityname = new ArrayList<>(jsonArray.length());
                      cityname.add("All");
                      for(int i=0;i<jsonArray.length();i++){
                          try {
                              jo = jsonArray.getJSONObject(i);
                              city = jo.getString("city");
                              cityname.add(city);
                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                      }
                      arrayAdapter = new ArrayAdapter<String>(home.this,android.R.layout.simple_spinner_item,cityname);
                      arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                      sp.setAdapter(arrayAdapter);
                  }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"Error avi City ma",Toast.LENGTH_SHORT).show();
            }
        });
        mqueue.add(ja);
    }

    public void setsp2data(){
        list = new ArrayList<>();
        list.add("All");
        list.add("Normal");
        list.add("Premium");
        list.add("Deluxe");
        list.add("Hall");
        catAdapter = new ArrayAdapter<String>(home.this,android.R.layout.simple_spinner_item,list);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(catAdapter);
        isloaded = true;
    }


    public void getFilterData(String city,String cat){

        JSONObject jo = new JSONObject();
        JSONArray jj = new JSONArray();
        try {
            jo.put("city",city);
            jo.put("category",cat);
            jj.put(jo);
            Log.e("ja:",jj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest ja = new JsonArrayRequest(Request.Method.POST,furl, jj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                  Log.e("jaLen:",String.valueOf(jsonArray.length()));
                if(jsonArray.length()>0){
                    try {
                        JSONObject jo;
                        String name,cat;
                        int price;
                        Item item;
                        data = new ArrayList<>(jsonArray.length());
                        for(int i=0;i<jsonArray.length();i++){

                            jo = jsonArray.getJSONObject(i);
                            name = jo.getString("H_name");
                            cat = jo.getString("category");
                            price = jo.getInt("price");
                            item = new Item(name,cat,price);
                            data.add(item);

                        }

                        adapter.clear();
                        adapter = new CustAdapter(home.this,R.layout.custrow,data);
                        lv.setAdapter(adapter);
                        Log.e("recordFound","trur");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Entry Found",Toast.LENGTH_SHORT).show();
                    adapter.clear();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(getApplicationContext(),"Error avi filter ma",Toast.LENGTH_SHORT).show();
            }
        });
       mqueue.add(ja);

    }
}
