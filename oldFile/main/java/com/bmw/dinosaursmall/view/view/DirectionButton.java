package com.bmw.dinosaursmall.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bmw.dinosaursmall.R;


/**
 * Created by admin on 2017/5/10.
 */

public class DirectionButton extends View {

    private Bitmap mUpBitmap;
    private Bitmap mBottomBitmap;
    private Bitmap mLeftBitmap;
    private Bitmap mRightBitmap;

    private Bitmap mUpBitmap_last;
    private Bitmap mBottomBitmap_last;
    private Bitmap mLeftBitmap_last;
    private Bitmap mRightBitmap_last;


    private String TAG = "directionButton";
    private Paint mPaintLeft = new Paint();
    private Paint mPaintTop = new Paint();
    private Paint mPaintRight = new Paint();
    private Paint mPaintBottom = new Paint();
    private Paint mPaintLine = new Paint();
    private int mSplitLineWidth = 2;
    private int mDefaultWidth = 300;
    private int mDefaultHeight = 300;
    private int mNormalColor = Color.BLUE;
    private int mPressColor = Color.YELLOW;
    private int mNormalCenterColor;
    private int mPressCenterColor;
    private int mSplitLineColor = Color.BLACK;

    private int width;
    private int height;

    private int lastClick;

    private int mDefaultLacuna = -2;
    private int topClickLacuna;
    private int leftClickLacuna;
    private int rightClickLacuna;
    private int bottomClickLacuna;

    private boolean isShowShadow;


    public DirectionButton(Context context) {
        super(context);
        initDraw();
    }

    public DirectionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.DirectionButton);
        mSplitLineWidth = typeArray.getDimensionPixelSize(R.styleable.DirectionButton_splitLineWidth, 2);
        mSplitLineColor = typeArray.getColor(R.styleable.DirectionButton_splitLineColor, Color.GREEN);
        mNormalColor = typeArray.getColor(R.styleable.DirectionButton_normalColor, Color.GRAY);
        mPressColor = typeArray.getColor(R.styleable.DirectionButton_pressColor, Color.BLUE);
        isShowShadow = typeArray.getBoolean(R.styleable.DirectionButton_isShowShadows, false);
        mNormalCenterColor = typeArray.getColor(R.styleable.DirectionButton_normalCenterColor, 0);
        mPressCenterColor = typeArray.getColor(R.styleable.DirectionButton_pressCenterColor, 0);

//        VectorDrawable upImg = (VectorDrawable) typeArray.getDrawable(R.styleable.DirectionButton_upImage);
        int mUpImgId = typeArray.getResourceId(R.styleable.DirectionButton_upImage, 0);
//        if(mUpImgId!=0){
//            mUpBitmap = Bitmap.createBitmap(upImg.getIntrinsicWidth(),upImg.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(mUpBitmap);
//            upImg.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
//            upImg.draw(canvas);
//        }
        mUpBitmap = BitmapFactory.decodeResource(getResources(), mUpImgId);

//        BitmapDrawable downImg = (BitmapDrawable) typeArray.getDrawable(R.styleable.DirectionButton_dowmImage);
        int mBottomImgId = typeArray.getResourceId(R.styleable.DirectionButton_dowmImage, 0);
        mBottomBitmap = BitmapFactory.decodeResource(getResources(), mBottomImgId);

//        BitmapDrawable leftImg = (BitmapDrawable) typeArray.getDrawable(R.styleable.DirectionButton_leftImage);
        int mLeftImgId = typeArray.getResourceId(R.styleable.DirectionButton_leftImage, 0);
        mLeftBitmap = BitmapFactory.decodeResource(getResources(), mLeftImgId);

