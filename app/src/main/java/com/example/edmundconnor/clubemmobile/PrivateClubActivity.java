package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.edmundconnor.clubemmobile.NewsfeedFragment.eventID;

public class PrivateClubActivity extends AppCompatActivity {

    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    protected static List<Event> eventItems;
    protected static EventAdapter esAdapter;
    private String url1, url2;
    private ArrayList<String> clubEventName;
    private String[] membersName;
    private final ArrayList<String> clubEventId = new ArrayList<String>();;
    private ListView lv;
    private LayoutInflater layoutinflater;
    private Context context;
    private int clubId;
    private static String club_desc;
    private static String club_name;
    private Button createEvent;
    private String cid;
    private String uid;
    public static final String clubID = "com.example.edmundconnor.clubemmobile.clubID";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference currentUserRef;
    private StorageReference mStorage;
    private ImageView imageView;


    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<String> keys = new ArrayList<String>();
    private ListView clubEvents;
    private int PICK_IMAGE_REQUEST = 1;
    private String imgRef = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_club);
        Intent intent = getIntent();
        cid = intent.getStringExtra(MyClubsFragment.clubID);
        uid = intent.getStringExtra(LoginActivity.ID);
        club_desc = intent.getStringExtra(MyClubsFragment.clubDESC);
        club_name = intent.getStringExtra(MyClubsFragment.clubNAME);
        System.out.print("Club ID: " + cid);
        imageView = (ImageView) findViewById(R.id.club_pic);

        clubEventName = new ArrayList<String>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        mStorage = FirebaseStorage.getInstance().getReference();


        setTitle(club_name);
        TextView description = (TextView) findViewById(R.id.club_description);
        description.setText(club_desc);

        url1 = url + cid + "/events";
        url2 = url + cid + "/members";
        //getClubEvents();
        getClubMembers();

        if (!imgRef.equals("")) {
            System.out.println("Attaching Image");
            mStorage.child(imgRef).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getApplicationContext()).load(uri).fit().centerCrop().into(imageView);
                }
            });
        }

        createEvent = (Button) findViewById(R.id.create_event_button);
        createEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PrivateClubActivity.this, CreateEventActivity.class);
                intent.putExtra(clubID, cid);
                startActivity(intent);
            }
        });

        clubEvents = (ListView) findViewById(R.id.list_clubEvents);

        populateList();
        setImage();

        clubEvents.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Event2Activity.class);
                System.out.println(position);
                String cid = clubEventId.get(position-1);
                System.out.print("Event ID " + cid);
                intent.putExtra(eventID, cid);
                startActivity(intent);
            }
        });

        Button addImgButton = (Button) findViewById(R.id.add_club_img);
        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Button saveImage = (Button) findViewById(R.id.save_image_button);
                //saveImage.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void populateList() {
        myRef.child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Event> localEvents = new ArrayList<Event>();
                List<String> localEventNames = new ArrayList<String>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Event value = child.getValue(Event.class);
                    Integer clubID = Integer.parseInt(cid);
                    if (value.getClubId() == clubID) {
                        keys.add(child.getKey());
                        localEvents.add(value);
                        clubEventId.add(value.getId());
                        System.out.println("Event ID " + value.getId());
                        localEventNames.add(value.getName());
                    }
                }

                System.out.print("************");

                ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                        PrivateClubActivity.this,
                        android.R.layout.simple_list_item_1,
                        localEventNames.toArray()
                );
                layoutinflater = getLayoutInflater();
                TextView head = (TextView) View.inflate(PrivateClubActivity.this, R.layout.item_header, null);
                head.setText("Upcoming Events");
                clubEvents.addHeaderView(head);
                clubEvents.setAdapter(lvAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
            final StorageReference filepath = mStorage.child("EventPhotos").child(uri.getLastPathSegment());
            imgRef = filepath.getPath();

            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                saveImageToInternalStorage(bitmap);
                Picasso.with(PrivateClubActivity.this).load(uri).fit().centerCrop().into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void setImage() {

        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                // Reference to an image file in Firebase Storage
                if (event.getImgId() != null && !event.getImgId().equals("")) {
                    System.out.println("Attaching Image");
                    mStorage.child(event.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(getApplicationContext()).load(uri).fit().centerCrop().into(imageView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }


    public boolean saveImageToInternalStorage(Bitmap image) {

        try {
            FileOutputStream fos = getBaseContext().openFileOutput("profileImage.png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    /*
    public void getClubEvents() {

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray events = response.getJSONArray("events");
                            clubEventName = new String[events.length()];
                            clubEventLocation = new String[events.length()];
                            clubEventDescription = new String[events.length()];
                            clubEventDate = new String[events.length()];
                            clubEventId = new Integer[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            List<Event> eventItems = new ArrayList<Event>();
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                String location = event.getString("location");
                                String startDate = event.getString("startDate");
                                String endDate = event.getString("endDate");
                                Integer id = event.getInt("eventId");
                                String sid = id.toString();
                                JSONArray eventTags = new JSONArray();
                                Event temp = new Event(sid, name, description, location, startDate, endDate, eventTags);
                                eventItems.add(temp);
                                clubEventName.add(name);

                                clubEventId.add(id);
                                System.out.print("Event Name " + name + ", ");

                            }
                            lv = (ListView) findViewById(R.id.list_clubEvents);

                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    PrivateClubActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    clubEventName
                            );


                            //esAdapter = new EventAdapter(getApplicationContext(), R.layout.event_row, eventItems);
                            //lv.setAdapter(lvAdapter);

                            layoutinflater = getLayoutInflater();
                            TextView head = (TextView) View.inflate(PrivateClubActivity.this, R.layout.item_header, null);
                            head.setText("Upcoming Events");
                            lv.addHeaderView(head);

                            lv.setAdapter(lvAdapter);

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
                }
        );
        Volley.newRequestQueue(this).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }
    */

    public void getClubMembers() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray members = response.getJSONArray("members");
                            membersName = new String[members.length()];
                            for (int i = 0; i < members.length(); i++) {
                                JSONObject member = members.getJSONObject(i);
                                String name = member.getString("name");
                                membersName[i] = name;
                            }

                            lv = (ListView) findViewById(R.id.list_clubMembers);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    PrivateClubActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    membersName
                            );

                            layoutinflater = getLayoutInflater();
                            TextView head = (TextView) View.inflate(PrivateClubActivity.this, R.layout.item_header, null);
                            head.setText("Club Members");
                            lv.addHeaderView(head);

                            lv.setAdapter(lvAdapter);

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
                }
        );
        Volley.newRequestQueue(this).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }


}
