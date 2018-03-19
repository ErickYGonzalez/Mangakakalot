package com.beardglasssquared.mangakakalot.mangakakalot;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.transitionseverywhere.TransitionManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReadingActivity extends AppCompatActivity {

    int chapterNumber = 0;
    LoadImage imageLoader;
    String[] chapterUrls;
    boolean isBottomExpanded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Activity to full screen
        setContentView(R.layout.reading_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Retrieves urls from InfoPage
        Bundle extras = getIntent().getExtras();
        chapterUrls = extras.getStringArray("chapterUrls");
        chapterNumber = extras.getInt("chapterNumber");


        //Pulls the urls for manga images
        imageLoader = new LoadImage(chapterUrls[chapterNumber]);
        imageLoader.execute();


        //Set up pull up bottom tools
        initBottomButtons();





    }

    public void initBottomButtons(){
        //Set backToInfoButton to return to previous activity
        Button backToInfoButton = findViewById(R.id.back_to_info_button);
        backToInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //text next to Chapter Select
        final TextView currentChapterText = findViewById(R.id.current_chapter_text);

        //Set actions for "prev" button
        Button previousChapter = findViewById(R.id.previous_chapter_button);
        previousChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chapterNumber > 0) {
                    chapterNumber--;
                    String s = "Current Chapter: " + String.valueOf(chapterNumber);
                    currentChapterText.setText(s);

                    Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();

                    findViewById(R.id.recycle_view).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                    imageLoader.cancel(true);
                    imageLoader = new LoadImage(chapterUrls[chapterNumber]);
                    imageLoader.execute();
                }
            }
        });

        //Set actions for "next" button
        Button nextChapter = findViewById(R.id.next_chapter_button);
        nextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chapterNumber < chapterUrls.length - 1) {
                    chapterNumber++;
                    String s = "Current Chapter: " + String.valueOf(chapterNumber);
                    currentChapterText.setText(s);

                    Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();

                    findViewById(R.id.recycle_view).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                    imageLoader.cancel(true);
                    imageLoader = new LoadImage(chapterUrls[chapterNumber]);
                    imageLoader.execute();
                }
            }
        });

        //Set chapter select ofr the current chapter text
        currentChapterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(ReadingActivity.this);
                dialog.setContentView(R.layout.chapter_select);
                TextView textView = dialog.findViewById(R.id.enter_chapter_name);
                textView.setText("Enter Chapter Number: (0 - " + String.valueOf(chapterUrls.length - 1) + ")");

                dialog.findViewById(R.id.button_chapter_select_go).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText text = dialog.findViewById(R.id.text_chapter_number);

                        String chapter = text.getText().toString();

                        chapterNumber = Integer.parseInt(chapter);
                        String s = "Current Chapter: " + String.valueOf(chapterNumber);
                        currentChapterText.setText(s);

                        if (chapterNumber <= chapterUrls.length - 1) {
                            imageLoader.cancel(true);

                            findViewById(R.id.recycle_view).setVisibility(View.GONE);
                            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                            imageLoader = new LoadImage(chapterUrls[Integer.parseInt(chapter)]);
                            imageLoader.execute();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(),"Invalid Chapter (0 - " + String.valueOf(chapterUrls.length - 1) + ")",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.findViewById(R.id.button_chapter_select_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        //Set action for "More/Less" button.
        final Button moreSettings = findViewById(R.id.more_settings_button);
        moreSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardView bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
                LinearLayout chapterSelectLayout = findViewById(R.id.chapter_select_layout);
                LinearLayout zoomChangerLayout = findViewById(R.id.zoom_changer_layout);
                Button backToInfoButton = findViewById(R.id.back_to_info_button);

                //Hides/reveals more settings
                if (!isBottomExpanded) {
                    // start bottomSheetLayout transistion
                    TransitionManager.beginDelayedTransition(bottomSheetLayout);
                    moreSettings.setText("Less");

                    String s = "Current Chapter: " + String.valueOf(chapterNumber);
                    currentChapterText.setText(s);

                    chapterSelectLayout.setVisibility(View.VISIBLE);
                    zoomChangerLayout.setVisibility(View.VISIBLE);
                    backToInfoButton.setVisibility(View.VISIBLE);
                    isBottomExpanded = true;
                } else {
                    TransitionManager.beginDelayedTransition(bottomSheetLayout);

                    moreSettings.setText("More");

                    chapterSelectLayout.setVisibility(View.GONE);
                    zoomChangerLayout.setVisibility(View.GONE);
                    backToInfoButton.setVisibility(View.GONE);
                    isBottomExpanded = false;
                }



            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.previous) {
            if (chapterNumber > 0) {
                chapterNumber--;
                findViewById(R.id.recycle_view).setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                imageLoader.cancel(true);
                imageLoader = new LoadImage(chapterUrls[chapterNumber]);
                imageLoader.execute();
            }
            return true;
        }
        if (id == R.id.next)
        {
            if (chapterNumber < chapterUrls.length - 1) {
                chapterNumber++;
                imageLoader.cancel(true);

                findViewById(R.id.recycle_view).setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                imageLoader = new LoadImage(chapterUrls[chapterNumber]);
                imageLoader.execute();
            }

            return true;
        }
        if (id == R.id.changeChapter)
        {
            final Dialog dialog = new Dialog(ReadingActivity.this);
            dialog.setContentView(R.layout.chapter_select);
            dialog.findViewById(R.id.button_chapter_select_go).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText text = (EditText) dialog.findViewById(R.id.text_chapter_number);
                    String chapter = text.getText().toString();

                    chapterNumber = Integer.parseInt(chapter);

                    if (chapterNumber < chapterUrls.length - 1) {
                        imageLoader.cancel(true);

                        findViewById(R.id.recycle_view).setVisibility(View.GONE);
                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        imageLoader = new LoadImage(chapterUrls[Integer.parseInt(chapter)]);
                        imageLoader.execute();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(),"Invalid Chapter",Toast.LENGTH_SHORT).show();
                    }

                }
            });

            dialog.findViewById(R.id.button_chapter_select_cancel).setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    dialog.dismiss();
                  }
              });

            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }


    public class LoadImage extends AsyncTask<String, Void, List<String>> {
        String mangaName;
        ProgressBar pb;
        MultiClickRecyclerView rv;

        public LoadImage(String managaName) {
            this.mangaName = managaName;
        }

        @Override
        protected List<String> doInBackground(String... strings) {


            InputStream is;

            //urls of images
            List<String> urls = new ArrayList<>();
            try {
                //url of page
                URL url = new URL(mangaName);


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