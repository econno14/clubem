package com.example.edmundconnor.clubemmobile;

import java.util.List;

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
    private List<String> tags;
    private String imgId;
    private long startTime;

    public Event() {
        this.name = "EJ";
        this.tags = null;
    }

    public Event(String id, String name, String description, String location, String startDate, String endDate, List<String> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tags = tags;
        this.imgId = "";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getTags() { return tags; }
    public String getDescription() {return description;}
    public String getLocation() {return location; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getImgId() { return imgId; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long x) { this.startTime = x;}


}
