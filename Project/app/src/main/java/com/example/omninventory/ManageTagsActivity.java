package com.example.omninventory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Activity for viewing and managing the master taglist.
 * @author Patrick
 */
public class ManageTagsActivity extends AppCompatActivity {

    private InventoryRepository repo;
    private ArrayList<Tag> tagListData;
    private TagAdapter tagListAdapter;
    private ListView tagList;
    private TextView titleText;
    private Dialog addTagDialog;
    private User currentUser;

    /**
     * Method called on Activity creation. Contains most of the logic of this Activity; programmatically
     * modifying UI elements, creating Intents to move to other Activites, and setting up connection
     * to the database.
     * @param savedInstanceState Information about this Activity's saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tags);

        // === set up database
        repo = new InventoryRepository();

        // === get references to views
        tagList = findViewById(R.id.tag_list);
        titleText = findViewById(R.id.title_text);

        // === UI setup
        titleText.setText(getString(R.string.manage_tags_title_text)); // set title text
        addTagDialog = new Dialog(this);

//        // add taskbar
//        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_manage_tags, null);
//        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
//        taskbarHolder.addView(taskbarLayout);

        // get taskbar buttons
        final ImageButton backButton = findViewById(R.id.back_button);
        final ImageButton deleteTagButton = findViewById(R.id.delete_tag_button);
        final ImageButton addTagButton = findViewById(R.id.add_tag_button);
        final ImageButton saveButton = findViewById(R.id.save_button);

        if (getIntent().getExtras().getSerializable("user") == null) {
            Log.d("EditActivity", "EditActivity opened without a User; possibly concerning");
        }
        else {
            currentUser = (User) getIntent().getExtras().getSerializable("user");
        }

        // === set up tag ListView
        tagListData = new ArrayList<>();

        tagListAdapter = new TagAdapter(this, tagListData);
        tagList.setAdapter(tagListAdapter);

        ListenerRegistration registration = repo.setupTagList(tagListAdapter, currentUser);

        // === set up click actions

        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addEditTagDialog(tagListData.get(i));
            }
        });

        // back button should return to MainActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // add tag button will open a dialog to define a new tag
        addTagButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addEditTagDialog(null);
            }
        });

    }

    /**
     * Displays a dialog that allows the user to define a new tag or edit an existing one.
     */
    private void addEditTagDialog(@Nullable Tag tag) {

        addTagDialog.setCancelable(false);
        addTagDialog.setContentView(R.layout.add_tag_dialog);

        // UI Elements
        EditText tagNameEditText = addTagDialog.findViewById(R.id.new_tag_name_editText);
        EditText tagPriorityEditText = addTagDialog.findViewById(R.id.new_tag_priority_editText);
        Button addTagDialogButton = addTagDialog.findViewById(R.id.add_tag_dialog_button);
        Button cancelDialogButton = addTagDialog.findViewById(R.id.cancel_dialog_button);
        TextView dialogHeader = addTagDialog.findViewById(R.id.filter_by_tags_header_text);

        if (tag != null) {
            tagNameEditText.setText(tag.getName());
            tagPriorityEditText.setText(Long.toString(tag.getPriority()));
            dialogHeader.setText("EDIT TAG");
        } else {
            dialogHeader.setText("ADD TAG");
        }
        // Add tag button will create a new tag with the specified name
        addTagDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check tag name not empty
                String tagName = tagNameEditText.getText().toString();
                int priority = Integer.parseInt(tagPriorityEditText.getText().toString());
                if (tagName.isEmpty()) {
                    CharSequence toastText = "Tag name cannot be empty!";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                // Check tag isn't a duplicate of an existing one
                boolean duplicate = false;
                ListIterator<Tag> tagIter = tagListData.listIterator();
                while (tagIter.hasNext()) {
                    Tag nextTag = tagIter.next();
                    if (tagName.equals(nextTag.getName()) && (tag == null || !tag.getId().equals(nextTag.getId()))) {
                        duplicate = true;
                    }
                }
                if (duplicate) {
                    CharSequence toastText = "This tag already exists!";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                // If not empty and not a duplicate, create the tag and dismiss the dialog
                CharSequence toastText = "";
                if (tag == null) {
                    repo.addTag(new Tag(tagName, currentUser.getUsername(), priority));
                    toastText = "Tag added successfully!";
                } else {
                    tag.setName(tagName);
                    tag.setPriority(priority);
                    repo.updateTag(tag);
                    toastText = "Tag updated successfully!";
                }
                Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                toast.show();
                addTagDialog.dismiss();

            }
        });

        // The cancel button will dismiss the dialog without creating a new tag
        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTagDialog.dismiss();
            }
        });

        addTagDialog.show();
    }
}