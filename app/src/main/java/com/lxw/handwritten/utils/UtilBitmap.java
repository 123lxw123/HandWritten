package com.lxw.handwritten.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.widget.NestedScrollView;
import android.view.View;

/**
 * Created by Zion on 2018/1/28.
 */

public class UtilBitmap {

    public static Bitmap getViewBitmap(View view) {
        return getViewBitmap(view, 0, view.getWidth(), 0, view.getHeight());
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
            return Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) (right - left), (int) (bottom - top), null, true);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            view.destroyDrawingCache();
//            bitmap.recycle();
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
            if (scale < 1f){
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                // 产生缩放后的Bitmap对象
                return Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            bitmap.recycle();
        }
        return bitmap;
    }

    /**
     * NestedScrollView截屏
     * @param scrollView 要截图的NestedScrollView
     * @return Bitmap
     */
    public static Bitmap getScrollViewBitmap(NestedScrollView scrollView) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#00000000"));
        }
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    public static Bitmap getRotationBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        try {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
