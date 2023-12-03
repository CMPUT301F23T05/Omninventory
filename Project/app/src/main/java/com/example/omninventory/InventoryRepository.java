package com.example.omninventory;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ListIterator;
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
    }

    /**
     * Sets up an InventoryItemAdapter to contain contents of Firebase collection, and be
     * automatically updated when inventoryItem changes.
     * TODO: will need to make this get only the items associated with current user
     *
     * @param adapter An InventoryItemAdapter to set up to track contents of database.
     * @return
     */
    public ListenerRegistration setupInventoryItemList(InventoryItemAdapter adapter, InventoryUpdateHandler handler) {
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
            Log.d("InventoryRepository", "images array is null");
        }

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
            (ArrayList<String>) doc.get("tags"),
            images
        );

        // don't want to download images here since they may not be displayed, e.g. if this is called
        // from homepage. but we could do it
        // attemptDownloadImages(item);

        return item;
    }

    public void attemptDownloadImages(InventoryItem item, ImageDownloadHandler handler) {
        // get all the images associated with this item from storage
        ArrayList<ItemImage> images = item.getImages();
        Log.d("InventoryRepository", "attemptDownloadImages called, item images: " + images.toString());

        for (int i = 0; i < images.size(); i++) {
            ItemImage image = images.get(i);
            Log.d("InventoryRepository", "attempting to download image: " + image.toString());

            // get image's uri, hopefully
            int finalPos = i;
            storageRef.child(image.getPath()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("InventoryRepository", String.format("Got URI for image path %s, URI %s", image.getPath(), uri));
                        image.setUri(uri);
                        handler.onImageDownload(finalPos, image); // call handler function to refresh its images
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("InventoryRepository", "Couldn't get URI for image path " + image.getPath());
                        handler.onImageDownloadFailed(finalPos);
                    }
                });
        }
    }

    /**
     * Attempt to download a single image, at position `pos` in item's images.
     * @param item
     * @param handler
     */
    public void attemptDownloadImage(InventoryItem item, int pos, ImageDownloadHandler handler) {
        // get all the images associated with this item from storage
        ItemImage image = item.getImages().get(pos);
        Log.d("InventoryRepository", "attemptDownloadImages called, image: " + image.toString());

        // get image's uri, hopefully
        storageRef.child(image.getPath()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("InventoryRepository", String.format("Got URI for image path %s, URI %s", image.getPath(), uri));
                        image.setUri(uri);
                        handler.onImageDownload(pos, image); // call handler function to refresh its images
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("InventoryRepository", "Couldn't get URI for image path " + image.getPath());
                        handler.onImageDownloadFailed(pos);
                    }
                });
    }



    /**
     * Add a new InventoryItem to the inventoryItem collection. Also adds reference to the current
     * User's list of owned items.
     * @param currentUser User currently signed in.
     * @param item        InventoryItem to add to currentUser's owned items.
     */
    public void addInventoryItem(User currentUser, InventoryItem item) {

        // upload images
        addImages(item.getImages()); // this also returns the storageRefs but we get them in item.convertToHashMap instead

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

        // add new item id to itemlist on each tag
        Object[] idToAdd = {newItemRef.getId()};
        item.getTags().forEach(tag -> {
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
        item.getTags().forEach(tag -> {
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
     * remove and add images
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
        // remove from inventoryItems collection
        usersRef.document(itemId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

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
     * Add a list of ItemImages to the storage.
     * @param images
     * @return
     */
    public ArrayList<String> addImages(ArrayList<ItemImage> images) {

        ArrayList<String> imagePaths = new ArrayList<String>();
//        ArrayList<Task> uploadTasks = new ArrayList<Task>();

        for (ItemImage image : images) {
            String filepath = "images/" + UUID.randomUUID().toString();

            // create filename for new image
            StorageReference imageRef = storageRef.child(filepath);

            // store reference
            imagePaths.add(filepath);
            image.setPath(filepath);
            Log.d(">????>", image.toString());

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
                        // usually this gets called if you're returning to ItemDetails and an image hasn't
                        // been uploaded yet. StorageException is also printed
                        Log.d("InventoryRepository", "addImages: Image failed to upload:" + image.getUri());
                    }
                });
        }

        // return a list of image paths
        Log.d("InventoryRepository", "Started tasks to upload images: " + imagePaths.toString());
        return imagePaths;
    }

    /**
     * Delete a list of ItemImages from the storage (based on path).
     * @param images
     * @return
     */
    public void deleteImages(ArrayList<ItemImage> images) {

        for (ItemImage image : images) {
            // get reference for image
            StorageReference imageRef = storageRef.child(image.getPath());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("InventoryRepository", "deleteImages: deleted image with path " + image.getPath());
                    image.setPath(null); // ensure we don't try to reference this path again
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("InventoryRepository", "deleteImages: failed to delete image with path " + image.getPath());
                    image.setPath(null); // get rid of path anyway because there is probably something wrong with it
                }
            });
        }

    }

    /**
     * Adds a new Tag to the *tag* collection.
     * @param tag Tag to add to the list of tags.
     */
    public void addTag(Tag tag) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("items", tag.getItemIds());
        tagsRef
                .document(tag.getName())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
        tagData.put("items", tag.getItemIds());

        // get the document for this tag
        DocumentReference tagRef = tagsRef.document(tag.getName());

        // overwrite data of document with tag data
        tagRef
                .set(tagData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
     * Convert fields of a DocumentSnapshot from the tag collection to a Tag.
     * @param doc DocumentSnapshot to convert
     * @return the resultant tag
     */
    public Tag convertDocumentToTag(DocumentSnapshot doc) {
        Log.d("TagRepository", "convert called with document name=" + doc.getId());
        Tag tag = new Tag(doc.getId());
        List<String> items = (List<String>) doc.get("items");
        ListIterator<String> itemIterator = items.listIterator();
        while (itemIterator.hasNext()) {
            tag.addItem(itemIterator.next());
        }

        return tag;
    }

    /**
     * Sets up a TagAdapter to contain contents of Firebase *tags* collection, and be automatically
     * updated when the list changes.
     * @param adapter the adapter to contain the tags
     * @return a snapshot listener for the collection that will automatically update the adapter
     */
    public ListenerRegistration setupTagList(TagAdapter adapter) {
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
                        Tag tag = convertDocumentToTag(doc);
                        adapter.add(tag);
                    }
                }
            }
        });

        adapter.notifyDataSetChanged();
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
                                String tagName = tags.get(j).getName();
                                if (!item.getTags().contains(tagName)) {
                                    item.addTag(tagName);
                                }
                            }

                            // write the updated item back to the db
                            updateInventoryItem(item);
                        }
                    });
        }

        // run through list of tags, adding items to each
        for (int i = 0; i < tags.size(); i++) {
            DocumentReference tagRef = tagsRef.document(tags.get(i).getName());
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

    /**
     * Retrieve the contents of the User's inventory.
     * @param username The username (ID) of the currently signed-in user.
     */
    public void getUserInventory(String username) {
        usersRef.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                        List<String> ownedItems = (List<String>) doc.get("ownedItems");
                        ArrayList<InventoryItem> inventory = new ArrayList<InventoryItem>();
                        // todo: get an array list of inventory items
//                        for (String itemRef : ownedItems) {
//
//                        }
                    } else {
                        Log.d(TAG, "Can't find user with username: " + username);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
