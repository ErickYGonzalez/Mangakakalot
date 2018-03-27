package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HotMangaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotMangaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotMangaFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    int pageNumber = 1;
    LoadHotMangaPage lpp;

    public HotMangaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HotMangaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HotMangaFragment newInstance() {
        HotMangaFragment fragment = new HotMangaFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lpp = new LoadHotMangaPage(pageNumber);
        lpp.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot_manga, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class LoadHotMangaPage extends AsyncTask<String, Void, List<MangaLink>> {

        int pageNumber;
        ProgressBar pb;
        RecyclerView rv;

        public LoadHotMangaPage(int pageNumber)
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

                                //Log.d("Image Url",imageUrl);
                                MangaLink link = new MangaLink(title,imageUrl,"",mangaUrl);
                                mangaLinks.add(link);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Unable to load ", Toast.LENGTH_LONG).show();

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
            pb = getView().findViewById(R.id.progressBar);
            rv = getView().findViewById(R.id.recycle_view);


            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            BrowserAdapter browserAdapter = new BrowserAdapter(urls, getContext());
            rv.setAdapter(browserAdapter);

            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }
}
