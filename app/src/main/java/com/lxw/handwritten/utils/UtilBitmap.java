package com.lxw.handwritten.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;

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

    public static Bitmap compress(Bitmap bitmap, int targetHeight) {
        try {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸
            float scale = (float) targetHeight / bitmapHeight;
            if (scale < 1f){
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                // 产生缩放后的Bitmap对象a
                return Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            bitmap.recycle();
        }
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

    public static void addEditTextSpan(EditText editText, Bitmap bitmap) {
        if(bitmap == null)
            return;
        SpannableString span = new SpannableString("1");
        int start = editText.getSelectionStart();
        span.setSpan(new ImageSpan(bitmap) , span.length() - 1, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(editText != null) {
            Editable et = editText.getText();
            et.insert(start, span);
            editText.setText(et);
            editText.setSelection(start + span.length());
        }
        editText.requestLayout();
    }
    /*
    * this is delete bitmap on edit text
    * from end to start
    */
    public static void deleteAllEditTextSpan(EditText editText) {
        Spanned s = editText.getEditableText();
        ImageSpan[] imageSpan = s.getSpans(0, s.length(), ImageSpan.class);
        for (int i = imageSpan.length - 1; i >= 0; i--) {
            int start = s.getSpanStart(imageSpan[i]);
            int end = s.getSpanEnd(imageSpan[i]);
            Editable et = editText.getText();
            et.delete(start, end);
        }
        editText.requestLayout();
    }

    public static void deleteEditTextSpan(EditText editText) {
        Spanned s = editText.getEditableText();
        int i = s.length() - 1;
        if (i >= 0) {
            Editable et = editText.getText();
            et.delete(i, i + 1);
            editText.setSelection(i);
        }
        editText.requestLayout();
    }
}
