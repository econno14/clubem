package com.example.edmundconnor.clubemmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by edmundConnor on 5/16/17.
 */

public class Event2Activity extends AppCompatActivity {

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

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Intent intent = getIntent();
        eventID = intent.getStringExtra(NewsfeedFragment.eventID);
        url2 = url + eventID + "/eventInfo";

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        imgView = (ImageView) findViewById(R.id.event_image);

        setImage();
        populateData();

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
