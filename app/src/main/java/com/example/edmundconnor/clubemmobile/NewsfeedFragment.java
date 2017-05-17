package com.example.edmundconnor.clubemmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.edmundconnor.clubemmobile.AllClubsFragment.clubDESC;
import static com.example.edmundconnor.clubemmobile.AllClubsFragment.clubID;
import static com.example.edmundconnor.clubemmobile.AllClubsFragment.clubNAME;
import static com.example.edmundconnor.clubemmobile.LoginActivity.ID;


public class NewsfeedFragment extends Fragment {

    String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    String url1 = "https://clubs-jhu.herokuapp.com/clubs/api/publicEvents";
    String url2 = "https://clubs-jhu.herokuapp.com/clubs/api/trendingEvents";
    static String url3;
    static String url4;
    String url5 = "https://clubs-jhu.herokuapp.com/clubs/api/trendingClubs";
    private String[] publicEventNames;
    private String[] publicEventDescriptions;
    private Integer[] publicEventIds;
    private String[] trendingEventNames;
    private String[] tredingEventDescriptions;
    private Integer[] trendingEventIds;
    private String[] suggestedEventNames;
    private String[] suggestedEventDescriptions;
    private Integer[] suggestedEventIds;
    private String[] trendingClubNames;
    private String[] tredingClubDescriptions;
    private Integer[] trendingClubIds;
    private String[] suggestedClubNames;
    private String[] suggestedClubDescriptions;
    private Integer[] suggestedClubIds;
    protected static EventAdapter seAdapter;
    private ListView lv;
    private LayoutInflater layoutinflater;
    public static final String eventID = "com.example.edmundconnor.clubemmobile.eventID";
    public static final String eventNAME = "com.example.edmundconnor.clubemmobile.eventNAME";
    public static final String eventDESC = "com.example.edmundconnor.clubemmobile.eventDESC";

    public NewsfeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("NewsfeedFragment");
        String uid = getActivity().getIntent().getStringExtra(ID);
        url3 = url + uid + "/suggestedEvents";
        url4 = url + uid + "/suggestedClubs";

        getUpcomingPublicEvents();
        getTrendingEvents();
        getSuggestedEvents();
        getSuggestedClubs();
        getTrendingClubs();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        ListView publicEventsLV = (ListView) view.findViewById(R.id.list_upcomingPublicEvents);

        publicEventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                position--;
                Integer cid = publicEventIds[position];
                System.out.println("**************** " + cid);
                String event_id = Integer.toString(cid);
                System.out.print("Event Id " + event_id);
                intent.putExtra(eventID, event_id);
                intent.putExtra(eventNAME, publicEventNames[position]);
                intent.putExtra(eventDESC, publicEventDescriptions[position]);
                startActivity(intent);
            }
        });

        ListView tredingEventsLV = (ListView) view.findViewById(R.id.list_trendingEvents);

        tredingEventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                position--; //NEEDED for error handling issue idk
                Integer cid = trendingEventIds[position];
                String event_id = Integer.toString(cid);
                System.out.println("**************** " + cid);
                intent.putExtra(eventID, event_id);
                intent.putExtra(eventNAME, trendingClubNames[position]);
                intent.putExtra(eventDESC, tredingClubDescriptions[position]);
                startActivity(intent);
            }
        });

        ListView suggestedEventsLV = (ListView) view.findViewById(R.id.list_suggestedEvents);

        suggestedEventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                position--; //NEEDED because array adapter starts at 1
                Integer cid = suggestedEventIds[position];
                String event_id = Integer.toString(cid);
                System.out.print("Event Id " + event_id);
                intent.putExtra(eventID, event_id);
                intent.putExtra(eventNAME, suggestedEventNames[position]);
                intent.putExtra(eventDESC, suggestedEventDescriptions[position]);
                startActivity(intent);
            }
        });

        ListView suggestedClubsLV = (ListView) view.findViewById(R.id.list_suggestedClubs);

        suggestedClubsLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), PublicClubActivity.class);
                position--;
                Integer cid = suggestedClubIds[position];
                String club_id = Integer.toString(cid);
                intent.putExtra(clubID, club_id);
                intent.putExtra(clubNAME, suggestedClubNames[position]);
                intent.putExtra(clubDESC, suggestedClubDescriptions[position]);
                startActivity(intent);
            }
        });

        ListView trendingClubsLV = (ListView) view.findViewById(R.id.list_trendingClubs);

        trendingClubsLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), PublicClubActivity.class);
                position--;
                Integer cid = trendingClubIds[position];
                String club_id = Integer.toString(cid);
                intent.putExtra(clubID, club_id);
                intent.putExtra(clubNAME, trendingClubNames[position]);
                intent.putExtra(clubDESC, tredingClubDescriptions[position]);
                startActivity(intent);
            }
        });

        return view;
    }

    public void getUpcomingPublicEvents() {
        System.out.println(url1);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(publicEventNames == null);
                        try {
                            System.out.print("HERE 1, ");
                            JSONArray events = response.getJSONArray("events");
                            publicEventNames = new String[events.length()];
                            publicEventDescriptions = new String[events.length()];
                            publicEventIds = new Integer[events.length()];
                            List<Event> publicEvents = new ArrayList<Event>();
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());
                                System.out.print("HERE i, ");
                                String name = event.getString("name");
                                String description = event.getString("description");
                                Integer id = event.getInt("eventId");
                                String date = event.getString("startDate");
                                String location = event.getString("location");
                                String endDate = event.getString("endDate");
                                JSONArray tags = (JSONArray) event.get("tags");

                                publicEventNames[i] = name;
                                publicEventIds[i] = event.getInt("eventId");
                                //System.out.print("Event ID " + publicEventIds[i]);
                                publicEventDescriptions[i] = description;
                                Event temp = new Event(id.toString(), name, description, location, date, endDate, tags);
                                publicEvents.add(temp);

                            }
                            System.out.print("HERE 2, ");
                            lv = (ListView) getView().findViewById(R.id.list_upcomingPublicEvents);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    publicEventNames
                            );

                            //EventAdapter eventAdapter = new EventAdapter(getActivity(), R.layout.event_row, publicEvents);
                            //lv.setAdapter(eventAdapter);

                            layoutinflater = getActivity().getLayoutInflater();
                            TextView head = (TextView) View.inflate(getActivity(), R.layout.item_header, null);
                            head.setText("Upcoming Public Events");
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
                            trendingEventIds = new Integer[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                Integer id = event.getInt("eventId");
                                trendingEventNames[i] = name;
                                trendingEventIds[i] = id;
                                tredingEventDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_trendingEvents);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    trendingEventNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            TextView head = (TextView) View.inflate(getActivity(), R.layout.item_header, null);
                            head.setText("Trending Events");
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
        Volley.newRequestQueue(getActivity()).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }

    public void getSuggestedEvents() {
        System.out.println("Suggested" + url3);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url3, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Suggested" + suggestedEventNames == null);
                        try {
                            JSONArray events = response.getJSONArray("events");
                            suggestedEventNames = new String[events.length()];
                            suggestedEventDescriptions = new String[events.length()];
                            suggestedEventIds = new Integer[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                Integer id = event.getInt("eventId");
                                suggestedEventNames[i] = name;
                                suggestedEventIds[i] = id;
                                suggestedEventDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_suggestedEvents);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    suggestedEventNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            TextView head = (TextView) View.inflate(getActivity(), R.layout.item_header, null);
                            head.setText("Suggested Events");
                            lv.addHeaderView(head);
                            System.out.println("events:" + events.length());
                            if (events.length() == 0) {
                                lv.setVisibility(View.GONE);
                            } else {
                                lv.setAdapter(lvAdapter);
                            }

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
                            trendingClubIds = new Integer[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                Integer id = event.getInt("clubId");
                                trendingClubNames[i] = name;
                                trendingClubIds[i] = id;
                                tredingClubDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_trendingClubs);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    trendingClubNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            TextView head = (TextView) View.inflate(getActivity(), R.layout.item_header, null);
                            head.setText("Trending Clubs");
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
                            suggestedClubIds = new Integer[events.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Events", club.toString());

                                String name = event.getString("name");
                                String description = event.getString("description");
                                Integer id = event.getInt("clubId");
                                suggestedClubNames[i] = name;
                                suggestedClubIds[i] = id;
                                suggestedClubDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_suggestedClubs);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    suggestedClubNames
                            );

                            layoutinflater = getActivity().getLayoutInflater();
                            TextView head = (TextView) View.inflate(getActivity(), R.layout.item_header, null);
                            head.setText("Suggested Clubs");
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
        Volley.newRequestQueue(getActivity()).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }
}
