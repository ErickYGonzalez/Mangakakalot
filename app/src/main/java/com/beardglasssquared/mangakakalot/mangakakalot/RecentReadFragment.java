package com.beardglasssquared.mangakakalot.mangakakalot;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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


    LinearLayout ll_recents;
    ProgressBar pb;
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recent_read, container, false);

        ll_recents = v.findViewById(R.id.ll_recent);
        ll_recents.setVerticalScrollBarEnabled(true);
        pb = v.findViewById(R.id.progressBar);
        rv = v.findViewById(R.id.recycle_view);
        rv.setNestedScrollingEnabled(false);
        reloadRecent();

        return v;
    }

    public void reloadRecent()
    {
        SharedPreferences sharedPref = getContext().getSharedPreferences("bookmarks",
                Context.MODE_PRIVATE);

        Map<String,?> keys = sharedPref.getAll();

        if (keys.size() > 0) {
            ArrayList<MangaLink> mangaLinks = new ArrayList<>();

            for(final Map.Entry<String,?> entry : keys.entrySet()){
                String mangaData = entry.getValue().toString();
                final String[] tokens = mangaData.split(",");
                    /* Tokens Key
                    0 - Chapter number (int/index)
                    1 - Chapter name (string)
                    2 - page progress (int/index)
                    3 - Manga Url (String)
                    4 - Image Url (String)
                    5 - time long
                    6 = isFav (bool)
                 */

                if (!tokens[0] .equals("0")) {
                    String title = entry.getKey();
                    String imageUrl = tokens[4];
                    String mangaUrl = tokens[3];
                    long time = Long.parseLong(tokens[5]);
                    Log.d("Saved: ", mangaData);
                    MangaLink mangaLink = new MangaLink(title,imageUrl,"",mangaUrl,time);
                    mangaLinks.add(mangaLink);
                    Collections.sort(mangaLinks);
                }
            }

            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            final BrowserAdapter browserAdapter = new BrowserAdapter(mangaLinks, getContext(),rv);

            //Loads more data when reaching the bottom of the scroll view

            rv.setAdapter(browserAdapter);

            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);

        } else {
            pb.setVisibility(View.GONE);
            TextView tv = new TextView(getContext());
            tv.setText("No Bookmarks Found");
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ll_recents.addView(tv);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ll_recents != null) {
            reloadRecent();
        }
    }

}
