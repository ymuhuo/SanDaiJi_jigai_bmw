package com.bmw.dinosaursmall.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.bmw.dinosaursmall.R;


public class Rudder extends SurfaceView implements Runnable, Callback {

    private SurfaceHolder mHolder;
    private boolean isStop = false;
    private Thread mThread;
    private Paint mPaint;
    private Point mRockerPosition; // 摇杆位置
    private Point mCtrlPoint = new Point(90, 90);// 摇杆起始位置
    private int mRudderRadius = 30;// 摇杆半径
    private int mWheelRadius = 80;// 摇杆活动范围半径
    private RudderListener listener = null; // 事件回调接口
    public static final int ACTION_RUDDER = 1, ACTION_STOP = 2; // 1：摇杆事件
    // 2：按钮事件（未实现）
    public static final int ACTION_UP = 3, ACTION_DOWN = 4, ACTION_LEFT = 5, ACTION_RIGHT = 6;
    private boolean isStart;
    private int current_len,current_angle;

    public Rudder(Context context) {
        super(context);
    }

    public Rudder(Context context, AttributeSet as) {
        super(context, as);

        this.setKeepScreenOn(true);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mThread = new Thread(this);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.bg_base));
        mPaint.setAntiAlias(true);// 抗锯齿
        mRockerPosition = new Point(mCtrlPoint);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSPARENT);// 设置背景透明
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mCtrlPoint = new Point(getWidth() / 2,getHeight() / 2);
        mRudderRadius = getHeight() / 6;
        mWheelRadius = getHeight() / 2 - 5;
        mRockerPosition = new Point(mCtrlPoint);

    }

    // 设置回调接口
    public void setRudderListener(RudderListener rockerListener) {
        listener = rockerListener;
    }

    @Override
    public void run() {
        Canvas canvas = null;

        while (!isStop) {
            try {
                isStart = true;
                canvas = mHolder.lockCanvas();
                canvas.drawColor(getResources().getColor(R.color.bg_spacerlayer), Mode.CLEAR);// 清除屏幕
                mPaint.setColor(getResources().getColor(R.color.bg_spacerlayer));
                canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, mWheelRadius,
                        mPaint);// 绘制范围
                mPaint.setColor(getResources().getColor(R.color.text_base));
                canvas.drawCircle(mRockerPosition.x, mRockerPosition.y,
                        mRudderRadius, mPaint);// 绘制摇杆
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!isStart)
            mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        isStop = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int len = MathUtils.getLength(mCtrlPoint.x, mCtrlPoint.y, event.getX(),
                event.getY());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 如果屏幕接触点不在摇杆挥动范围内,则不处理
            if (len > mWheelRadius- mRudderRadius) {
                return true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (len <= mWheelRadius - mRudderRadius) {
                // 如果手指在摇杆活动范围内，则摇杆处于手指触摸位置

                mRockerPosition.set((int) event.getX(), (int) event.getY());

            } else {
                // 设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘

               mRockerPosition = MathUtils.getBorderPoint(mCtrlPoint,
                        new Point((int) event.getX(), (int) event.getY()),
                        mWheelRadius - mRudderRadius);
            }
            if (len > mRudderRadius * 1.1) {
                if (listener != null) {
                    float radian = MathUtils.getRadian(mCtrlPoint, new Point(
                            (int) event.getX(), (int) event.getY()));
                    int angel = Rudder.this.getAngleCouvert(radian);
                    if(current_angle != angel || current_len != len) {
                        listener.onSteeringWheelChanged(ACTION_RUDDER, mWheelRadius - mRudderRadius, len, angel);
                        current_len = len;
                        current_angle = angel;
                    }

                }
            }
        }

        // 如果手指离开屏幕，则摇杆返回初始位置
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mRockerPosition = new Point(mCtrlPoint);
            if (listener != null) {
                listener.onSteeringWheelChanged(ACTION_STOP, mWheelRadius - mRudderRadius, len, 0);
            }
        }
        return true;
    }

    // 获取摇杆偏移角度 0-360°
    private int getAngleCouvert(float radian) {
        int tmp = (int) Math.round(radian / Math.PI * 180);
        if (tmp < 0) {
            return -tmp;
        } else {
            return 180 + (180 - tmp);
        }
    }

    // 回调接口
    public interface RudderListener {
        void onSteeringWheelChanged(int action, int R, int len, int angle);
    }

    public void release() {
        isStop = true;
    }

}