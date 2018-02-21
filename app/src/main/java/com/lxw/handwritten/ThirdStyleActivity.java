package com.lxw.handwritten;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

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

public class ThirdStyleActivity extends AppCompatActivity implements View.OnClickListener {

    private NewDrawPenView drawPenView;
    private Button resetBtn, colorBtn, saveBtn, cancelBtn;

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
        saveBtn = findViewById(R.id.save);
        cancelBtn = findViewById(R.id.cancel);
        resetBtn.setOnClickListener(this);
        colorBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
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
