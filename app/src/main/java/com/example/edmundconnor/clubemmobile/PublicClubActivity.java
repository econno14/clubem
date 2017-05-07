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

import static com.example.edmundconnor.clubemmobile.LoginActivity.ID;

public class PublicClubActivity extends AppCompatActivity {

    String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    String url1, url2, url3;
    String[] clubEventName;
    String[] clubEventLocation;
    String[] clubEventDescription;
    String[] clubEventDate;
    String[] membersName;
    String[] clubAdmin;
    private static String club_desc, club_name;
    private ListView lv;
    private ListView lav;
    private LayoutInflater layoutinflater;
    private Context context;
    private Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_club);
        Intent intent = getIntent();

        String cid = intent.getStringExtra(AllClubsFragment.clubID);
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

        getClubEvents();
        getClubMembers();
    }

    public void applyToClub() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userID", ID);
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
                            lv = (ListView) findViewById(R.id.list_publicClubEvents);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    PublicClubActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    clubEventName
                            );

                            layoutinflater = getLayoutInflater();
                            TextView head = (TextView) View.inflate(PublicClubActivity.this, R.layout.item_header, null);
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
