package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static com.example.edmundconnor.clubemmobile.LoginActivity.ID;
import static com.example.edmundconnor.clubemmobile.NewsfeedFragment.eventID;

public class PublicClubActivity extends AppCompatActivity {

    String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    String url1, url2, url3;
    String[] clubEventLocation;
    String[] clubEventDescription;
    String[] clubEventDate;
    String[] membersName;
    String[] clubAdmin;
    private ArrayList<String> clubEventName;
    private ArrayList<String> clubEventId;
    private static String club_desc, club_name;
    private ListView lv;
    private ListView lav;
    private LayoutInflater layoutinflater;
    private Context context;
    private Button apply;
    private String cid;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<String> keys = new ArrayList<String>();
    private ListView clubEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_club);
        Intent intent = getIntent();
        clubEventId = new ArrayList<String>();
        clubEventName = new ArrayList<String>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        cid = intent.getStringExtra(AllClubsFragment.clubID);
        String uid = intent.getStringExtra(ID);
        club_desc = intent.getStringExtra(AllClubsFragment.clubDESC);
        club_name = intent.getStringExtra(AllClubsFragment.clubNAME);
        System.out.print("Club ID: " + cid);

        url1 = url + cid + "/events";
        url2 = url + cid + "/members";
        url3 = url + cid + "/apply";

        apply = (Button) findViewById(R.id.apply_button);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               applyToClub();
            }
        });

        setTitle(club_name);
        TextView description = (TextView) findViewById(R.id.public_club_description);
        description.setText(club_desc);

        //getClubEvents();
        getClubMembers();

        ListView clubEvents = (ListView) findViewById(R.id.list_publicClubEvents);

        clubEvents.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Event2Activity.class);
                String cid = clubEventId.get(position);
                intent.putExtra(eventID, cid);
                startActivity(intent);
            }
        });

        populateList();
    }

    public void applyToClub() {
        JSONObject jsonBody = new JSONObject();
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String uid = myPrefs.getString("ID", "6");
        try {
            jsonBody.put("userID", uid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url3, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
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
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
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
                        clubEventId.add(value.getId());
                        localEvents.add(value);
                        localEventNames.add(value.getName());
                    }
                }

                lv = (ListView) findViewById(R.id.list_publicClubEvents);

                System.out.print("************");

                ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                        PublicClubActivity.this,
                        android.R.layout.simple_list_item_1,
                        localEventNames.toArray()
                );
                layoutinflater = getLayoutInflater();
                TextView head = (TextView) View.inflate(PublicClubActivity.this, R.layout.item_header, null);
                head.setText("Upcoming Events");
                lv.addHeaderView(head);
                lv.setAdapter(lvAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


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

                            lv = (ListView) findViewById(R.id.list_publicClubMembers);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    PublicClubActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    membersName
                            );

                            layoutinflater = getLayoutInflater();
                            TextView head = (TextView) View.inflate(PublicClubActivity.this, R.layout.item_header, null);
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


    public void getClubAdmin() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray members = response.getJSONArray("members");
                            clubAdmin = new String[members.length()];
                            for (int i = 0; i < members.length(); i++) {
                                JSONObject member = members.getJSONObject(i);
                                String name = member.getString("name");
                                String role = member.getString("role");
                                if (!role.equalsIgnoreCase("member")) {
                                    clubAdmin[i] = name;
                                }
                            }

                            lv = (ListView) findViewById(R.id.list_publicClubAdmin);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    PublicClubActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    clubAdmin
                            );

                            layoutinflater = getLayoutInflater();
                            TextView head = (TextView) View.inflate(PublicClubActivity.this, R.layout.item_header, null);
                            head.setText("Club Administrators");
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
