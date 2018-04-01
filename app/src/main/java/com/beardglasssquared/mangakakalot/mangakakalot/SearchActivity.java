package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    GetSearch search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = getIntent().getExtras();
        String searchTerm = extras.getString("search_term");
        setTitle(searchTerm);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        search = new GetSearch(searchTerm);
        search.execute(searchTerm);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.getItem(0);
        final SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (hasFocus) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(view.findFocus(), 0);
                        }
                    }, 200);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                findViewById(R.id.recycle_view).setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                search.cancel(true);
                search = new GetSearch(s);
                search.execute();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    public class GetSearch extends AsyncTask<String, Void, List<MangaLink>> {

        ProgressBar pb;
        RecyclerView rv;
        String searchTerm;

        public GetSearch(String s)
        {
            searchTerm = s;
            if (searchTerm.contains(" ")) {
                searchTerm = searchTerm.replace(" ","_");
            }
        }

        @Override
        protected List<MangaLink> doInBackground(String... strings) {

            InputStream is;
            List<MangaLink> mangaLinks = new ArrayList<>();
            try {
                //This is where the input box and number picker changes the manga
                //String rootUrl = "http://mangakakalot.com/chapter/";
                String rootUrl = "http://mangakakalot.com/search/" + searchTerm;

                URL url = new URL(rootUrl);


                //stuff setting up just to make it connect
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if (connection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                    int response = -1;
                    //Due to security protocol with the website, this is need in before the connection
                    //I think it tricks the server to thinking this is a pc
                    httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    httpURLConnection.connect();
                    response = httpURLConnection.getResponseCode();

                    if (response == HttpURLConnection.HTTP_OK) {
                        is = httpURLConnection.getInputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(is));

                        //Where reading from html code starts
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            if (inputLine.contains("item-name"))
                            {
                                inputLine = in.readLine();

                                String mangaName = inputLine.substring(inputLine.indexOf("\">") + 2, inputLine.indexOf("</a>"));

                                String mangaUrl = inputLine.substring(inputLine.indexOf("http"),inputLine.indexOf("\">"));

                                MangaLink link = new MangaLink(mangaName,"","",mangaUrl, 0);
                                mangaLinks.add(link);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Unable to load ", Toast.LENGTH_LONG).show();

                e.printStackTrace();
            }
            return mangaLinks;
        }


        protected void onPostExecute(List<MangaLink> urls) {

            pb = findViewById(R.id.progressBar);
            rv = findViewById(R.id.recycle_view);


            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            SearchAdapter searchAdapter = new SearchAdapter(getApplicationContext(),urls);
            rv.setAdapter(searchAdapter);

            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
