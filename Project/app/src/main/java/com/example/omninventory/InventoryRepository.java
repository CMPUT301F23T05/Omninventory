package com.example.omninventory;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.google.firebase.firestore.ListenerRegistration;
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
     * Constructor that sets up connection to Firestore and references.
     */
    public InventoryRepository() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        inventoryItemsRef = db.collection("inventoryItems");
    }

    /**
     * Sets up an InventoryItemAdapter to contain contents of Firebase collection, and be
     * automatically updated when inventoryItem changes.
     * TODO: will need to make this get only the items associated with current user
     *
     * @param adapter An InventoryItemAdapter to set up to track contents of database.
     * @return
     */
    public ListenerRegistration setupInventoryItemList(InventoryItemAdapter adapter, ItemListUpdateHandler handler) {
        // set up listener
        inventoryItemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("InventoryRepository", error.toString());
                    return;
                }
                if (snapshot != null) {
                    adapter.clear(); // clear existing list data
                    for (QueryDocumentSnapshot doc : snapshot) {
                        // get each item returned by query and add to adapter
                        InventoryItem item = convertDocumentToInventoryItem(doc);
                        adapter.add(item);
                    }
                }
                adapter.notifyDataSetChanged(); // TODO: is this necessary?
                handler.onItemListUpdate();
            }
        });
        return null;
    }

    /**
     * Convert fields of a DocumentSnapshot from the inventoryItem collection to an InventoryItem.
     * @param doc DocumentSnapshot to convert.
     * @return
     */
    public InventoryItem convertDocumentToInventoryItem(DocumentSnapshot doc) {
        Log.d("InventoryRepository", "convert called with document id=" + doc.getId());
        Log.d("InventoryRepository", doc.getData().toString());

        InventoryItem item = new InventoryItem(
            doc.getId(),
            doc.getString("name"),
            doc.getString("description"),
            doc.getString("comment"),
            doc.getString("make"),
            doc.getString("model"),
            doc.getString("serialno"),
            new ItemValue(doc.getLong("value")), // convert to ItemValue
            new ItemDate(doc.getDate("date")) // convert to ItemDate
        );

        return item;
    }

    /**
     * Add a new InventoryItem to the inventoryItem collection. Also adds reference to the current
     * User's list of owned items.
     * @param currentUser User currently signed in.
     * @param item InventoryItem to add to currentUser's owned items.
     */
    public void addInventoryItem(User currentUser, InventoryItem item) {
        // create data for new item document
        HashMap<String, Object> itemData = item.convertToHashMap();

        // create new inventoryItems document with auto-generated id
        DocumentReference newItemRef = inventoryItemsRef.document();

        // set data of new document
        newItemRef
            .set(itemData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("InventoryRepository", String.format("Updated new InventoryItem document, id=%s", newItemRef.getId()));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("InventoryRepository", String.format("Error updating new InventoryItem document, id=%s", newItemRef.getId()), e);
                }
            });

        // get document for currentUser (id is username)
        DocumentReference currentUserRef = usersRef.document(currentUser.getUsername());

        // add new inventoryItem reference to currentUser's list of ownedItems
        Object[] arrayToAdd = {newItemRef};

        currentUserRef
            .update("ownedItems", FieldValue.arrayUnion(arrayToAdd))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("InventoryRepository", String.format("Updated User, id=%s", currentUserRef.getId()));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("InventoryRepository", String.format("Error updating User, id=%s", currentUserRef.getId()), e);
                }
            });
    }

    /**
     * Updates an InventoryItem in the database with new data. Effectively, this means overwriting
     * the document at the firebaseId of the given InventoryItem with entirely new InventoryItem
     * data.
     * @param item The InventoryItem to update.
     */
    public void updateInventoryItem(InventoryItem item) {
        // create data for new item document
        HashMap<String, Object> itemData = item.convertToHashMap();

        // get the document for this item
        DocumentReference itemRef = inventoryItemsRef.document(item.getFirebaseId());

        // overwrite data of document with item data
        itemRef
            .set(itemData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("InventoryRepository", String.format("New inventoryItems DocumentSnapshot written, id=%s", itemRef.getId()));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("InventoryRepository", String.format("Error writing inventoryItems DocumentSnapshot, id=%s", itemRef.getId()), e);
                }
            });
    }

    /**
     * Bit of a roundabout way to update the DetailsActivity with an InventoryItem on update.
     * This sends the InventoryItem from Firebase into the onGetInventoryItem method of the handler,
     * which must implement the GetInventoryItemHandler interface.
     * @param firebaseId ID of item to get.
     * @param handler GetInventoryItemHandler that implements function to call with InventoryItem
     *                data, once received.
     */
    public void getInventoryItemInto(String firebaseId, GetInventoryItemHandler handler) {
        Log.d("InventoryRepository", "getInventoryItem called with id=" + firebaseId);

        // get document reference
        DocumentReference itemRef = inventoryItemsRef.document(firebaseId);
        Log.d("InventoryRepository", itemRef.getId());

        // get actual data
        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d("InventoryRepository", "(getInventoryItemInto) data gotten from db: " + doc.getData());
                        handler.onGetInventoryItem(convertDocumentToInventoryItem(doc)); // call handler function
                    } else {
                        Log.d("InventoryRepository", "(getInventoryItemInto) couldn't find document id=" + firebaseId);
                    }
                } else {
                    Log.d("InventoryRepository", "(getInventoryItemInto) failed with ", task.getException());
                }
            }
        });
    }

    public void deleteInventoryItem(User currentUser, String itemId) {
        // get document for currentUser (id is username)
        DocumentReference currentUserRef = usersRef.document(currentUser.getUsername());
        // remove item from user's ownedItems in users collection
        currentUserRef.update("ownedItems", FieldValue.arrayRemove(itemId));
        // remove from inventoryItems collection
        inventoryItemsRef.document(itemId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "InventoryItem successfully deleted!");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting item", e);
                    }
                });
    };

    public void addUser(User user) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("password", user.getPassword());
        data.put("ownedItems", user.getOwnedItems());
        usersRef.document(user.getUsername())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });

    };

    public void getItemListData(ArrayList<String> ownedItems, GetItemListDataHandler handler) {
        for (String itemID : ownedItems) {
            Log.d("getItemListData", itemID);
            DocumentReference itemRef = inventoryItemsRef.document(itemID);
            itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            Log.d("InventoryRepository", "(getItemListData) found item with document id=" + itemID);
                            handler.onGetItemListData(convertDocumentToInventoryItem(doc)); // call handler function
                        } else {
                            Log.d("InventoryRepository", "(getItemListData) couldn't find document id=" + itemID);
                        }
                    } else {
                        Log.d("InventoryRepository", "(getItemListData) failed with ", task.getException());
                    }
                }
            });
        }
    }
}
