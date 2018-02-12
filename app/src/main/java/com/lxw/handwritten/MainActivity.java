package com.lxw.handwritten;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxw.handwritten.utils.UtilBitmap;
import com.lxw.handwritten.utils.UtilScreen;
import com.lxw.handwritten.utils.UtilSharedPreference;
import com.lxw.handwritten.widget.handwrittenview.IPenConfig;
import com.lxw.handwritten.widget.handwrittenview.NewDrawPenView;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView charactersRecyclerView;
    private NewDrawPenView drawPenView;
    private LinearLayout operateLayout;
    private ImageView previewImage;
    private NestedScrollView charactersScrollView;
    private Button resetBtn, colorBtn, confirmBtn, newLineBtn, deleteBtn, previewBtn;
    private List<Bitmap> characterBitmaps;
    private BaseQuickAdapter<Bitmap, BaseViewHolder> adapter;
    private static final int MSG_ADD_CHARACTER = 1000;
    private float left = 0f, right = 0f, top = 0f, bottom = 0f;
    private boolean isDrawPenViewReset = true;
    private boolean isConfirm = false;
    private int targetWidth, targetHeight, screenWidth, screenHeight;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_CHARACTER:
                    Log.d("MSG_ADD_CHARACTER", "" + (int)left + (int)right + (int)top + (int)bottom);
                    Bitmap bitmap = UtilBitmap.getViewBitmap(drawPenView, left, right, top, bottom);
                    bitmap = UtilBitmap.compress(bitmap, targetWidth, targetHeight);
                    adapter.addData(adapter.getData().size() - 1, bitmap);
                    drawPenView.setCanvasCode(IPenConfig.STROKE_TYPE_ERASER);
                    isDrawPenViewReset = true;
                    charactersScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            charactersScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
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

    @SuppressLint("ClickableViewAccessibility")
    private void setUpView() {
        int spanCount = Constants.RECYCLERVIEW_SPAN_COUNT;
        screenWidth = UtilScreen.getScreenWidth(this);
        screenHeight = UtilScreen.getScreenHeight(this);
        targetWidth = (screenWidth - 2 * getResources().getDimensionPixelSize(R.dimen.dp_10) - 2 *
                spanCount * getResources().getDimensionPixelSize(R.dimen.dp_3)) / spanCount;
        targetHeight = (int) (targetWidth / Constants.CHARACTER_WIDTH_HEIGHT_SCALE);
        charactersRecyclerView = findViewById(R.id.charactersRecyclerView);
        drawPenView = findViewById(R.id.drawPenView);
        operateLayout = findViewById(R.id.operateLayout);
        previewImage = findViewById(R.id.previewImage);
        charactersScrollView = findViewById(R.id.charactersScrollView);
        resetBtn = findViewById(R.id.reset);
        colorBtn = findViewById(R.id.color);
        confirmBtn = findViewById(R.id.confirm);
        newLineBtn = findViewById(R.id.newLine);
        deleteBtn = findViewById(R.id.delete);
        previewBtn = findViewById(R.id.preview);
        resetBtn.setOnClickListener(this);
        colorBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        newLineBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        previewBtn.setOnClickListener(this);
        characterBitmaps = new ArrayList<>();
        characterBitmaps.add(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        charactersRecyclerView.setLayoutManager(new GridLayoutManager(this, Constants.RECYCLERVIEW_SPAN_COUNT){

            @Override
            public void setSmoothScrollbarEnabled(boolean enabled) {
                super.setSmoothScrollbarEnabled(isConfirm);
            }
        });
        adapter = new BaseQuickAdapter<Bitmap, BaseViewHolder>(R.layout.item_character, characterBitmaps) {
            @Override
            protected void convert(BaseViewHolder helper, Bitmap item) {

                if (!isConfirm && helper.getPosition() == getData().size() - 1) {
                    helper.getView(R.id.imageView).setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, targetHeight));
                    Animation cursorAnim = AnimationUtils.loadAnimation(MainActivity.this,
                            R.anim.cursor_invest_alpha);
                    helper.setImageResource(R.id.imageView, R.drawable.ic_cursor);
                    helper.getView(R.id.imageView).startAnimation(cursorAnim);
                } else{
                    helper.getView(R.id.imageView).setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, targetHeight));
                    helper.setImageBitmap(R.id.imageView, item);
                    helper.getView(R.id.imageView).clearAnimation();
                }
            }
        };
        charactersRecyclerView.setNestedScrollingEnabled(false);
        charactersRecyclerView.setAdapter(adapter);
        drawPenView.setPaintColor(UtilSharedPreference.getInstance(this).getIntValue(Constants.KEY_DEFAULT_PAINT_COLOR,
                getResources().getColor(R.color.colorBlack)));
        drawPenView.setOnTouchListener(new View.OnTouchListener() {
            int penPadding = getResources().getDimensionPixelSize(R.dimen.dp_20);
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        handler.removeCallbacksAndMessages(null);
                        if (isDrawPenViewReset){
                            left = event.getX() - penPadding;
                            right = event.getX() + penPadding;
                            top = event.getY() - penPadding;
                            bottom = event.getY() + penPadding;
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
        int penPadding = getResources().getDimensionPixelSize(R.dimen.dp_20);
        if (left > event.getX()  - penPadding) left = event.getX() - penPadding;
        if (left < 0) left = 0;
        if (right < event.getX() + penPadding) right = event.getX() + penPadding;
        if (right > screenWidth) right = screenWidth;
        if (top > event.getY() - penPadding) top = event.getY() - penPadding;
        if (top < 0) top = 0;
        else if (top > drawPenView.getHeight() * Constants.PADDING_HEIGHT_SCALE) top = drawPenView.getHeight() * Constants.PADDING_HEIGHT_SCALE;
        if (bottom < event.getY()  + penPadding) bottom = event.getY() + penPadding;
        if (bottom > drawPenView.getHeight() - operateLayout.getHeight()) bottom = drawPenView.getHeight() - operateLayout.getHeight();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                if (adapter.getData().size() > 1) {
                    new MaterialDialog.Builder(this)
                            .title("随手输入")
                            .positiveText("确定")
                            .negativeText("取消")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    characterBitmaps.clear();
                                    characterBitmaps.add(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
                                    adapter.setNewData(characterBitmaps);
                                }
                            })
                            .content("当前操作会清空所有随写，是否继续？")
                            .show();
                }
                break;
            case R.id.color:
                MaterialDialog.Builder colorsBuilder = new MaterialDialog.Builder(this)
                        .title("字体颜色")
                        .negativeText("取消");
                View customView = getLayoutInflater().inflate(R.layout.view_choose_color, null);
                colorsBuilder.customView(customView, true);
                final Dialog colorsDialog = colorsBuilder.build();
                RecyclerView colorsRecyclerView = customView.findViewById(R.id.colorsRecyclerView);
                colorsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                List<Integer> colors = new ArrayList<>();
                colors.add(getResources().getColor(R.color.colorBlack));
                colors.add(getResources().getColor(R.color.colorBlue));
                colors.add(getResources().getColor(R.color.colorRed));
                colors.add(getResources().getColor(R.color.colorGray));
                colors.add(getResources().getColor(R.color.colorYellow));
                colors.add(getResources().getColor(R.color.colorGreen));
                BaseQuickAdapter<Integer, BaseViewHolder> colorsAdapter = new BaseQuickAdapter<Integer,
                        BaseViewHolder>(R.layout.item_color, colors ) {

                    @Override
                    protected void convert(BaseViewHolder helper, Integer item) {
                        helper.setBackgroundColor(R.id.colorLayout, item);
                    }
                };
                colorsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        drawPenView.setPaintColor((Integer) adapter.getData().get(position));
                        UtilSharedPreference.getInstance(MainActivity.this).setValue(Constants.KEY_DEFAULT_PAINT_COLOR,
                                (Integer) adapter.getData().get(position));
                        colorsDialog.dismiss();
                    }
                });
                colorsRecyclerView.setAdapter(colorsAdapter);
                colorsDialog.show();
                break;
            case R.id.confirm:
                if (isConfirm) {
                    confirmBtn.setText("完成");
                    drawPenView.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                    colorBtn.setVisibility(View.VISIBLE);
                    newLineBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    previewBtn.setVisibility(View.GONE);
                    adapter.addData(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
                } else {
                    confirmBtn.setText("编辑");
                    drawPenView.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                    colorBtn.setVisibility(View.GONE);
                    newLineBtn.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);
                    previewBtn.setVisibility(View.VISIBLE);
                    if (adapter.getData().size() > 0) adapter.remove(adapter.getData().size() - 1);
                }
                isConfirm = !isConfirm;
                if (!adapter.getData().isEmpty()) {
                    charactersScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            charactersScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                previewImage.setVisibility(View.GONE);
                break;
            case R.id.newLine:
                if (!charactersRecyclerView.isAnimating()) {
                    List<Bitmap> originData = adapter.getData();
                    int targetPosition = (originData.size() / Constants.RECYCLERVIEW_SPAN_COUNT + 1 ) *
                            Constants.RECYCLERVIEW_SPAN_COUNT + 1;
                    List<Bitmap> emptyBitmaps = new ArrayList<>();
                    for (int i = originData.size(); i < targetPosition; i++) {
                        emptyBitmaps.add(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
                    }
                    adapter.addData(adapter.getData().size() - 1, emptyBitmaps);
                    charactersScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            charactersScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                break;
            case R.id.delete:
                if (adapter.getData().size() > 1 && !charactersRecyclerView.isAnimating()) {
                    adapter.remove(adapter.getData().size() - 2);
                    charactersScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            charactersScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                break;
                case R.id.preview:
                    previewImage.setImageBitmap(UtilBitmap.getScrollViewBitmap(charactersScrollView));
                    previewImage.setVisibility(View.VISIBLE);
                break;
        }
    }
}
