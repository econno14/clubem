package com.example.edmundconnor.clubemmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.edmundconnor.clubemmobile.LoginActivity.ID;
import static com.example.edmundconnor.clubemmobile.R.id.nav_feed;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String id1;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference currentUserRef;
    TextView navNameTextView;
    ImageView navProfileImg;


    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/user/";
    private String userName;

    private final Map<String, String> profile = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        id1 = intent.getStringExtra(LoginActivity.ID);
        //Integer userId = Integer.parseInt(id1);

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("ID", id1);
        editor.apply();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserRef = database.getReference().child("users").child(currentUser.getUid());

        System.out.println(id1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        url = url + id1;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View headerView = navigationView.getHeaderView(0);
        navNameTextView = (TextView) headerView.findViewById(R.id.nav_profile_name);

        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_feed);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile user = dataSnapshot.getValue(UserProfile.class);
                navNameTextView.setText(user.getName());

                try {
                    String imgId = user.getImagePath();
                    if (imgId != null) {
                        final ImageView navHeader = (ImageView) headerView.findViewById(R.id.nav_header_image);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference sRef = storage.getReference();
                        sRef.child(user.getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(NavigationActivity.this).load(uri).fit().centerCrop().into(navHeader);
                            }
                        });
                    }
                } catch (IllegalArgumentException ex) {
                    System.out.println("Please select and Image in from a profile");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
//        currentUserRef.addValueEventListener(userListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProfile();
        setProfileImage();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(NavigationActivity.this, ShowProfile.class);
            intent.putExtra(ID, id1);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id1);
        Fragment fragment = null;
        //fragment.setArguments(bundle);

        switch (id) {
            case nav_feed:
                fragment = new NewsfeedFragment();
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle("Newsfeed");
                break;
            case R.id.nav_calendar:
                fragment = new CalendarFragment();
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle("Calendar");
                break;
            case R.id.nav_myClubs:
                fragment = new MyClubsFragment();
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle("My Clubs");
                break;
            case R.id.nav_allClubs:
                fragment = new AllClubsFragment();
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle("All Clubs");
                break;
            default:
                fragment = new NewsfeedFragment();
                fragment.setArguments(bundle);
                getSupportActionBar().setTitle("Newsfeed");
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_navigation, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);
        return true;
    }

    public void setProfile() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response);
                            TextView navName = (TextView) findViewById(R.id.nav_profile_name);
                            navName.setText(response.getString("name"));
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
        Volley.newRequestQueue(this).add(getRequest);
        //Log.i("Club Name here", jsonArray[0].toString());
    }


    public Bitmap getThumbnail(String filename) {

        Bitmap thumbnail = null;

        if (thumbnail == null) {
            try {
                File filePath = getBaseContext().getFileStreamPath(filename);
                FileInputStream fi = new FileInputStream(filePath);
                thumbnail = BitmapFactory.decodeStream(fi);
                final ImageView imageView = (ImageView) findViewById(R.id.nav_header_image);
                imageView.setImageBitmap(thumbnail);
                imageView.setRotation(-90);
            } catch (Exception ex) {

            }
        }
        return thumbnail;
    }

    public void setProfileImage() {
        Bitmap bitmap = getThumbnail("profileImage.png");
    }
}
