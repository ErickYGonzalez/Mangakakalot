package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MangaBrowserActivity extends AppCompatActivity {

    int pageNumber = 1;
    LoadPopularPage lpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_activity);

        lpp = new LoadPopularPage(pageNumber);
        lpp.execute();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO: Change this to a more robust system
        if (id == R.id.previous) {
            if (pageNumber > 1) {
                pageNumber--;
                findViewById(R.id.recycle_view).setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(),"Page " + String.valueOf(pageNumber), Toast.LENGTH_LONG).show();

                lpp.cancel(true);
                lpp = new LoadPopularPage(pageNumber);
                lpp.execute();
            }
            return true;
        }
        if (id == R.id.next)
        {
            pageNumber++;
            findViewById(R.id.recycle_view).setVisibility(View.GONE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            Toast.makeText(getApplicationContext(),"Page " + String.valueOf(pageNumber), Toast.LENGTH_LONG).show();


            lpp.cancel(true);
            lpp = new LoadPopularPage(pageNumber);
            lpp.execute();
            return true;
        }
        if (id == R.id.changeChapter)
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public class LoadPopularPage extends AsyncTask<String, Void, List<MangaLink>> {

        int pageNumber;
        ProgressBar pb;
        RecyclerView rv;

        public LoadPopularPage(int pageNumber)
        {
            this.pageNumber = pageNumber;
        }

        @Override
        protected List<MangaLink> doInBackground(String... strings) {

            InputStream is;
            List<MangaLink> mangaLinks = new ArrayList<>();
            try {
                //This is where the input box and number picker changes the manga
                //String rootUrl = "http://mangakakalot.com/chapter/";
                String rootUrl = "http://mangakakalot.com/manga_list?type=topview&category=all&state=all&page=" + Integer.toString(pageNumber);
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
                            if (inputLine.contains("list-truyen-item-wrap")) {
                                inputLine = in.readLine();
                                String title = inputLine.substring(inputLine.indexOf("title=") + 7, inputLine.length() - 2);
                                String mangaUrl = inputLine.substring(inputLine.indexOf("http:"), inputLine.indexOf("title=") - 2);

                                //contains the image url for the manga
                                inputLine = in.readLine();
                                String imageUrl = inputLine.substring(inputLine.indexOf("http:"), inputLine.indexOf(".jpg") + 4);

                                Log.d("Image Url",imageUrl);
                                MangaLink link = new MangaLink(title,imageUrl,"",mangaUrl, 0);
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
                /*
                recList.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recList.setLayoutManager(llm);
                */
            pb = findViewById(R.id.progressBar);
            rv = findViewById(R.id.recycle_view);


            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            BrowserAdapter browserAdapter = new BrowserAdapter(urls, getApplicationContext(),rv);
            rv.setAdapter(browserAdapter);

            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }
}
