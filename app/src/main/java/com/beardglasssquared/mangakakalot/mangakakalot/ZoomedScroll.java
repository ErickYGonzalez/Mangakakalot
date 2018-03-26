package com.beardglasssquared.mangakakalot.mangakakalot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * Created by erick on 2/27/2018.
 */

public class ZoomedScroll implements View.OnTouchListener {
    private float xTouch,yTouch;

    int height, width;

    private int mActivePointerId = INVALID_POINTER_ID;
    private PhotoView photoView;
    private RecyclerView recyclerView;
    private int position;
    private float zoomAmount;
    private Context context;
    private boolean isLongPressed = false, isDown = true;


    public ZoomedScroll(PhotoView photoView, RecyclerView recyclerView, Context context, int position)
    {
        super();
        this.photoView = photoView;
        photoView.setMaximumScale(10f);
        this.recyclerView = recyclerView;
        this.position = position;
        this.context = context;
    }



    @Override
    public boolean onTouch(View view, MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where we started (for dragging)


                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);


                //Log.d("Anchor: ", "x = " + String.valueOf(xTouch) + ", y = " + String.valueOf(yTouch) );

                /*
                //disable scrolling
                recyclerView.smoothScrollToPosition(position);
                photoView.setScale(1.5f,xOffset ,yOffset ,true);
                */


                xTouch = ev.getX();
                yTouch = ev.getY();

                final Runnable r = new Runnable() {
                    @SuppressLint("ResourceType")
                    public void run() {
                        if (isDown)
                        {
                            height = photoView.getHeight();
                            width = photoView.getWidth();

                            //recyclerView.smoothScrollToPosition(position);
                            photoView.setZoomTransitionDuration(100);

                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                            int pos = sharedPref.getInt(context.getString(R.string.zoom_amount),1);
                            zoomAmount = 1.25f + 0.25f * pos;

                            photoView.setScale(zoomAmount,getXProjection(xTouch,width) ,getYProjection(yTouch,height),true);
                            isLongPressed = true;
                        }
                    }
                };

                isDown = true;
                android.os.Handler h = new android.os.Handler();
                h.postDelayed(r,250);


                break;
            }

            case MotionEvent.ACTION_MOVE: {
                height = photoView.getHeight();
                width = photoView.getWidth();

                xTouch= ev.getX();
                yTouch = ev.getY();

                if (isLongPressed)
                {

                    recyclerView.scrollToPosition(position);

                    //Disables Scrollview
                    photoView.setScale(zoomAmount, getXProjection(xTouch,width), getYProjection(yTouch,height),false);
                    recyclerView.stopScroll();

                }

                break;
            }

            case MotionEvent.ACTION_UP: {
                photoView.setScale(1.0f,true);
                isDown = false;
                isLongPressed = false;
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                photoView.setScale(1.0f,true);
                isDown = false;
                isLongPressed = false;
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                break;
            }
        }
        return true;

    }

    private float getXProjection(float x, int width)
    {
        //Projection of xOffset to the zoom
        if (xTouch < width/4)
        {
            return 0;
        }
        else if (xTouch < 3 * width /4)
        {
            return (2 * xTouch - width / 2);
        } else {
            return width;
        }
    }

    private float getYProjection(float y, int height)
    {

        if (yTouch < height/2)
        {
            return 0;
        } else {
            return yTouch * 3 - 3 * height /2;
        }
    }

}