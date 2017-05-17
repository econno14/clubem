package com.example.edmundconnor.clubemmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity  {

    SharedPreferences myPrefs;
    GoogleApiClient mGoogleApi;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    Button login,signUp;
    EditText editEmail,editPw;
    String url = "https://clubs-jhu.herokuapp.com/clubs/api/login";
    TextView txt;
    public static final String ID = "com.example.edmundconnor.clubemmobile.ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Context context = getApplicationContext();
        myPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        mAuth = FirebaseAuth.getInstance();

        login = (Button)findViewById(R.id.login);
        editEmail = (EditText)findViewById(R.id.email);
        editPw = (EditText)findViewById(R.id.pw);
        txt = (TextView) findViewById(R.id.textview2);

        signUp = (Button)findViewById(R.id.signup);
        txt.setVisibility(View.INVISIBLE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = editPw.getText().toString();
                final String email = editEmail.getText().toString();
                System.out.println(password + email );
                Log.d("params", email + " " + password);

                if (password == null || email == null) {
                    txt.setVisibility(View.VISIBLE);
                    txt.setText("Please enter valid input.");
                    return;
                }

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", email);
                    jsonBody.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("response", response.toString());
                                    String id = response.get("userID").toString();
                                    Integer userId = Integer.parseInt(id);
                                    System.out.println(userId);
                                    txt.setVisibility(View.INVISIBLE);
                                    signInTapped();
                                    Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                                    intent.putExtra(ID, id);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                txt.setVisibility(View.VISIBLE);
                                txt.setText("Invalid login.");
                                Log.d("Error", "Error: " + error.getMessage());
                            }
                        }
                ) {
                };
                Volley.newRequestQueue(getApplicationContext()).add(putRequest);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void signInTapped() {

        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(editEmail.getText().toString(), editPw.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.putBoolean("logged_in", true);
                            editor.apply();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Task not successful",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Required.");
            valid = false;
        } else {
            editEmail.setError(null);
        }

        String password = editPw.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editPw.setError("Required.");
            valid = false;
        } else {
            editPw.setError(null);
        }

        return valid;
    }
}