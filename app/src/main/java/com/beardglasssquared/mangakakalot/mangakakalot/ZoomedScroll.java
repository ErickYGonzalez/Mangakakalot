package com.beardglasssquared.mangakakalot.mangakakalot;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * Created by erick on 2/27/2018.
 */

public class ZoomedScroll implements View.OnTouchListener {
    private float xOffset,yOffset,mPosX,mPosY;

    private int mActivePointerId = INVALID_POINTER_ID;
    private PhotoView photoView;
    private RecyclerView recyclerView;
    private int position;
    RecyclerViewDisabler disabler;


    public ZoomedScroll(PhotoView photoView, RecyclerView recyclerView, int position)
    {
        super();
        this.photoView = photoView;
        this.recyclerView = recyclerView;
        this.position = position;
        disabler = new RecyclerViewDisabler();
    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where we started (for dragging)
                xOffset = ev.getX();
                yOffset = ev.getY();

                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

                photoView.setScale(1.5f,xOffset,yOffset,true);

                //disable scrolling
                recyclerView.smoothScrollToPosition(position);
                recyclerView.addOnItemTouchListener(disabler);

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                view.getDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                xOffset = ev.getX();
                yOffset = ev.getY();

                Log.d("Touch Event Move: ", "x = " + String.valueOf(xOffset) + "/" + String.valueOf(width) +
                                                    " y = " + String.valueOf(yOffset) + "/" + String.valueOf(height));
                Log.d("PhotoView Specs:", "maxWidth = " + String.valueOf(photoView.getMaxWidth() + " maxHeight = " + String.valueOf(photoView.getMaxHeight())));

                photoView.setScale(1.5f,xOffset,yOffset,false);

                //TODO: Disable rv scrolling on touch

                break;
            }

            case MotionEvent.ACTION_UP: {
                photoView.setScale(1.0f,true);
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                photoView.setScale(1.0f,true);
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    xOffset = MotionEventCompat.getX(ev, newPointerIndex);
                    yOffset= MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;

    }
}