//        BitmapDrawable rightImg = (BitmapDrawable) typeArray.getDrawable(R.styleable.DirectionButton_rightImage);
        int mRightImgId = typeArray.getResourceId(R.styleable.DirectionButton_rightImage, 0);
        mRightBitmap = BitmapFactory.decodeResource(getResources(), mRightImgId);


        typeArray.recycle();
        initDraw();
    }

    public DirectionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDraw();
    }

    private void initDraw() {
        mPaintLeft.setColor(mNormalColor);
        mPaintLeft.setStrokeWidth((float) 1.5);
        mPaintLeft.setAntiAlias(true);
        mPaintLeft.setFilterBitmap(true);

        mPaintTop.setColor(mNormalColor);
        mPaintTop.setStrokeWidth((float) 1.5);
        mPaintTop.setAntiAlias(true);
        mPaintTop.setFilterBitmap(true);

        mPaintRight.setColor(mNormalColor);
        mPaintRight.setStrokeWidth((float) 1.5);
        mPaintRight.setAntiAlias(true);
        mPaintRight.setFilterBitmap(true);

        mPaintBottom.setColor(mNormalColor);
        mPaintBottom.setStrokeWidth((float) 1.5);
        mPaintBottom.setAntiAlias(true);
        mPaintBottom.setFilterBitmap(true);

        mPaintLine.setColor(mSplitLineColor);
        mPaintLine.setStrokeWidth((float) 1.5);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setFilterBitmap(true);

        if (isShowShadow) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintBottom);
            mPaintBottom.setShadowLayer(2, 0, 1, Color.GRAY);
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintRight);
            mPaintRight.setShadowLayer(2, 1, 0, Color.GRAY);
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintLeft);
            mPaintLeft.setShadowLayer(2, -1, 0, Color.GRAY);
            setLayerType(LAYER_TYPE_SOFTWARE, mPaintTop);
            mPaintTop.setShadowLayer(2, 0, -1, Color.GRAY);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean isFirst = false;
        if (width == 0) {
            isFirst = true;
        }

        int padLeft = getPaddingLeft();
        int padTop = getPaddingTop();
        int padBottom = getPaddingBottom();
        int padRight = getPaddingRight();

        height = getHeight();
        width = getWidth();
        width = width - padLeft - padRight;
        height = height - padTop - padBottom;

        if (isFirst) {
            for (int i = 1; i < 5; i++) {
                resetPaint(i, mNormalColor, mNormalCenterColor, 0);
            }
            isFirst = false;
        }

        RectF rectFbottom = new RectF(padLeft + mSplitLineWidth + bottomClickLacuna,
                padTop + mSplitLineWidth * 2 + bottomClickLacuna,
                width + padLeft - mSplitLineWidth - bottomClickLacuna,
                height + padTop + bottomClickLacuna);

        RectF rectFleft = new RectF(padLeft - leftClickLacuna,
                mSplitLineWidth + padTop + leftClickLacuna,
                width + padLeft - mSplitLineWidth * 2 - leftClickLacuna,
                height + padTop - mSplitLineWidth - leftClickLacuna);

        RectF rectFTop = new RectF(padLeft + mSplitLineWidth + topClickLacuna,
                padTop - topClickLacuna,
                width + padLeft - mSplitLineWidth - topClickLacuna,
                height + padTop - mSplitLineWidth * 2 - topClickLacuna);

        RectF rectFright = new RectF(padLeft + mSplitLineWidth * 2 + rightClickLacuna,
                padTop + mSplitLineWidth + rightClickLacuna,
                width + padLeft + rightClickLacuna,
                height + padTop - mSplitLineWidth - rightClickLacuna);


        if (width == height)
            canvas.drawCircle(width / 2 + padLeft, height / 2 + padTop, height / 2 - 1, mPaintLine);
        else {
            RectF rectFoval = new RectF(0 + padLeft + 1, 0 + padTop + 1, width + padLeft - 1, height + padTop - 1);
//            rectFoval.set();
            canvas.drawOval(rectFoval, mPaintLine);
        }


