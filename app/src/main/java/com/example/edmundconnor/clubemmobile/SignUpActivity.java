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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {
    EditText nameIn, emailIn, gradYearIn, pwIn, pwReIn;
    Button signUp;
    TextView txt;

    String url = "https://clubs-jhu.herokuapp.com/clubs/api/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameIn = (EditText)findViewById(R.id.nameSignUp);
        emailIn = (EditText)findViewById(R.id.emailSignUp);
        gradYearIn = (EditText)findViewById(R.id.gradYearSignUp);
        pwIn = (EditText)findViewById(R.id.passwordSignUp);
        pwReIn = (EditText) findViewById(R.id.password1SignUp);
        txt = (TextView) findViewById(R.id.textview1);

        signUp = (Button)findViewById(R.id.buttonSignUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameIn.getText().toString();
                final String email = emailIn.getText().toString();
                final String password = pwIn.getText().toString();
                final String passwordRe = pwReIn.getText().toString();
                final String gradYear = gradYearIn.getText().toString();
                Log.d("params", name + " " + email + " " + password + " " + passwordRe + " " + gradYear);

                if (!password.equals(passwordRe)) {
                    txt.setVisibility(View.VISIBLE);
                    return;
                }

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("name", name);
                    jsonBody.put("year", Integer.parseInt(gradYear));
                    jsonBody.put("email", email);
                    jsonBody.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response", "passed");
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                txt.setText("Invalid Input.");
                                Log.d("Error", "Error: " + error.getMessage());
                            }
                        }
                ) {
                };
                Volley.newRequestQueue(getApplicationContext()).add(postRequest);
            }
        });
    }
}
