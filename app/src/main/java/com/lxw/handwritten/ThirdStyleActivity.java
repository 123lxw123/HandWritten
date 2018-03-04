package com.lxw.handwritten;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import static com.lxw.handwritten.Constants.THIRD_HEIGHT_SPAN_COUNT;

public class ThirdStyleActivity extends AppCompatActivity implements View.OnClickListener {

    private NewDrawPenView drawPenView;
    private RelativeLayout background;
    private Button resetBtn, colorBtn, sizeBtn, saveBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_style);
        setUpView();
    }

    private void setUpView() {
        drawPenView = findViewById(R.id.drawPenView);
        resetBtn = findViewById(R.id.reset);
        colorBtn = findViewById(R.id.color);
        sizeBtn = findViewById(R.id.size);
        saveBtn = findViewById(R.id.save);
        cancelBtn = findViewById(R.id.cancel);
        resetBtn.setOnClickListener(this);
        colorBtn.setOnClickListener(this);
        sizeBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        background = findViewById(R.id.background);
        background.post(new Runnable() {
            @Override
            public void run() {
                background.removeAllViews();
                for (int i = 1; i < THIRD_HEIGHT_SPAN_COUNT; i++) {
                    View view = new View(ThirdStyleActivity.this);
                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp_1));
                    layoutParams.setMargins(0, background.getHeight() * i / THIRD_HEIGHT_SPAN_COUNT, 0, 0);
                    view.setLayoutParams(layoutParams);
                    view.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dashed));
                    background.addView(view);
                }
            }
        });
        drawPenView.setPaintColor(UtilSharedPreference.getInstance(this).getIntValue(Constants.KEY_DEFAULT_PAINT_COLOR,
                getResources().getColor(R.color.colorBlack)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                new MaterialDialog.Builder(this)
                        .title("随手输入")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull
                                    DialogAction which) {
                                drawPenView.setCanvasCode(IPenConfig.STROKE_TYPE_ERASER);
                            }
                        })
                        .content("当前操作会清空所有随写，是否继续？")
                        .show();
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
                        UtilSharedPreference.getInstance(ThirdStyleActivity.this).setValue(Constants.KEY_DEFAULT_PAINT_COLOR,
                                (Integer) adapter.getData().get(position));
                        colorsDialog.dismiss();
                    }
                });
                colorsRecyclerView.setAdapter(colorsAdapter);
                colorsDialog.show();
                break;
            case R.id.size:
                MaterialDialog.Builder sizesBuilder = new MaterialDialog.Builder(this)
                        .title("笔锋粗细")
                        .negativeText("取消");
                View customSizeView = getLayoutInflater().inflate(R.layout.view_choose_color, null);
                sizesBuilder.customView(customSizeView, true);
                final Dialog sizesDialog = sizesBuilder.build();
                RecyclerView sizesRecyclerView = customSizeView.findViewById(R.id.colorsRecyclerView);
                sizesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                List<Integer> sizes = new ArrayList<>();
                sizes.add(drawPenView.getPaintColor());
                sizes.add(drawPenView.getPaintColor());
                sizes.add(drawPenView.getPaintColor());
                BaseQuickAdapter<Integer, BaseViewHolder> sizesAdapter = new BaseQuickAdapter<Integer,
                        BaseViewHolder>(R.layout.item_color, sizes ) {

                    @Override
                    protected void convert(BaseViewHolder helper, Integer item) {
                        helper.setBackgroundColor(R.id.colorLayout, item);
                        helper.getView(R.id.colorLayout).getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.dp_30) +
                                helper.getAdapterPosition() * getResources().getDimensionPixelSize(R.dimen.dp_6);
                        helper.getView(R.id.colorLayout).getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.dp_30) +
                                helper.getAdapterPosition() * getResources().getDimensionPixelSize(R.dimen.dp_6);
                    }
                };
                sizesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Constants.MIN_PEN_WIDTH = Constants.DEFAULT_MIN_PEN_WIDTH + position * 20;
                        Constants.MAX_PEN_WIDTH= Constants.DEFAULT_MAX_PEN_WIDTH + position * 20;
                        Constants.PEN_WIDTH = Constants.DEFAULT_PEN_WIDTH + position * 20;
                        UtilSharedPreference.getInstance(getApplicationContext()).setValue(Constants.KEY_MIN_PEN_WIDTH, Constants.MIN_PEN_WIDTH);
                        UtilSharedPreference.getInstance(getApplicationContext()).setValue(Constants.KEY_MAX_PEN_WIDTH, Constants.MAX_PEN_WIDTH);
                        UtilSharedPreference.getInstance(getApplicationContext()).setValue(Constants.KEY_PEN_WIDTH, Constants.PEN_WIDTH);
                        drawPenView.setPaintWidth(Constants.PEN_WIDTH);
                        sizesDialog.dismiss();
                    }
                });
                sizesRecyclerView.setAdapter(sizesAdapter);
                sizesDialog.show();
                break;
                case R.id.save:
                    Intent intent = new Intent();
                    Constants.characters = UtilBitmap.getRotationBitmap(UtilBitmap.getViewBitmap(drawPenView), 90);
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    finish();
                break;
                case R.id.cancel:
                    finish();
                break;
        }
    }
}
