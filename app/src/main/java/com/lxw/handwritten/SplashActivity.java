package com.lxw.handwritten;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lxw.handwritten.utils.UtilSharedPreference;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener{

    private Button styleBtn1, styleBtn2, styleBtn3, countBtn;
    private ImageView characters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setUpDefaultValue();
        setUpView();
    }

    private void setUpDefaultValue() {
        Constants.FIRST_HEIGHT_SPAN_COUNT = UtilSharedPreference.getInstance(this)
                .getIntValue(Constants.KEY_FIRST_HEIGHT_SPAN_COUNT, Constants.DEFAULT_FIRST_HEIGHT_SPAN_COUNT);
        Constants.SECOND_HEIGHT_SPAN_COUNT = UtilSharedPreference.getInstance(this)
                .getIntValue(Constants.KEY_SECOND_HEIGHT_SPAN_COUNT, Constants.DEFAULT_SECOND_HEIGHT_SPAN_COUNT);
        Constants.THIRD_HEIGHT_SPAN_COUNT = UtilSharedPreference.getInstance(this)
                .getIntValue(Constants.KEY_THIRD_HEIGHT_SPAN_COUNT, Constants.DEFAULT_THIRD_HEIGHT_SPAN_COUNT);
        Constants.PEN_WIDTH = UtilSharedPreference.getInstance(this).getIntValue(Constants.KEY_PEN_WIDTH, Constants.DEFAULT_PEN_WIDTH);
        Constants.MAX_PEN_WIDTH = UtilSharedPreference.getInstance(this).getIntValue(Constants.KEY_MAX_PEN_WIDTH, Constants.DEFAULT_MAX_PEN_WIDTH);
        Constants.MIN_PEN_WIDTH = UtilSharedPreference.getInstance(this).getIntValue(Constants.KEY_MIN_PEN_WIDTH, Constants.DEFAULT_MIN_PEN_WIDTH);
    }

    private void setUpView() {
        styleBtn1 = findViewById(R.id.style1);
        styleBtn2 = findViewById(R.id.style2);
        styleBtn3 = findViewById(R.id.style3);
        countBtn = findViewById(R.id.count);
        characters = findViewById(R.id.characters);
        styleBtn1.setOnClickListener(this);
        styleBtn2.setOnClickListener(this);
        styleBtn3.setOnClickListener(this);
        countBtn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constants.characters != null) characters.setImageBitmap(Constants.characters);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.style1:
                Intent intent1 = new Intent(this, FirstStyleActivity.class);
                startActivityForResult(intent1, 1000);
                break;
            case R.id.style2:
                Intent intent2 = new Intent(this, SecondStyleActivity.class);
                startActivityForResult(intent2, 2000);
                break;
            case R.id.style3:
                Intent intent3 = new Intent(this, ThirdStyleActivity.class);
                startActivityForResult(intent3, 3000);
                break;
            case R.id.count:
                MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                        .title("行数设置")
                        .positiveText("确定")
                        .negativeText("取消");
                View customView = getLayoutInflater().inflate(R.layout.view_count, null);
                final EditText first = customView.findViewById(R.id.etFirstCount);
                final EditText second = customView.findViewById(R.id.etSecondCount);
                final EditText third = customView.findViewById(R.id.etThirdCount);
                first.setText(Constants.FIRST_HEIGHT_SPAN_COUNT + "");
                second.setText(Constants.SECOND_HEIGHT_SPAN_COUNT + "");
                third.setText(Constants.THIRD_HEIGHT_SPAN_COUNT + "");
                builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try{
                            int count1 = Integer.valueOf(first.getText().toString());
                            int count2 = Integer.valueOf(second.getText().toString());
                            int count3 = Integer.valueOf(third.getText().toString());
                            if (count1 < 4 || count2 < 3 || count3 < 1) {
                                Toast.makeText(SplashActivity.this, "设置失败，行数必须为正整数且风格1不少于4行，风格2不少于3行", Toast.LENGTH_SHORT).show();
                            } else {
                                Constants.FIRST_HEIGHT_SPAN_COUNT = count1;
                                Constants.SECOND_HEIGHT_SPAN_COUNT = count2;
                                Constants.THIRD_HEIGHT_SPAN_COUNT = count3;
                                UtilSharedPreference.getInstance(getApplicationContext()).setValue(Constants.KEY_FIRST_HEIGHT_SPAN_COUNT, Constants.FIRST_HEIGHT_SPAN_COUNT);
                                UtilSharedPreference.getInstance(getApplicationContext()).setValue(Constants.KEY_SECOND_HEIGHT_SPAN_COUNT, Constants.SECOND_HEIGHT_SPAN_COUNT);
                                UtilSharedPreference.getInstance(getApplicationContext()).setValue(Constants.KEY_THIRD_HEIGHT_SPAN_COUNT, Constants.THIRD_HEIGHT_SPAN_COUNT);
                            }
                        }catch (Exception e) {
                            Toast.makeText(SplashActivity.this, "设置失败，行数必须为正整数",Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.customView(customView, true);
                final Dialog dialog = builder.build();
                dialog.show();
                break;
        }
    }
}
