package com.bmw.dinosaursmall.view.ui;

import com.bmw.dinosaursmall.presenter.ControlPresenter;

/**
 * Created by admin on 2017/7/8.
 */

public class PtzAutoThread extends Thread {

    private float touchX;
    private float touchY;
    private int centerX;
    private int centerY;
    private ControlPresenter controlPresenter;
    private boolean isNotRunning;

    public void isNotRunning(boolean isNotRunning){
        this.isNotRunning = isNotRunning;
    }

    public PtzAutoThread(float touchX, float touchY, int centerX, int centerY, ControlPresenter controlPresenter) {
        this.touchX = touchX;
        this.touchY = touchY;
        this.centerX = centerX;
        this.centerY = centerY;
        this.controlPresenter = controlPresenter;
    }

    @Override
    public void run() {
        super.run();

        mySleep(200);
        if(isNotRunning){
            isNotRunning = false;
            return;
        }

        boolean isLeft = false;
        boolean isUp = false;
        if(touchX<centerX){
            isLeft = true;
        }
        if(touchY<centerY)
            isUp = true;
        int lengthX = (int) Math.abs(touchX-centerX);
        int lengthY = (int) Math.abs(touchY-centerY);
        float bateX = lengthX/(float)centerX;
        float bateY = lengthY/(float)centerY;
        int timeX = (int) (1500*bateX);
        int timeY = (int) (1600*bateY);
        if(isLeft){
            controlPresenter.set_ptz_left();
        }else {
            controlPresenter.set_ptz_right();
        }
        mySleep(timeX);
        controlPresenter.set_ptz_stop();
        mySleep(100);
        if(isUp)
            controlPresenter.set_ptz_up();
        else
            controlPresenter.set_ptz_down();
        mySleep(timeY);
        controlPresenter.set_ptz_stop();

    }

    public void mySleep(long time){
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
