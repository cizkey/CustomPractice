package com.maxwell.circlemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/6/3.
 */

public class CircleMenuLayout extends ViewGroup {

    //直径
    private int diameter;

    //半径
    private int radius;

    //父布局中心点到子View矩形中心点的距离
    private int distance;

    //开始角度
    private float startAngle;

    public CircleMenuLayout(Context context) {
        this(context, null);
    }

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measureWidth;
        int measureHeight;
        //获得测量大小和模式
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) { //精确模式
            measureWidth = measureHeight = Math.min(size, getDefaultWidth());
        } else { //wrap_content模式
            //获得指定的背景大小,背景有多大,控件就有多大
            int suggestedMinimumWidth = getSuggestedMinimumWidth();
            //如果suggestedMinimumWidth为0, 则没有背景
            if (suggestedMinimumWidth == 0) { //如果没有背景,则设置为默认宽度
                measureWidth = measureHeight = getDefaultWidth();
            } else { //如果有背景,则设置为背景宽和默认宽度的最小值
                measureWidth = measureHeight = Math.min(suggestedMinimumWidth, getDefaultWidth());
            }
        }
        setMeasuredDimension(measureWidth, measureHeight);

        //直径为测量的宽度
        diameter = measureWidth;

        //测量子View
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            //默认系统是通过onMeasure给予MeasureSpec参数的,而对于inflate进来的子视图是没有MeasureSpec参数的
            //因此,我们需要自己设计MeasureSpec,传递给子视图
            int makeMeasureSpec = MeasureSpec.makeMeasureSpec(diameter / 3, MeasureSpec.EXACTLY);
            childView.measure(makeMeasureSpec, makeMeasureSpec);
        }
    }

    /**
     * 获得屏幕宽高中的较小值
     */
    public int getDefaultWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        return Math.min(widthPixels, heightPixels);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = diameter / 2;
        distance = diameter / 3;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childWidth = childView.getMeasuredWidth();
            int left = (int) (radius + distance * Math.cos(Math.toRadians(startAngle)) - childWidth / 2);
            int top = (int) (radius + distance * Math.sin(Math.toRadians(startAngle)) - childWidth / 2);
            int right = left + childWidth;
            int bottom = top + childWidth;
            childView.layout(left, top, right, bottom);
            startAngle += 360 / getChildCount();
        }
    }

    private float lastX;
    private float lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:

                float start = CircleUtil.getAngle(lastX, lastY, diameter); //开始角度
                float end = CircleUtil.getAngle(x, y, diameter); //结束角度
                float angle; //旋转的角度

                //todo 这里有很大的疑问? 为什么这个角度是负数?
                //判断点击的点所处的象限,如果是1,4象限,角度值是正数,否则是负数
                if (CircleUtil.getQuadrant(x, y, diameter) == 1 || CircleUtil.getQuadrant(x, y, diameter) == 4) {
                    angle = end - start;
                } else {
                    angle = start - end;
                }
                startAngle += angle;
                requestLayout();//重新布局
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 添加子View
     */
    public void addItems(List<CircleItem> circleItems) {
        for (CircleItem circleItem : circleItems) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_circle_item, this, false);
            TextView tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvName.setText(circleItem.itemName);
            ImageView ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            ivIcon.setImageResource(circleItem.itemIcon);
            addView(itemView);
        }
    }
}
