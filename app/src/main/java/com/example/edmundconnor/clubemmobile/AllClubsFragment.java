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
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class AllClubsFragment extends Fragment {

    String url = "https://clubs-jhu.herokuapp.com/clubs/api/allClubs";
    private String[] clubNames;
    private String[] clubDescriptions;
    private int [] clubIds;
    private JSONObject[] jsonArray;
    private ListView lv;
    public static final String clubID = "com.example.edmundconnor.clubemmobile.clubID";
    public static final String clubNAME = "com.example.edmundconnor.clubemmobile.clubNAME";
    public static final String clubDESC = "com.example.edmundconnor.clubemmobile.clubDESC";

    public AllClubsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("All Clubs");
        clubNames = new String[1];
        clubNames[0] = "empty";

        getAllClubs();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allclub, container, false);

        ListView lv = (ListView) view.findViewById(R.id.list_allClubs);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), PublicClubActivity.class);
                Integer cid = clubIds[position];
                String club_id = cid.toString();
                intent.putExtra(clubID, club_id);
                intent.putExtra(clubNAME, clubNames[position]);
                intent.putExtra(clubDESC, clubDescriptions[position]);
                startActivity(intent);
            }
        });

        return view;
    }

    public void getAllClubs() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(clubNames == null);
                        try {
                            JSONArray clubs = response.getJSONArray("clubs");
                            clubNames = new String[clubs.length()];
                            clubDescriptions = new String[clubs.length()];
                            clubIds = new int[clubs.length()];
                            //jsonArray = new JSONObject[clubs.length()];
                            for (int i = 0; i < clubs.length(); i++) {
                                JSONObject club = clubs.getJSONObject(i);
                                //jsonArray[i] = club;
                                Log.i("Club", club.toString());

                                String name = club.getString("name");
                                String description = club.getString("description");
                                Integer id = club.getInt("clubId");
                                clubNames[i] = name;
                                clubIds[i] = id;
                                clubDescriptions[i] = description;


                            }
                            lv = (ListView) getView().findViewById(R.id.list_allClubs);
                            ArrayAdapter<String> lvAdapter = new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    clubNames
                            );
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
