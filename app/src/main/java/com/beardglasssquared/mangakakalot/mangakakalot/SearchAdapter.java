package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

/**
 * Created by erick on 3/25/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder>{

    List<MangaLink> urls;
    Context context;

    public SearchAdapter(Context applicationContext, List<MangaLink> urls) {
        context = applicationContext;
        this.urls = urls;

    }

    @Override
    public int getItemCount() {
        return urls.size();
    }


    @Override
    public SearchHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.chapter, viewGroup, false);

        return new SearchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, final int position) {
        holder.tv.setText(urls.get(position).title);
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MangaInfoActiviy.class);

                String imgUrl = "NONE";
                String name = urls.get(position).title;
                String mangaUrl = urls.get(position).mangaUrl;
                //String

                intent.putExtra("imgurl",imgUrl);
                intent.putExtra("name",name);
                intent.putExtra("mangaUrl",mangaUrl);

                context.startActivity(intent);
            }
        });
    }

    public static class SearchHolder extends RecyclerView.ViewHolder {

        TextView tv;
        public SearchHolder(View v) {
            super(v);
            tv = (TextView) v.findViewById(R.id.text_chapter_number);
        }
    }
}
