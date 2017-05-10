package com.example.edmundconnor.clubemmobile;

import java.util.List;

/**
 * Created by edmundConnor on 5/10/17.
 */

public class Event {
    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private String date;

    public Event() {
        this.name = "EJ";
        this.tags = null;
    }

    public Event(String id, String name, String description, List<String> tags, String date) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.tags = tags;
        this.date = date;


    }

    public Event(String id, String name, String description, String date) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.tags = null;
        this.date = date;


    }


    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getTags() { return tags; }
    public String getDate() {return date; }
    public String getDescription() {return description;}

}
