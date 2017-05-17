package com.example.edmundconnor.clubemmobile;

import com.google.firebase.database.Exclude;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by edmundConnor on 5/10/17.
 */

public class Event {
    private String id;
    private String name;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private HashMap<String, String> tags;
    private JSONArray types;
    private String imgId;
    private long start_time;
    private long end_time;
    private int clubId;

    public Event() {
        this.name = "EJ";
        this.tags = null;
    }

    public Event(String id, String name, String description, String location, long startDate, long endDate, String imgId, int cid, HashMap<String, String> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.start_time = startDate;
        this.end_time = endDate;
        this.tags = tags;
        this.clubId = cid;
        this.imgId = imgId;
    }

    public Event(String id, String name, String description, String location, String startDate, String endDate, JSONArray tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.types = tags;
        this.imgId = imgId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public HashMap<String, String> getTags() { return tags; }
    public JSONArray getTypes() { return types; }
    public String getDescription() {return description;}
    public String getLocation() {return location; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getImgId() { return imgId; }
    public long getStartTime() { return start_time; }
    public void setStartTime(long x) { this.start_time = x;}
    public long getEndTime() { return end_time; }
    public void setEndTime(long x) { this.end_time = x;}
    public int getClubId() { return clubId;}

    @Exclude
    public String grabStartTimeString() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(this.start_time);

        String time;

        if (c.get(Calendar.HOUR_OF_DAY) < 12) {
            time = String.format("%d:%02d AM", c.get(Calendar.HOUR), c.get(Calendar.MINUTE));
        } else {
            time = String.format("%d:%02d PM", c.get(Calendar.HOUR), c.get(Calendar.MINUTE));
        }

        return time;
    }

    @Exclude
    public String grabEndTimeString() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(this.end_time);

        String time;

        if (c.get(Calendar.HOUR_OF_DAY) < 12) {
            time = String.format("%d:%02d AM", c.get(Calendar.HOUR), c.get(Calendar.MINUTE));
        } else {
            time = String.format("%d:%02d PM", c.get(Calendar.HOUR), c.get(Calendar.MINUTE));
        }

        return time;
    }

    @Exclude
    public String grabDateString() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(start_time);

        return String.format("%d/%d/%d", c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.YEAR));
    }

    @Exclude
    public String grabDateString2() {

        Date d = new Date(this.start_time);
        return String.format("(%tA) %tB %te, %tY", d, d, d, d);
    }

}
