package com.bmw.dinosaursmall.view.view;

/**
 * Created by ymh on 2017/7/1.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bmw.dinosaursmall.R;


/**
 * Created by admin on 2017/5/10.
 */

public class RollButton extends View {


    private int mDefaultWidth = 300;
    private int mDefaultHeight = 100;

    private Paint mPaintStroke = new Paint();
    private Paint mPaintBack = new Paint();
    private Paint mPaintBall = new Paint();
    private int mBorderColor = Color.YELLOW;
    private int mBackColor;
    private int mBorderWidth = 5;
    private int mBallCenterColor = -1;
    private int mBallMarginColor = -1;

    private int mCoordsX = -1;
    private int width;
    private int height;
    private int mMaxProgress = 100;
    private int mProgress;
    private int mDirection = -1;//左边为0，右边为1
    private long lastMoveTime;
    private boolean isShadowShow;
    public RollButton(Context context) {
        super(context);
        initDraw();
    }

    public RollButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.RollButton);
        mBorderWidth = typeArray.getDimensionPixelSize(R.styleable.RollButton_borderWidth,5);
        mBorderColor = typeArray.getColor(R.styleable.RollButton_borderColor,Color.YELLOW);
        mBackColor = typeArray.getColor(R.styleable.RollButton_backColor,0);
        mBallCenterColor = typeArray.getColor(R.styleable.RollButton_ballCenterColor,-1);
        mBallMarginColor = typeArray.getColor(R.styleable.RollButton_ballMarginColor,-1);
        if(mBallMarginColor == -1)
            mBallMarginColor = mBorderColor;
        isShadowShow = typeArray.getBoolean(R.styleable.RollButton_isShadowShow,false);
        typeArray.recycle();
        initDraw();
    }

    public RollButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDraw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //set for macth_parent and wrap_content
        //1.get current specMode
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        //2.get current specSize
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //3.set specSize by specMode:AT_MOST mean wrap_content
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            //unit:px
            setMeasuredDimension(mDefaultWidth, mDefaultHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mDefaultHeight);
        }


    }


    private void initDraw() {

        mPaintStroke.setColor(mBorderColor);
        mPaintStroke.setStrokeWidth((float) mBorderWidth);
        mPaintStroke.setAntiAlias(true);
        mPaintStroke.setStyle(Paint.Style.STROKE);

        mPaintBack.setStrokeWidth((float) mBorderWidth);
        mPaintBack.setAntiAlias(true);
        mPaintBack.setStyle(Paint.Style.FILL);
        mPaintBack.setColor(mBackColor);

        mPaintBall.setStrokeWidth((float) mBorderWidth);
        mPaintBall.setAntiAlias(true);
        mPaintBall.setStyle(Paint.Style.FILL);
        mPaintBall.setColor(mBallMarginColor);

        if(isShadowShow) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintStroke);
            mPaintStroke.setShadowLayer(1, 1, 1, Color.GRAY);
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintBall);
            mPaintBall.setShadowLayer(2, 2, 3, Color.GRAY);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        height = getHeight();
        width = getWidth();

        int padLeft = getPaddingLeft();
        int padRight = getPaddingRight();
        int padTop = getPaddingTop();
        int padBottom = getPaddingBottom();
        width = width - padLeft - padRight;
        height = height - padTop - padBottom;

        int radiusOut = height / 2;


        RectF rectFout = new RectF(0 + padLeft
                , 0 + padTop
                , width + padLeft
                , height + padTop);
        canvas.drawRoundRect(rectFout,radiusOut,radiusOut, mPaintStroke);

        if(mBackColor != 0) {
            RectF rectFin = new RectF(0 + padLeft+mBorderWidth/2
                    , 0 + mBorderWidth/2 + padTop
                    , width - 0 + padLeft-mBorderWidth/2
                    , height - mBorderWidth/2 + padTop);

            canvas.drawRoundRect(rectFin, radiusOut, radiusOut, mPaintBack);
        }

        if (mCoordsX == -1 ) {
            mCoordsX = padLeft + width / 2;
        }

        int mCoordsY = padTop + radiusOut;


        if(mBallCenterColor!= -1) {
            RadialGradient radialGradient = new RadialGradient(mCoordsX
                    , mCoordsY
                    , radiusOut - mBorderWidth / 2
                    , mBallCenterColor
                    , mBallMarginColor
                    , RadialGradient.TileMode.CLAMP
            );
            mPaintBall.setShader(radialGradient);
        }

        canvas.drawCircle(mCoordsX, mCoordsY, radiusOut - mBorderWidth/2, mPaintBall);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchEvent(event);
                if (listener != null) {
                    listener.onTouchChange(mDirection, mProgress);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                touchEvent(event);
                if(System.currentTimeMillis() - lastMoveTime>50)
                    if (listener != null) {
                        lastMoveTime = System.currentTimeMillis();
                        listener.onTouchChange(mDirection, mProgress);
                    }
                break;
            case MotionEvent.ACTION_UP:
                stopTouch();
                if (listener != null) {
                    listener.stopTouch();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                stopTouch();
                if (listener != null) {
                    listener.onTouchChange(mDirection, mProgress);
                }
                break;
            default:

                break;
        }
        return true;
    }

    private void touchEvent(MotionEvent event) {
        float x = event.getX();

        int leftBorder = getPaddingLeft();
        int rightBorder = width + getPaddingLeft();
        int leftRadius = leftBorder + height / 2;
        int rightRadius = rightBorder - height / 2;
        if(x<leftBorder || x>rightBorder)
            return;

        if (x >= leftRadius && x <= rightRadius) {
            mCoordsX = (int) x;

            int subLength = width/2+leftBorder;
            int dividend = subLength-leftRadius;
            int currentLength = (int) Math.abs(x-subLength);
            float bi = currentLength/(float)dividend;
            mProgress =(int)(mMaxProgress*bi);
            if(x<subLength){
                mDirection = 0;
            }else {
                mDirection = 1;
            }

        } else if (x >= leftBorder && x <= rightBorder) {
            mProgress = mMaxProgress;
            if (x <=leftRadius) {
                mCoordsX = leftRadius;
                mDirection = 0;
            }else{
                mCoordsX = rightRadius;
                mDirection = 1;
            }
        }
        invalidate();
    }

    public void setMaxProgress(int maxProgress){
        this.mMaxProgress = maxProgress;
    }

    private void stopTouch() {
        mCoordsX = -1;
        mProgress = 0;
        mDirection = -1;
        invalidate();
    }


    public interface OnRollBtnChangeListener{
        void onTouchChange(int direction, int progress);
        void stopTouch();
    }

    private OnRollBtnChangeListener listener;

    public void setOnRollBtnChangeListener(OnRollBtnChangeListener listener){
        this.listener = listener;
    }
}