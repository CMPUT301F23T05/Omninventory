package com.example.omninventory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A RecyclerView that doesn't try to scroll within the parent View. Useful for when we have a
 * RecyclerView that holds images inside a ScrollView (that is, in DetailsActivity and
 * EditActivity).
 * @author Castor
 * @reference https://stackoverflow.com/questions/30531091/how-to-disable-recyclerview-scrolling
 */
public class NonScrollRecyclerView extends RecyclerView {

    /**
     * Constructor that initializes the NonScrollRecyclerView from context.
     * @param context
     */
    public NonScrollRecyclerView(Context context) {
        super(context);
        makeNonScroll();
    }

    /**
     * Constructor that initializes the NonScrollRecyclerView from context and additional attributes.
     * @param context
     * @param attrs
     */
    public NonScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        makeNonScroll();
    }

    /**
     * Constructor that initializes the NonScrollRecyclerView from context, additional
     * attributes, and style.
     * @param context
     * @param attrs
     * @param defStyle
     */
    public NonScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        makeNonScroll();
    }

    /**
     * Method that enforces set RecyclerView size (necessary because if we want a non-scrolling
     * RecyclerView, we want it to be big enough to show all items without scrolling).
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @reference https://stackoverflow.com/questions/30531091/how-to-disable-recyclerview-scrolling
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

    /**
     * When called, adds an OnItemTouchListener that prevents touches on this RecyclerView from
     * causing scroll.
     * @reference https://stackoverflow.com/questions/30531091/how-to-disable-recyclerview-scrolling
     */
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