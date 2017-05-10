package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrivateClubActivity extends AppCompatActivity {

    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    private String url1, url2;
    private String[] clubEventName;
    private String[] clubEventLocation;
    private String[] clubEventDescription;
    private String[] clubEventDate;
    private String[] membersName;
    private ListView lv;
    private LayoutInflater layoutinflater;
    private Context context;
    private int clubId;
    private static String club_desc;
    private static String club_name;
    private Button createEvent;
    private String cid;
    public static final String clubID = "com.example.edmundconnor.clubemmobile.clubID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_club);
        Intent intent = getIntent();
        cid = intent.getStringExtra(MyClubsFragment.clubID);
        String uid = intent.getStringExtra(LoginActivity.ID);
        club_desc = intent.getStringExtra(MyClubsFragment.clubDESC);
        club_name = intent.getStringExtra(MyClubsFragment.clubNAME);
        System.out.print("Club ID: " + cid);

        setTitle(club_name);
        TextView description = (TextView) findViewById(R.id.club_description);
        description.setText(club_desc);

        url1 = url + cid + "/events";
        url2 = url + cid + "/members";
        getClubEvents();
        getClubMembers();

        createEvent = (Button) findViewById(R.id.create_event_button);
        createEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PrivateClubActivity.this, CreateEventActivity.class);
                intent.putExtra(clubID, cid);
                startActivity(intent);
            }
        });
    }

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
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                String location = event.getString("location");
                                String startDate = event.getString("startDate");
                                //Integer id = event.getInt("eventId");
                                clubEventName[i] = name;
                                clubEventLocation[i] = location;
                                clubEventDescription[i] = description;
                                clubEventDate[i] = startDate;

                            }
                            lv = (ListView) findViewById(R.id.list_clubEvents);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    PrivateClubActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    clubEventName
                            );

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
