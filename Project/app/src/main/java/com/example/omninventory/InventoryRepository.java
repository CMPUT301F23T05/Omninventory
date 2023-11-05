package com.example.omninventory;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Encapsulate behaviours related to Firestore, going from Firestore document to InventoryItem and
 * vice-versa, etc.
 */
public class InventoryRepository {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference inventoryItemsRef;

    /**
     * Basic constructor that sets up connection to Firestore and references.
     */
    public InventoryRepository() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        inventoryItemsRef = db.collection("inventoryItems");
    }

    /**
     * Sets up an InventoryItemAdapter as a list that is automatically updated when inventoryItem
     * changes.
     * @param adapter
     */
    public void setupInventoryItemList(InventoryItemAdapter adapter) {
        inventoryItemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, error.toString());
                    return;
                }
                if (value != null) {
                    adapter.clear(); // clear existing list data
                    for (QueryDocumentSnapshot doc : value) {
                        InventoryItem item = convertDocumentToInventoryItem(doc);
                        adapter.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * Convert fields of a QueryDocumentSnapshot from the inventoryItem collection to an
     * InventoryItem.
     * @param doc
     * @return
     */
    public InventoryItem convertDocumentToInventoryItem(QueryDocumentSnapshot doc) {
        InventoryItem item = new InventoryItem(
            doc.getString("name"),
            doc.getString("description"),
            doc.getString("comment"),
            doc.getString("make"),
            doc.getString("model"),
            doc.getString("serialno"),
            doc.getLong("value").intValue(),
            doc.getDate("date")
        );

        return item;
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
