package com.example.omninventory;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class ItemImage implements Serializable {
    private transient Uri uri; // uri of downloaded image. might get de-set whenever this ItemImage passed between activities, because Uri is not serializable.
    private StorageReference storageRef;
    private String path;

    public ItemImage(String path) {
        this.path = path;
    }

    public ItemImage(Uri uri) {
        this.uri = uri;
        this.storageRef = null;
        this.path = null;
    }

    public ItemImage(StorageReference storageRef) {
        // asynchronously get URI
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ItemImage.this.uri = uri;
            }
        });
        this.storageRef = storageRef;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public void setStorageRef(StorageReference storageReference) {
        this.storageRef = storageReference;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("[IMAGE | uri=%s | path=%s]", this.uri, this.path);
    }
}
