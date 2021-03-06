package com.lxw.handwritten;

import android.graphics.Bitmap;

/**
 * Created by Zion on 2018/2/1.
 */

public class Constants {
    public static int RECYCLERVIEW_SPAN_COUNT = 8;// 每行字数
    public static int ADD_CHARACTER_DELAY_MILLIS = 1000;// 停止滑动延迟添加字的时间
    public static float CHARACTER_WIDTH_HEIGHT_SCALE = 4f/5f;// 字的宽高比例
    public static float PADDING_HEIGHT_SCALE = 1f/4f;// 截取字的 bitmap 顶部尽量不截取屏幕比例
    public static String KEY_DEFAULT_PAINT_COLOR = "KEY_DEFAULT_PAINT_COLOR";// 默认笔的颜色
    public static int MAX_PEN_WIDTH = 20;
    public static int MIN_PEN_WIDTH = 10;
    public static Bitmap characters = null;
}
