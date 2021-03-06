package com.example.edmundconnor.clubemmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    TextView eventName;
    TextView eventDescription;
    TextView eventLocation;
    TextView eventStartTime;
    TextView eventEndTime;
    Spinner eventTags;
    private final Map<String, String> eventInfo = new HashMap<>();
    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    private String url2;

    private String eventID;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        eventID = intent.getStringExtra(NewsfeedFragment.eventID);
        url2 = url + eventID + "/eventInfo";

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        imgView = (ImageView) findViewById(R.id.event_image);

        getEventData();
    }

    public void setImage() {

        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                // Reference to an image file in Firebase Storage
                if (event.getImgId() != null && !event.getImgId().equals("")) {
                    System.out.println("Attaching Image");
                    storageRef.child(eventID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(getApplicationContext()).load(uri).fit().centerCrop().into(imgView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
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
                        System.out.println(response);

                        try {
                            eventName.setText(response.getString("name"));
                            eventDescription.setText(response.getString("description"));
                            eventLocation.setText(response.getString("location"));
                            eventStartTime.setText(response.getString("startDate"));
                            eventEndTime.setText(response.getString("endDate"));

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

    private void populateData() {
        myRef.child("events").child(eventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event thisEvent = dataSnapshot.getValue(Event.class);
                eventName = (TextView) findViewById(R.id.event_name);
                eventDescription = (TextView) findViewById(R.id.event_description);
                eventLocation = (TextView) findViewById(R.id.event_location);
                eventStartTime = (TextView) findViewById(R.id.event_start_time);
                eventEndTime = (TextView) findViewById(R.id.event_end_time);
                eventName.setText(thisEvent.getName());
                eventDescription.setText(thisEvent.getDescription());
                eventLocation.setText(thisEvent.getLocation());
                eventStartTime.setText(thisEvent.getStartDate());
                eventEndTime.setText(thisEvent.getEndDate());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





}
