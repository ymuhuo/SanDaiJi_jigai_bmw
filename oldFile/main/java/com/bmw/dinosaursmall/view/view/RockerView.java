package com.bmw.dinosaursmall.view.view;

/**
 * Created by admin on 2016/10/20.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.bmw.dinosaursmall.R;

public class RockerView extends View {

    //固定摇杆背景圆形的X,Y坐标以及半径
    private float mRockerBg_X;
    private float mRockerBg_Y;
    private float mRockerBg_R;
    //摇杆的X,Y坐标以及摇杆的半径
    private float mRockerBtn_X;
    private float mRockerBtn_Y;
    private float mRockerBtn_R;
    private Bitmap mBmpRockerBg;
    private Bitmap mBmpRockerBtn;

    private PointF mCenterPoint;
    private double current_len;
    private int current_angle;
    private long current_time;
    private boolean isStopThread;
    private float current_x,current_y;
    public static final int ACTION_RUDDER = 1, ACTION_STOP = 2; // 1：摇杆事件

    public RockerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        // 获取bitmap
        mBmpRockerBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.rocker_bg);
        mBmpRockerBtn = BitmapFactory.decodeResource(context.getResources(), R.mipmap.rocker_btn);

        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

            // 调用该方法时可以获取view实际的宽getWidth()和高getHeight()
            @Override
            public boolean onPreDraw() {
                // TODO Auto-generated method stub
                getViewTreeObserver().removeOnPreDrawListener(this);

                Log.e("RockerView", getWidth() + "/" + getHeight());
                int width = getWidth()>getHeight()?getHeight():getWidth();
                if(getHeight()!=0)
                mCenterPoint = new PointF(getWidth() / 2, getHeight() / 2);
                else
                mCenterPoint = new PointF(getWidth()/2,120);
                mRockerBg_X = mCenterPoint.x;
                mRockerBg_Y = mCenterPoint.y;

                mRockerBtn_X = mCenterPoint.x;
                mRockerBtn_Y = mCenterPoint.y;

                float tmp_f = mBmpRockerBg.getWidth() / (float) (mBmpRockerBg.getWidth() + mBmpRockerBtn.getWidth());
                mRockerBg_R = tmp_f * width / 2;
                mRockerBtn_R = (1.0f - tmp_f) * width / 2;

                return true;
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                isStopThread = true;
                // TODO Auto-generated method stub
                while (isStopThread) {

                    //系统调用onDraw方法刷新画面
                    RockerView.this.postInvalidate();

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawBitmap(mBmpRockerBg, null,
                new Rect((int) (mRockerBg_X - mRockerBg_R),
                        (int) (mRockerBg_Y - mRockerBg_R),
                        (int) (mRockerBg_X + mRockerBg_R),
                        (int) (mRockerBg_Y + mRockerBg_R)),
                null);
        canvas.drawBitmap(mBmpRockerBtn, null,
                new Rect((int) (mRockerBtn_X - mRockerBtn_R),
                        (int) (mRockerBtn_Y - mRockerBtn_R),
                        (int) (mRockerBtn_X + mRockerBtn_R),
                        (int) (mRockerBtn_Y + mRockerBtn_R)),
                null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ) {

            double len = Math.sqrt(Math.pow((mRockerBg_X - (int) event.getX()), 2) + Math.pow((mRockerBg_Y - (int) event.getY()), 2));
            // 当触屏区域不在活动范围内
            if ( len >= mRockerBg_R) {
                //得到摇杆与触屏点所形成的角度
                double tempRad = getRad(mRockerBg_X, mRockerBg_Y, event.getX(), event.getY());
                //保证内部小圆运动的长度限制
                getXY(mRockerBg_X, mRockerBg_Y, mRockerBg_R, tempRad);
            } else {//如果小球中心点小于活动区域则随着用户触屏点移动即可

                mRockerBtn_X = (int) event.getX();
                mRockerBtn_Y = (int) event.getY();

            }

            if(System.currentTimeMillis()-current_time>=50 ) {
                if (mRockerChangeListener != null  ) {
                    double rad = getRad(mCenterPoint.x, mCenterPoint.y, event.getX(), event.getY());
                    int angle = getAngleCouvert(rad);
                    if((current_angle != angle || current_len != len) && len>=mRockerBtn_R*0.5 ) {
                        mRockerChangeListener.report(ACTION_RUDDER, mRockerBg_R, len, angle);
                        current_len = len;
                        current_angle = angle;
                    }
                }
                current_time = System.currentTimeMillis();
            }
            current_x = event.getX();
            current_y = event.getY();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //当释放按键时摇杆要恢复摇杆的位置为初始位置
            mRockerBtn_X = mCenterPoint.x;
            mRockerBtn_Y = mCenterPoint.y;
            if (mRockerChangeListener != null) {
                mRockerChangeListener.report(ACTION_STOP, 0,0,0);
            }
        }
        return true;
    }


    // 获取摇杆偏移角度 0-360°
    private int getAngleCouvert(double radian) {
        int tmp = (int) Math.round(radian / Math.PI * 180);
        if (tmp < 0) {
            return -tmp;
        } else {
            return 180 + (180 - tmp);
        }
    }

    /***
     * 得到两点之间的弧度
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float x = px2 - px1;
        //得到两点Y的距离
        float y = py1 - py2;
        //算出斜边长
        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = x / xie;
        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }

    /**
     * @param R       圆周运动的旋转点
     * @param centerX 旋转点X
     * @param centerY 旋转点Y
     * @param rad     旋转的弧度
     */
    public void getXY(float centerX, float centerY, float R, double rad) {
        //获取圆周运动的X坐标
        mRockerBtn_X = (float) (R * Math.cos(rad)) + centerX;
        //获取圆周运动的Y坐标
        mRockerBtn_Y = (float) (R * Math.sin(rad)) + centerY;
    }

    RockerChangeListener mRockerChangeListener = null;

    public void setRockerChangeListener(RockerChangeListener rockerChangeListener) {
        mRockerChangeListener = rockerChangeListener;
    }

    public interface RockerChangeListener {
        public void report(int action, double R, double len, int angle);
    }

    public void release(){
        isStopThread = false;
    }
}