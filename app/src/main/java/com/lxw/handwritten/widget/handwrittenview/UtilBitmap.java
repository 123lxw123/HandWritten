package com.lxw.handwritten.widget.handwrittenview;

import android.graphics.Bitmap;
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

}
