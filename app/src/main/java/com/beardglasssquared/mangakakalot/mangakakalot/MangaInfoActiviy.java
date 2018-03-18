package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MangaInfoActiviy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_info_activiy);

        ImageView mangaCover = findViewById(R.id.manga_cover);

        Bundle extras = getIntent().getExtras();
        String imgurl = extras.getString("imgurl");
        String mangaUrl = extras.getString("mangaUrl");
        String name = extras.getString("name");

        Picasso.with(getApplicationContext()).load(imgurl).into(mangaCover);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LoadChapters loadChapters = new LoadChapters(mangaUrl);
        loadChapters.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent i = new Intent(this,ReadingActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class LoadChapters extends AsyncTask<String, Void, List<String>> {

        ProgressBar pb;
        RecyclerView rv;
        String mangaUrl;

        public LoadChapters(String mangaUrl) {
            this.mangaUrl = mangaUrl;
        }

        @Override
        protected List<String> doInBackground(String... strings) {

            InputStream is;
            List<String> chapterUrls = new ArrayList<>();
            ;

            try {
                //This is where the input box and number picker changes the manga
                //String rootUrl = "http://mangakakalot.com/chapter/";
                URL url = new URL(mangaUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if (connection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                    int response = -1;
                    httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    httpURLConnection.connect();
                    response = httpURLConnection.getResponseCode();

                    if (response == HttpURLConnection.HTTP_OK) {
                        is = httpURLConnection.getInputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(is));

                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            if (inputLine.contains("chapter-list")) {
                                while (!inputLine.contains("comment-info")) {
                                    inputLine = in.readLine();
                                    if (inputLine.contains("href"))
                                    {
                                        String chapterUrl = inputLine.substring(inputLine.indexOf("http"), inputLine.indexOf("title") - 2);

                                        //inputLine.substring(inputLine.indexOf("chapter_") + "chapter_".length(), inputLine.indexOf("title") - 2);
                                        chapterUrls.add(chapterUrl);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Unable to load ", Toast.LENGTH_LONG).show();

                e.printStackTrace();
            }

            ArrayList<String> reverse = new ArrayList<>(chapterUrls.size());
            for (int i = 0; i < chapterUrls.size(); i++)
            {
                reverse.add(chapterUrls.get(chapterUrls.size() - i - 1));
            }
            return reverse;
        }


        protected void onPostExecute(List<String> urls) {

            pb = findViewById(R.id.progressBar);
            rv = findViewById(R.id.more_info_rv);


            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            MoreInfoAdapter browserAdapter = new MoreInfoAdapter(urls,getApplicationContext());
            rv.setAdapter(browserAdapter);

            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }
}
