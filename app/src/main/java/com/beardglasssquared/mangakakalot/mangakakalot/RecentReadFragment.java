package com.beardglasssquared.mangakakalot.mangakakalot;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentReadFragment extends Fragment {


    public RecentReadFragment() {
        // Required empty public constructor
    }

    public static RecentReadFragment newInstance() {
        RecentReadFragment fragment = new RecentReadFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recent_read, container, false);

        tv = v.findViewById(R.id.textview);
        reloadRecent();

        return v;
    }

    public void reloadRecent()
    {
        SharedPreferences sharedPref = getContext().getSharedPreferences("bookmarks",
                Context.MODE_PRIVATE);

        Map<String,?> keys = sharedPref.getAll();

        String s = "";
        for(Map.Entry<String,?> entry : keys.entrySet()){
            s += entry.getKey() + ": " +
                    entry.getValue().toString() + "\n";
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }
        tv.setText(s);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}
