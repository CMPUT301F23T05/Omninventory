package com.example.omninventory;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class InventoryRepository {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference inventoryItemsRef;

    public InventoryRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Add a new InventoryItem to the inventoryItem collection. Also adds reference to the current
     * User's list of owned items.
     * @param currentUser
     * @param item
     */
    public void addInventoryItem(User currentUser, InventoryItem item) {
        // create data for new item document
        HashMap<String, Object> itemData = new HashMap<>();
        itemData.put("name", item.getName());
        itemData.put("description", item.getDescription());
        itemData.put("comment", item.getComment());
        itemData.put("make", item.getMake());
        itemData.put("model", item.getModel());
        itemData.put("serialno", item.getSerialno());
        itemData.put("value", item.getValue());
        itemData.put("date", item.getDate());
        // TODO: tags and images go here

        // create new inventoryItems document with auto-generated id
        DocumentReference newInventoryItemRef = db.collection("inventoryItems").document();

        // set data of new document
        newInventoryItemRef
            .set(itemData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "New inventoryItems DocumentSnapshot written");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding inventoryItems document", e);
                }
            });

        // get document for currentUser (id is username)
        DocumentReference currentUserRef = db.collection("users").document(currentUser.getUsername());

        // add new inventoryItem reference to currentUser's list of ownedItems
        Object[] arrayToAdd = {newInventoryItemRef};

        currentUserRef
            .update("ownedItems", FieldValue.arrayUnion(arrayToAdd))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Updated users DocumentSnapshot");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });
    }

    // TODO: implement these...
    public void deleteInventoryItem() {};
    public void updateInventoryItem() {};

    public void addUser() {};
}
