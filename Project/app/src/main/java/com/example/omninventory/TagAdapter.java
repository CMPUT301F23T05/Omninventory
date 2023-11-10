package com.example.omninventory;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter that works with Tag objects. Uses tag_list_content.xml
 * for layout display of Tags in a ListView.
 */
public class TagAdapter extends ArrayAdapter<Tag>  {
    private ArrayList<Tag> tagListData;
    private Context context;

    public TagAdapter(Context context, ArrayList<Tag> tags)  {
        super(context, 0, tags);
        this.tagListData = tags;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // === setup view
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.tag_list_content, parent, false);
        }

        // === find views
        TextView tagNameText = view.findViewById(R.id.tag_name_text);
        TextView tagDetailText = view.findViewById(R.id.tag_detail_text);

        // === UI setup
        Tag tag = tagListData.get(position);

        // set fields
        tagNameText.setText(tag.getName());
        tagDetailText.setText(String.format("Currently applied to %d items", tag.getItemCount()));

        return view;
    }
}
