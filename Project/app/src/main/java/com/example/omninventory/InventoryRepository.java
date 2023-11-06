package com.example.omninventory;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    public InventoryItem convertDocumentToInventoryItem(DocumentSnapshot doc) {
        Log.d("InventoryRepository", "convert called with document" + doc.getId());
        InventoryItem item = new InventoryItem(
            doc.getId(),
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
     * Convert fields of an InventoryItem into a HashMap for writing to Firebase.
     * Note that item.firebaseId is not stored in the HashMap.
     * @param item
     * @return
     */
    public HashMap<String, Object> convertInventoryItemToHashMap(InventoryItem item) {
        HashMap<String, Object> itemData = new HashMap<>();
        itemData.put("name", item.getName());
        itemData.put("description", item.getDescription());
        itemData.put("comment", item.getComment());
        itemData.put("make", item.getMake());
        itemData.put("model", item.getModel());
        itemData.put("serialno", item.getSerialno());
        itemData.put("value", item.getValue());
        itemData.put("date", item.getDate());
        // TODO: tags and images
        return itemData;
    }

    /**
     * Add a new InventoryItem to the inventoryItem collection. Also adds reference to the current
     * User's list of owned items.
     * @param currentUser
     * @param item
     */
    public void addInventoryItem(User currentUser, InventoryItem item) {
        // create data for new item document
        HashMap<String, Object> itemData = convertInventoryItemToHashMap(item);

        // create new inventoryItems document with auto-generated id
        DocumentReference newInventoryItemRef = inventoryItemsRef.document();

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
        DocumentReference currentUserRef = usersRef.document(currentUser.getUsername());

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

    public void updateInventoryItem(InventoryItem item) {
        // create data for new item document
        HashMap<String, Object> itemData = convertInventoryItemToHashMap(item);

        // get the document for this item
        DocumentReference itemRef = inventoryItemsRef.document(item.getFirebaseId());

        // overwrite data of document with item data
        itemRef
            .set(itemData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, String.format("New inventoryItems DocumentSnapshot written, id=%s", itemRef.getId()));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, String.format("Error writing inventoryItems DocumentSnapshot, id=%s", itemRef.getId()), e);
                }
            });
    }

    public InventoryItem getInventoryItem(String firebaseId) {
        Log.d("InventoryRepository", "getInventoryItem called with id" + firebaseId);

        // get document reference
        DocumentReference itemRef = inventoryItemsRef.document(firebaseId);

        // get actual data
        // TODO: i dont know why but a weird one-element array is the only way i can get this to work. marking as todo revise
        final DocumentSnapshot[] doc = new DocumentSnapshot[1];

        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    doc[0] = task.getResult();
                    if (doc[0].exists()) {
                        Log.d("InventoryRepository", "DocumentSnapshot data gotten from db: " + doc[0].getData());
                    } else {
                        Log.d("InventoryRepository", "Couldn't find document id=" + firebaseId);
                    }
                } else {
                    Log.d("InventoryRepository", "get failed with ", task.getException());
                }
            }
        });

        return convertDocumentToInventoryItem(doc[0]); // convert to InventoryItem and return
    }

    // TODO: implement these...
    public void deleteInventoryItem() {};

    public void addUser() {};
}
