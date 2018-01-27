package com.lxw.handwritten;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxw.handwritten.widget.handwrittenview.NewDrawPenView;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewDrawPenView drawPenView;
    private List<Bitmap> bitmaps;
    private BaseQuickAdapter<Bitmap, BaseViewHolder> adapter;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

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

                        break;
                        case ACTION_UP:

                        break;
                }
                return false;
            }
        });
    }
}
