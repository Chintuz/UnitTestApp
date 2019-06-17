package com.test.testapp.utils;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class BindingUtils {

    @BindingAdapter({"app:image"})
    public static void setImage(ImageView view, Bitmap bitmap) {
        if (bitmap != null)
            view.setImageBitmap(bitmap);
        else
            view.setImageBitmap(null);
    }
}
