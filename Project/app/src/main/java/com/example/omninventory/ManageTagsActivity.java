package com.example.omninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.ListIterator;

public class ManageTagsActivity extends AppCompatActivity {

    private InventoryRepository repo;
    private ArrayList<Tag> tagListData;
    private TagAdapter tagListAdapter;
    private ListView tagList;
    private TextView titleText;
    private Dialog addTagDialog;

    private void addTagDialog() {

        addTagDialog.setCancelable(false);
        addTagDialog.setContentView(R.layout.add_tag_dialog);

        EditText tagNameEditText = addTagDialog.findViewById(R.id.new_tag_name_editText);
        Button addTagDialogButton = addTagDialog.findViewById(R.id.add_tag_dialog_button);
        Button cancelDialogButton = addTagDialog.findViewById(R.id.cancel_dialog_button);

        addTagDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagName = tagNameEditText.getText().toString();
                if (tagName.isEmpty()) {
                    CharSequence toastText = "Tag name cannot be empty!";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                boolean duplicate = false;
                ListIterator<Tag> tagIter = tagListData.listIterator();
                while (tagIter.hasNext()) {
                    Tag nextTag = tagIter.next();
                    if (tagName.equals(nextTag.getName())) {
                        duplicate = true;
                    }
                }
                if (duplicate) {
                    CharSequence toastText = "This tag already exists!";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                repo.addTag(new Tag(tagName));
                CharSequence toastText = "Tag added successfully!";
                Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                toast.show();
                addTagDialog.dismiss();


            }
        });
        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTagDialog.dismiss();
            }
        });

        addTagDialog.show();
    }
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

        addTagDialog = new Dialog(this);


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

        addTagButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addTagDialog();
            }
        });

    }
}