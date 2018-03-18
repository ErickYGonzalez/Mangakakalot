package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by erick on 3/17/2018.
 */

public class BrowserAdapter extends RecyclerView.Adapter<BrowserAdapter.BrowserHolder>{
    Context context;
    MultiClickRecyclerView rv;
    List<MangaLink> urls;

    public BrowserAdapter(List<MangaLink> urls, Context context) {
        this.urls = urls;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return urls.size()/2;

    }

    @Override
    public void onBindViewHolder(BrowserAdapter.BrowserHolder holder, int position) {
        final int leftIndex = position * 2;
        final int rightIndex = position * 2 + 1;

        Picasso.with(context).load(urls.get(leftIndex).imageUrl).into(holder.imageL);
        Picasso.with(context).load(urls.get(rightIndex).imageUrl).into(holder.imageR);

        holder.titleL.setText(urls.get(leftIndex).title);
        holder.titleR.setText(urls.get(rightIndex).title);

        holder.chapterR.setText(urls.get(leftIndex).chapters);
        holder.chapterL.setText(urls.get(rightIndex).chapters);

        //add on click listeners to title and images

        holder.imageL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MangaInfoActiviy.class);


                String imgUrl = urls.get(leftIndex).imageUrl;
                String name = urls.get(leftIndex).title;
                String mangaUrl = urls.get(leftIndex).mangaUrl;
                //String

                intent.putExtra("imgurl",imgUrl);
                intent.putExtra("name",name);
                intent.putExtra("mangaUrl",mangaUrl);

                context.startActivity(intent);
            }
        });

        holder.imageR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MangaInfoActiviy.class);

                String imgUrl = urls.get(rightIndex).imageUrl;
                String name = urls.get(rightIndex).title;
                String mangaUrl = urls.get(rightIndex).mangaUrl;
                //String

                intent.putExtra("imgurl",imgUrl);
                intent.putExtra("name",name);
                intent.putExtra("mangaUrl",mangaUrl);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public BrowserAdapter.BrowserHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.manga_link_layout, viewGroup, false);

        return new BrowserAdapter.BrowserHolder(itemView);
    }

    public static class BrowserHolder extends RecyclerView.ViewHolder {
        protected ImageView imageL;
        protected ImageView imageR;

        protected TextView titleL;
        protected TextView titleR;

        protected TextView chapterL;
        protected TextView chapterR;

        public BrowserHolder(View v) {
            super(v);
            imageL = (ImageView) v.findViewById(R.id.image_left);
            imageR = (ImageView) v.findViewById(R.id.image_right);

            titleL = (TextView) v.findViewById(R.id.title_left);
            titleR = (TextView) v.findViewById(R.id.title_right);

            chapterL = (TextView) v.findViewById(R.id.chapter_left);
            chapterR = (TextView) v.findViewById(R.id.chapter_right);
        }
    }
}

