package com.example.omninventory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

// referenced from https://stackoverflow.com/questions/30531091/how-to-disable-recyclerview-scrolling
public class NonScrollRecyclerView extends RecyclerView {

    public NonScrollRecyclerView(Context context) {
        super(context);
        makeNonScroll();
    }

    public NonScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        makeNonScroll();
    }

    public NonScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        makeNonScroll();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

    public void makeNonScroll() {
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