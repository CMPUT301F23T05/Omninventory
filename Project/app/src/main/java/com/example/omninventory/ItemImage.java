package com.example.omninventory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Encapsulates functionality related to an image attached to an item; the URI, path in storage
 * and/or on system, etc.
 *
 * @auther Castor
 */
public class ItemImage implements Serializable {

    // uri of downloaded image. might get de-set whenever this ItemImage passed between activities,
    // because Uri is not serializable. this is OK, because when that happens the image should be
    // getting redownloaded from Firebase Storage anyway
    private transient Uri uri;
    private String storagePath; // path in Firebase Storage
    private String filePath; // path in device filesystem, if applicable

    /**
     * Constructor for initialization from a String path on device.
     * @param storagePath
     */
    public ItemImage(String storagePath) {
        this.storagePath = storagePath;
    }

    /**
     * Constructor for initialization from a URI.
     * @param uri
     */
    public ItemImage(Uri uri) {
        this.uri = uri;
    }

    /**
     * Getter for URI.
     * @return
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * Setter for URI.
     * @param uri
     */
    public void setUri(Uri uri) {
        this.uri = uri;
    }

    /**
     * Getter for storagePath (the String path on Firebase Storage), if it has been set.
     * @return
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * Setter for storagePath (the String path in Firebase Storage)
     * @param storagePath
     */
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    /**
     * Getter for filePath (the String path on device), if it has been set.
     * @return
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Setter for filePath (the String path on device)
     * @param filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Method for printing an image with all three attributes (URI, storagePath, filePath), useful
     * for debug output purposes.
     * @return A formatted string that displays URI, storagePath, and filePath.
     */
    @NonNull
    @Override
    public String toString() {
        return String.format("[IMAGE | uri=%s | storagePath=%s | filePath=%s ]", this.uri, this.storagePath, this.filePath);
    }

    /**
     * Checks if the storagePaths of two images are both non-null and equal. This is useful for checking
     * if two images refer to the same image in Firebase Storage.
     * @param image ItemImage to compare with this ItemImage.
     * @return boolean, 'true' if ItemImages both non-null and refer to same image in Firebase storage, 'false' otherwise.
     */
    public boolean equalRef(@Nullable ItemImage image) {
        if (storagePath == null || image.storagePath == null) {
            return false;
        }
        return storagePath.equals(image.storagePath);
    }

    /**
     * Reads image EXIF data from a filePath (this is only applicable if image stored locally
     * in filesystem by app; i.e. if image was taken using EditActivity's feature for taking images).
     * If this does apply, and filePath has been set properly, this method reads the EXIF data
     * and returns the degrees by which the image should be rotated in order to display it properly.
     * @return Integer number of degrees to rotate image; either 0, 90, 180, or 270. Returns 0
     *      if an error occurs.
     * @reference https://stackoverflow.com/questions/6813166/set-orientation-of-android-camera-started-with-intent-action-image-capture
     */
    public int getNeededRotation() {
        // if image not stored locally, hopefully not an issue
        if (filePath == null) {
            return 0;
        }
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
            return 0;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * If the image represented by this ItemImage is stored in the local filesystem accessible by
     * the app (i.e. if image was taken using EditActivity's feature for taking images), this method
     * obtains the necessary EXIF information to correctly rotate it, rotates it, and then writes
     * the correctly-rotated image bitmap to the same file location.
     *
     * This is useful because rotating the image in-app allows us to display it properly, but it
     * also needs to be rotated in-filesystem if we want it to upload to Firebase Storage with the
     * correct rotation.
     * @param context Context from which this method called.
     * @reference https://stackoverflow.com/questions/30294153/rotate-image-from-uri-and-save-the-rotated-image-to-the-same-place
     */
    public void fixRotation(Context context) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            Log.e("ItemImage", "IOException in fixRotation, dont rotate image");
            return;
        }

        // handle rotation
        Matrix matrix = new Matrix();
        int rot = getNeededRotation();
        Log.d("ItemImage", String.format("Rotation for %s is %d", this, rot));
        matrix.postRotate(getNeededRotation());
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Create an output stream which will write the Bitmap bytes to the file located at the URI path.
        OutputStream os;

        try {
            os = context.getContentResolver().openOutputStream(uri);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            Log.e("ItemImage", "IOException in fixRotation, dont rotate image");
        }

    }
}
