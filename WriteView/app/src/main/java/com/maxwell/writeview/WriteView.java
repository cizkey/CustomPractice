package com.maxwell.writeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 手写View
 * Created by Administrator on 2017/5/25.
 */

public class WriteView extends View {

    //画笔
    private Paint paint;

    //用户触摸的路径
    private Path path;

    public WriteView(Context context) {
        this(context, null);
    }

    public WriteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WriteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true); //抗锯齿
        paint.setDither(true); //防抖动
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);//设置画笔连接处圆润
        paint.setStrokeWidth(10f);//画笔大小

        path = new Path();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        postInvalidate();//重绘
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path,paint); //绘制路径
    }

    /**
     * 清空路径
     */
    public void reset(){
        path.reset();
        postInvalidate();
    }
}
