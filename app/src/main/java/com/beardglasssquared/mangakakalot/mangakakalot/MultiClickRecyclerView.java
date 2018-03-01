package com.beardglasssquared.mangakakalot.mangakakalot;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by erick on 2/28/2018.
 */

public class MultiClickRecyclerView extends RecyclerView {

    public MultiClickRecyclerView(Context context) {
        super(context);
    }

    public MultiClickRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiClickRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && this.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
            this.stopScroll();
        }
        CardView cardView = (CardView) this.findChildViewUnder(event.getX(), event.getY());

        if (cardView !=  null) {
            PhotoView photoView = (PhotoView)cardView.findViewById(R.id.photo_view);
            if (photoView != null) {
                if (photoView.getScale() > 1.0) return false;
                else return super.onInterceptTouchEvent(event);
            }

        }
        return false;
    }
}