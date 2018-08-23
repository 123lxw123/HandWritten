package com.lxw.handwritten.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.lxw.handwritten.R;

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

    public static Bitmap getScrollViewBitmap(ScrollView scrollView) {
        int height = 0;
        //理论上scrollView只会有一个子View啦
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            height += scrollView.getChildAt(i).getHeight();
        }
        Log.d("ScrollViewheight", height + "");
        //创建保存缓存的bitmap
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), height, Bitmap.Config.ARGB_8888);
        //可以简单的把Canvas理解为一个画板 而bitmap就是块画布
        Canvas canvas = new Canvas(bitmap);
        //获取ScrollView的背景颜色
        Drawable background = scrollView.getContext().getResources().getDrawable(R.drawable.transparent);
        //画出ScrollView的背景色 这里只用了color一种 有需要也可以自己扩展 也可以自己直接指定一种背景色
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            int color = colorDrawable.getColor();
            canvas.drawColor(color);
        }
        //把view的内容都画到指定的画板Canvas上
        scrollView.draw(canvas);
        return bitmap;
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
