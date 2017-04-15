package com.example.edmundconnor.clubemmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.edmundconnor.clubemmobile.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("ProfileActivity");
        setContentView(R.layout.activity_profile);
    }
}
