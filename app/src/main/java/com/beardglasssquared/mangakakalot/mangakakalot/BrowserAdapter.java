package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by erick on 3/17/2018.
 */

public class BrowserAdapter extends RecyclerView.Adapter<BrowserAdapter.BrowserHolder>{
    Context context;
    RecyclerView rv;
    List<MangaLink> urls;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 4;

    public BrowserAdapter(List<MangaLink> urls, Context context,RecyclerView rv) {
        this.urls = urls;
        this.context = context;
        if (rv.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rv
                    .getLayoutManager();


            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager
                            .findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        Log.d("Loading....","Reached Bottom!!!");

                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return urls.size()/2 + 1;
    }

    @Override
    public void onBindViewHolder(final BrowserAdapter.BrowserHolder holder, int position) {
        final int leftIndex = position * 2;
        final int rightIndex = position * 2 + 1;
        if (leftIndex < urls.size())
        {
            final int[] color = new int[4];
            color[0] = Color.parseColor("#ffffff");
            color[1] = Color.parseColor("#000000");
            color[2] = Color.parseColor("#808080");
            color[3] = Color.parseColor("#ffffff");

            holder.cardL.setVisibility(View.VISIBLE);
            Picasso.with(context).load(urls.get(leftIndex).imageUrl).into(holder.imageL, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) holder.imageL.getDrawable()).getBitmap();
                    if (bitmap != null) {
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch vibSwatch = palette.getDarkVibrantSwatch();
                                if (vibSwatch == null) {
                                    vibSwatch = palette.getDominantSwatch();
                                }
                                color[0] = vibSwatch.getRgb();
                                color[1] = vibSwatch.getTitleTextColor();
                                color[2] = vibSwatch.getBodyTextColor();
                                //Darken primary color to get secondary
                                int darkerRGB = vibSwatch.getRgb();
                                float[] hsv = vibSwatch.getHsl();
                                Color.colorToHSV(darkerRGB, hsv);
                                hsv[2] *= 0.9f; // value component
                                color[3] = Color.HSVToColor(hsv);
                            }
                        });
                    }
                }

                @Override
                public void onError() {

                }
            });


            holder.titleL.setText(urls.get(leftIndex).title);
            holder.chapterL.setText(urls.get(leftIndex).chapters);

            holder.imageL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MangaInfoActivity.class);


                    String imgUrl = urls.get(leftIndex).imageUrl;
                    String name = urls.get(leftIndex).title;
                    String mangaUrl = urls.get(leftIndex).mangaUrl;
                    //String

                    intent.putExtra("imgurl",imgUrl);
                    intent.putExtra("name",name);
                    intent.putExtra("mangaUrl",mangaUrl);
                    intent.putExtra("mainColor",color[0]);
                    intent.putExtra("titleColor",color[1]);
                    intent.putExtra("bodyColor",color[2]);
                    intent.putExtra("secondColor",color[3]);



                    context.startActivity(intent);
                }
            });
        } else {
            holder.cardL.setVisibility(View.GONE);
            holder.cardR.setVisibility(View.GONE);
        }

        if (rightIndex < urls.size()) {
            final int[] color = new int[4];
            color[0] = Color.parseColor("#ffffff");
            color[1] = Color.parseColor("#000000");
            color[2] = Color.parseColor("#808080");
            color[3] = Color.parseColor("#ffffff");

            holder.cardR.setVisibility(View.VISIBLE);
            Picasso.with(context).load(urls.get(rightIndex).imageUrl).into(holder.imageR, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) holder.imageR.getDrawable()).getBitmap();
                    if (bitmap != null) {
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch vibSwatch = palette.getDarkVibrantSwatch();
                                if (vibSwatch == null) {
                                    vibSwatch = palette.getDominantSwatch();
                                }
                                color[0] = vibSwatch.getRgb();
                                color[1] = vibSwatch.getTitleTextColor();
                                color[2] = vibSwatch.getBodyTextColor();

                                //Darken primary color to get secondary
                                int darkerRGB = vibSwatch.getRgb();
                                float[] hsv = vibSwatch.getHsl();
                                Color.colorToHSV(darkerRGB, hsv);
                                hsv[2] *= 0.9f; // value component
                                color[3] = Color.HSVToColor(hsv);
                            }
                        });
                    }
                }

                @Override
                public void onError() {

                }
            });

            holder.titleR.setText(urls.get(rightIndex).title);
            holder.chapterR.setText(urls.get(leftIndex).chapters);
            holder.imageR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MangaInfoActivity.class);

                    String imgUrl = urls.get(rightIndex).imageUrl;
                    String name = urls.get(rightIndex).title;
                    String mangaUrl = urls.get(rightIndex).mangaUrl;
                    //String

                    intent.putExtra("imgurl",imgUrl);
                    intent.putExtra("name",name);
                    intent.putExtra("mangaUrl",mangaUrl);
                    intent.putExtra("mainColor",color[0]);
                    intent.putExtra("titleColor",color[1]);
                    intent.putExtra("bodyColor",color[2]);
                    intent.putExtra("secondColor",color[3]);

                    context.startActivity(intent);
                }
            });
        }
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

        protected CardView cardL;
        protected CardView cardR;

        public BrowserHolder(View v) {
            super(v);
            imageL = v.findViewById(R.id.image_left);
            imageR = v.findViewById(R.id.image_right);

            titleL = v.findViewById(R.id.title_left);
            titleR = v.findViewById(R.id.title_right);

            chapterL = v.findViewById(R.id.chapter_left);
            chapterR = v.findViewById(R.id.chapter_right);

            cardL = v.findViewById(R.id.card_left);
            cardR = v.findViewById(R.id.card_right);
        }
    }
}

