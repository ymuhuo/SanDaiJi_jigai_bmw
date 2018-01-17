package com.bmw.dinosaursmall.utils;

/**
 * Created by yMuhuo on 2017/1/5.
 */
public class SpeedUtil {

    public static double getLeftSpeed(int angle,double speed){

        if((angle>105 && angle<178) || (angle>182 && angle<255) || (angle<75 && angle>2) || (angle>275 && angle<358)){
            speed = speed/2;
        }

        if(angle>=0 && angle<=105)
            return -speed;
        if(angle>105 && angle<180){
            int subAngle = angle-90;
            double mSpeed = -speed + (speed*subAngle)/45;
            return mSpeed;
        }

        if(angle>285 && angle<360){
            int subAngle = angle-270;
            double mSpeed = speed - (speed*subAngle)/45;
            return mSpeed;
        }
        return speed;
    }

    public static double getRightSpeed(int angle,double speed){

        if((angle>105 && angle<178) || (angle>182 && angle<255) || (angle<75 && angle>2) || (angle>275 && angle<358)){
            speed = speed/2;
        }

        if(angle>=255 && angle<=360)
            return -speed;
        if(angle>=0 && angle<75){
            int subAngle = angle-0;
            double mSpeed = -speed + (speed*subAngle)/45;
            return mSpeed;
        }

        if(angle>180 && angle<255){
            int subAngle = angle-180;
            double mSpeed = speed - (speed*subAngle)/45;
            return mSpeed;
        }

        return speed;
    }


}
