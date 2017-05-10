package com.example.edmundconnor.clubemmobile;

import android.content.Intent;
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
import com.example.edmundconnor.clubemmobile.R;

import org.json.JSONException;
import org.json.JSONObject;
public class ShowProfile extends AppCompatActivity {
    String id;
    TextView name, email, year;
    Button edit;
    public static final String ID = "com.example.edmundconnor.clubemmobile.ID";

    String url = "https://clubs-jhu.herokuapp.com/clubs/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        name = (TextView) findViewById(R.id.t2);
        email = (TextView) findViewById(R.id.t3);
        year = (TextView) findViewById(R.id.t4);
        edit = (Button) findViewById(R.id.b1);

        Intent intent = getIntent();
        id = intent.getStringExtra(LoginActivity.ID);
        Integer userId = Integer.parseInt(id);
        final String urlEnd = url + "/user/" + id;
        System.out.println(urlEnd);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlEnd, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", response.toString());
                            String name_ = response.get("name").toString();
                            String email_ = response.get("email").toString();
                            String year_ = response.get("year").toString();
                            name.setText(name_);
                            email.setText(email_);
                            year.setText(year_);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        Volley.newRequestQueue(getApplicationContext()).add(getRequest);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra(ID, id);
                startActivity(intent);
            }
        });
    }
}
