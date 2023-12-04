package com.example.omninventory;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

/**
 * Encapsulate behaviours related to Firestore, going from Firestore document to InventoryItem and
 * vice-versa, etc.
 * @author Castor
 * @author Patrick
 * @author Rose
 */
public class InventoryRepository {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference inventoryItemsRef;
    private CollectionReference tagsRef;
    private HashMap<String, Tag> tagDict;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    /**
     * Constructor that sets up connection to Firestore and references.
     */
    public InventoryRepository() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        inventoryItemsRef = db.collection("inventoryItems");
        tagsRef = db.collection("tags");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        tagDict = new HashMap<>();

        tagsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("TagRepository", error.toString());
                    return;
                }
                if (snapshot != null) {
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Tag tag = convertDocumentToTag(doc);
                        tagDict.put(tag.getId(), tag);
                    }
                }
            }
        });
    }

    public ListenerRegistration listenToUserUpdate(String username, UserUpdateHandler handler) {
        usersRef.document(username).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("listenToUserUpdate", error.toString());
                    return;
                }
                // listens to the following changes: item addition/removal, name/password update
                if (snapshot != null && snapshot.exists()) {
                    handler.onUserUpdate(convertDocumentToUser(snapshot));
                }
            }
        });
        return null;
    }
    /**
     * Sets up an InventoryItemAdapter to contain contents of Firebase collection, and be
     * automatically updated when inventoryItem changes.
     * TODO: will need to make this get only the items associated with current user
     *
     * @param adapter An InventoryItemAdapter to set up to track contents of database.
     * @return
     */
    public ListenerRegistration setupInventoryItemList(InventoryItemAdapter adapter, InventoryUpdateHandler handler, ArrayList<String> itemIDs) {
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
                        if (itemIDs.contains(doc.getId())) {
                            InventoryItem item = convertDocumentToInventoryItem(doc);
                            adapter.add(item);
                        }
                    }
                }
                handler.onItemListUpdate();
            }
        });
        return null;
    }

    /**
     * Convert fields of a DocumentSnapshot from the inventoryItem collection to an InventoryItem.
     * @param doc DocumentSnapshot to convert.
     * @return    An InventoryItem whose fields match the DocumentSnapshot.
     */
    public InventoryItem convertDocumentToInventoryItem(DocumentSnapshot doc) {
        Log.d("InventoryRepository", "convert called with document id=" + doc.getId() + " data=" + doc.getData().toString());

        // get all image paths as 'empty' ItemImages
        ArrayList<ItemImage> images = new ArrayList<ItemImage>();
        ArrayList<String> imagePaths = (ArrayList<String>) doc.get("images");
        if (imagePaths != null) {
            for (String imagePath : imagePaths) {
                images.add(new ItemImage(imagePath));
            }
        }
        else {
            // may be executed if item was in database from before image functionality was added
            Log.d("InventoryRepository", "images array is null");
        }

        ArrayList<Tag> tagList = new ArrayList<>();
        InventoryItem item = new InventoryItem(
                doc.getId(),
                doc.getString("name"),
                doc.getString("description"),
                doc.getString("comment"),
                doc.getString("make"),
                doc.getString("model"),
                doc.getString("serialno"),
                new ItemValue(doc.getLong("value")), // convert to ItemValue
                new ItemDate(doc.getDate("date")), // convert to ItemDate
                tagList,
                images
        );

        // don't want to download images here since they may not be displayed, e.g. if this is called
        // from homepage. but we could do it
        // attemptDownloadImages(item);

        ArrayList<String> tagIdList = (ArrayList<String>) doc.get("tags");
        tagIdList.forEach(tagId -> {
            try {
                item.addTag(tagDict.get(tagId));
            } catch (NullPointerException e) {
                Log.e("InventoryRepository", "Unable to retrieve tag with id=" + tagId);
            }
        });

        return item;

    }

    /**
     * Add a new InventoryItem to the inventoryItem collection. Also adds reference to the current
     * User's list of owned items.
     * @param currentUser User currently signed in.
     * @param item        InventoryItem to add to currentUser's owned items.
     */
    public void addInventoryItem(User currentUser, InventoryItem item) {

        // upload images and save their storage paths in the fields of item's ItemImages
        addImages(item.getImages());

        // create data for new item document
        HashMap<String, Object> itemData = item.convertToHashMap();
        Log.d("InventoryRepository", "addInventoryItem called with itemData: " + itemData.toString());

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
        Object[] arrayToAdd = {newItemRef.getId()};

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

        // add new item id to itemlist on each tag
        Object[] idToAdd = {newItemRef.getId()};
        item.getTagIds().forEach(tag -> {
            // get document for tag
            DocumentReference tagRef = tagsRef.document(tag);
            tagRef
                    .update("items", FieldValue.arrayUnion(idToAdd))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("InventoryRepository", String.format("Updated Tag, id=%s", tagRef.getId()));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("InventoryRepository", String.format("Error updating Tag, id=%s", tagRef.getId()), e);
                        }
                    });

        });
    }

    /**
     * Updates an InventoryItem in the database with new data. Effectively, this means overwriting
     * the document at the firebaseId of the given InventoryItem with entirely new InventoryItem
     * data.
     * @param item The InventoryItem to update.
     */
    public void updateInventoryItem(InventoryItem item) {
        // update images. this needs to happen before item.convertToHashMap because image paths are written to item here
        updateItemImages(item.getOriginalImages(), item.getImages());

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
                    Log.d("InventoryRepository", String.format("Updated inventoryItems DocumentSnapshot written, id=%s", itemRef.getId()));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("InventoryRepository", String.format("Error writing inventoryItems DocumentSnapshot, id=%s", itemRef.getId()), e);
                }
            });

        // add new item id to itemlist on each tag
        Object[] idToAdd = {item.getFirebaseId()};
        item.getTagIds().forEach(tag -> {
            // get document for tag (name is id)
            DocumentReference tagRef = tagsRef.document(tag);

            tagRef
                    .update("items", FieldValue.arrayUnion(idToAdd))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("InventoryRepository", String.format("Updated Tag, id=%s", tagRef.getId()));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("InventoryRepository", String.format("Error updating Tag, id=%s", tagRef.getId()), e);
                        }
                    });

        });
    }

    /**
     * Given an ArrayList of the previous images held by an item and the images now held by that item
     * (i.e. after the item is edited), this method compares the two lists, uploads any new images
     * to storage, and deletes any old images from storage.
     * Specifically:
     * - An image that appears in prevImages but not newImages will be deleted from storage.
     * - An image that appears in newImages but not prevImages will be uploaded to storage.
     *
     * This aims to minimize storage usage (by deleting unused images) and wasted upload time (by
     * not uploading extra copies of the same image).
     *
     * Images are compared using their storagePath field, that is the path associated with the image
     * in Firebase Storage. Any image that has not yet been uploaded to storage will have a
     * storagePath attribute of null and will always be uploaded.
     *
     * @param prevImages The array of 'previous' images held by an InventoryItem before update.
     * @param newImages The array of 'new' images held by an InventoryItem after update.
     */
    public void updateItemImages(ArrayList<ItemImage> prevImages, ArrayList<ItemImage> newImages) {
        // if no prev images, we are just adding all images
        if (prevImages == null || prevImages.size() == 0) {
            addImages(newImages);
            return;
        }

        // otherwise, optimize by only uploading/deleting necessary images
        ArrayList<ItemImage> toUpload = new ArrayList<>();
        ArrayList<ItemImage> toDelete = new ArrayList<>();

        // find images that haven't been uploaded yet
        for (ItemImage newImg : newImages) {
            boolean alreadyUploaded = false;

            for (ItemImage oldImg : prevImages) {
                if (newImg.equalRef(oldImg)) {
                    // image was already uploaded to the database, don't need to reupload
                    alreadyUploaded = true;
                    break;
                }
            }
            if (!alreadyUploaded) {
                toUpload.add(newImg);
            }
        }

        // find images that can be deleted
        for (ItemImage oldImg : prevImages) {
            boolean keep = false;

            for (ItemImage newImg : newImages) {
                if (newImg.equalRef( oldImg )) {
                    // image is kept in new list of images
                    keep = true;
                    break;
                }
            }
            if (!keep) {
                toDelete.add(oldImg);
            }
        }

        // upload images
        Log.d("InventoryRepository", "Uploading new images: " + toUpload);
        addImages(toUpload);

        // delete images
        Log.d("InventoryRepository", "Deleting images no longer used: " + toDelete);
        deleteImages(toDelete);
    }

    /**
     * Add a list of ItemImages to Firebase Storage.
     * @param images The images to upload.
     * @return An ArrayList containing the storage path of each image uploaded.
     */
    public ArrayList<String> addImages(ArrayList<ItemImage> images) {

        ArrayList<String> imagePaths = new ArrayList<String>();

        for (ItemImage image : images) {
            String filepath = "images/" + UUID.randomUUID().toString();

            // create filename for new image
            StorageReference imageRef = storageRef.child(filepath);

            // store reference
            imagePaths.add(filepath);
            image.setStoragePath(filepath);

            imageRef.putFile(image.getUri())
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("InventoryRepository", "addImages: Image uploaded successfully:" + image.getUri() + " as " + filepath);
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("InventoryRepository", "addImages: Image failed to upload:" + image.getUri());
                        }
                    });
        }

        // return a list of image paths
        Log.d("InventoryRepository", "Started tasks to upload images: " + imagePaths.toString());
        return imagePaths;
    }

    /**
     * Delete a list of ItemImages from the Firebase Storage (based on storage path).
     * @param images The list of ItemImages to delete.
     */
    public void deleteImages(ArrayList<ItemImage> images) {

        for (ItemImage image : images) {
            // get reference for image
            StorageReference imageRef = storageRef.child(image.getStoragePath());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("InventoryRepository", "deleteImages: deleted image with path " + image.getStoragePath());
                    image.setStoragePath(null); // ensure we don't try to reference this path again
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("InventoryRepository", "deleteImages: failed to delete image with path " + image.getStoragePath());
                    image.setStoragePath(null); // get rid of path anyway because there is probably something wrong with it
                }
            });
        }
    }

    /**
     * Attempt to download all the ItemImages referenced by Firebase Storage path held by the
     * InventoryItem.
     * @param item
     * @param handler
     */
    public void attemptDownloadImages(InventoryItem item, ImageDownloadHandler handler) {
        // get all the images associated with this item from storage
        Log.d("InventoryRepository", "attemptDownloadImages called, item images: " + item.getImages().toString());

        for (int i = 0; i < item.getImages().size(); i++) {
            // get image's uri, hopefully
            attemptDownloadImage(item, i, handler);
        }
    }

    /**
     * Attempt to download a single image, at position `pos` in item's images. Calls a handler
     * function on each successful or failed download.
     * @param item
     * @param handler
     */
    public void attemptDownloadImage(InventoryItem item, int pos, ImageDownloadHandler handler) {
        // get all the images associated with this item from storage
        ItemImage image = item.getImages().get(pos);
        Log.d("InventoryRepository", "attemptDownloadImage called, image: " + image.toString());

        // get image's uri, hopefully
        storageRef.child(image.getStoragePath()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("InventoryRepository", String.format("Got URI for image path %s, URI %s", image.getStoragePath(), uri));
                        image.setUri(uri);
                        handler.onImageDownload(pos, image); // call handler function to refresh its images
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("InventoryRepository", "Couldn't get URI for image path " + image.getStoragePath());
                        handler.onImageDownloadFailed(pos);
                    }
                });
    }

    /**
     * A rather roundabout way to update the DetailsActivity with an InventoryItem on update.
     * This sends the InventoryItem from Firebase into the onGetInventoryItem method of the handler,
     * which must implement the GetInventoryItemHandler interface.
     * @param firebaseId ID of item to get.
     * @param handler    GetInventoryItemHandler that implements function to call with InventoryItem
     *                   data, once received.
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


    /**
     * Deletes an InventoryItem from the database.
     * @param currentUser The User currently signed in (to whom the item belongs).
     * @param itemId      The ID of the item to delete.
     */
    public void deleteInventoryItem(User currentUser, String itemId) {
        // get document for currentUser (id is username)
        DocumentReference currentUserRef = usersRef.document(currentUser.getUsername());
        // remove item from user's ownedItems in users collection
        currentUserRef.update("ownedItems", FieldValue.arrayRemove(itemId));
        Log.w("deleteInventoryItem", "Deleting: " + itemId);
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

    /**
     * Adds a new Tag to the tags collection.
     *
     * @param tag Tag to add to the list of tags.
     */
    public void addTag(Tag tag) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", tag.getName());
        data.put("owner", tag.getOwner());
        data.put("priority", tag.getPriority());
        data.put("items", tag.getItemIds());
        if (tag.getId().isEmpty()) {
            tag.setId(tagsRef.document().getId());
        }
        tagsRef
                .document(tag.getId())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tagDict.put(tag.getId(), tag);
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

    /**
     * Updates a tag in the database with new data. Effectively, this means overwriting
     * the document at the id of the given Tag with entirely new tag
     * data.
     * @param tag The Tag to update.
     */
    public void updateTag(Tag tag) {
        // create data for new tag document
        HashMap<String, Object> tagData = new HashMap<>();
        tagData.put("name", tag.getName());
        tagData.put("owner", tag.getOwner());
        tagData.put("priority", tag.getPriority());
        tagData.put("items", tag.getItemIds());

        // get the document for this tag
        DocumentReference tagRef = tagsRef.document(tag.getId());

        // overwrite data of document with tag data
        tagRef
                .set(tagData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tagDict.replace(tag.getId(), tag);
                        Log.d("InventoryRepository", String.format("New tags DocumentSnapshot written, id=%s", tagRef.getId()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("InventoryRepository", String.format("Error writing tags DocumentSnapshot, id=%s", tagRef.getId()), e);
                    }
                });
    }

    /**
     * Deletes a specified tag for a given owner.
     *
     * @param tag  The Tag to be deleted
     * @param owner The owner of the tag
     */
    public void deleteTag(Tag tag, String owner) {
        // Check if the tag's owner matches the specified owner
        if (tag.getOwner().equals(owner)) {

            // Delete the tag
            tagsRef.document(tag.getId()).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Tag successfully deleted: " + tag.getId());
                            // Remove this tag from all items that contain it
                            removeTagFromItems(tag.getId());

                            // Remove the tag from the local tag dictionary if used
                            tagDict.remove(tag.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error deleting tag: " + tag.getId(), e);
                        }
                    });
        } else {
            Log.e(TAG, "Attempted to delete a tag with mismatched owner. Tag ID: " + tag.getId() + ", Owner: " + owner);
        }
    }

    /**
     * Remove the deleted tag from all owner items containing the tag
     * @param tagId ID of tag to remove from specific inventory items
     */
    private void removeTagFromItems(String tagId) {
        // Query all items that contain the tag in their 'tags' field
        inventoryItemsRef.whereArrayContains("tags", tagId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Remove the tag from each item
                            DocumentReference itemRef = inventoryItemsRef.document(documentSnapshot.getId());
                            itemRef.update("tags", FieldValue.arrayRemove(tagId))
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Tag removed from item: " + documentSnapshot.getId()))
                                    .addOnFailureListener(e -> Log.e(TAG, "Error removing tag from item: " + documentSnapshot.getId(), e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error querying items for tag removal", e));
    }

    /**
     * Convert fields of a DocumentSnapshot from the tag collection to a Tag.
     * @param doc DocumentSnapshot to convert
     * @return the resultant tag
     */
    public Tag convertDocumentToTag(DocumentSnapshot doc) {
        Log.d("TagRepository", "convert called with document name=" + doc.getId());
        long priority = 0;
        try {
            priority = (long) doc.get("priority");
        } catch (NullPointerException e) {
            priority = 0;
        }
        Tag tag = new Tag(doc.getId(), doc.getString("name"), doc.getString("owner"), priority,   (ArrayList<String>) doc.get("items"));

        return tag;
    }

    /**
     * Sets up a TagAdapter to contain contents of Firebase tags collection, and be automatically
     * updated when the list changes.
     * @param adapter the adapter to contain the tags
     * @return a snapshot listener for the collection that will automatically update the adapter
     */
    public ListenerRegistration setupTagList(TagAdapter adapter, User currentUser) {
        // set up listener
        ListenerRegistration registration = tagsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("TagRepository", error.toString());
                    return;
                }
                if (snapshot != null) {
                    adapter.clear(); // clear existing list data
                    for (QueryDocumentSnapshot doc : snapshot) {
                        // get each item returned by query and add to adapter
                        if (currentUser.getUsername().equals(doc.getString("owner"))) {
                            Tag tag = convertDocumentToTag(doc);
                            tagDict.put(tag.getId(), tag);
                            adapter.add(tag);
                        }
                    }
                    adapter.sort(Comparator.reverseOrder());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return registration;
    }

    public ListenerRegistration setupTagListsForItem(TagAdapter appliedAdapter, TagAdapter unappliedAdapter, User currentUser, InventoryItem item) {
        // set up listener
        ListenerRegistration registration = tagsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("TagRepository", error.toString());
                    return;
                }
                if (snapshot != null) {
                    appliedAdapter.clear(); // clear existing list data
                    unappliedAdapter.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        // get each item returned by query and add to adapter
                        if (currentUser.getUsername().equals(doc.getString("owner"))) {
                            Tag tag = convertDocumentToTag(doc);
                            tagDict.put(tag.getId(), tag);
                            if (item.getTagIds().contains(doc.getId())) {
                                appliedAdapter.add(tag);
                            } else {
                                unappliedAdapter.add(tag);
                            }
                        }
                    }
                    appliedAdapter.sort(Comparator.reverseOrder());
                    unappliedAdapter.sort(Comparator.reverseOrder());
                    appliedAdapter.notifyDataSetChanged();
                    unappliedAdapter.notifyDataSetChanged();
                }
            }
        });
        return registration;
    }

    /**
     * Apply a list of tags to a list of items, correctly populating the fields of both so they
     * reference each other.
     * @param tags the list of Tag objects to apply
     * @param items the list of inventoryItem objects to be tagged
     */
    public void applyTagsToItems(ArrayList<Tag> tags, ArrayList<InventoryItem> items) {
        // run through list of items, adding tags to each
        for (int i = 0; i < items.size(); i++) {
            DocumentReference itemRef = inventoryItemsRef.document(items.get(i).getFirebaseId());
            itemRef.
                    get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // get item from db and add all tags it doesn't already have
                            InventoryItem item = convertDocumentToInventoryItem(documentSnapshot);
                            Log.d("Successfully read document id=", documentSnapshot.getId());
                            for (int j = 0; j < tags.size(); j++) {
                                String tagId = tags.get(j).getId();
                                if (!item.getTagIds().contains(tagId)) {
                                    item.addTag(tags.get(j));
                                }
                            }

                            // write the updated item back to the db
                            item.setOriginalImages(item.getImages());
                            updateInventoryItem(item);
//                            updateInventoryItem(new InventoryItem(
//                                    item.getFirebaseId(),
//                                    item.getName(),
//                                    item.getDescription(),
//                                    item.getComment(),
//                                    item.getMake(),
//                                    item.getModel(),
//                                    item.getSerialNo(),
//                                    item.getValue(),
//                                    item.getDate(),
//                                    item.getTags(),
//                                    item.getImages(),
//                                    item.getImages()
//                            ));
                        }
                    });
        }

        // run through list of tags, adding items to each
        for (int i = 0; i < tags.size(); i++) {
            DocumentReference tagRef = tagsRef.document(tags.get(i).getId());
            tagRef.
                    get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // get Tag from db and add all items it doesn't already have
                            Tag tag = convertDocumentToTag(documentSnapshot);
                            Log.d("Successfully read document id=", documentSnapshot.getId());
                            for (int j = 0; j < items.size(); j++) {
                                String itemId = items.get(j).getFirebaseId();
                                if (!tag.getItemIds().contains(itemId)) {
                                    tag.addItem(itemId);
                                }
                            }

                            // write updated Tag back to db
                            updateTag(tag);
                        }
                    });
        }

    }

    /**
     * Adds a new User to the database.
     * @param user The User to add.
     */
    public void addUser(User user) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("password", user.getPassword());
        data.put("ownedItems", user.getItemsRefs());
        usersRef.document(user.getUsername())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });

    };
    public User convertDocumentToUser(DocumentSnapshot doc) {
        Log.d("convertDocumentToUser", "(convertDocumentToUser) converting to User");
        User user = new User(
                doc.getString("name"),
                doc.getId(),
                doc.getString("password"),
                (ArrayList<String>) doc.get("ownedItems")
        );
        Log.d("convertDocumentToUser", "(convertDocumentToUser) done");
        return user;
    }

    public void updateUser(User user) {
        HashMap<String, Object> userData = user.convertToHashMap();

        // get the document for this item
        DocumentReference docRef = usersRef.document(user.getUsername());

        // overwrite data of document with item data
        docRef
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("InventoryRepository", String.format("Updated user, id=%s", docRef.getId()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("InventoryRepository", String.format("Error updating user, id=%s", docRef.getId()), e);
                    }
                });

    }

    public void isUsernameUnique(String username, UpdateUsernameHandler handler) {
        DocumentReference userDocRef = db.collection("users").document(username);
        Log.d("canUpdateUsername", "checking if this username already exists: " + username);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("canUpdateUsername", "username taken");
                        handler.onUsernameValidation(false, false, "");
                    }
                    else {
                        Log.d("canUpdateUsername", "username available");
                        handler.onUsernameValidation(true, false, username);
                    }
                }
                else {
                    Log.d(TAG, "Failed with: ", task.getException());
                    handler.onUsernameValidation(false, false, "");
                }
            }
        });
    }

    public void updateUsername(User user, String oldUsername) {
        // create a new document
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("password", user.getPassword());
        data.put("ownedItems", user.getItemsRefs());
        usersRef.document(user.getUsername())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });

        // delete old document
        usersRef.document(oldUsername)
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
    }
}
