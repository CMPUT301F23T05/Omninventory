package com.example.omninventory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NonScrollRecyclerView extends RecyclerView {

    public NonScrollRecyclerView(Context context) {
        super(context);
        this.makeNonScroll();
    }

    public NonScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.makeNonScroll();
    }

    public NonScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.makeNonScroll();
    }

    public void makeNonScroll() {
        // referenced from https://stackoverflow.com/questions/30531091/how-to-disable-recyclerview-scrolling
        this.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
        });
    }
}