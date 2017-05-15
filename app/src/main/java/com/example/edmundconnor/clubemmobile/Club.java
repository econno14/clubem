package com.example.edmundconnor.clubemmobile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edmundConnor on 5/15/17.
 */

public class Club {
    private String name;
    private String president;
    private List<String> types;
    private String description;

    public Club(String name, String userId, String description, ArrayList types) {
        this.name = name;
        this.president = userId;
        this.types = types;
        this.description = description;
    }

    public String getName() {return name;}
    public String getPresident() { return president;}
    public String getDescription() {return description;}
    private List<String> getTypes() {return types;}

}
