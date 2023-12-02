package com.example.omninventory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/** Referenced from https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
 *
 */
public class StaticListView extends ListView {

    public StaticListView(Context context) {
        super(context);
    }
    public StaticListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public StaticListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}