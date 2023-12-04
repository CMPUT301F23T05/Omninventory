package com.example.omninventory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ListIterator;

/**
 * Activity for applying tags to one or more items. Accepts a flag as part of the intent passed by
 * the parent function to determine whether it will immediately apply the tag changes upon completion
 * or return an inventoryItem object with the tags added.
 *
 * @author Patrick
 */
public class ApplyTagsActivity extends AppCompatActivity  {
    private InventoryRepository repo;
    private ArrayList<InventoryItem> selectedItems;
    private Boolean apply;
    private User currentUser;

    private ArrayList<Tag> appliedTagsListData;
    private ArrayList<Tag> unappliedTagsListData;

    private ListView appliedTagsList;
    private TagAdapter appliedTagsListAdapter;
    private ListView unappliedTagsList;
    private TagAdapter unappliedTagsListAdapter;
    private Dialog addTagDialog;
    private ImageButton backButton;
    private ImageButton addTagButton;
    private ImageButton confirmTagsButton;

    /**
     * Method called on Activity creation. Contains most of the logic of this Activity; programmatically
     * modifying UI elements, reading information from previous activity, setting up connection to
     * the database, and setting up click listeners to enable functionality.
     *
     * @param savedInstanceState Information about this Activity's saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_tags);

        // === set up database
        repo = new InventoryRepository();


        // === get references to views
        final TextView titleText = findViewById(R.id.title_text);
        if (apply) {
            titleText.setText(R.string.apply_tags_title_text);
        } else {
            titleText.setText("EDIT TAGS");
        }
        appliedTagsList = findViewById(R.id.applied_tag_list);
        unappliedTagsList = findViewById(R.id.unapplied_tag_list);
        backButton = findViewById(R.id.back_button);
        addTagButton = findViewById(R.id.add_tag_button);
        confirmTagsButton = findViewById(R.id.confirm_tags_button);
        addTagDialog = new Dialog(this);

        // === load info passed in from previous activity
        selectedItems = (ArrayList<InventoryItem>) getIntent().getExtras().get("selectedItems");
        apply = (Boolean) getIntent().getExtras().get("apply"); // "return" or "apply", controls what to do on return
        if (getIntent().getExtras().getSerializable("user") == null) {
            Log.d("ApplyTagsActivity", "ApplyTagsActivity opened without a User; possibly concerning");
        }
        else {
            currentUser = (User) getIntent().getExtras().getSerializable("user");
        }

        // === set up the ListViews, Adapters, etc
        appliedTagsListData = new ArrayList<>();
        unappliedTagsListData = new ArrayList<>();

        appliedTagsListAdapter = new TagAdapter(this, appliedTagsListData);
        unappliedTagsListAdapter = new TagAdapter(this, unappliedTagsListData);

        if (!apply) {
            ListenerRegistration registration = repo.setupTagListsForItem(appliedTagsListAdapter, unappliedTagsListAdapter, currentUser, selectedItems.get(0));
        } else {
            ListenerRegistration registration = repo.setupTagList(unappliedTagsListAdapter, currentUser);
        }

        appliedTagsList.setAdapter(appliedTagsListAdapter);
        unappliedTagsList.setAdapter(unappliedTagsListAdapter);

        // === set up click actions

        // clicking a tag in the unapplied list should apply it
        unappliedTagsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tag tagClicked = unappliedTagsListAdapter.getItem(i);
                appliedTagsListAdapter.add(tagClicked);
                unappliedTagsListAdapter.remove(tagClicked);
            }
        });

        // clicking an applied tag should unapply it
        appliedTagsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tag tagClicked = appliedTagsListAdapter.getItem(i);
                unappliedTagsListAdapter.add(tagClicked);
                appliedTagsListAdapter.remove(tagClicked);
            }
        });

        // clicking the back button should return to the previous activity without saving any tags
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence toastText = "No tags applied.";
                Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                toast.show();

                // if in "return" mode we return the same item we were passed
                if (!apply) {
                    Intent itemReturn = new Intent();
                    itemReturn.putExtra("taggedItem", selectedItems.get(0));
                    setResult(RESULT_OK, itemReturn);
                }

                finish();
            }
        });

        // add button should open a dialog to define a new tag
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Tag> allTags = new ArrayList<>();
                allTags.addAll(appliedTagsListData);
                allTags.addAll(unappliedTagsListData);
                addTagDialog(allTags, null);
            }
        });

        // if in "return" mode, the confirm button should apply the tags to a local InventoryItem and return it
        if (!apply) {
            confirmTagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selectedItems.get(0).setTags(appliedTagsListData);

                    Intent itemReturn = new Intent();
                    itemReturn.putExtra("taggedItem", selectedItems.get(0));
                    setResult(RESULT_OK, itemReturn);
                    finish();
                }
            });

            // if in "apply" mode, confirm button should apply the tags to the item(s) in the db
        } else {
            confirmTagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    repo.applyTagsToItems(appliedTagsListData, selectedItems);
                    finish();
                }
            });
        }




    }

    /**
     * Displays a dialog that allows the user to specify a new tag, which will appear in the
     * unapplied tags list.
     */
    private void addTagDialog(ArrayList<Tag> tagList, @Nullable Tag tag) {

        addTagDialog.setCancelable(false);
        addTagDialog.setContentView(R.layout.add_tag_dialog);

        // UI Elements
        EditText tagNameEditText = addTagDialog.findViewById(R.id.new_tag_name_editText);
        EditText tagPriorityEditText = addTagDialog.findViewById(R.id.new_tag_priority_editText);
        Button addTagDialogButton = addTagDialog.findViewById(R.id.add_tag_dialog_button);
        Button cancelDialogButton = addTagDialog.findViewById(R.id.cancel_dialog_button);
        TextView dialogHeader = addTagDialog.findViewById(R.id.dialog_tag_text);

        if (tag != null) {
            Log.d("ManageTagsActivity", "ManageTagsActivity is here");
            tagNameEditText.setText(tag.getName());
            tagPriorityEditText.setText(Long.toString(tag.getPriority()));
            dialogHeader.setText("EDIT TAG");
        } else {
            dialogHeader.setText("ADD TAG");
        }
        // Add tag button will create a new tag with the name specified in tagNameEditText
        addTagDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check tag name not empty
                String tagName = tagNameEditText.getText().toString();
                String tagPriorityString = tagPriorityEditText.getText().toString();
                int priority = 0;

                if (tagName.isEmpty()) {
                    CharSequence toastText = "Tag name cannot be empty!";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                try {
                    priority = Integer.parseInt(tagPriorityEditText.getText().toString());
                } catch(NumberFormatException e) {
                    CharSequence toastText = "Invalid priority! Setting to 0...";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                    priority = 0;
                }
                if (priority < 0) {
                    CharSequence toastText = "Invalid priority! Setting to 0...";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                    priority = 0;
                }

                // Check tag isn't a duplicate of an existing one
                boolean duplicate = false;
                ListIterator<Tag> tagIter = tagList.listIterator();
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

        // The cancel button will dismiss the dialog without creating the tag
        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTagDialog.dismiss();
            }
        });

        addTagDialog.show();
    }

}

