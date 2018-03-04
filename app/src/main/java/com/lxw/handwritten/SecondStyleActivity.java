package com.lxw.handwritten;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxw.handwritten.utils.UtilBitmap;
import com.lxw.handwritten.utils.UtilSharedPreference;
import com.lxw.handwritten.widget.handwrittenview.IPenConfig;
import com.lxw.handwritten.widget.handwrittenview.NewDrawPenView;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static com.lxw.handwritten.Constants.SECOND_HEIGHT_SPAN_COUNT;

public class SecondStyleActivity extends AppCompatActivity implements View.OnClickListener {

    private NewDrawPenView drawPenView;
    private RelativeLayout background;
    private EditText editCharacters;
    private Button resetBtn, colorBtn, confirmBtn, spaceBtn, newLineBtn, deleteBtn, saveBtn, cancelBtn;
    private static final int MSG_ADD_CHARACTER = 1000;
    private float left = 0f, right = 0f, top = 0f, bottom = 0f;
    private boolean isDrawPenViewReset = true;
    private boolean isConfirm = false;
    private int rowWidth,  rowHeight, bitmapHeight;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_CHARACTER:
                    Log.d("MSG_ADD_CHARACTER", "" + (int)left + (int)right + (int)top + (int)bottom);
                    Bitmap bitmap = UtilBitmap.getViewBitmap(drawPenView, left, right, top, bottom);
                    bitmap = UtilBitmap.compress(bitmap, bitmapHeight);
                    UtilBitmap.addEditTextSpan(editCharacters, bitmap);
                    drawPenView.setCanvasCode(IPenConfig.STROKE_TYPE_ERASER);
                    isDrawPenViewReset = true;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_style);
        setUpView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpView() {
        drawPenView = findViewById(R.id.drawPenView);
        resetBtn = findViewById(R.id.reset);
        colorBtn = findViewById(R.id.color);
        confirmBtn = findViewById(R.id.confirm);
        newLineBtn = findViewById(R.id.newLine);
        spaceBtn = findViewById(R.id.space);
        deleteBtn = findViewById(R.id.delete);
        saveBtn = findViewById(R.id.save);
        cancelBtn = findViewById(R.id.cancel);
        resetBtn.setOnClickListener(this);
        colorBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        spaceBtn.setOnClickListener(this);
        newLineBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        editCharacters = findViewById(R.id.editCharacters);
        editCharacters.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        editCharacters.setMaxLines(Constants.SECOND_HEIGHT_SPAN_COUNT);
        editCharacters.setTextColor(UtilSharedPreference.getInstance(this).getIntValue(Constants.KEY_DEFAULT_PAINT_COLOR,
                getResources().getColor(R.color.colorBlack)));
        editCharacters.setCursorVisible(true);
        editCharacters.setTextSize(0);
        editCharacters.post(new Runnable() {
            @Override
            public void run() {
                rowHeight = background.getHeight() / SECOND_HEIGHT_SPAN_COUNT;
                bitmapHeight = rowHeight;
                background.removeAllViews();
                for (int i = 1; i < SECOND_HEIGHT_SPAN_COUNT; i++) {
                    View view = new View(SecondStyleActivity.this);
                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp_1));
                    layoutParams.setMargins(0, rowHeight * i, 0, 0);
                    view.setLayoutParams(layoutParams);
                    view.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dashed));
                    background.addView(view);
                }
                setBitmapHeight();
            }
        });
        // 虚线背景
        background = findViewById(R.id.charactersBackground);
        drawPenView.setPaintColor(UtilSharedPreference.getInstance(this).getIntValue(Constants.KEY_DEFAULT_PAINT_COLOR,
                getResources().getColor(R.color.colorBlack)));
        drawPenView.setOnTouchListener(new View.OnTouchListener() {
            int penPadding = getResources().getDimensionPixelSize(R.dimen.dp_10);
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

    public void setBitmapHeight() {
        UtilBitmap.addEditTextSpan(editCharacters, Bitmap.createBitmap(1, rowHeight, Bitmap.Config.ARGB_8888));
        editCharacters.post(new Runnable() {
            @Override
            public void run() {
                bitmapHeight = (int) ((float) rowHeight * rowHeight / editCharacters.getHeight());
                rowWidth = (int) ((float) rowHeight * editCharacters.getWidth() / editCharacters.getHeight());
                UtilBitmap.deleteAllEditTextSpan(editCharacters);
                UtilBitmap.addEditTextSpan(editCharacters, Bitmap.createBitmap(1, bitmapHeight, Bitmap.Config.ARGB_8888));
            }
        });
    }

    public void measureBitmapRectangle(MotionEvent event){
        int penPadding = getResources().getDimensionPixelSize(R.dimen.dp_20);
        if (left > event.getX()  - penPadding) left = event.getX() - penPadding;
        if (left < 0) left = 0;
        if (right < event.getX() + penPadding) right = event.getX() + penPadding;
        if (right > drawPenView.getWidth()) right = drawPenView.getWidth() ;
        top = 0;
        bottom = drawPenView.getHeight();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                if (editCharacters.getText().length() > 1) {
                    new MaterialDialog.Builder(this)
                            .title("随手输入")
                            .positiveText("确定")
                            .negativeText("取消")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    UtilBitmap.deleteAllEditTextSpan(editCharacters);
                                    UtilBitmap.addEditTextSpan(editCharacters, Bitmap.createBitmap(1, bitmapHeight, Bitmap.Config.ARGB_8888));
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
                        UtilSharedPreference.getInstance(SecondStyleActivity.this).setValue(Constants.KEY_DEFAULT_PAINT_COLOR,
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
                    editCharacters.setCursorVisible(true);
                    drawPenView.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                    colorBtn.setVisibility(View.VISIBLE);
                    spaceBtn.setVisibility(View.VISIBLE);
                    newLineBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.GONE);
                } else {
                    confirmBtn.setText("编辑");
                    editCharacters.setCursorVisible(false);
                    drawPenView.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                    colorBtn.setVisibility(View.GONE);
                    spaceBtn.setVisibility(View.GONE);
                    newLineBtn.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);
                    saveBtn.setVisibility(View.VISIBLE);
                }
                isConfirm = !isConfirm;
                break;
            case R.id.space:
                UtilBitmap.addEditTextSpan(editCharacters, Bitmap.createBitmap(bitmapHeight / 3, 1, Bitmap.Config.ARGB_8888));
                break;
            case R.id.newLine:
                newLineBtn.setEnabled(false);
                int lineCount = editCharacters.getLineCount();
                int space = 4; // 决定计算换行的速度，越大越快
                for (int i = 1; i < rowWidth; i = i + space) {
                    Log.d("rowWidth", i + "     " +  editCharacters.getLineCount());
                    SpannableString span = new SpannableString("1");
                    span.setSpan(new ImageSpan(Bitmap.createBitmap(i, 1, Bitmap.Config.ARGB_8888)) , span.length() - 1, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    Editable editable = Editable.Factory.getInstance().newEditable(editCharacters.getText());
                    editable.insert(editCharacters.getSelectionStart(), span);
                    Layout layout = new StaticLayout(editable, editCharacters.getPaint(),
                            editCharacters.getWidth() - editCharacters.getPaddingLeft() - editCharacters.getPaddingRight(),
                            Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
                    if (layout.getLineCount() > lineCount) {
                        UtilBitmap.addEditTextSpan(editCharacters, Bitmap.createBitmap(i - space, 1, Bitmap.Config.ARGB_8888));
                        UtilBitmap.addEditTextSpan(editCharacters, Bitmap.createBitmap(space + 1, bitmapHeight, Bitmap.Config.ARGB_8888));
                        break;
                    }
                }
                newLineBtn.setEnabled(true);
                break;
            case R.id.delete:
                deleteBtn.setEnabled(false);
                UtilBitmap.deleteEditTextSpan(editCharacters);
                if (editCharacters.length() == 0) UtilBitmap.addEditTextSpan(editCharacters, Bitmap.createBitmap(1, bitmapHeight, Bitmap.Config.ARGB_8888));
                editCharacters.setCursorVisible(true);
                deleteBtn.setEnabled(true);
                break;
            case R.id.save:
                Intent intent = new Intent();
                editCharacters.setCursorVisible(false);
                Constants.characters = UtilBitmap.getViewBitmap(editCharacters);
                setResult(AppCompatActivity.RESULT_OK, intent);
                finish();
                break;
            case R.id.cancel:
                finish();
                break;}
    }
}
