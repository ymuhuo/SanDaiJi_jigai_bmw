package com.bmw.dinosaursmall.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.model.Environment;
import com.bmw.dinosaursmall.model.LoginInfo;
import com.bmw.dinosaursmall.presenter.ControlPresenter;
import com.bmw.dinosaursmall.presenter.SocketReaderListener;
import com.bmw.dinosaursmall.utils.SocketUtil;
import com.bmw.dinosaursmall.utils.SpeedUtil;
import com.bmw.dinosaursmall.utils.UdpSocketUtil;
import com.bmw.dinosaursmall.view.viewImpl.PreviewImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

/**
 * Created by admin on 2016/9/30.
 */
public class ControlPresentImpl implements ControlPresenter {
    private UdpSocketUtil socketUtil;
    private byte[] commands;
    private PreviewImpl preview;
    private String action_name;
    private float swing_angle, heading_angle, roll_angle, pitch_angle;
    private float current_swing, current_handing, current_pitch;
    private Context context;
    private boolean isStop;
    private boolean isSendCommand, isGetCommand;
    private SharedPreferences sharedPreferences;
    private Runnable batteryRunnable, posturerRunnable, environmentRunnable;   //电量线程,姿态线程
    private boolean isGetDeviceInfo;   //线程是否运行控制
    ExecutorService fixedThreadPool;
    private int control_addr;

    private int car_moveSpeed;
    private int car_turnSpeed;
    private int car_moveSpeed_high;
    private int car_moveSpeed_low;
    private int car_turnSpeed_high;
    private int car_turnSpeed_low;
    private int swing_speed;

    public ControlPresentImpl(final PreviewImpl preview, Context context) {
        this.preview = preview;
        this.context = context;
        isSendCommand = true;
        isGetCommand = true;
        fixedThreadPool = Executors.newFixedThreadPool(8);
        initSocket();
//        socketUtil = new SocketUtil(preview, context);
        sharedPreferences = context.getSharedPreferences(Environment.ENVIRONMENT_INFO, Context.MODE_PRIVATE);
        initSharedPreferences();
        control_addr = LoginInfo.getInstance().getControl_addr();
//        control_addr = 1;
        setAllThread();


        fixedThreadPool.execute(batteryRunnable);
        fixedThreadPool.execute(posturerRunnable);
//        fixedThreadPool.execute(environmentRunnable);

    }


    private void initSocket() {
        socketUtil = new UdpSocketUtil();
        socketUtil.socketLogin("172.169.10.7", 20108,  20108);
        listenSocketResult();
    }

    private void listenSocketResult() {
        socketUtil.setOnCommandResultListener(new UdpSocketUtil.OnCommandResultListener() {
            @Override
            public void result(byte[] bytes) {
                Message msg = new Message();
                msg.obj = bytes;
                handler.sendMessage(msg);
            }
        });
    }

