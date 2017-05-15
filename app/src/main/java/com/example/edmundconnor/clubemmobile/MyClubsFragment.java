package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.edmundconnor.clubemmobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyClubsFragment extends Fragment {

    String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    String urlend = "/userClubs";
    private String[] clubNames;
    private String[] clubDescriptions;
    private Integer[] clubId;
    private JSONObject[] jsonArray;
    private ListView lv;
    private LayoutInflater layoutinflater;
    public static final String clubID = "com.example.edmundconnor.clubemmobile.clubID";
    public static final String clubNAME = "com.example.edmundconnor.clubemmobile.clubNAME";
    public static final String clubDESC = "com.example.edmundconnor.clubemmobile.clubDESC";

    public MyClubsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("My Clubs");

        clubNames = new String[1];
        clubNames[0] = "empty";

        Intent intent = getActivity().getIntent();
        String id = intent.getStringExtra(LoginActivity.ID);
        String getUrl = url + id + urlend;
        //System.out.println(getUrl);

        getMyClubs(getUrl);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myclub, container, false);

        ListView lv = (ListView) view.findViewById(R.id.list_myClubs);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //System.out.println("CHECK " + position + " " + clubId[position]);
                Intent intent = new Intent(getActivity(), PrivateClubActivity.class);
                System.out.print("CHECK " + position + " " +clubId[position]);
                Integer cid = clubId[position];
                String club_id = cid.toString();
                intent.putExtra(clubID, club_id);
                intent.putExtra(clubNAME, clubNames[position]);
                intent.putExtra(clubDESC, clubDescriptions[position]);
                startActivity(intent);
            }
        });

        return view;
    }

    public void getMyClubs(String getUrl) {

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println(clubNames == null);

                        try {
                            JSONArray clubs = response.getJSONArray("clubs");
                            //System.out.println(clubs);
                            clubNames = new String[clubs.length()];
                            clubDescriptions = new String[clubs.length()];
                            clubId = new Integer[clubs.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < clubs.length(); i++) {
                                JSONObject club = clubs.getJSONObject(i);
                                //jsonArray[i] = club;
                                //Log.i("Club", club.toString());
                                String name = club.getString("name");
                                String description = club.getString("description");
                                Integer id = club.getInt("clubId");
                                clubNames[i] = name;
                                clubId[i] = id;

                                clubDescriptions[i] = description;

                            }
                            lv = (ListView) getView().findViewById(R.id.list_myClubs);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    clubNames
                            );

                            /*
                            layoutinflater = getActivity().getLayoutInflater();
                            ViewGroup header = (ViewGroup)layoutinflater.inflate(R.layout.item_header,lv,false);
                            lv.addHeaderView(header);
                            */
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
