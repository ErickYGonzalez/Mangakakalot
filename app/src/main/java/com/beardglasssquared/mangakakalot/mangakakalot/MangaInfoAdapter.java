package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by erick on 3/18/2018.
 */

public class MangaInfoAdapter extends RecyclerView.Adapter<MangaInfoAdapter.MoreInfoHolder>{

    Manga manga;
    String[] urls;
    Context context;

    public MangaInfoAdapter(Manga manga, Context context) {
        this.manga = manga;
        urls = manga.chaptersLinks;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return urls.length;
    }

    @Override
    public void onBindViewHolder(MangaInfoAdapter.MoreInfoHolder holder, final int position) {
        String url = urls[position];
        holder.tv.setText("Chapter " + url.substring(url.indexOf("chapter_") + "chapter_".length()));
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ReadingActivity.class);
                intent.putExtra("chapterUrls",urls);
                intent.putExtra("chapterNumber",position);
                intent.putExtra("name",manga.title);
                intent.putExtra("mangaUrl",manga.mangaUrl);
                intent.putExtra("imgUrl",manga.imgUrl);

                context.startActivity(intent);
            }
        });
    }
    @Override
    public MangaInfoAdapter.MoreInfoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.chapter, viewGroup, false);

        return new MangaInfoAdapter.MoreInfoHolder(itemView);
    }

    public static class MoreInfoHolder extends RecyclerView.ViewHolder {

        TextView tv;
        public MoreInfoHolder(View v) {
            super(v);
            tv = v.findViewById(R.id.text_chapter_number);
        }
    }
}