//        RadialGradient radialGradient = new RadialGradient(width / 2 + padLeft, height / 2 + padTop, width / 2 - mSplitLineWidth, 0x50ff9900, mNormalColor, RadialGradient.TileMode.CLAMP);
//        mPaintTop.setShader(radialGradient);

        canvas.drawArc(rectFbottom, 45, 90, true, mPaintBottom);
        canvas.drawArc(rectFleft, 135, 90, true, mPaintLeft);
        canvas.drawArc(rectFTop, 225, 90, true, mPaintTop);
        canvas.drawArc(rectFright, 315, 90, true, mPaintRight);

        if (mUpBitmap != null) {

            int topWidth = width - 2 * mSplitLineWidth - 2 * topClickLacuna;
            int topHeight = height - mSplitLineWidth * 2;
            Rect rectTop = new Rect(mSplitLineWidth + padLeft + topClickLacuna + topWidth / 3 + topWidth / 10,
                    padTop - topClickLacuna + topHeight / 12,
                    width - mSplitLineWidth + padLeft - topClickLacuna - topWidth / 3 - topWidth / 10,
                    height - mSplitLineWidth * 2 + padTop - topClickLacuna - topHeight / 2 - topHeight / 6);

//            Bitmap bitmap = VLCApplication.getBitmap(String.valueOf(mUpImgId));
            canvas.drawBitmap(mUpBitmap, null, rectTop, mPaintTop);

        }
        if (mBottomBitmap != null) {
            int bottomWidth = width - 2 * mSplitLineWidth - 2 * bottomClickLacuna;
            int bottomHeight = height - mSplitLineWidth * 2;
            Rect rectBottom = new Rect(padLeft + mSplitLineWidth + bottomClickLacuna + bottomWidth / 3 + bottomWidth / 10,
                    padTop + mSplitLineWidth * 2 + bottomClickLacuna + bottomHeight / 2 + bottomHeight / 6,
                    width + padLeft - mSplitLineWidth - bottomClickLacuna - bottomWidth / 3 - bottomWidth / 10,
                    height + padTop + bottomClickLacuna - bottomHeight / 12);

//            Bitmap bitmap = VLCApplication.getBitmap(String.valueOf(mBottomImgId));
            canvas.drawBitmap(mBottomBitmap, null, rectBottom, mPaintBottom);
        }
        if (mLeftBitmap != null) {
            int leftWidth = width - mSplitLineWidth * 2;
            int leftHeght = height - 2 * mSplitLineWidth - 2 * leftClickLacuna;
            Rect rectLeft = new Rect(padLeft - leftClickLacuna + leftWidth / 12,
                    mSplitLineWidth + padTop + leftClickLacuna + leftHeght / 3 + leftHeght / 10,
                    width + padLeft - mSplitLineWidth * 2 - leftClickLacuna - leftWidth / 2 - leftWidth / 6,
                    height + padTop - mSplitLineWidth - leftClickLacuna - leftHeght / 3 - leftHeght / 10);

//            Bitmap bitmap = VLCApplication.getBitmap(String.valueOf(mLeftImgId));
            canvas.drawBitmap(mLeftBitmap, null, rectLeft, mPaintBottom);
        }
        if (mRightBitmap != null) {
            int rightWidth = width - mSplitLineWidth * 2;
            int rightHeight = height - 2 * mSplitLineWidth - 2 * rightClickLacuna;
            Rect rectRight = new Rect(padLeft + mSplitLineWidth * 2 + rightClickLacuna + rightWidth / 2 + rightWidth / 6,
                    padTop + mSplitLineWidth + rightClickLacuna + rightHeight / 3 + rightHeight / 10,
                    width + padLeft + rightClickLacuna - rightWidth / 12,
                    height + padTop - mSplitLineWidth - rightClickLacuna - rightHeight / 3 - rightHeight / 10);

//            Bitmap bitmap = VLCApplication.getBitmap(String.valueOf(mRightImgId));
            canvas.drawBitmap(mRightBitmap, null, rectRight, mPaintBottom);
        }

        recylerLastBitmap();
    }

    private void recylerLastBitmap() {
        if (mUpBitmap_last!=mUpBitmap) {
            recylerBitmap(mUpBitmap_last);
            mUpBitmap_last = mUpBitmap;
        }
        if (mBottomBitmap_last!=mBottomBitmap) {
            recylerBitmap(mBottomBitmap_last);
            mBottomBitmap_last = mBottomBitmap;
        }
        if (mLeftBitmap_last!=mLeftBitmap) {
            recylerBitmap(mLeftBitmap_last);
            mLeftBitmap_last = mLeftBitmap;
        }
        if (mRightBitmap_last!=mRightBitmap) {
            recylerBitmap(mRightBitmap_last);
            mRightBitmap_last = mRightBitmap;
        }
    }

    private void recylerChangeBitmap(Bitmap bitmapDes, Bitmap bitmapSrc) {
        Log.i(TAG, "recylerChangeBitmap: "+(bitmapDes != bitmapSrc));
        if (bitmapDes != bitmapSrc) {
            recylerBitmap(bitmapDes);
        }
        bitmapDes = bitmapSrc;
    }


    private void recylerBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            Log.i(TAG, "recylerBitmap: "+bitmap.toString());
            bitmap.recycle();
            bitmap = null;
        }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
