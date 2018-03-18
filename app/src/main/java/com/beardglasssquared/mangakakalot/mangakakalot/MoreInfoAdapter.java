package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by erick on 3/18/2018.
 */

public class MoreInfoAdapter extends RecyclerView.Adapter<MoreInfoAdapter.MoreInfoHolder>{

    List<String> urls;
    Context context;

    public MoreInfoAdapter(List<String> urls, Context context) {
        this.urls = urls;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    @Override
    public void onBindViewHolder(MoreInfoAdapter.MoreInfoHolder holder, final int position) {
        String url = urls.get(position);
        holder.tv.setText("Chapter " + url.substring(url.indexOf("chapter_") + "chapter_".length()));
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ReadingActivity.class);
                String[] chapterUrls = new String[urls.size()];
                chapterUrls = urls.toArray(chapterUrls);
                intent.putExtra("chapterUrls",chapterUrls);
                intent.putExtra("chapterNumber",position);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public MoreInfoAdapter.MoreInfoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.chapter, viewGroup, false);

        return new MoreInfoAdapter.MoreInfoHolder(itemView);
    }

    public static class MoreInfoHolder extends RecyclerView.ViewHolder {

        TextView tv;
        public MoreInfoHolder(View v) {
            super(v);
            tv = (TextView) v.findViewById(R.id.text_chapter_number);
        }
    }
}

