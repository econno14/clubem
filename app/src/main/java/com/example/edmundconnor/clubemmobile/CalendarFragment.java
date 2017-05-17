package com.example.edmundconnor.clubemmobile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.example.edmundconnor.clubemmobile.LoginActivity.ID;


public class CalendarFragment extends Fragment {
    CalendarView calendar;
    String url = "https://clubs-jhu.herokuapp.com/clubs/api/";
    private JSONArray events;

    //public GregorianCalendar cal_month, cal_month_copy;
    //private CalendarAdapter cal_adapter;
    //private TextView tv_month;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("CalendarFragment");

        calendar = (CalendarView) getActivity().findViewById(R.id.simpleCalendarView);
        Context context = getActivity().getApplicationContext();
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int x = myPrefs.getInt(ID, 6);
        getEvents(x);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        String userId = getArguments().getString("id");
        System.out.println(userId);
        calendar = (CalendarView) view.findViewById(R.id.simpleCalendarView);
        getEvents(Integer.parseInt(userId));


        return view;

    }

    public void getEvents(int i) {
        String urlEnd = url + i + "/userEvents";
        System.out.println(urlEnd);
        final ContentResolver cr = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        final long calId = 1;


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlEnd, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray events = response.getJSONArray("events");
                            System.out.println(events.length());
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject event = events.getJSONObject(i);
                                ContentValues values = new ContentValues();

                                String name = event.getString("name");
                                String start = event.getString("startDate");
                                String end = event.getString("endDate");
                                Integer eventId = event.getInt("eventId");
                                //startDate: <String>, start date of event in format (ex. Nov 11, 2017 12:00:00 PM)
                                //endDate: <String>, end date of event in format (ex. Nov 11, 2017 12:00:00 PM)

                                // Add to Android db; duration is null for nonrecurring events.
                                values.put(CalendarContract.Events.CALENDAR_ID, Long.toString(calId));
                                values.put(CalendarContract.Events.DTSTART, dateInMillis(start));
                                values.put(CalendarContract.Events.DTEND, dateInMillis(end));
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
                                values.put(CalendarContract.Events.TITLE, name);
                                values.put(CalendarContract.Events.DESCRIPTION, eventId.toString());

                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                //Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                                calendar.setDate(event.getLong("startTime"));
                            }

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
        Volley.newRequestQueue(getActivity()).add(getRequest);
    }


    public long dateInMillis(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
        long d = 0;
        try {
            Date mDate = sdf.parse(date);
            return mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

}
