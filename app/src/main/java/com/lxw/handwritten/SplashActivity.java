package com.lxw.handwritten;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener{

    private Button styleBtn1, styleBtn2, styleBtn3;
    private ImageView characters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setUpView();
    }

    private void setUpView() {
        styleBtn1 = findViewById(R.id.style1);
        styleBtn2 = findViewById(R.id.style2);
        styleBtn3 = findViewById(R.id.style3);
        characters = findViewById(R.id.characters);
        styleBtn1.setOnClickListener(this);
        styleBtn2.setOnClickListener(this);
        styleBtn3.setOnClickListener(this);
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
        }
    }
}
