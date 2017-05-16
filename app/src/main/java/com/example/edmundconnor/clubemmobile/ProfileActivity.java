package com.example.edmundconnor.clubemmobile;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.edmundconnor.clubemmobile.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;


public class ProfileActivity extends AppCompatActivity {
    String id;
    EditText name_, email_, year_, pw_, pwRe_;
    Button edit;
    TextView txt;
    ImageView profileImg;
    public static final String ID = "com.example.edmundconnor.clubemmobile.ID";
    private StorageReference mStorage;
    private String imgRef;

    String url = "https://clubs-jhu.herokuapp.com/clubs/api/";

    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setTitle("ProfileActivity");
        setContentView(R.layout.activity_profile);

        mStorage = FirebaseStorage.getInstance().getReference();
      
        name_ = (EditText)findViewById(R.id.nameEdit);
        email_ = (EditText)findViewById(R.id.emailEdit);
        year_ = (EditText)findViewById(R.id.gradYearEdit);
        pw_ = (EditText)findViewById(R.id.passwordEdit);
        pwRe_ = (EditText) findViewById(R.id.password1Edit);
        profileImg = (ImageView) findViewById(R.id.profile_image);

        edit = (Button)findViewById(R.id.edit_profile_button);
        txt = (TextView) findViewById(R.id.textview1);

        Intent intent = getIntent();
        id = intent.getStringExtra(LoginActivity.ID);
        Integer userId = Integer.parseInt(id);
        final String urlEnd = url + id + "/editUser";
        System.out.println(urlEnd);

        // Retrieves Fire base Storage Reference
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();



        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = name_.getText().toString();
                final String email = email_.getText().toString();
                final String password = pw_.getText().toString();
                final String passwordRe = pwRe_.getText().toString();
                final String gradYear = year_.getText().toString();
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



                int y = Integer.parseInt(gradYear);
                System.out.println(y);
                if (gradYear == "") {
                    y = 0;
                }

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("name", name);
                    jsonBody.put("year", y);
                    jsonBody.put("email", email);
                    jsonBody.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, urlEnd, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response", "passed");
                                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                                intent.putExtra(ID, id);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                txt.setVisibility(View.VISIBLE);
                                txt.setText("Invalid Input.");
                                Log.d("Error", "Error: " + error.getMessage());
                            }
                        }
                ) {
                };
                Volley.newRequestQueue(getApplicationContext()).add(putRequest);
            }
        });


        Button addImgButton = (Button) findViewById(R.id.add_img_button);
        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Button saveImage = (Button) findViewById(R.id.save_image_button);
                //saveImage.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("EventPhotos").child(uri.getLastPathSegment());
            imgRef = filepath.getPath();
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(ProfileActivity.this).load(downloadUri).fit().centerCrop().into(profileImg);
                }
            });
        }
    }

    public boolean saveImageToInternalStorage(Bitmap image) {

        try {
            FileOutputStream fos = getBaseContext().openFileOutput("profileImage.png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }
}
