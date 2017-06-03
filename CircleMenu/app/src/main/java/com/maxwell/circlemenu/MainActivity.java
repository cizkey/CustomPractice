package com.maxwell.circlemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] names = new String[]{"安全中心 ", "特色服务", "投资理财",
            "转账汇款", "我的账户", "信用卡"};

    private int[] icons = new int[]{R.drawable.home_mbank_1_normal,
            R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal,
            R.drawable.home_mbank_4_normal, R.drawable.home_mbank_5_normal,
            R.drawable.home_mbank_6_normal};

    private List<CircleItem> circleItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        circleItems = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            CircleItem circleItem = new CircleItem();
            circleItem.itemName = names[i];
            circleItem.itemIcon = icons[i];
            circleItems.add(circleItem);
        }
    }

    private void initView() {
        CircleMenuLayout circleMenuLayout = (CircleMenuLayout) findViewById(R.id.circleMenu);
        circleMenuLayout.addItems(circleItems);
    }
}
