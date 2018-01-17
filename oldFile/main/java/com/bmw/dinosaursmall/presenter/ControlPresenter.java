package com.bmw.dinosaursmall.presenter;

/**
 * Created by admin on 2016/9/30.
 */
public interface ControlPresenter {



    void isSendGetCommand(boolean isGet);

    void moveUpBate(double bate);
    void moveBottomBate(double bate);
    void moveLeftBate(double bate);
    void moveRightBate(double bate);

    void moveUp();
    void moveBottom();
    void moveLeft();
    void moveRight();
    void moveTurnSpeed(int moveTurnSpeed);

    void moveWithSpeed(double bi, int angel);  //摇杆控制
    void turnFoward(int speed);  //摆臀向前
    void turnBack(int speed);    //摆臀向后
    void stopSwing();   //停止摆臂
    void stopMove();    //停止移动
    void setCarSpeed(int speed);   //车速设置
    void setSwingSpeed(int speed);  //摆速设置
    void getMotor_state();  //获取电机状态
    void set_ptz_up();  //云台运动-上仰
    void set_ptz_down();    //云台运动-下俯
    void set_ptz_left();    //云台运动-左转
    void set_ptz_right();   //云台运动-右转
    void set_ptz_openLight(int Strength);   //云台运动-开灯
    void set_ptz_closeLight();  //云台运动-关灯
    void set_ptz_Vcamera_boom_min();    //云台运动-可见光摄像头变倍小
    void set_ptz_Vcamera_boom_max();    //云台运动-可见光摄像头变倍大
    void set_ptz_Vcamera_focus_low();   //云台运动-可见光摄像头聚焦近
    void set_ptz_Vcamera_focus_high();  //云台运动-可见光摄像头聚焦远
    void set_ptz_Rcamera_boom_min();    //云台运动-红外摄像头变倍小
    void set_ptz_Rcamera_boom_max();    //云台运动-红外摄像头变倍大
    void set_ptz_stop();    //云台运动-停止
    void setVideo_model(int model); //视频切换命令
    void open_foward_light(int strength);   //开启前灯
    void close_foward_light();  //关闭前灯
    void open_back_light(int strength); //开启后灯
    void close_back_light();    //关闭后灯
    void setPower(int which,int state); //电源控制
    void getPowerStat();    //获取电源状态
    void getBattery();  //电量读取
    void getTurn_angle();   //获取摆臂相对机体角度
    void getDevice_state(); //获取姿态数据
    void getGasInfo();  //获取气体信息
    void getHumidityInfo();  //获取温湿度信息
    void getGpsInfo();  //获取gps信息

    void getProduct_id();   //获取产品编号
    void getSoftware_stat();    //获取机器人软件版本
    void getCarSpeed_max(); //获取行走最高速度
    void getSwingSpeed_max();   //获取摆臂最高速度
    void setCarSpeed_max(int car_speed_max); //设置行走最高速度、
    void setSwingSpeed_max(int swing_speed_max);   //设置摆臂最高速度

    void release();


}