    //电量线程控制
    private void setAllThread() {
        batteryRunnable = new Runnable() {
            @Override
            public void run() {
                preview.ilog("thread: batteryRunnable");
                isGetDeviceInfo = true;
                while (isGetDeviceInfo) {
                    getBattery();
                    sleep(10 * 1000);
                }
            }
        };


        posturerRunnable = new Runnable() {
            @Override
            public void run() {
                preview.ilog("thread: posturerRunnable");
                while (isGetDeviceInfo) {
                    getTurn_angle();
                    sleep(140);
                    getDevice_state();
                    sleep(500);
                }
            }
        };

        environmentRunnable = new Runnable() {
            @Override
            public void run() {
                preview.ilog("thread: environmentRunnable");
                while (isGetDeviceInfo) {
                    getGasInfo();
                    sleep(140);
                    getHumidityInfo();
                    sleep(1000 * 2);
                }
            }
        };

    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void initSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Environment.CH4, -10000);
        editor.putFloat(Environment.CO, -10000);
        editor.putFloat(Environment.CO2, -10000);
        editor.putFloat(Environment.O2, -10000);
        editor.putFloat(Environment.TEMPERATURE, -10000);
        editor.putFloat(Environment.HUMIDITY, -10000);
        editor.commit();
        editor.clear();
    }

    //速度模式前后左右
    public void foward(int mcar_speed_low, int mcar_speed_high) {
        action_name = "向前";
        car_speed_Control(mcar_speed_low, mcar_speed_high | 0x80, mcar_speed_low, mcar_speed_high);
    }

    public void back(int mcar_speed_low, int mcar_speed_high) {
        action_name = "向后";
        car_speed_Control(mcar_speed_low, mcar_speed_high, mcar_speed_low, mcar_speed_high | 0x80);
    }

    public void left_foward(int mcar_speed_low, int mcar_speed_high) {
        action_name = "向左上";
        car_speed_Control(0, 0, mcar_speed_low, mcar_speed_high);
    }

    public void right_foward(int mcar_speed_low, int mcar_speed_high) {
        action_name = "向右上";
        car_speed_Control(mcar_speed_low, mcar_speed_high | 0x80, 0, 0);
    }

    public void left(int mcar_speed_low, int mcar_speed_high) {
        action_name = "向左";
        car_speed_Control(mcar_speed_low, mcar_speed_high, mcar_speed_low, mcar_speed_high);
    }

    public void right(int mcar_speed_low, int mcar_speed_high) {
        action_name = "向右";
        car_speed_Control(mcar_speed_low, mcar_speed_high | 0x80, mcar_speed_low, mcar_speed_high | 0x80);
    }

    public void left_back(int mcar_speed_low, int mcar_speed_high) {
        action_name = "向左下";
        car_speed_Control(mcar_speed_low, mcar_speed_high, 0, 0);
    }

    public void right_back(int mcar_speed_low, int mcar_speed_high) {
        action_name = "向右下";
        car_speed_Control(0, 0, mcar_speed_low, mcar_speed_high | 0x80);
    }


    @Override
    public void isSendGetCommand(boolean isGet) {
        isGetCommand = isGet;
    }


    @Override
    public void moveUpBate(double bate) {
        action_name = "摇杆向上";
        log("move up moveSpeed = " + car_moveSpeed, false);
        int moveSpeed = (int) (car_moveSpeed * bate);
        int low = moveSpeed & 0x00ff;
        int high = moveSpeed >> 8;
        car_speed_Control(low, (high | 0x80), low, high);
    }

    @Override
    public void moveBottomBate(double bate) {
        action_name = "摇杆向下";
        log("move bottom moveSpeed = " + car_moveSpeed, false);
        int moveSpeed = (int) (car_moveSpeed * bate);
        int low = moveSpeed & 0x00ff;
        int high = moveSpeed >> 8;
        car_speed_Control(low, high, low, (high | 0x80));
    }

    @Override
    public void moveLeftBate(double bate) {
        action_name = "摇杆向左";
        log("move left turnSpeed = " + car_turnSpeed, false);
        int turnSpeed = (int) (car_turnSpeed * bate);
        int low = turnSpeed & 0x00ff;
        int high = turnSpeed >> 8;
        car_speed_Control(low, high, low, high);
    }

    @Override
    public void moveRightBate(double bate) {
        action_name = "摇杆向右";
        log("move right turnSpeed = " + car_turnSpeed, false);
        int turnSpeed = (int) (car_turnSpeed * bate);
        int low = turnSpeed & 0x00ff;
        int high = turnSpeed >> 8;
        car_speed_Control(low, (high | 0x80), low, (high | 0x80));
    }

    @Override
    public void moveUp() {
        action_name = "摇杆向上";
        log("move up moveSpeed = " + car_moveSpeed, false);
        car_speed_Control(car_moveSpeed_low, (car_moveSpeed_high | 0x80), car_moveSpeed_low, car_moveSpeed_high);
    }

    @Override
    public void moveBottom() {
        action_name = "摇杆向下";
        log("move bottom moveSpeed = " + car_moveSpeed, false);
        car_speed_Control(car_moveSpeed_low, car_moveSpeed_high, car_moveSpeed_low, (car_moveSpeed_high | 0x80));
    }

    @Override
    public void moveLeft() {
        action_name = "摇杆向左";
        log("move left turnSpeed = " + car_turnSpeed, false);
        car_speed_Control(car_turnSpeed_low, car_turnSpeed_high, car_turnSpeed_low, car_turnSpeed_high);
    }

    @Override
    public void moveRight() {
        action_name = "摇杆向右";
        log("move right turnSpeed = " + car_turnSpeed, false);
        car_speed_Control(car_turnSpeed_low, (car_turnSpeed_high | 0x80), car_turnSpeed_low, (car_turnSpeed_high | 0x80));
    }

    @Override
    public void moveTurnSpeed(int speed) {
        this.car_turnSpeed = speed;
        car_turnSpeed_high = (speed >> 8);
        car_turnSpeed_low = (speed & 0x00ff);
    }

    @Override
    public void moveWithSpeed(double bi, int angel) {


        action_name = "摇杆移动，角度：" + angel;
        double mCar_speed = car_moveSpeed;
        if (bi < 1)
            mCar_speed = car_moveSpeed * bi;
        int leftSpeed = (int) SpeedUtil.getLeftSpeed(angel, mCar_speed);
        int rightSpeed = (int) SpeedUtil.getRightSpeed(angel, mCar_speed);

//        preview.ilog("无接收 speed=  "+ mCar_speed+" left = "+leftSpeed+ "  right = "+rightSpeed);
        int leftSpeedAbs = Math.abs(leftSpeed);
        int left_low = leftSpeedAbs & 0x00ff;
        int left_high = leftSpeed >= 0 ? (leftSpeedAbs >> 8) : (leftSpeedAbs >> 8) | 0x80;

        int rightSpeedAbs = Math.abs(rightSpeed);
        int right_low = rightSpeedAbs & 0x00ff;
        int right_high = rightSpeed >= 0 ? (rightSpeedAbs >> 8) : (rightSpeedAbs >> 8) | 0x80;

//        log("无接收 摇杆-速度处理-左速度："+leftSpeed+" 右速度："+rightSpeed,false);

//        commandOprater(0x09, 0x04, new int[]{left_low, left_high, right_low, right_high}, true);

        car_speed_Control(left_low, left_high, right_low, right_high);

       /* double mCar_speed = car_speed;
        if (bi < 1)
            mCar_speed = car_speed * bi;
        preview.ilog("拖动速度为：speed = " + mCar_speed + " bi= " + bi);
        int mcar_speed_low = (((int) mCar_speed) & 0x00ff);
        int mcar_speed_high = (((int) mCar_speed) >> 8);

        yaogan_angleProcessor(mcar_speed_low, mcar_speed_high, angel, mCar_speed, angel);
        directionProcessor(mcar_speed_low, mcar_speed_high, angel);*/

    }

    private void yaogan_angleProcessor(int mcar_speed_low, int mcar_speed_high, int angel, double mCar_speed, int angel1) {
        double mCar_speed2 = 0;
        int mcar_speed_low2 = 0;
        int mcar_speed_high2 = 0;
        double average_speed = mCar_speed / 45;
        if (angel > 0 && angel < 80 && angel != 45) {
            action_name = "向右上";
            mCar_speed2 = (Math.abs(45 - angel)) * average_speed;
            mcar_speed_low2 = (int) mCar_speed2 & 0x00ff;
            if (angel < 45)
                mcar_speed_high2 = (((int) mCar_speed2) >> 8) | 0x80;
            else
                mcar_speed_high2 = (((int) mCar_speed2) >> 8);
            car_speed_Control(mcar_speed_low, mcar_speed_high | 0x80, mcar_speed_low2, mcar_speed_high2);
        } else if (angel >= 80 && angel <= 100) {

            foward(mcar_speed_low * 2, mcar_speed_high * 2);
        } else if (angel > 100 && angel < 180 && angel != 135) {
            action_name = "向左上";
            mCar_speed2 = (Math.abs(135 - angel)) * average_speed;
            mcar_speed_low2 = (int) mCar_speed2 & 0x00ff;
            if (angel < 135)
                mcar_speed_high2 = (((int) mCar_speed2) >> 8) | 0x80;
            else
                mcar_speed_high2 = (((int) mCar_speed2) >> 8);
            car_speed_Control(mcar_speed_low2, mcar_speed_high2, mcar_speed_low, mcar_speed_high);
        } else if (angel > 180 && angel < 260 && angel != 225) {
            action_name = "向左下";
            mCar_speed2 = (Math.abs(225 - angel)) * average_speed;
            mcar_speed_low2 = (int) mCar_speed2 & 0x00ff;
            if (angel > 225)
                mcar_speed_high2 = (((int) mCar_speed2) >> 8) | 0x80;
            else
                mcar_speed_high2 = (((int) mCar_speed2) >> 8);
            car_speed_Control(mcar_speed_low, mcar_speed_high, mcar_speed_low2, mcar_speed_high2);
        } else if (angel >= 260 && angel <= 280) {
            back(mcar_speed_low * 2, mcar_speed_high * 2);
        } else if (angel > 280 && angel < 360 && angel != 315) {
            action_name = "向右下";
            mCar_speed2 = (Math.abs(315 - angel)) * average_speed;
            mcar_speed_low2 = (int) mCar_speed2 & 0x00ff;
            if (angel > 315)
                mcar_speed_high2 = (((int) mCar_speed2) >> 8) | 0x80;
            else
                mcar_speed_high2 = (((int) mCar_speed2) >> 8);
            car_speed_Control(mcar_speed_low2, mcar_speed_high2, mcar_speed_low, mcar_speed_high | 0x80);
        }

    }


    private void directionProcessor(int mcar_speed_low, int mcar_speed_high, int angel) {
        switch (angel) {
            case 0:
            case 360:
                right(mcar_speed_low, mcar_speed_high);
                break;
            case 45:
                right_foward(mcar_speed_low, mcar_speed_high);
                break;
            case 90:
                break;
            case 135:
                left_foward(mcar_speed_low, mcar_speed_high);
                break;
            case 180:
                left(mcar_speed_low, mcar_speed_high);
                break;
            case 225:
                left_back(mcar_speed_low, mcar_speed_high);
                break;
            case 270:
                break;
            case 315:
                right_back(mcar_speed_low, mcar_speed_high);
                break;
        }
    }

    @Override
    public void turnBack(int speed) {
        action_name = "摆臂向后";
        log("swing back  swingSpeed = " + speed, false);
        int speed_low = (speed & 0x00ff);
        int speed_high = (speed >> 8);
        swing_speed_Control(speed_low, speed_high);
    }

    @Override
    public void turnFoward(int speed) {
        action_name = "摆臂向前";
        log("swing fowoard  swingSpeed = " + speed, false);
        int speed_low = (speed & 0x00ff);
        int speed_high = (speed >> 8);
        swing_speed_Control(speed_low, speed_high | 0x80);
    }

    @Override
    public void stopSwing() {
        action_name = "停止摆臂";
        swing_speed_Control(0, 0);
    }

    @Override
    public void stopMove() {
        action_name = "摇杆停止移动";
        car_speed_Control(0, 0, 0, 0);
    }

    private void car_speed_Control(int left_speed_low, int left_speed_high, int right_speed_low, int right_speed_high) {
        int sum = (control_addr + 0x09 + 0x04 + left_speed_high + left_speed_low + right_speed_high + right_speed_low) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x09, (byte) 0x04,
                (byte) left_speed_low, (byte) left_speed_high, (byte) right_speed_low, (byte) right_speed_high, (byte) sum};
        if (left_speed_high == 0 && left_speed_low == 0 && right_speed_high == 0 && right_speed_low == 0) {
            sendCommand2(6);
        } else
            sendCommand(6);
    }

    private void swing_speed_Control(int low_speed, int high_speed) {
        int sum = (control_addr + 0x0a + 0x02 + high_speed + low_speed) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x0a,
                (byte) 0x02, (byte) low_speed, (byte) high_speed, (byte) sum};
        if (low_speed == 0 && high_speed == 0)
            sendCommand2(6);
        else
            sendCommand(6);
    }

    //车速设置
    @Override
    public void setCarSpeed(int speed) {
        car_moveSpeed = speed;
        car_moveSpeed_high = (speed >> 8);
        car_moveSpeed_low = (speed & 0x00ff);

    }

    //摆速设置
    @Override
    public void setSwingSpeed(int speed) {
        this.swing_speed = speed;

    }

    //获取电机状态
    @Override
    public void getMotor_state() {

        action_name = "获取电机状态";
        int sum = (control_addr + 0x0b + 0x01 + 0x00) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x0b,
                (byte) 0x01, (byte) 0x00, (byte) sum};
        byte[] result = new byte[8];
        getReader(result);
    }


    //云台运动-上仰
    @Override
    public void set_ptz_up() {
        action_name = "上仰";
        ptz_control(0x03, 0x00);
    }

    //云台运动-下俯
    @Override
    public void set_ptz_down() {
        action_name = "下俯";
        ptz_control(0x04, 0x00);
    }

    //云台运动-左转
    @Override
    public void set_ptz_left() {
        action_name = "左转";
        ptz_control(0x01, 0x00);
    }

    //云台运动-右转
    @Override
    public void set_ptz_right() {
        action_name = "右转";
        ptz_control(0x02, 0x00);
    }

    //云台运动-开灯
    @Override
    public void set_ptz_openLight(int Strength) {
        action_name = "开灯";
        ptz_control(0x05, Strength);
    }

    //云台运动-关灯
    @Override
    public void set_ptz_closeLight() {
        action_name = "关灯";
        ptz_control(0x05, 0x00);
    }

    //云台运动-可见光摄像头变倍小
    @Override
    public void set_ptz_Vcamera_boom_min() {
        action_name = "可见光摄像头变倍小";
        ptz_control(0x07, 0x00);
    }

    //云台运动-可见光摄像头变倍大
    @Override
    public void set_ptz_Vcamera_boom_max() {
        action_name = "可见光摄像头变倍大";
        ptz_control(0x06, 0x00);
    }

    //云台运动-可见光摄像头聚焦近
    @Override
    public void set_ptz_Vcamera_focus_low() {
        action_name = "可见光摄像头聚焦近";
        ptz_control(0x09, 0x00);
    }

    //云台运动-可见光摄像头聚焦远
    @Override
    public void set_ptz_Vcamera_focus_high() {
        action_name = "可见光摄像头聚焦远";
        ptz_control(0x08, 0x00);
    }

    //云台运动-红外摄像头变倍小
    @Override
    public void set_ptz_Rcamera_boom_min() {
        action_name = "红外摄像头变倍小";
        ptz_control(0x0a, 0x00);
    }

    //云台运动-红外摄像头变倍大
    @Override
    public void set_ptz_Rcamera_boom_max() {
        action_name = "红外摄像头变倍大";
        ptz_control(0x0b, 0x00);
    }

    //云台运动-停止
    @Override
    public void set_ptz_stop() {
        action_name = "云台运动-停止";
        ptz_control(0x0c, 0x00);


    }

    private void ptz_control(int ptz_command1, int data) {
        int sum = (control_addr + 0x0c + 0x02 + ptz_command1 + data) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x0c,
                (byte) 0x02, (byte) ptz_command1, (byte) data, (byte) sum};
        if (ptz_command1 == 0x0c)
            sendCommand2(6);
        else
            sendCommand(6);
    }


    /**
     * 视频切换命令
     *
     * @param model 通道 1 为可见光；
     *              通道 2 为热红外；
     *              通道 3 为前摄像头；
     *              通道 4 为后摄像头；
     */
    @Override
    public void setVideo_model(int model) {
        action_name = "视频切换命令";
        int sum = (control_addr + 0x0d + 0x01 + model) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x0d, (byte) 0x01,
                (byte) model, (byte) sum};
        sendCommand(6);
    }


    //开启前灯
    @Override
    public void open_foward_light(int strength) {
        action_name = "开启前灯";
        light_control(0x00, strength);
    }

    //关闭前灯
    @Override
    public void close_foward_light() {
        action_name = "关闭前灯";
        light_control(0x00, 0);
    }

    //开启后灯
    @Override
    public void open_back_light(int strength) {
        action_name = "开启后灯";
        light_control(0x01, strength);
    }

    //关闭后灯
    @Override
    public void close_back_light() {
        action_name = "关闭后灯";
        light_control(0x01, 0);
    }

    private void light_control(int which, int strength) {
        int sum = (control_addr + 0x0e + 0x02 + which + strength) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x0e,
                (byte) 0x02, (byte) which, (byte) strength, (byte) sum};
        sendCommand(6);
    }


    //电源控制

    /**
     * @param which 路数选择：
     *              0x01,5vA
     *              0x02,5vB
     *              0x03,12vA
     *              0x04,12vB
     * @param state 状态（0，关闭/1，开启）
     */
    @Override
    public void setPower(int which, int state) {
        action_name = "电源控制";
        int sum = (control_addr + 0x0f + 0x02 + which + state) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x0f, (byte) 0x02,
                (byte) which, (byte) state, (byte) sum};
        sendCommand(6);
    }

    //获取电源状态
    @Override
    public void getPowerStat() {
        action_name = "获取电源状态";
        int sum = (control_addr + 0x10 + 0x02 + 0x00) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x10, (byte) 0x02, (byte) 0x00,
                (byte) sum};
        sendCommand(6);
    }


    //电量读取
    @Override
    public void getBattery() {
        action_name = "获取电量";
        int sum = (control_addr + 0x11 + 0x00) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x11,
                (byte) 0x00, (byte) sum};
        byte[] result = new byte[6];
        getReader(result);
    }


    //获取摆臂相对机体角度
    @Override
    public void getTurn_angle() {
        action_name = "获取摆臂角度";
        int sum = (control_addr + 0x12 + 0x00) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x12, (byte) 0x00,
                (byte) sum};
        byte[] result = new byte[7];
        getReader(result);
    }

    //获取姿态数据
    @Override
    public void getDevice_state() {
        action_name = "获取姿态数据";
        int sum = (control_addr + 0x13 + 0x00) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x13, (byte) 0x00,
                (byte) sum};
        byte[] result = new byte[11];
        getReader(result);
    }

    //获取气体数据
    @Override
    public void getGasInfo() {
        action_name = "获取气体数据";
        getRoofModule_info(0x01, 9);
    }

    //获取温湿度数据
    @Override
    public void getHumidityInfo() {
        action_name = "获取温湿度数据";
        getRoofModule_info(0x02, 7);
    }

    //获取GPS数据
    @Override
    public void getGpsInfo() {
        action_name = "获取GPS数据";
        getRoofModule_info(0x03, 28);
    }

    @Override
    public void getProduct_id() {
        action_name = "获取产品编号";
        int sum = (control_addr + 0x01 + 0x00) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x01,
                (byte) 0x00, (byte) sum};
        byte[] result = new byte[9];
        getReader(result);
    }

    @Override
    public void getSoftware_stat() {
        action_name = "获取机器人软件版本";
        int sum = (control_addr + 0x02 + 0x01) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x02,
                (byte) 0x01, (byte) sum};
        byte[] result = new byte[7];
        getReader(result);

    }

    @Override
    public void getCarSpeed_max() {
        action_name = "获取行走最高速度";
        int sum = (control_addr + 0x06 + 0x00) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x06,
                (byte) 0x00, (byte) sum};
        byte[] result = new byte[7];
        getReader(result);
    }

    @Override
    public void getSwingSpeed_max() {
        action_name = "获取摆臂最高速度";
        int sum = (control_addr + 0x08 + 0x00) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x08,
                (byte) 0x00, (byte) sum};
        byte[] result = new byte[7];
        getReader(result);
    }

    @Override
    public void setCarSpeed_max(int car_speed_max) {
        action_name = "设置行走最高速度";
        int car_speedMax_low = (car_speed_max & 0x00ff);
        int car_speedMax_high = (car_speed_max >> 8);
        int sum = (control_addr + 0x05 + 0x02 + car_speedMax_low + car_speedMax_high) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x05,
                (byte) 0x02, (byte) car_speedMax_low, (byte) car_speedMax_high, (byte) sum};
        byte[] result = new byte[6];
        getReader(result);
    }

    @Override
    public void setSwingSpeed_max(int swing_speed_max) {
        action_name = "设置摆臂最高速度";
        int swing_speedMax_low = (swing_speed_max & 0x00ff);
        int swing_speedMax_high = (swing_speed_max >> 8);
        int sum = (control_addr + 0x05 + 0x02 + swing_speedMax_low + swing_speedMax_high) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x07,
                (byte) 0x02, (byte) swing_speedMax_low, (byte) swing_speedMax_high, (byte) sum};
        byte[] result = new byte[6];
        getReader(result);

    }

    @Override
    public void release() {
        isStop = false;
        isGetDeviceInfo = false;
        isStop = false;
        fixedThreadPool.shutdownNow();
        socketUtil.release();
    }

    /**
     * @param i 下发数据： 1 字节模组对象
     *          0x01： 环境气体参数 （ 包括常用 O2，CO， CO2， CH4， H2S...）
     *          0x02： 环境温湿度
     *          0x03： GPS 数据
     */
    private void getRoofModule_info(int i, int byte_num) {
        int sum = (control_addr + 0x14 + i) % 0x100;
        commands = new byte[]{(byte) 0x7e, (byte) control_addr, (byte) 0x14, (byte) i,
                (byte) sum};
        byte[] result = new byte[byte_num];
        getReader(result);
    }


    private void sendCommand(int byte_nu) {
        if (isSendCommand){
            socketUtil.sendCommandInThread(commands);
        }
            /*socketUtil.getReader(commands, new byte[byte_nu], new SocketReaderListener() {
                @Override
                public void Result(byte[] bytes) {
                    Message msg = new Message();
                    msg.obj = bytes;
                    handler.sendMessage(msg);

                }
            }, 1, "无接收控制" + action_name, 0);*/

    }

    private void sendCommand2(int byte_nu) {
        if (isSendCommand){
            socketUtil.sendCommandInThread(commands);
        }
            /*socketUtil.getReader(commands, new byte[byte_nu], new SocketReaderListener() {
                @Override
                public void Result(byte[] bytes) {
                    Message msg = new Message();
                    msg.obj = bytes;
                    handler.sendMessage(msg);

                }
            }, 2, "无接收控制" + action_name, 2);*/

    }

    private void getReader(byte[] result) {
        if (isGetCommand){
            socketUtil.sendCommandInThread(commands);
        }
          /*  socketUtil.getReader(commands, result, new SocketReaderListener() {
                @Override
                public void Result(byte[] bytes) {
                    Message msg = new Message();
                    msg.obj = bytes;
                    handler.sendMessage(msg);
                }
            }, 5, "接收" + action_name, 1);*/
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] bytes = (byte[]) msg.obj;
            handler_bytesProcessor(bytes);

        }
    };

    private void handler_bytesProcessor(byte[] bytes) {
        switch (bytes[2] & 0xff) {
            case 0x81:  //获取产品编号
                break;
            case 0x82:  //获取机器人软件版本
                break;
            case 0x86:  //获取行走最高速度
                break;
            case 0x88:  //获取摆臂最高速度
                break;
            case 0x89:  //行走控制
                break;
            case 0x8a:  //摆臂控制
                break;
            case 0x90:  //电机控制状态返回数据
                break;
            case 0x91:  //获取电量信息
                if (bytes.length == 6) {
                    preview.ilog("电池电量：" + bytes[4]);
                    preview.showBattery(setBatteryForActivity(bytes[4]));
                }

                break;
            case 0x92:  //获取摆臀角度
                if (bytes.length == 7 && bytes[6] != 0 && bytes[3] == 0x02) {
                    swing_angle = bytes[5] << 8 | (bytes[4] & 0xff);
                }
                break;
            case 0x93:  //获取姿态数据
                if (bytes.length == 11 && bytes[10] != 0 && bytes[3] == 0x06) {
                    preview.isConnect(true);
                    heading_angle = ((bytes[5] & 0x7f) << 8 | (bytes[4]) & 0xff);
                    pitch_angle = ((bytes[7] & 0x7f) << 8 | (bytes[6] & 0xff));
                    roll_angle = ((bytes[9] & 0x7f) << 8 | (bytes[8] & 0xff));
                    heading_angle = ((bytes[5]) >> 7) == 0 ? heading_angle : -heading_angle;
                    pitch_angle = ((bytes[7]) >> 7) == 0 ? pitch_angle : -pitch_angle;
                    roll_angle = ((bytes[9]) >> 7) == 0 ? roll_angle : -roll_angle;


                    preview.ilog("成功接收获取姿态数据：航向角度：" + heading_angle + "  垂直角度：" + pitch_angle + " 横滚角度：" + roll_angle + " 摆臂相对角度：" + swing_angle);

                    device_angleProcessor();
                }
                break;
            case 0x94:  //获取环境数据
                if (bytes.length > 4 && bytes.length > 7)
                    if (bytes[3] == 23) {

                    } else {
                        dealEnvironment_info(bytes, 0);
                    }

                break;


        }
    }

    private void device_angleProcessor() {
        if (current_handing == heading_angle && current_pitch == pitch_angle && current_swing == swing_angle)
            return;
        RotateAnimation ra_roll = new RotateAnimation(roll_angle, roll_angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        RotateAnimation ra_swing = null;
        RotateAnimation ra_picth = null;
        if (pitch_angle > 0)
            ra_picth = new RotateAnimation(-pitch_angle, -pitch_angle, Animation.RELATIVE_TO_SELF, 0.37f, Animation.RELATIVE_TO_SELF, 0.5f);
        else
            ra_picth = new RotateAnimation(-pitch_angle, -pitch_angle, Animation.RELATIVE_TO_SELF, 0.63f, Animation.RELATIVE_TO_SELF, 0.5f);

        /**
         * 手机：0.84
         * 平板：0.84
         * */
        ra_swing = new RotateAnimation(swing_angle, swing_angle, Animation.RELATIVE_TO_SELF, 0.84f, Animation.RELATIVE_TO_SELF, 0.53f);

        ra_roll.setDuration(100);
        ra_picth.setDuration(100);
        ra_swing.setDuration(100);
        ra_roll.setFillAfter(true);
        ra_picth.setFillAfter(true);
        ra_swing.setFillAfter(true);
        preview.ilog("成功设置角度数据------- swing_angle" + swing_angle + " picth: " + pitch_angle);
        preview.setAngleAnimation(ra_swing, ra_picth, ra_roll, null);
        current_handing = heading_angle;
        current_pitch = pitch_angle;
        current_swing = swing_angle;
    }

    private int setBatteryForActivity(byte batteryNum) {
        if (batteryNum >= 80) {
            return R.mipmap.battery5;
        } else if (batteryNum >= 60) {
            return R.mipmap.battery4;
        } else if (batteryNum >= 40) {
            return R.mipmap.battery3;
        } else if (batteryNum >= 20) {
            return R.mipmap.battery2;
        } else {
            return R.mipmap.battery1;
        }
    }

    /**
     * @param bytes 返回的字节数组
     * @param which 数据类型：
     *              气体
     *              温湿度
     */
    private void dealEnvironment_info(byte[] bytes, int which) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (which == 0) {
            float mCO_num = bytes[4] & 0xff;
            float mCH4_num = bytes[5] & 0xff;
            float mO2_num = bytes[6] & 0xff;
            float mCO2_num = bytes[7] & 0xff;

            editor.putFloat(Environment.CO, mCO_num);
            editor.putFloat(Environment.CH4, mCH4_num);
            editor.putFloat(Environment.O2, mO2_num);
            editor.putFloat(Environment.CO2, mCO2_num);

            preview.ilog("成功装载所有气体环境数据！");
        } else {
            float mTemparature_num = bytes[4] & 0xff;
            float mHumidity_num = bytes[5] & 0xff;

            editor.putFloat(Environment.TEMPERATURE, mTemparature_num);
            editor.putFloat(Environment.HUMIDITY, mHumidity_num);

            preview.ilog("成功装载所有温湿度环境数据！");
        }
        editor.commit();
        editor.clear();

    }


    private void log(String msg, boolean isError) {
        if (preview != null) {
            if (isError)
                preview.ierror(msg);
            else
                preview.ilog(msg);
        }
    }
}
