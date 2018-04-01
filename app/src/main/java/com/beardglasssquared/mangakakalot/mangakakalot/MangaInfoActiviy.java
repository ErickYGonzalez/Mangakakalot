package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

    String name, imgUrl, mangaUrl;
    Manga finishedManga;
    ImageView mangaCover, mangaCoverSmall;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manga_info_activity);


        Bundle extras = getIntent().getExtras();
        imgUrl = extras.getString("imgurl");
        mangaUrl = extras.getString("mangaUrl");
        name = extras.getString("name");

        TextView title = findViewById(R.id.title_text);
        title.setText(name);

        mangaCoverSmall = findViewById(R.id.manga_cover_small);

        mangaCoverSmall.bringToFront();
        FrameLayout fl = findViewById(R.id.fl);
        fl.invalidate();

        LoadBackgroundImages loadBackgroundImages = new LoadBackgroundImages(mangaUrl);
        loadBackgroundImages.execute();

        setTitle(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LoadChapters loadChapters = new LoadChapters(mangaUrl);
        loadChapters.execute();
    }

    public Manga getManga()
    {
        return finishedManga;
    }


    public void loadBookmark(final Manga m) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("bookmarks",
                Context.MODE_PRIVATE);
        final String mangaData = sharedPref.getString(m.title,"No Data");
        if (!mangaData.equals("No Data")) {
            String[] tokens = mangaData.split(",");
            final int lastVisitedChapter = Integer.parseInt(tokens[0]);
            final int lastVistedPage = Integer.parseInt(tokens[2]);
            Button lastVisitedButton = findViewById(R.id.last_visited_button);
            lastVisitedButton.setVisibility(View.VISIBLE);
            String url = m.chaptersLinks[lastVisitedChapter];


            final String chapterName = "Chapter " + url.substring(url.indexOf("chapter_") + "chapter_".length());
            lastVisitedButton.setText("Continue to " + chapterName);
            lastVisitedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),ReadingActivity.class);
                    intent.putExtra("chapterUrls",m.chaptersLinks);
                    intent.putExtra("chapterNumber",lastVisitedChapter);
                    intent.putExtra("name",m.title);
                    intent.putExtra("mangaUrl",mangaUrl);
                    intent.putExtra("imgUrl",imgUrl);
                    intent.putExtra("pageNumber",lastVistedPage);

                    getApplicationContext().startActivity(intent);
                }
            });
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();

        if (getManga() != null) {
            loadBookmark(getManga());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;


            /* Tokens Key
            0 - Chapter number (int/index)
            1 - Chapter name (string)
            2 - page progress (int/index)
            3 - Manga Url (String)
            4 - Image Url (String)
            5 - time
            6 = isFav (bool)
         */
            case R.id.fav:
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("bookmarks",
                        Context.MODE_PRIVATE);
                final String mangaData = sharedPref.getString(name,"No Data");
                if (!mangaData.equals("No Data")) {
                    String[] tokens = mangaData.split(",");
                    boolean isFav = Boolean.parseBoolean(tokens[6]);


                    if (isFav) item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    else item.setIcon(R.drawable.ic_favorite_white_24dp);


                    Log.d("Is fav",String.valueOf(isFav));


                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(name,
                            tokens[0] + "," +
                                    tokens[1] + "," +
                                    tokens[2] + "," +
                                    tokens[3] + "," +
                                    tokens[4] + "," +
                                    tokens[5] + "," +
                                    String.valueOf(isFav));
                } else {
                    item.setIcon(R.drawable.ic_favorite_white_24dp);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(name,
                            "0" + "," +
                                    "1" + "," +
                                    "0" + "," +
                                    mangaUrl + "," +
                                    imgUrl + "," +
                                    0 + "," +
                                    true);
                }
                return true;
            case R.id.flip:

                ArrayList<String> reverse = new ArrayList<>(finishedManga.chaptersLinks.length);
                for (int i = 0; i < finishedManga.chaptersLinks.length; i++)
                {
                    reverse.add(finishedManga.chaptersLinks[finishedManga.chaptersLinks.length - i - 1]);
                }

                String[] c = new String[reverse.size()];
                c = reverse.toArray(c);

                finishedManga.chaptersLinks = c;

                rv.getAdapter().notifyDataSetChanged();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class LoadChapters extends AsyncTask<String, Void, Manga> {

        ProgressBar pb;

        String mangaUrl;

        public LoadChapters(String mangaUrl) {
            this.mangaUrl = mangaUrl;
        }

        @Override
        protected Manga doInBackground(String... strings) {

            Manga manga = new Manga();
            manga.title = name;
            manga.mangaUrl = mangaUrl;
            manga.imgUrl = imgUrl;

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
            return manga;
        }


        protected void onPostExecute(final Manga m) {
            pb = findViewById(R.id.progressBar);
            rv = findViewById(R.id.more_info_rv);

            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            finishedManga = m;

            final CardView card = findViewById(R.id.card_view);
            final TextView description = findViewById(R.id.discriptions_text);
            TextView author = findViewById(R.id.author_text);

            MangaInfoAdapter browserAdapter = new MangaInfoAdapter(m,getApplicationContext());
            rv.setNestedScrollingEnabled(true);
            rv.setAdapter(browserAdapter);

            TransitionManager.beginDelayedTransition(card);
            author.setText(m.author);
            description.setText("\n" + Html.fromHtml(m.description));
            pb.setVisibility(View.GONE);


            loadBookmark(m);

            rv.setVisibility(View.VISIBLE);


        }
    }

    public void setBackground(Bitmap bitmap)
    {
        mangaCoverSmall.setImageBitmap(bitmap);
        mangaCover = findViewById(R.id.manga_cover);

        if (bitmap != null) {
            Blurry.with(getApplicationContext())
                    .radius(3)
                    .sampling(1)
                    .from(bitmap)
                    .into(mangaCover);
        }
    }
    public class LoadBackgroundImages extends AsyncTask<String, Void, Bitmap> {

        ProgressBar pb;
        RecyclerView rv;
        String mangaUrl;

        public LoadBackgroundImages(String mangaUrl) {
            this.mangaUrl = mangaUrl;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String mangaCoverUrl = "";
            InputStream is;
            ;

            Bitmap bitmap = null;

            try {
                //this get the url of no url

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
                            if (inputLine.contains("manga-info-pic")) {
                                inputLine = in.readLine();
                                mangaCoverUrl = inputLine.substring(inputLine.indexOf("http"),inputLine.indexOf(".jpg") + ".jpg".length());

                                imgUrl = mangaCoverUrl;
                            }
                        }
                    }



                }

                InputStream iStream = null;
                URL u = new URL(mangaCoverUrl);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setReadTimeout(5000 /* milliseconds */);
                conn.setConnectTimeout(7000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                iStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(iStream);
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), "Unable to load ", Toast.LENGTH_LONG).show();

                e.printStackTrace();
            }


            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            setBackground(b);
        }

    }
}
