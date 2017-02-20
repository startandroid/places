package ru.startandroid.places.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class PicassoWrapper {

    private Context context;
    private Picasso picasso;

    public PicassoWrapper() {
        // to mock Picasso in tests
    }

    public PicassoWrapper(Context context) {
        this.context = context;
        picasso = Picasso.with(context);
    }

    public Bitmap loadBitmap(String path) {
        if (picasso == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            bitmap = picasso.load(path).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

