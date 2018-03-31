package com.beardglasssquared.mangakakalot.mangakakalot;

import android.support.annotation.NonNull;

/**
 * Created by erick on 3/17/2018.
 */

public class MangaLink implements Comparable<MangaLink> {
    String imageUrl;
    String title;
    String chapters;
    String mangaUrl;
    String pageNumber;
    long time;

    public MangaLink(String title, String imageUrl, String chapters, String mangaUrl, long time)
    {
        this.chapters = chapters;
        this.imageUrl = imageUrl;
        this.mangaUrl = mangaUrl;
        this.title = title;
        this.time = time;
    }

    //sorted from new to low
    @Override
    public int compareTo(@NonNull MangaLink mangaLink) {
        if (this.time > mangaLink.time)
            return -1;
        if (this.time < mangaLink.time)
            return 1;
        else
        return 0;
    }
}
