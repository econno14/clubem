package com.example.edmundconnor.clubemmobile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;



public class SelectEventAdapter extends ArrayAdapter<Event> {
    int res;
    int imgID;
    String event_name;
    String event_date_time;

    public SelectEventAdapter(Context ctx, int res, List<Event> items) {
        super(ctx, res, items);
        this.res = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout eventSelectListView;
        final Event eventSelectedItem = getItem(position);
        //imgID = Integer.valueOf(eventSelectedItem.getImgId());
        event_name = eventSelectedItem.getName();
        event_date_time = (eventSelectedItem.getDate());

        if (convertView == null) {
            eventSelectListView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(res, eventSelectListView, true);
        } else {
            eventSelectListView = (LinearLayout) convertView;
        }

        TextView eventSelect_name = (TextView) eventSelectListView.findViewById(R.id.selected_event_name);
        TextView eventSelect_desc = (TextView) eventSelectListView.findViewById(R.id.selected_event_description);

        //eventSelectOrganizationLogo.setImageResource(imgID);
        eventSelect_name.setText(event_name);
        eventSelect_desc.setText(event_date_time);

        return eventSelectListView;
    }

}
