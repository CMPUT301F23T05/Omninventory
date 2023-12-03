package com.example.omninventory;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class ItemImage implements Serializable {
    private transient Uri uri; // uri of downloaded image. might get de-set whenever this ItemImage passed between activities, because Uri is not serializable.
    private String storagePath; // path in storage
    private String filePath; // path on system, if applicable

    public ItemImage(String storagePath) {
        this.storagePath = storagePath;
    }

    public ItemImage(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("[IMAGE | uri=%s | storagePath=%s | filePath=%s ]", this.uri, this.storagePath, this.filePath);
    }

    public boolean equalRef(@Nullable ItemImage image) {
        if (storagePath == null || image.storagePath == null) {
            return false;
        }
        return storagePath.equals(image.storagePath);
    }

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

//    public Bitmap getBitmap(Context context) {
//        return getRotatedBitmap(context, uri);
//    }
//
//    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
//
//        ContentResolver cr = context.getContentResolver();
//        cr.notifyChange(uri, null);
//        Bitmap bitmap;
//
//        try {
//            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);
//            return bitmap;
//        }
//        catch (IOException e) {
//            return null;
//        }
//    }
//
//    private static Bitmap getRotatedBitmap(Context context, Uri imageUri) {
//
//        // referenced from https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
//        ExifInterface ei;
//
//        try {
//            ei = new ExifInterface(imageUri.toString());
//        }
//        catch (IOException e) {
//            Log.e("ItemImage", "IOException in bitmap");
//            return null;
//        }
//
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED);
//
//        Bitmap bitmap = getBitmapFromUri(context, imageUri);
//        Bitmap rotatedBitmap;
//
//        switch(orientation) {
//
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                rotatedBitmap = rotateImage(bitmap, 90);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                rotatedBitmap = rotateImage(bitmap, 180);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                rotatedBitmap = rotateImage(bitmap, 270);
//                break;
//
//            case ExifInterface.ORIENTATION_NORMAL:
//            default:
//                rotatedBitmap = bitmap;
//        }
//
//        return rotatedBitmap;
//    }
//
//    public static Bitmap rotateImage(Bitmap source, float angle) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
//                matrix, true);
//    }
}
