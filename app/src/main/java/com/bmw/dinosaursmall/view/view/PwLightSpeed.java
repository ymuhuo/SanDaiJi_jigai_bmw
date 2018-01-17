package com.bmw.dinosaursmall.view.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.model.Model_info;
import com.bmw.dinosaursmall.view.ui.MainActivity;


/**
 * Created by yMuhuo on 2017/1/9.
 */
public class PwLightSpeed {

    private  VerticalPageSeekBar swing_speed;
    private  VerticalPageSeekBar car_speed;
    private VerticalPageSeekBar car_turnSpeed;
    private  LinearLayout foward_line;
    private  LinearLayout back_line;
    private  LinearLayout ptz_line;
    private  VerticalPageSeekBar ptz_light;
    private  VerticalPageSeekBar back_light;
    private  PopupWindow popupWindow;
    private  VerticalPageSeekBar foward_light;
    private boolean isSpeedModel;

    public PwLightSpeed(Context context, int layoutId) {

        View view = LayoutInflater.from(context).inflate(layoutId, null);
         popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.dialog_anim);

        if(layoutId == R.layout.ppw_light_control){

            isSpeedModel = false;
             foward_light = (VerticalPageSeekBar) view.findViewById(R.id.foward_light_seekBar);
             back_light = (VerticalPageSeekBar) view.findViewById(R.id.back_light_seekBar);
             ptz_light = (VerticalPageSeekBar) view.findViewById(R.id.ptz_light_seekBar);
             foward_line = (LinearLayout) view.findViewById(R.id.light_first);
             back_line = (LinearLayout) view.findViewById(R.id.light_second);
             ptz_line = (LinearLayout) view.findViewById(R.id.light_third);

//            foward_line.setVisibility(View.GONE);
//            back_line.setVisibility(View.GONE);
//            ptz_line.setVisibility(View.GONE);

        }else{
            isSpeedModel = true;
             car_speed = (VerticalPageSeekBar) view.findViewById(R.id.car_speed);
             swing_speed = (VerticalPageSeekBar) view.findViewById(R.id.swing_speed);
            car_turnSpeed = (VerticalPageSeekBar) view.findViewById(R.id.car_turnSpeed);
        }

        View rootView = LayoutInflater.from((MainActivity)context).inflate(R.layout.activity_main, null);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    public void setMax(int foward,int back,int ptz){
        if(isSpeedModel)
            return;
        foward_light.setMax(foward);
        back_light.setMax(back);
        ptz_light.setMax(ptz);
    }

    public void setMax(int cSpeed,int sSpeed){
        if(!isSpeedModel)
            return;
        car_speed.setMax(cSpeed);
        car_turnSpeed.setMax(cSpeed);
        swing_speed.setMax(sSpeed);
    }

    public void setCurrentProgress(int foward,int back,int ptz){
        if(isSpeedModel)
            return;
        foward_light.setProgress(foward);
        back_light.setProgress(back);
        ptz_light.setProgress(ptz);
    }

    public void setCurrentSpeedProgress(int cSpeed,int sSpeed,int tSpeed) {
        if (!isSpeedModel)
            return;
        car_speed.setProgress(cSpeed);
        car_turnSpeed.setProgress(tSpeed);
        swing_speed.setProgress(sSpeed);
    }

    public void setOnSeekBarStopTouchListener(VerticalPageSeekBar.OnSeekBarStopTouchListener listener){
        if(!isSpeedModel) {
            foward_light.setOnSeekBarStopTouchListener(listener);
            back_light.setOnSeekBarStopTouchListener(listener);
            ptz_light.setOnSeekBarStopTouchListener(listener);
        }else{
            car_speed.setOnSeekBarStopTouchListener(listener);
            car_turnSpeed.setOnSeekBarStopTouchListener(listener);
            swing_speed.setOnSeekBarStopTouchListener(listener);
        }
    }






}
