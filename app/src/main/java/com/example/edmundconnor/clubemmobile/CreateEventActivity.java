package com.example.edmundconnor.clubemmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    Button saveEvent;
    Button cancelEvent;
    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    private String putUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        String cid = getIntent().getStringExtra(MyClubsFragment.clubID);
        putUrl = url + cid + "/createEvent";

        saveEvent = (Button) findViewById(R.id.event_save);
        saveEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, NavigationActivity.class);
                startActivity(intent);
            }
        });

        cancelEvent = (Button) findViewById(R.id.event_cancel);
        cancelEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, NavigationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createEvent() {

        StringRequest putRequest = new StringRequest(Request.Method.PUT, putUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("name", "Alif");
                params.put("domain", "http://itsalif.info");

                return params;
            }

        };

        Volley.newRequestQueue(this).add(putRequest);
    }
}
