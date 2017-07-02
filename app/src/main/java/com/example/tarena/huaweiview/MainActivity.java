package com.example.tarena.huaweiview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    HuaWeiView hwv;
    LinearLayout ll_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hwv = (HuaWeiView) findViewById(R.id.hwv);
        //实例父布局
        ll_parent= (LinearLayout) findViewById(R.id.activity_main);
        hwv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击事件中，调用动的方法
                hwv.changeAngle(200);
            }
        });
        // 设置角度颜色变化监听
        hwv.setOnAngleColorListener(new HuaWeiView.OnAngleColorListener() {
            @Override
            public void colorListener(int red, int green) {
                Color color = new Color();
                // 通过Color对象将RGB值转为int类型
                int backColor = color.argb(100,red,green,0);
                // 父布局设置背景
                ll_parent.setBackgroundColor(backColor);
            }
        });
    }

}
