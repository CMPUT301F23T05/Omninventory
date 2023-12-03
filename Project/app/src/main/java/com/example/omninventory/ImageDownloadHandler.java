package com.example.omninventory;

public interface ImageDownloadHandler {
    public abstract void onImageDownload(int pos, ItemImage image);
    public abstract void onImageDownloadFailed(int pos);
}
