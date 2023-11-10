package com.example.omninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class ManageTagsActivity extends AppCompatActivity {

    private InventoryRepository repo;
    private ArrayList<Tag> tagListData;
    private TagAdapter tagListAdapter;
    private ListView tagList;
    private TextView titleText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tags);

        repo = new InventoryRepository();

        // Get references to views
        tagList = findViewById(R.id.tag_list);
        titleText = findViewById(R.id.title_text);

        // === UI setup
        titleText.setText(getString(R.string.manage_tags_title_text)); // set title text

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_manage_tags, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

        // get taskbar buttons
        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton deleteTagButton = findViewById(R.id.delete_tag_button);
        ImageButton addTagButton = findViewById(R.id.add_tag_button);
        ImageButton saveButton = findViewById(R.id.save_button);

        tagListData = new ArrayList<>();

        tagListAdapter = new TagAdapter(this, tagListData);
        tagList.setAdapter(tagListAdapter);

        ListenerRegistration registration = repo.setupTagList(tagListAdapter);



        // set up click actions
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // display an exit message
                CharSequence toastText = "Tag edits discarded.";
                Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                toast.show();

                // return without changing any item fields
                finish();
            }
        });

    }
}