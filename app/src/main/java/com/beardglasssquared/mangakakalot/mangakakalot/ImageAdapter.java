package com.beardglasssquared.mangakakalot.mangakakalot;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Erick on 8/6/2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder>{
    Context context;
    MultiClickRecyclerView rv;
    List<String> urls;
    int wrap = ViewGroup.LayoutParams.MATCH_PARENT;


    public ImageAdapter(List<String> urls, Context context, MultiClickRecyclerView rv) {
        this.urls = urls;
        this.context = context;
        this.rv = rv;

    }

    @Override
    public int getItemCount() {
        return urls.size();

    }

    public void switchWidth() {
        wrap = wrap == ViewGroup.LayoutParams.MATCH_PARENT ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
    }


    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        Picasso.with(context).load(urls.get(position)).into(holder.vPhotoView);


        holder.vCardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, wrap));


        //this is where I can change the views to act the way I want with "setOn<Event>"

        /*
        holder.vPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                rv.smoothScrollToPosition(p);

                if (pv.getScale() != 1.0f) {
                    pv.setZoomTransitionDuration(50);
                    pv.setScale(1.0f,true);
                } else {

                }
                return true;
            }
        });*/

        holder.vPhotoView.setOnTouchListener(new ZoomedScroll(holder.vPhotoView,rv,position));


    }
    @Override
    public ImageHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.image_card_view, viewGroup, false);

        return new ImageHolder(itemView);
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {
        protected PhotoView vPhotoView;
        protected CardView vCardView;

        public ImageHolder(View v) {
            super(v);
            vPhotoView = v.findViewById(R.id.photo_view);
            vCardView = v.findViewById(R.id.card_view);

        }
    }
}


