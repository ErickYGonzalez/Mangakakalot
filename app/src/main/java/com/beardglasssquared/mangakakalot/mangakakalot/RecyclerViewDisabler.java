package com.beardglasssquared.mangakakalot.mangakakalot;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by erick on 2/27/2018.
 */

public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}