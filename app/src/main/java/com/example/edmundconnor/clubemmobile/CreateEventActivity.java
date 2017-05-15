package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarException;

import static com.example.edmundconnor.clubemmobile.NavigationActivity.ID;

public class CreateEventActivity extends AppCompatActivity {

    Button saveEvent;
    Button cancelEvent;
    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    private String putUrl;
    private EditText eventName;
    private EditText eventDesc;
    private EditText eventLoc;
    private CheckBox eventPrivate;
    private DatePicker eventDate;
    private TimePicker startTime;
    private TimePicker endTime;
    private ListView eventTags;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        String cid = getIntent().getStringExtra(MyClubsFragment.clubID);
        putUrl = url + cid + "/createEvent";

        eventName = (EditText) findViewById(R.id.event_name);
        eventDesc = (EditText) findViewById(R.id.event_description);
        eventLoc = (EditText) findViewById(R.id.event_location);
        eventPrivate = (CheckBox) findViewById(R.id.event_public);
        eventDate = (DatePicker) findViewById(R.id.event_date);
        startTime = (TimePicker) findViewById(R.id.event_start_time);
        endTime = (TimePicker) findViewById(R.id.event_end_time);
        eventTags = (ListView) findViewById(R.id.event_tags);


        saveEvent = (Button) findViewById(R.id.event_save);
        saveEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    createEvent();
                    Intent intent = new Intent(CreateEventActivity.this, NavigationActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }

            }
        });

        cancelEvent = (Button) findViewById(R.id.event_cancel);
        cancelEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, NavigationActivity.class);
                startActivity(intent);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }


    public void createEvent() throws JSONException {
        List<String> tags = new ArrayList<String>();
        JSONObject jsonBody = new JSONObject();
        String date = eventDate.getMonth() + "-" + eventDate.getDayOfMonth() + "-" + eventDate.getYear();
        String start_date = date + " " + startTime.getCurrentHour() + ":" + startTime.getCurrentMinute();
        String end_date = date + " " + endTime.getCurrentHour() + ":" + endTime.getCurrentMinute();
        try {
            System.out.print("HERE");
            jsonBody.put("name", eventName.getText().toString());
            jsonBody.put("description", eventDesc.getText());
            jsonBody.put("userID", ID);
            jsonBody.put("tags", tags);
            jsonBody.put("startDate", start_date);
            jsonBody.put("endDate", end_date);
            jsonBody.put("location", eventLoc.getText().toString());
            jsonBody.put("isPrivate", eventPrivate.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, putUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Intent intent = new Intent(CreateEventActivity.this, NavigationActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error", "Error: " + error.getMessage());
                    }
                }
        ) {
        };
        Volley.newRequestQueue(getApplicationContext()).add(putRequest);
    }

}
