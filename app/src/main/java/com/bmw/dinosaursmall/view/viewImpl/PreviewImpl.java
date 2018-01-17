package com.bmw.dinosaursmall.view.viewImpl;

import android.view.animation.RotateAnimation;

import com.bmw.dinosaursmall.model.EnvironmentInfo;

/**
 * Created by admin on 2016/9/28.
 */
public interface PreviewImpl {
    void ilog(String msg);
    void ierror(String msg);
    void record(int i,boolean isRecord);
    void capture(String path);
    void iToast(String msg);
    void showBattery(int i);
    void isConnect(boolean isConnect);
    void setAngleAnimation(RotateAnimation ra_swing, RotateAnimation ra_picth, RotateAnimation ra_roll, RotateAnimation ra_head);
    void stop();

    void setEnvironmentData(EnvironmentInfo environmentData);

}
