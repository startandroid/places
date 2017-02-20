package ru.startandroid.places.web.data;

import android.graphics.Bitmap;

public class ItemBitmapWrapper {
    private final Item item;
    private final Bitmap bitmap;

    public ItemBitmapWrapper(Item item, Bitmap bitmap) {
        this.item = item;
        this.bitmap = bitmap;
    }

    public Item getItem() {
        return item;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}