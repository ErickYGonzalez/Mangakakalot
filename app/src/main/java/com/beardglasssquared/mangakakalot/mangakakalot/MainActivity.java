package com.beardglasssquared.mangakakalot.mangakakalot;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int chapterNumber = 1;
    LoadImage imageLoader;
    String manga = "goblin_slayer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Pulls the urls for manga images
        imageLoader = new LoadImage(manga,String.valueOf(chapterNumber));
        imageLoader.execute();
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
            if (chapterNumber > 1) {
                chapterNumber--;
                findViewById(R.id.recycle_view).setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                imageLoader.cancel(true);
                imageLoader = new LoadImage(manga,String.valueOf(chapterNumber));
                imageLoader.execute();
            }
            return true;
        }
        if (id == R.id.next)
        {
            chapterNumber++;
            imageLoader.cancel(true);

            findViewById(R.id.recycle_view).setVisibility(View.GONE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            imageLoader = new LoadImage(manga,String.valueOf(chapterNumber));
            imageLoader.execute();
            return true;
        }
        if (id == R.id.changeManga)
        {

        }

        return super.onOptionsItemSelected(item);
    }


    public class LoadImage extends AsyncTask<String, Void, List<String>> {
        String mangaName;
        String chapterNumber;
        ProgressBar pb;
        RecyclerView rv;

        public LoadImage(String managaName, String chapterNumber) {
            this.mangaName = managaName;
            this.chapterNumber = chapterNumber;
        }

        @Override
        protected List<String> doInBackground(String... strings) {


            InputStream is;
            List<String> urls = new ArrayList<>();
            try {
                //This is where the input box and number picker changes the manga
                String rootUrl = "http://mangakakalot.com/chapter/";
                URL url = new URL(rootUrl + mangaName + "/chapter_" + chapterNumber);


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
                            // vungdoc is the keyword to start looking for image urls
                            if (inputLine.contains("vungdoc")) {
                                inputLine = in.readLine();
                                String[] imagesInfo = inputLine.split("<img src=");
                                for (int i = 1; i < imagesInfo.length; i++) {
                                    String s = imagesInfo[i];
                                    urls.add(s.substring(s.indexOf("http"), s.indexOf(".jpg") + 4));
                                }

                                for (String s : urls) {
                                    Log.d("URL", s);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Unable to load ", Toast.LENGTH_LONG).show();

                e.printStackTrace();
            }
            return urls;
        }


        protected void onPostExecute(List<String> urls) {
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

            ImageAdapter ca = new ImageAdapter(urls, getApplicationContext(), rv);
            rv.setAdapter(ca);

            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }
}