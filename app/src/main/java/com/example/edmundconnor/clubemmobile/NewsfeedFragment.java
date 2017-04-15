package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.edmundconnor.clubemmobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NewsfeedFragment extends Fragment {

    String url1 = "https://clubs-jhu.herokuapp.com/clubs/api/publicEvents";
    String url2 = "https://clubs-jhu.herokuapp.com/clubs/api/trendingEvents";
    String url3 = "https://clubs-jhu.herokuapp.com/clubs/api/5/suggestedEvents";
    String url4 = "https://clubs-jhu.herokuapp.com/clubs/api/5/suggestedClubs";
    String url5 = "https://clubs-jhu.herokuapp.com/clubs/api/trendingClubs";
    private String[] publicEventNames;
    private String[] publicEventDescriptions;
    private String[] trendingEventNames;
    private String[] tredingEventDescriptions;
    private String[] suggestedEventNames;
    private String[] suggestedEventDescriptions;
    private String[] trendingClubNames;
    private String[] tredingClubDescriptions;
    private String[] suggestedClubNames;
    private String[] suggestedClubDescriptions;
    private JSONObject[] jsonArray;
    private ListView lv;
    private LayoutInflater layoutinflater;

    public NewsfeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("NewsfeedFragment");

        publicEventNames = new String[1];
        publicEventNames[0] = "empty";

        getUpcomingPublicEvents();
        getSuggestedEvents();
        getTrendingEvents();
        getSuggestedClubs();
        getTrendingClubs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    public void getUpcomingPublicEvents() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(publicEventNames == null);
                        try {
                            JSONArray events = response.getJSONArray("events");
                            publicEventNames = new String[events.length()];
                            publicEventDescriptions = new String[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                //Integer id = event.getInt("eventId");
                                publicEventNames[i] = name;

                                publicEventDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_upcomingPublicEvents);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    publicEventNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            //TextView head = (TextView) getActivity().findViewById(R.id.item_header);
                            //head.setText("@strings/upcomingPE");
                            ViewGroup header = (ViewGroup)layoutinflater.inflate(R.layout.item_header,lv,false);
                            lv.addHeaderView(header);

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
        Volley.newRequestQueue(getActivity()).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }

    public void getTrendingEvents() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(trendingEventNames == null);
                        try {
                            JSONArray events = response.getJSONArray("events");
                            trendingEventNames = new String[events.length()];
                            tredingEventDescriptions = new String[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                //Integer id = event.getInt("eventId");
                                trendingEventNames[i] = name;

                                tredingEventDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_trendingEvents);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    trendingEventNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            //TextView head = (TextView) getActivity().findViewById(R.id.item_header);
                            //head.setText("@strings/upcomingPE");
                            ViewGroup header = (ViewGroup)layoutinflater.inflate(R.layout.item_header,lv,false);
                            lv.addHeaderView(header);

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
        Volley.newRequestQueue(getActivity()).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }

    public void getSuggestedEvents() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url3, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(suggestedEventNames == null);
                        try {
                            JSONArray events = response.getJSONArray("events");
                            suggestedEventNames = new String[events.length()];
                            suggestedEventDescriptions = new String[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                //Integer id = event.getInt("eventId");
                                suggestedEventNames[i] = name;

                                suggestedEventDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_suggestedEvents);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    suggestedEventNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            //TextView head = (TextView) getActivity().findViewById(R.id.item_header);
                            //head.setText("@strings/upcomingPE");
                            ViewGroup header = (ViewGroup)layoutinflater.inflate(R.layout.item_header,lv,false);
                            lv.addHeaderView(header);

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
        Volley.newRequestQueue(getActivity()).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }

    public void getTrendingClubs() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url5, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(trendingClubNames == null);
                        try {
                            JSONArray events = response.getJSONArray("clubs");
                            trendingClubNames = new String[events.length()];
                            tredingClubDescriptions = new String[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                //Integer id = event.getInt("eventId");
                                trendingClubNames[i] = name;

                                tredingClubDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_trendingClubs);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    trendingClubNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            //TextView head = (TextView) getActivity().findViewById(R.id.item_header);
                            //head.setText("@strings/upcomingPE");
                            ViewGroup header = (ViewGroup)layoutinflater.inflate(R.layout.item_header,lv,false);
                            lv.addHeaderView(header);

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
        Volley.newRequestQueue(getActivity()).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }

    public void getSuggestedClubs() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url4, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(suggestedClubNames == null);
                        try {
                            JSONArray events = response.getJSONArray("clubs");
                            suggestedClubNames = new String[events.length()];
                            suggestedClubDescriptions = new String[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                //Integer id = event.getInt("eventId");
                                suggestedClubNames[i] = name;

                                suggestedClubDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_suggestedClubs);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    suggestedClubNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            //TextView head = (TextView) getActivity().findViewById(R.id.item_header);
                            //head.setText("@strings/upcomingPE");
                            ViewGroup header = (ViewGroup)layoutinflater.inflate(R.layout.item_header,lv,false);
                            lv.addHeaderView(header);

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
        Volley.newRequestQueue(getActivity()).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }
}
