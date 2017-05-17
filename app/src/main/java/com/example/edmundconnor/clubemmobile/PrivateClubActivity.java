package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<String> keys = new ArrayList<String>();
    private ListView clubEvents;

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

        clubEventName = new ArrayList<String>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        setTitle(club_name);
        TextView description = (TextView) findViewById(R.id.club_description);
        description.setText(club_desc);

        url1 = url + cid + "/events";
        url2 = url + cid + "/members";
        //getClubEvents();
        getClubMembers();

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
