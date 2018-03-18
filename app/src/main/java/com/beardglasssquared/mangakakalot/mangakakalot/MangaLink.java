package com.beardglasssquared.mangakakalot.mangakakalot;

/**
 * Created by erick on 3/17/2018.
 */

public class MangaLink {
    String imageUrl;
    String title;
    String chapters;
    String mangaUrl;

    public MangaLink(String title, String imageUrl, String chapters, String mangaUrl)
    {
        this.chapters = chapters;
        this.imageUrl = imageUrl;
        this.mangaUrl = mangaUrl;
        this.title = title;
    }
}
