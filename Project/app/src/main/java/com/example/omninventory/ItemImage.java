package com.example.omninventory;

import android.net.Uri;

public class ItemImage {
    private Uri uri;

    public ItemImage(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
