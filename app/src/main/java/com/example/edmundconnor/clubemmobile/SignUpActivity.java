package com.example.edmundconnor.clubemmobile;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    EditText nameIn, emailIn, gradYearIn, pwIn, pwReIn;
    Button signUp;
    TextView txt;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    SharedPreferences myPrefs;

    /** Firebase References */
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    GoogleApiClient mGoogleApi;

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
        Button cancel = (Button) findViewById(R.id.buttonCancel);

        database = FirebaseDatabase.getInstance();
        usersReference = database.getReference().child("users");

        Context context = getApplicationContext();
        myPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApi = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

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

                if (gradYear.equals("")) {
                    txt.setVisibility(View.VISIBLE);
                    txt.setText("Enter valid year.");
                    return;
                }

                if (Integer.parseInt(gradYear) < 1990 || Integer.parseInt(gradYear) > 2100) {
                    txt.setVisibility(View.VISIBLE);
                    txt.setText("Enter valid year.");
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

                signUp(email, password);

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

        mAuth = FirebaseAuth.getInstance();
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("RegistrationActivity", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            final String name = nameIn.getText().toString();
                            final String email = emailIn.getText().toString();
                            final String password = pwIn.getText().toString();
                            final String gradYear = gradYearIn.getText().toString();

                            createUser(user.getUid(), name, email, gradYear, password);
                            user = mAuth.getCurrentUser();

                        } else {
                            Log.w("RegistrationActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Signup failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUser(String uid, String name, String email, String year, String password) {
        UserProfile user = new UserProfile(uid, year, email, year);

        usersReference.child(uid).setValue(user.toMap());
        signIn(email, password);

    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.putBoolean("logged_in", true);
                            editor.apply();

                            Intent intent = new Intent(SignUpActivity.this, NavigationActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(getApplicationContext(), "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            //statusTextView.setText("Hello, " + acct.getDisplayName());

            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putBoolean("logged_in", true);
            editor.apply();

            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
            finish();

        } else if (result.getStatus().isInterrupted()) {
            // DO NOTHING!
        } else {
            Toast.makeText(getApplicationContext(), "Account not registered.", Toast.LENGTH_SHORT).show();
        }
    }
}
