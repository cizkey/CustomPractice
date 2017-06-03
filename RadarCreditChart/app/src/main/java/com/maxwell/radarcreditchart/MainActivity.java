package com.maxwell.radarcreditchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //各维度标题
    private String[] titles = {"履约能力", "信用历史", "人脉关系", "行为偏好", "身份特质"};

    //各维度图标
    private int[] icons = {R.mipmap.ic_performance, R.mipmap.ic_history, R.mipmap.ic_contacts,
            R.mipmap.ic_predilection, R.mipmap.ic_identity};

    //各维度分值
    private float[] scores = {170, 180, 120, 170, 180};

    private List<CreditBean> creditBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        creditBeans = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            CreditBean creditBean = new CreditBean();
            creditBean.title = titles[i];
            creditBean.icon = icons[i];
            creditBean.score = scores[i];
            creditBeans.add(creditBean);
        }
    }

    private void initView() {
        RadarCreditChart radarCreditChart = (RadarCreditChart) findViewById(R.id.radarCreditChart);
        radarCreditChart.setData(creditBeans, 200);
    }

}
