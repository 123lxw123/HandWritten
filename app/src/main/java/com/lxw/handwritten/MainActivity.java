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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxw.handwritten.widget.handwrittenview.IPenConfig;
import com.lxw.handwritten.widget.handwrittenview.NewDrawPenView;
import com.lxw.handwritten.widget.handwrittenview.UtilBitmap;

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
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_CHARACTER:
                    Log.d("MSG_ADD_CHARACTER", "" + (int)left + (int)right + (int)top + (int)bottom);
                    Bitmap bitmap = UtilBitmap.getViewBitmap(drawPenView, left, right, top, bottom);
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
        recyclerView = findViewById(R.id.recyclerView);
        drawPenView = findViewById(R.id.drawPenView);
        bitmaps = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 10));
        adapter = new BaseQuickAdapter<Bitmap, BaseViewHolder>(R.layout.item_character, bitmaps) {
            @Override
            protected void convert(BaseViewHolder helper, Bitmap item) {
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
                            if (left > event.getX()) left = event.getX();
                            if (right < event.getX()) right = event.getX();
                            if (top > event.getY()) top = event.getY();
                            if (bottom < event.getY()) bottom = event.getY();
                        }
                        break;
                    case ACTION_MOVE:
                        if (left > event.getX()) left = event.getX();
                        if (right < event.getX()) right = event.getX();
                        if (top > event.getY()) top = event.getY();
                        if (bottom < event.getY()) bottom = event.getY();
                        break;
                    case ACTION_UP:
                        if (left > event.getX()) left = event.getX();
                        if (right < event.getX()) right = event.getX();
                        if (top > event.getY()) top = event.getY();
                        if (bottom < event.getY()) bottom = event.getY();
                        Message message = Message.obtain();
                        message.what = MSG_ADD_CHARACTER;
                        handler.sendMessageDelayed(message, 1000);
                        break;
                }
                return false;
            }
        });
    }
}
