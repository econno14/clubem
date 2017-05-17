package com.example.edmundconnor.clubemmobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateClubActivity extends AppCompatActivity {

    Button createClub;
    Button cancelClub;
    EditText clubName;
    EditText clubDescription;
    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/createClub";
    private String id;
    private JSONArray clubTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        clubTags = new JSONArray();
        clubTags.put("Religious");
        clubTags.put("Cultural");

        Intent intent = getIntent();
        id = intent.getStringExtra(LoginActivity.ID);

        createClub = (Button) findViewById(R.id.create_new_club);
        cancelClub = (Button) findViewById(R.id.cancel_new_club);
        clubName = (EditText) findViewById(R.id.new_club_name);
        clubDescription = (EditText) findViewById(R.id.new_club_description);

        createClub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                createClub();
            }
        });
        cancelClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClub();
            }
        });

    }

    public void createClub() {
        Integer uid = Integer.parseInt(id);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", clubName.getText().toString());
            jsonBody.put("description", clubDescription.getText().toString());
            jsonBody.put("userID", uid);
            jsonBody.put("types", clubTags);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Intent intent = new Intent(CreateClubActivity.this, NavigationActivity.class);
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
        Volley.newRequestQueue(getApplicationContext()).add(putRequest);
    }

    public void cancelClub() {
        // Confirmation to delete:
        AlertDialog deleteDialogue = new AlertDialog.Builder(this)
                .setTitle("Cancel")
                .setMessage("Are you sure you cancel this club?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete from database
                        //dbAdapt.removeItem(did);
                        Intent intent = new Intent(CreateClubActivity.this, NavigationActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
