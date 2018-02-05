package com.lxw.handwritten;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxw.handwritten.utils.UtilBitmap;
import com.lxw.handwritten.utils.UtilScreen;
import com.lxw.handwritten.widget.handwrittenview.IPenConfig;
import com.lxw.handwritten.widget.handwrittenview.NewDrawPenView;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewDrawPenView drawPenView;
    private List<Bitmap> bitmaps;
    private BaseQuickAdapter<Bitmap, BaseViewHolder> adapter;
    private static final int MSG_ADD_CHARACTER = 1000;
    private float left = 0f, right = 0f, top = 0f, bottom = 0f;
    private boolean isDrawPenViewReset = true;
    private int targetWidth;
    private int targetHeight;
    private int screenWidth;
    private int screenHeight;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_CHARACTER:
                    Log.d("MSG_ADD_CHARACTER", "" + (int)left + (int)right + (int)top + (int)bottom);
                    Bitmap bitmap = UtilBitmap.getViewBitmap(drawPenView, left, right, top, bottom);
                    bitmap = UtilBitmap.compress(bitmap, targetWidth, targetHeight);
                    adapter.addData(bitmap);
                    drawPenView.setCanvasCode(IPenConfig.STROKE_TYPE_ERASER);
                    isDrawPenViewReset = true;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpView();
    }

    private void setUpView() {
        int spanCount = Constants.RECYCLERVIEW_SPAN_COUNT;
        screenWidth = UtilScreen.getScreenWidth(this);
        screenHeight = UtilScreen.getScreenHeight(this);
        targetWidth = (screenWidth - (spanCount + 1) *
                getResources().getDimensionPixelSize(R.dimen.dp_6)) / spanCount;
        targetHeight = (int) (targetWidth / Constants.CHARACTER_WIDTH_HEIGHT_SCALE);
        recyclerView = findViewById(R.id.recyclerView);
        drawPenView = findViewById(R.id.drawPenView);
        bitmaps = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, Constants.RECYCLERVIEW_SPAN_COUNT));
        adapter = new BaseQuickAdapter<Bitmap, BaseViewHolder>(R.layout.item_character, bitmaps) {
            @Override
            protected void convert(BaseViewHolder helper, Bitmap item) {
                helper.getView(R.id.imageView).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, targetHeight));
                helper.setImageBitmap(R.id.imageView, item);
            }
        };
        recyclerView.setAdapter(adapter);
        drawPenView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        handler.removeCallbacksAndMessages(null);
                        if (isDrawPenViewReset){
                            left = event.getX();
                            right = event.getX();
                            top = event.getY();
                            bottom = event.getY();
                            isDrawPenViewReset = false;
                        } else {
                            measureBitmapRectangle(event);
                        }
                        break;
                    case ACTION_MOVE:
                        measureBitmapRectangle(event);
                        break;
                    case ACTION_UP:
                        measureBitmapRectangle(event);
                        Message message = Message.obtain();
                        message.what = MSG_ADD_CHARACTER;
                        handler.sendMessageDelayed(message, Constants.ADD_CHARACTER_DELAY_MILLIS);
                        break;
                }
                return false;
            }
        });
    }

    public void measureBitmapRectangle(MotionEvent event){
        // TODO 最小矩形要添加笔锋宽度的一半
        int penPadding = getResources().getDimensionPixelSize(R.dimen.dp_10);
        if (left > event.getX()) left = event.getX() - penPadding;
        if (left < 0) left = 0;
        if (right < event.getX()) right = event.getX() + penPadding;
        if (right > screenWidth) right = screenWidth;
        if (top > event.getY()) top = event.getY() - penPadding;
        if (top < 0) top = 0;
        else if (top > screenHeight * Constants.PADDING_HEIGHT_SCALE) top = screenHeight * Constants.PADDING_HEIGHT_SCALE;
        if (bottom < event.getY()) bottom = event.getY() + penPadding;
        if (bottom > screenHeight) bottom = screenHeight;
    }
}
