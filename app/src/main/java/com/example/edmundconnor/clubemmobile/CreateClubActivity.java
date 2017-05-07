package com.example.edmundconnor.clubemmobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.edmundconnor.clubemmobile.LoginActivity.ID;

public class CreateClubActivity extends AppCompatActivity {

    Button createClub;
    Button cancelClub;
    EditText clubName;
    EditText clubDescription;
    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/createClub";
    private String id;
    private List<String> clubTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        clubTags = new ArrayList<String>();
        clubTags.add("Arts");
        clubTags.add("Religious");
        clubTags.add("Music");
        clubTags.add("Cultural");

        Intent intent = getIntent();
        id = intent.getStringExtra(LoginActivity.ID);

        createClub = (Button) findViewById(R.id.create_club_button);
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

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", clubName.getText());
            jsonBody.put("description", clubDescription.getText());
            jsonBody.put("userID", id);
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
