package net.solutinno.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DrawableHelper
{
    public static Drawable getDrawableFromBitmap(Bitmap bitmap, int... dimension) {
        if (bitmap == null) return null;
        BitmapDrawable result;
        if (dimension == null || dimension.length < 2) result = new BitmapDrawable(null, bitmap);
        else result = new BitmapDrawable(null, Bitmap.createScaledBitmap(bitmap, dimension[0], dimension[1], true));
        return result;
    }
}
