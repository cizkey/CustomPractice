package com.maxwell.radarcreditchart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.List;

/**
 * 雷达信用图
 * Created by Administrator on 2017/6/3.
 */

public class RadarCreditChart extends View {

    //数据的数量
    private int dataCount;
    //弧度
    private float radian;
    //多边形的外接圆的半径
    private int radius;
    //中心点x
    private int centerX;
    //中心点y
    private int centerY;

    private List<CreditBean> creditBeans;
    //最大分值
    public int maxScore;

    private Paint linePaint;
    private Paint regionPaint;
    private Paint totalScorePaint;
    private Paint titlePaint;

    public RadarCreditChart(Context context) {
        this(context, null);
    }

    public RadarCreditChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarCreditChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.MITER);
        linePaint.setStrokeWidth(2);

        regionPaint = new Paint();
        regionPaint.setAntiAlias(true);
        regionPaint.setDither(true);
        regionPaint.setColor(Color.WHITE);
        regionPaint.setAlpha(120);
        regionPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        totalScorePaint = new Paint();
        totalScorePaint.setAntiAlias(true);
        totalScorePaint.setDither(true);
        totalScorePaint.setColor(Color.WHITE);
        totalScorePaint.setStyle(Paint.Style.STROKE);
        totalScorePaint.setTextSize(DensityUtils.dp2px(getContext(), 24));

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setDither(true);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setStyle(Paint.Style.STROKE);
        titlePaint.setTextSize(DensityUtils.dp2px(getContext(), 14));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth;
        int measureHeight;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            measureWidth = measureHeight = Math.min(size, getDefaultWidth());
        } else {
            measureWidth = measureHeight = getDefaultWidth();
            if (mode == MeasureSpec.AT_MOST) {
                measureWidth = measureHeight = Math.min(measureWidth, size);
            }
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /**
     * 获得屏幕中宽高中的较小值
     */
    public int getDefaultWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = (int) (Math.min(h, w) / 2 * 0.5f); //外接圆半径为View宽度的1/4
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制多边形
        drawPolygon(canvas);
        //绘制连接线
        drawLines(canvas);
        //绘制填充区域
        drawRegion(canvas);
        //绘制总分数
        drawTotalScore(canvas);
        //绘制标题和图标
        drawTitleAndIcons(canvas);
    }

    /**
     * 绘制多边形
     */
    private void drawPolygon(Canvas canvas) {
        Path polygonPath = new Path();
        for (int i = 0; i < dataCount; i++) {
            if (i == 0) {
                polygonPath.moveTo(getPoint(i).x, getPoint(i).y);
            } else {
                polygonPath.lineTo(getPoint(i).x, getPoint(i).y);
            }
        }
        polygonPath.close();
        canvas.drawPath(polygonPath, linePaint);
    }

    /**
     * 绘制连接线
     */
    private void drawLines(Canvas canvas) {
        for (int i = 0; i < dataCount; i++) {
            canvas.drawLine(centerX, centerY, getPoint(i).x, getPoint(i).y, linePaint);
        }
    }

    /**
     * 绘制填充区域
     */
    private void drawRegion(Canvas canvas) {
        Path regionPath = new Path();
        float percent;
        for (int i = 0; i < creditBeans.size(); i++) {
            percent = creditBeans.get(i).score * 1.0f / maxScore;
            int x = getPoint(i, percent).x;
            int y = getPoint(i, percent).y;
            if (i == 0) {
                regionPath.moveTo(x, y);
            } else {
                regionPath.lineTo(x, y);
            }
        }
        regionPath.close();
        canvas.drawPath(regionPath, regionPaint);
    }

    /**
     * 绘制总分数
     */
    private void drawTotalScore(Canvas canvas) {
        int totalScore = 0;
        for (CreditBean creditBean : creditBeans) {
            totalScore += creditBean.score;
        }
        //获得文字宽度
        float textWidth = totalScorePaint.measureText(String.valueOf(totalScore));
        Paint.FontMetrics fontMetrics = totalScorePaint.getFontMetrics();
        //获得文字高度
        float textHeight = Math.abs(fontMetrics.ascent) - fontMetrics.descent;
        canvas.drawText(String.valueOf(totalScore), centerX - textWidth / 2, centerY + textHeight / 2, totalScorePaint);
    }

    /**
     * 绘制标题和图标
     */
    private void drawTitleAndIcons(Canvas canvas) {
        for (int i = 0; i < creditBeans.size(); i++) {
            String title = creditBeans.get(i).title;
            int textX = getPoint(i, 1.2f).x;
            int textY = getPoint(i, 1.2f).y;

            //获得文字的宽高
            int textWidth = (int) titlePaint.measureText(title);
            Paint.FontMetrics fontMetrics = titlePaint.getFontMetrics();
            int textHeight = (int) (Math.abs(fontMetrics.ascent) - fontMetrics.descent);

            //获得图标的宽高
            Bitmap icon = BitmapFactory.decodeResource(getResources(), creditBeans.get(i).icon);
            int iconWidth = icon.getWidth();
            int iconHeight = icon.getHeight();
            if (i == 0) {
                //不需要修正位置
            } else if (i == 1) {
                textY += textHeight;
            } else if (i == 2) {
                textX -= textWidth;
                textY += textHeight;
            } else if (i == 3) {
                textX -= textWidth;
            } else {
                textX -= textWidth / 2;
            }
            int iconX = textX + textWidth / 2 - iconWidth / 2;
            int iconY = textY - textHeight - iconHeight - DensityUtils.dp2px(getContext(), 8);
            canvas.drawText(title, textX, textY, titlePaint);
            canvas.drawBitmap(icon, iconX, iconY, null);
        }
    }

    /**
     * 获得点坐标
     */
    public Point getPoint(int index) {
        return getPoint(index, 1);
    }

    /**
     * 获得点坐标,percent为百分比,用于获得填充区域和文字等的坐标位置.
     * 坐标的计算是重难点. 可以画图分析.
     */
    public Point getPoint(int index, float percent) {
        int x = 0;
        int y = 0;
        if (index == 0) {
            x = (int) (centerX + radius * Math.sin(radian) * percent);
            y = (int) (centerY - radius * Math.cos(radian) * percent);
        } else if (index == 1) {
            x = (int) (centerX + radius * Math.sin(radian * 1.0f / 2) * percent);
            y = (int) (centerY + radius * Math.cos(radian * 1.0f / 2) * percent);
        } else if (index == 2) {
            x = (int) (centerX - radius * Math.sin(radian * 1.0f / 2) * percent);
            y = (int) (centerY + radius * Math.cos(radian * 1.0f / 2) * percent);
        } else if (index == 3) {
            x = (int) (centerX - radius * Math.sin(radian) * percent);
            y = (int) (centerY - radius * Math.cos(radian) * percent);
        } else if (index == 4) {
            x = centerX;
            y = (int) (centerY - radius * percent);
        }
        return new Point(x, y);
    }

    /**
     * 设置数据
     */
    public void setData(List<CreditBean> creditBeans, int maxScore) {
        this.creditBeans = creditBeans;
        this.dataCount = creditBeans.size();
        this.maxScore = maxScore;
        this.radian = (float) (Math.PI * 2 * 1.0f / dataCount);
    }

}
