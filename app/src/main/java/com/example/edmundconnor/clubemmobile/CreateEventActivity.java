package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarException;

import static android.provider.Telephony.MmsSms.PendingMessages.ERROR_TYPE;
import static com.example.edmundconnor.clubemmobile.LoginActivity.ID;

public class CreateEventActivity extends AppCompatActivity {

    protected FirebaseDatabase database;
    protected DatabaseReference myRef;

    Button saveEvent;
    Button cancelEvent;
    private String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    private String putUrl;
    private EditText eventName;
    private EditText eventDesc;
    private EditText eventLoc;
    private CheckBox eventPrivate;
    private DatePicker eventDate;
    private TimePicker startTime;
    private TimePicker endTime;
    private ListView eventTags;
    protected static ArrayList<String> attributeItems = new ArrayList<>();
    protected int PICK_IMAGE_REQUEST = 1;
    ImageView eventImage;
    private String userID;
    private Integer clubID;

    protected UserProfile user;
    public StorageReference mStorage;
    public Uri uri;
    protected String imgReference;
    protected static ListAttributeAdapter laAdapter;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();

        // Retrieves Fire base Storage Reference
        mStorage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        user = new UserProfile();
        initializeUser();

        String cid = getIntent().getStringExtra(MyClubsFragment.clubID);
        clubID = Integer.parseInt(cid);
        putUrl = url + cid + "/createEvent";
        System.out.print("***********");
        System.out.println(putUrl);

        eventName = (EditText) findViewById(R.id.event_name);
        eventDesc = (EditText) findViewById(R.id.event_description);
        eventLoc = (EditText) findViewById(R.id.event_location);
        eventPrivate = (CheckBox) findViewById(R.id.event_public);
        eventDate = (DatePicker) findViewById(R.id.event_date);
        startTime = (TimePicker) findViewById(R.id.event_start_time);
        endTime = (TimePicker) findViewById(R.id.event_end_time);
        eventTags = (ListView) findViewById(R.id.event_tags);
        eventImage = (ImageView) findViewById(R.id.event_img);

        Button addImage = (Button) findViewById(R.id.event_add_image);
        Button addTags = (Button) findViewById(R.id.event_add_tags);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });


        saveEvent = (Button) findViewById(R.id.event_save);
        saveEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //createEvent();
                writeToDB();
                Intent intent = new Intent(CreateEventActivity.this, NavigationActivity.class);
                SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                String uID = myPrefs.getString("ID", "1");
                intent.putExtra(ID, uID);
                startActivity(intent);


            }
        });

        cancelEvent = (Button) findViewById(R.id.event_cancel);
        cancelEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, NavigationActivity.class);
                SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                String uID = myPrefs.getString("ID", "1");
                intent.putExtra(ID, uID);
                startActivity(intent);
            }
        });

        eventTags = (ListView) findViewById(R.id.event_tag_list_view);
        laAdapter = new ListAttributeAdapter(this, R.layout.check_list_item, attributeItems);
        eventTags.setAdapter(laAdapter); // Layout File

        // setup the alert builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Event Attributes");
        // Add a Checkbox List
        final String[] event_attributes = getResources().getStringArray(R.array.tags);
        final boolean[] checkedItems = new boolean[event_attributes.length];
        builder.setMultiChoiceItems(event_attributes, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    // Add Attribute Item!
                    attributeItems.add(event_attributes[which]);
                } else {
                    // Remove Attribute Item!
                    attributeItems.remove(event_attributes[which]);
                }
            }
        });

        // Add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
            }
        });
        builder.setNegativeButton("Cancel", null);


        addTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void writeToDB() {
        myRef = database.getReference().child("events").push();
        String myKey = myRef.getKey();

        String imgId = imgReference;

        // Setting the date and start/end times
        final Calendar c = Calendar.getInstance();
        c.set(eventDate.getYear(), eventDate.getMonth(), eventDate.getDayOfMonth(), startTime.getCurrentHour(), startTime.getCurrentMinute());
        long start_time = c.getTimeInMillis();
        c.set(eventDate.getYear(), eventDate.getMonth(), eventDate.getDayOfMonth(), endTime.getCurrentHour(), endTime.getCurrentMinute());
        long end_time = c.getTimeInMillis();

        HashMap<String, String> tags = new HashMap<>();
        Event e = new Event(myKey, eventName.getText().toString(), eventDesc.getText().toString(), eventLoc.getText().toString(), start_time, end_time, imgId, clubID, tags);
        // Write a message to the database
        myRef.setValue(e);
        populateAttributeList(myKey);
    }

    public void populateAttributeList(String key) {

        final DatabaseReference myAtrRef = database.getReference().child("events").child(key).child("tags");

        final ArrayList<String> eAttributes = attributeItems;

        for(String tags: eAttributes) {
            myAtrRef.push().setValue(tags);
            System.out.println("Tags: " + tags);
        }

    }

    public void createEvent() throws JSONException {
        JSONArray tags = new JSONArray();
        JSONObject jsonBody = new JSONObject();
        String date = eventDate.getYear() + "-" + eventDate.getMonth() + "-" + eventDate.getDayOfMonth();
        String start_date = date + " " + startTime.getCurrentHour() + ":" + startTime.getCurrentMinute();
        String end_date = date + " " + endTime.getCurrentHour() + ":" + endTime.getCurrentMinute();

        Calendar c = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        c.set(eventDate.getYear(), eventDate.getMonth(), eventDate.getDayOfMonth(), startTime.getCurrentHour(), startTime.getCurrentMinute());
        //c.set(eventDate.getYear(), eventDate.getMonth(), eventDate.getDayOfMonth(), endTime.getCurrentHour(), endTime.getCurrentMinute());

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final String uID = myPrefs.getString("ID", "1");
        try {
            System.out.print("YAAS");
            jsonBody.put("name", eventName.getText().toString());
            jsonBody.put("description", eventDesc.getText().toString());
            jsonBody.put("userID", Integer.parseInt(uID));
            jsonBody.put("tags", tags);
            jsonBody.put("startDate", start_date);
            jsonBody.put("endDate", end_date);
            jsonBody.put("location", eventLoc.getText().toString());
            jsonBody.put("isPrivate", eventPrivate.isChecked());
            System.out.println(jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, putUrl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("******************Event ID " + response.getInt("eventID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(CreateEventActivity.this, NavigationActivity.class);
                        intent.putExtra(ID, uID);
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
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();
            // Stores image in firebase.
            StorageReference filepath = mStorage.child("EventPhotos").child(uri.getLastPathSegment());
            imgReference = filepath.getPath();

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(CreateEventActivity.this).load(downloadUri).fit().centerCrop().into(eventImage);
                    Toast.makeText(CreateEventActivity.this, "Upload Complete", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void initializeUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference currentUserRef = database.getReference().child("users").child(currentUser.getUid());
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(UserProfile.class);
                userID = user.getUid();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        currentUserRef.addValueEventListener(userListener);
    }

    public class ListAttributeAdapter extends ArrayAdapter<String> {
        int res;

        public ListAttributeAdapter(Context ctx, int res, List<String> attributes) {
            super(ctx, res, attributes);
            this.res = res;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout attributeView;
            String attribute = getItem(position);

            if (convertView == null) {
                attributeView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(res, attributeView, true);
            } else {
                attributeView = (LinearLayout) convertView;
            }

            TextView attribute_name = (TextView) attributeView.findViewById(R.id.event_attribute);

            attribute_name.setText(attribute);

            return attributeView;
        }
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CreateEvent Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

}
