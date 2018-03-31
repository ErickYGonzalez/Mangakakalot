package com.beardglasssquared.mangakakalot.mangakakalot;

/**
 * Created by erick on 3/17/2018.
 */

public class Manga {
    /*
    title
    author
    description
    current number of chapter
    array of links to all chapters
    int chapter - based on index of all chapters

    Other data available
    rating
    view
    status
    last updated
     */

    String title;
    String description;
    String author;
    String[] chaptersLinks;
    public String mangaUrl;
    public String imgUrl;

    public Manga (String title, String[] chaptersLinks)
    {
        this.title = title;
        this.chaptersLinks = chaptersLinks;
    }

    public Manga(){

    }

}
