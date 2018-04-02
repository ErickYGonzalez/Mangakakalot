package com.beardglasssquared.mangakakalot.mangakakalot;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
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
    ProgressBar recent_pb, fav_pb;
    RecyclerView recent_rv, fav_rv;
    TextView recent_tv, fav_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recent_read, container, false);

        ll_recents = v.findViewById(R.id.ll_recent);
        ll_recents.setVerticalScrollBarEnabled(true);

        recent_pb = v.findViewById(R.id.progressBar);
        recent_rv = v.findViewById(R.id.recycle_view);
        recent_rv.setNestedScrollingEnabled(false);

        fav_pb = v.findViewById(R.id.fav_progressBar);
        fav_rv = v.findViewById(R.id.fav_recycle_view);
        fav_rv.setNestedScrollingEnabled(false);

        fav_tv = v.findViewById(R.id.fav_tv);
        recent_tv = v.findViewById(R.id.recent_tv);

        reloadRecent();
        reloadFav();

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
            recent_rv.setLayoutManager(llm);

            final BrowserAdapter browserAdapter = new BrowserAdapter(mangaLinks, getContext(), recent_rv);

            //Loads more data when reaching the bottom of the scroll view

            recent_rv.setAdapter(browserAdapter);

            recent_rv.setVisibility(View.VISIBLE);
            recent_tv.setVisibility(View.GONE);

        } else {
            recent_tv.setVisibility(View.VISIBLE);
        }
        recent_pb.setVisibility(View.GONE);

    }

    public void reloadFav() {
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

                boolean isFav = Boolean.parseBoolean(tokens[6]);

                if (isFav) {
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

            if (mangaLinks.size() < 1) {
                fav_tv.setVisibility(View.VISIBLE);
            }

            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            fav_rv.setLayoutManager(llm);

            final BrowserAdapter browserAdapter = new BrowserAdapter(mangaLinks, getContext(), recent_rv);

            //Loads more data when reaching the bottom of the scroll view

            fav_rv.setAdapter(browserAdapter);

            fav_rv.setVisibility(View.VISIBLE);
            fav_tv.setVisibility(View.GONE);
        } else {
            fav_tv.setVisibility(View.VISIBLE);
        }
        fav_pb.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ll_recents != null) {
            reloadRecent();
            reloadFav();
        }
    }

}
