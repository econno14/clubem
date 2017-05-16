package com.example.edmundconnor.clubemmobile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by edmundConnor on 5/14/17.
 */

public class UserProfile {

    /** Variables */
    private String uid;
    private String name;
    private String gradyear;
    private String email;
    private String imagePath;

    /** Empty constructor for Firebase snapshot initialization */
    public UserProfile() {}

    /** Constructor with manual fields */
    public UserProfile(String id, String firstname, String email, String year) {
        this.uid = id;
        this.name = firstname;
        this.email = email;
        this.imagePath = "";
        this.gradyear = year;
    }

    /** Get methods */
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getImagePath() { return imagePath; }
    public String getGradyead() {return gradyear; }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("email", email);
        result.put("gradyear", gradyear);
        result.put("imagePath", imagePath);
        return result;
    }
}

