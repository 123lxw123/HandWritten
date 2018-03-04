package com.lxw.handwritten;

import android.graphics.Bitmap;

/**
 * Created by Zion on 2018/2/1.
 */

public class Constants {
    // 默认值 start
    public static int DEFAULT_FIRST_HEIGHT_SPAN_COUNT = 10;// 风格1 虚线行数
    public static int DEFAULT_SECOND_HEIGHT_SPAN_COUNT = 5;// 风格2 线行数
    public static int DEFAULT_THIRD_HEIGHT_SPAN_COUNT = 6;// 风格3 虚线行数
    public static int DEFAULT_MAX_PEN_WIDTH = 20;// 最大笔宽
    public static int DEFAULT_MIN_PEN_WIDTH = 10;// 最小笔宽
    public static int DEFAULT_PEN_WIDTH = 30;// 初始化笔宽
    public static int ADD_CHARACTER_DELAY_MILLIS = 1000;// 停止滑动延迟添加字的时间
    // 默认值 end

    public static int FIRST_HEIGHT_SPAN_COUNT = DEFAULT_FIRST_HEIGHT_SPAN_COUNT;
    public static int SECOND_HEIGHT_SPAN_COUNT = DEFAULT_SECOND_HEIGHT_SPAN_COUNT;
    public static int THIRD_HEIGHT_SPAN_COUNT = DEFAULT_THIRD_HEIGHT_SPAN_COUNT;
    public static String KEY_DEFAULT_PAINT_COLOR = "KEY_DEFAULT_PAINT_COLOR";
    public static String KEY_FIRST_HEIGHT_SPAN_COUNT = "KEY_FIRST_HEIGHT_SPAN_COUNT";
    public static String KEY_SECOND_HEIGHT_SPAN_COUNT = "KEY_SECOND_HEIGHT_SPAN_COUNT";
    public static String KEY_THIRD_HEIGHT_SPAN_COUNT = "KEY_THIRD_HEIGHT_SPAN_COUNT";
    public static String KEY_MAX_PEN_WIDTH = "KEY_MAX_PEN_WIDTH";
    public static String KEY_MIN_PEN_WIDTH = "KEY_MIN_PEN_WIDTH";
    public static String KEY_PEN_WIDTH = "KEY_PEN_WIDTH";
    public static int MAX_PEN_WIDTH = DEFAULT_MAX_PEN_WIDTH;
    public static int MIN_PEN_WIDTH = DEFAULT_MIN_PEN_WIDTH;
    public static int PEN_WIDTH = DEFAULT_PEN_WIDTH;
    public static Bitmap characters = null;
}
