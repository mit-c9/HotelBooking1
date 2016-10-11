package com.example.mit.hotelbooking;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static android.widget.Toast.*;

/**
 * A login screen that offers login via email/password.
 */
public class login extends AppCompatActivity {
    EditText lusername,lpassword;
    RequestQueue mqueue;
    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lusername=(EditText) findViewById(R.id.lusername);
        lpassword=(EditText) findViewById(R.id.lpassword);
        mqueue=CustomVolleyRequestQueue.getInstance(this).getRequestQueue();
        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();


    }

    public void startprogress(){
        pDialog = new ProgressDialog(login.this);

        pDialog.setMessage("Checking...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    public void volleyConnect(){

        String url = "http://localhost/1login.php";
        editor = sharedPreferences.edit();
        JSONObject jo = new JSONObject();
        try {
            jo.put("name",getstr(lusername));
            jo.put("password",getstr(lpassword));

            Log.d("json",jo.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest mreq = new JsonObjectRequest(Request.Method.POST,url, jo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                try {
                    String msg = response.getString("message");

                    int Success = response.getInt("success");
                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                    if (Success==1){
                        Intent i = new Intent(login.this,home.class);
                        editor.putString("id",getstr(lusername));
                        editor.apply();
                        i.putExtra("username",getstr(lusername));
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                editor.putString("id","");
                editor.apply();
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();

            }
        });
        mqueue.add(mreq);
    }
    public String getstr(EditText et){
        return et.getText().toString();
    }
    public void signup(View view){
        Intent intent = new Intent(this,register.class);
        startActivity(intent);
    }
    public void signin(View view){
        startprogress();
        volleyConnect();
    }
}