//            Log.d(TAG, "onTouchEvent: up");
            stopTouchEvent();
        } else if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
//            Log.d(TAG, "onTouchEvent: down");
            touchEvent(event);
        }

        return true;
    }

    private void touchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        getDirection(x, y);


    }

    private void getDirection(float x, float y) {

        if (width != height && !isTouchOvalIn(x, y))
            return;
        if (width == height) {
            float distanceX = Math.abs(getWidth() / 2 - x);
            float distanceY = Math.abs(getHeight() / 2 - y);
            float distanceXY = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
            int radius = width >= height ? width / 2 : height / 2;
//        Log.d(TAG, "getDirection: distanceXY = "+distanceXY+"\nradius = "+radius);
            if (distanceXY > radius)
                return;
        }
        int radiusX = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
        int radiusY = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();

        double angleSource = Math.atan2(y - radiusY, x - radiusX);
        int angle = (int) (angleSource * 180 / Math.PI);
        if (angle <= 0)
            angle = angle + 360;
//        Log.d(TAG, "getDirection: angleSource = "+angleSource+"\nangle = "+angle);
        if (angle >= 45 && angle < 135) {
            if (lastClick != 1) {
                if (listener != null)
                    listener.bottomClick();
                resetPaint(lastClick, mNormalColor, mNormalCenterColor, 0);
                resetPaint(1, mPressColor, mPressCenterColor, mDefaultLacuna);
                lastClick = 1;
            }
        } else if (angle >= 135 && angle < 225) {
            if (lastClick != 2) {
                if (listener != null)
                    listener.leftClick();
                resetPaint(lastClick, mNormalColor, mNormalCenterColor, 0);
                resetPaint(2, mPressColor, mPressCenterColor, mDefaultLacuna);
                lastClick = 2;
            }

        } else if (angle >= 225 && angle < 315) {
            if (lastClick != 3) {
                if (listener != null)
                    listener.topClick();
                resetPaint(lastClick, mNormalColor, mNormalCenterColor, 0);
                resetPaint(3, mPressColor, mPressCenterColor, mDefaultLacuna);
                lastClick = 3;
            }

        } else {
            if (lastClick != 4) {
                if (listener != null)
                    listener.rightClick();
                resetPaint(lastClick, mNormalColor, mNormalCenterColor, 0);
                resetPaint(4, mPressColor, mPressCenterColor, mDefaultLacuna);
                lastClick = 4;
            }

        }
        invalidate();

    }

    private boolean isTouchOvalIn(float x, float y) {
        int centerX = width / 2 + getPaddingLeft();
        int centerY = height / 2 + getPaddingTop();
        boolean isFocusInX = width > height ? true : false;
        int a = 0;
        int b = 0;
        if (isFocusInX) {
            a = width / 2;
            b = height / 2;
        } else {
            a = height / 2;
            b = width / 2;
        }
        double c = Math.sqrt(Math.pow(a, 2) - Math.pow(b, 2));
//        ThrowUtil.log(" c = "+ c);
        double f1 = 0;
        double f2 = 0;

        if (isFocusInX) {
            double xLeft = centerX - c;
            double xRight = centerX + c;
            f1 = getLengthBy2Dot(x, y, xLeft, centerY);
            f2 = getLengthBy2Dot(x, y, xRight, centerY);
        } else {
            double yUp = centerY - c;
            double yDown = centerY + c;
            f1 = getLengthBy2Dot(x, y, centerX, yUp);
            f2 = getLengthBy2Dot(x, y, centerX, yDown);
        }
        if ((f1 + f2) <= (2 * a))
            return true;
        return false;
    }

    private double getLengthBy2Dot(float srcX, float srcY, double desX, double desY) {
        return Math.sqrt(Math.pow(Math.abs(srcX - desX), 2) + Math.pow(Math.abs(srcY - desY), 2));
    }

    private void resetPaint(int who, int color, int centerColor, int lacuna) {
        switch (who) {
            case 1:
                mPaintBottom.setColor(color);
                bottomClickLacuna = lacuna;
                if (centerColor != 0) {
                    RadialGradient radialGradient = new RadialGradient(width / 2 + getPaddingLeft()
                            , height / 2 + getPaddingTop()
                            , width / 2 - mSplitLineWidth
                            , centerColor, color
                            , RadialGradient.TileMode.CLAMP
                    );
                    mPaintBottom.setShader(radialGradient);
                }
                break;
            case 2:
                mPaintLeft.setColor(color);
                leftClickLacuna = lacuna;
                if (centerColor != 0) {
                    RadialGradient radialGradient2 = new RadialGradient(width / 2 + getPaddingLeft()
                            , height / 2 + getPaddingTop()
                            , width / 2 - mSplitLineWidth
                            , centerColor, color
                            , RadialGradient.TileMode.CLAMP
                    );
                    mPaintLeft.setShader(radialGradient2);
                }
                break;
            case 3:
                mPaintTop.setColor(color);
                topClickLacuna = lacuna;
                if (centerColor != 0) {
                    RadialGradient radialGradient3 = new RadialGradient(width / 2 + getPaddingLeft()
                            , height / 2 + getPaddingTop()
                            , width / 2 - mSplitLineWidth
                            , centerColor, color
                            , RadialGradient.TileMode.CLAMP
                    );
                    mPaintTop.setShader(radialGradient3);
                }
                break;
            case 4:
                mPaintRight.setColor(color);
                rightClickLacuna = lacuna;
                if (centerColor != 0) {
                    RadialGradient radialGradient4 = new RadialGradient(width / 2 + getPaddingLeft()
                            , height / 2 + getPaddingTop()
                            , width / 2 - mSplitLineWidth
                            , centerColor, color
                            , RadialGradient.TileMode.CLAMP
                    );
                    mPaintRight.setShader(radialGradient4);
                }
                break;
        }

    }

    private void stopTouchEvent() {
        if (listener != null)
            listener.stopTouch();

        resetPaint(lastClick, mNormalColor, mNormalCenterColor, 0);
        invalidate();
        lastClick = 0;
    }

    public void setmAllImgId(int mLeftImgId, int mUpImgId, int mRightImgId, int mBottomImgId) {
        if (mUpImgId != 0) {
            mUpBitmap = BitmapFactory.decodeResource(getResources(),mUpImgId);
//            this.mUpImgId = mUpImgId;
//            putBitmap(mUpImgId);

        }
        if (mBottomImgId != 0) {
            mBottomBitmap = BitmapFactory.decodeResource(getResources(),mBottomImgId);
//            this.mBottomImgId = mBottomImgId;
//            putBitmap(mBottomImgId);
        }
        if (mLeftImgId != 0) {
            mLeftBitmap = BitmapFactory.decodeResource(getResources(),mLeftImgId);
//            this.mLeftImgId = mLeftImgId;
//            putBitmap(mLeftImgId);
        }
        if (mRightImgId != 0) {
            mRightBitmap = BitmapFactory.decodeResource(getResources(),mRightImgId);
//            this.mRightImgId = mRightImgId;
//            putBitmap(mRightImgId);
        }
        invalidate();
    }



    public interface OnItemClickListener {
        void topClick();

        void bottomClick();

        void leftClick();

        void rightClick();

        void stopTouch();
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
