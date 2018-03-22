package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.transitionseverywhere.TransitionManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class MangaInfoActiviy extends AppCompatActivity {

    String name, imgurl, mangaUrl;
    Manga finishedManga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manga_info_activity);


        Bundle extras = getIntent().getExtras();
        imgurl = extras.getString("imgurl");
        mangaUrl = extras.getString("mangaUrl");
        name = extras.getString("name");

        TextView title = findViewById(R.id.title_text);
        title.setText(name);

        ImageView mangaCover = findViewById(R.id.manga_cover);
        ImageView mangaCoverSmall = findViewById(R.id.manga_cover_small);
        Picasso.with(getApplicationContext()).load(imgurl).into(mangaCover);
        Picasso.with(getApplicationContext()).load(imgurl).into(mangaCoverSmall);

        mangaCoverSmall.bringToFront();
        FrameLayout fl = findViewById(R.id.fl);
        fl.invalidate();


        BitmapDrawable drawable = (BitmapDrawable) mangaCover.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Blurry.with(getApplicationContext())
                .radius(3)
                .sampling(1)
                .from(bitmap)
                .into(mangaCover);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(name);

        LoadChapters loadChapters = new LoadChapters(mangaUrl);
        loadChapters.execute();
    }

    public Manga getManga()
    {
        return finishedManga;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        if (getManga() != null) {

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("bookmarks",
                    Context.MODE_PRIVATE);
            final int lastVisitedChapter = sharedPref.getInt(getManga().title,-1);
            if (lastVisitedChapter > -1) {
                Button lastVisitedButton = findViewById(R.id.last_visited_button);
                lastVisitedButton.setVisibility(View.VISIBLE);
                String url = getManga().chaptersLinks[lastVisitedChapter];
                String chapterName = "Chapter " + url.substring(url.indexOf("chapter_") + "chapter_".length());
                lastVisitedButton.setText("Continue to " + chapterName);
                lastVisitedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),ReadingActivity.class);
                        intent.putExtra("chapterUrls",getManga().chaptersLinks);
                        intent.putExtra("chapterNumber",lastVisitedChapter);
                        intent.putExtra("name",getManga().title);

                        getApplicationContext().startActivity(intent);
                    }
                });
            }
        }


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

    public class LoadChapters extends AsyncTask<String, Void, Manga> {

        ProgressBar pb;
        RecyclerView rv;
        String mangaUrl;

        public LoadChapters(String mangaUrl) {
            this.mangaUrl = mangaUrl;
        }

        @Override
        protected Manga doInBackground(String... strings) {

            Manga manga = new Manga();
            manga.title = name;

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

                            if(inputLine.contains("Author(s) :")){
                                inputLine = in.readLine();
                                manga.author = inputLine.substring(inputLine.indexOf(">")+ 1, inputLine.indexOf("</a>"));
                            }
                            if(inputLine.contains("noidungm"))
                            {
                                inputLine = in.readLine();
                                inputLine = in.readLine();
                                manga.description = inputLine;
                            }

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

            String[] c = new String[reverse.size()];
            c = reverse.toArray(c);

            manga.chaptersLinks = c;
            manga.title = name;
            return manga;
        }


        protected void onPostExecute(final Manga m) {
            pb = findViewById(R.id.progressBar);
            rv = findViewById(R.id.more_info_rv);

            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            finishedManga = m;

            MangaInfoAdapter browserAdapter = new MangaInfoAdapter(m,getApplicationContext());
            rv.setAdapter(browserAdapter);

            rv.setNestedScrollingEnabled(false);
            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);

            final CardView card = findViewById(R.id.card_view);
            final TextView description = findViewById(R.id.discriptions_text);
            TextView author = findViewById(R.id.author_text);

            TransitionManager.beginDelayedTransition(card);
            author.setText(m.author);
            description.setText("\n" + Html.fromHtml(m.description));

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("bookmarks",
                    Context.MODE_PRIVATE);
            final int lastVisitedChapter = sharedPref.getInt(m.title,-1);
            if (lastVisitedChapter > -1) {
                Button lastVisitedButton = findViewById(R.id.last_visited_button);
                lastVisitedButton.setVisibility(View.VISIBLE);
                String url = m.chaptersLinks[lastVisitedChapter];
                String chapterName = "Chapter " + url.substring(url.indexOf("chapter_") + "chapter_".length());
                lastVisitedButton.setText("Continue to " + chapterName);
                lastVisitedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),ReadingActivity.class);
                        intent.putExtra("chapterUrls",m.chaptersLinks);
                        intent.putExtra("chapterNumber",lastVisitedChapter);
                        intent.putExtra("name",m.title);

                        getApplicationContext().startActivity(intent);
                    }
                });
            }
        }
    }
}
