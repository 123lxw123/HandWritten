package com.lxw.handwritten.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;

/**
 * Created by Zion on 2018/1/28.
 */

public class UtilBitmap {

    public static Bitmap getViewBitmap(View view) {
        return getViewBitmap(view, 0, 0, view.getWidth(), view.getHeight());
    }

    /**
     * view 的局部截图
     * @param view
     * @param left
     * @param right
     * @param top
     * @param bottom
     * @return
     */
    public static Bitmap getViewBitmap(View view, float left, float right, float top, float bottom) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        try {
            bitmap = Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) (right - left), (int) (bottom - top));

            return bitmap;
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            view.destroyDrawingCache();
        }
        return null;
    }

    public static Bitmap compress(Bitmap bitmap, int targetWidth, int targetHeight) {
        try {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸
            float scaleWidth = (float) targetWidth / bitmapWidth;
            float scaleHeight = (float) targetHeight / bitmapHeight;
            float scale = Math.max(scaleWidth, scaleHeight);
            if (scale > 1f){
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                // 产生缩放后的Bitmap对象
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

}
