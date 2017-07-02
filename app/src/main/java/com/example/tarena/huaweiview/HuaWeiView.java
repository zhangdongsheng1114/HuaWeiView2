package com.example.tarena.huaweiview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tarena on 2017/7/1.
 */

public class HuaWeiView extends View {

    private final Paint paint;
    private int len;
    private RectF oval;
    private float sweepAngle = 300;
    private double targetAngle = 300;
     private int red;
     private int green;
     private int score;

     /**
     * 用来初始化画笔等
     *
     * @param context
     * @param attrs
     */
    public HuaWeiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        // 设置画笔颜色
        paint.setColor(Color.WHITE);
        // 设置画笔抗锯齿
        paint.setAntiAlias(true);
        // 让画出的图形是空心的（不填充）
        paint.setStyle(Paint.Style.STROKE);

    }

    /**
     * 用来测量限制view为正方形
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        // 以最小值为正方形的长
        len = Math.min(width, height);
        // 实例化矩形
        oval = new RectF(0, 0, len, len);
        // 设置测量高度和宽宽（必须要调用，不然无效果）
        setMeasuredDimension(len, len);


    }

    /**
     * 实现各种绘制功能
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画圆弧的方法
        // 参数1：oval是一个RectF对象为一个矩形
        // 参数2：startAngle为圆弧的起始角度
        // 参数3：sweepAngle为圆弧扫过的角度
        // 参数4：useCenter为圆弧是一个boolean值，为true时画的是圆弧，为false时画的是割弧
        // 参数5：paint为一个画笔对象
        canvas.drawArc(oval, 120, sweepAngle, false, paint);
        // 画刻度线的方法
        drawViewLine(canvas);
        drawScoreText(canvas);

    }

    private void drawViewLine(Canvas canvas) {
        // 先保存之前canvas的内容
        canvas.save();
        // 移动canvas（X轴移动距离，Y轴移动距离）
        // radius = len/2;
        canvas.translate(len / 2, len / 2);
        // 旋转坐标系(30度)
        canvas.rotate(30);
        Paint linePaint = new Paint();
        // 设置画笔颜色
        linePaint.setColor(Color.WHITE);
        // 线宽
        linePaint.setStrokeWidth(2);
        // 设置画笔抗锯齿
        linePaint.setAntiAlias(true);
        // 确定每次旋转的角度
        float rotateAngle = sweepAngle / 99;
        // 绘制有色部分的画笔
        Paint targetLinePaint = new Paint();
        targetLinePaint.setColor(Color.GREEN);
        targetLinePaint.setStrokeWidth(2);
        targetLinePaint.setAntiAlias(true);
        // 记录已经绘制过的有色部分范围
        float hasDraw = 0;
        for (int i = 0; i < 100; i++) {
            if (hasDraw <= targetAngle && targetAngle != 0) { // 需要绘制有色部分的时候
                // 计算以绘制的比例
                float percent = hasDraw / sweepAngle;
                int red = 255 - (int) (255 * percent);
                int green = (int) (255 * percent);
                // 实现接口回调，传递颜色值
                if (onAngleColorListener != null) {
                    onAngleColorListener.colorListener(red, green);
                }
                targetLinePaint.setARGB(255, red, green, 0);
                // 画一条刻度线
                canvas.drawLine(0, len / 2, 0, len / 2 - 20, targetLinePaint);
            } else {  // 不需要绘制有色部分的时候
                // 画一条刻度线
                canvas.drawLine(0, len / 2, 0, len / 2 - 20, linePaint);
            }
            // 累计绘制过的部分
            hasDraw += rotateAngle;
            // 旋转
            canvas.rotate(rotateAngle);
        }
        // 操作完成后恢复状态
        canvas.restore();


    }

    //判断是否在动
    private boolean isRunning;
    //判断是回退的状态还是前进状态
    private int state = 1;

    public void changeAngle(final float trueAngle) {
        if (isRunning) {//如果在动直接返回
            return;
        }
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (state) {
                    case 1://后退状态
                        isRunning = true;
                        targetAngle -= 3;
                        if (targetAngle <= 0) {//如果回退到0
                            targetAngle = 0;
                            //改为前进状态
                            state = 2;
                        }
                        break;
                    case 2://前进状态
                        targetAngle += 3;
                        if (targetAngle >= trueAngle) {//如果增加到指定角度
                            targetAngle = trueAngle;
                            //改为后退状态
                            state = 1;
                            isRunning = false;
                            //结束本次运动
                            timer.cancel();
                        }
                        break;
                    default:
                        break;
                }
                //重新绘制（子线程中使用的方法）
                postInvalidate();
            }
        }, 500, 30);
    }

    private OnAngleColorListener onAngleColorListener;

    public void setOnAngleColorListener(OnAngleColorListener onAngleColorListener) {
        this.onAngleColorListener = onAngleColorListener;
    }

    public interface OnAngleColorListener {
        void colorListener(int red, int green);
    }

    /**
     * 绘制小圆和文本的方法，小圆颜色同样可变
     *
     * @param canvas
     */
    private void drawScoreText(Canvas canvas) {
        // 先绘制一个小圆
        Paint smallPaint = new Paint();
        smallPaint.setARGB(100, red, green, 0);
        // 画小圆指定圆心坐标，半径，画笔即可
        // radius = 130;
        int smallRadius = 120 - 25;
        canvas.drawCircle(120, 120, 120 - 25, smallPaint);
        // 绘制副本
        Paint textPaint = new Paint();
        // 设置文本居中对齐
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(smallRadius / 2);
        // score需要计算得到
        canvas.drawText("" + score, 120, 120, textPaint);
        // 绘制分，在分数的右上方
        textPaint.setTextSize(smallRadius / 6);
        canvas.drawText("分", 120 + smallRadius / 2, 120 - smallRadius / 4, textPaint);
        // 绘制点击优化在分数的下方
        textPaint.setTextSize(smallRadius / 6);
        canvas.drawText("点击优化", 120, 120 + smallRadius / 2, textPaint);
        // 计算得到的分数
        score = (int) (targetAngle / sweepAngle * 100);
        // 重新绘制（子线程中使用的方法）
        postInvalidate();
    }
}
