package com.example.edmundconnor.clubemmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    TextView eventName;
    TextView eventDescription;
    TextView eventLocation;
    TextView eventStartTime;
    TextView eventEndTime;
    Spinner eventTags;
    private String eventID;
    private final Map<String, String> eventInfo = new HashMap<>();
    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    private String url2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        eventID = intent.getStringExtra(NewsfeedFragment.eventID);
        url2 = url + eventID + "/eventInfo";
        getEventData();

    }

    public void getEventData() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        eventName = (TextView) findViewById(R.id.event_name);
                        eventDescription = (TextView) findViewById(R.id.event_description);
                        eventLocation = (TextView) findViewById(R.id.event_location);
                        eventStartTime = (TextView) findViewById(R.id.event_start_time);
                        eventEndTime = (TextView) findViewById(R.id.event_end_time);
                        eventTags = (Spinner) findViewById(R.id.event_tags);
                        System.out.println(response);

                        try {
                            eventName.setText(response.getString("name"));
                            eventDescription.setText(response.getString("description"));
                            eventLocation.setText(response.getString("location"));
                            eventStartTime.setText(response.getString("startDate"));
                            eventEndTime.setText(response.getString("endDate"));
                            /*
                            ArrayList<String> eTags = (ArrayList<String>) response.get("tags");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    getApplicationContext(),
                                    android.R.layout.simple_list_item_1,
                                    eTags
                            );
                            eventTags.setAdapter(adapter);
                            */
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error", "Error");
                    }
                });
        Volley.newRequestQueue(this).add(getRequest);
    }


}
