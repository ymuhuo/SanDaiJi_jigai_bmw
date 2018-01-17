package com.bmw.dinosaursmall.view.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.model.EnvironmentInfo;
import com.bmw.dinosaursmall.model.LoginInfo;
import com.bmw.dinosaursmall.model.Model_info;
import com.bmw.dinosaursmall.presenter.ControlPresenter;
import com.bmw.dinosaursmall.presenter.HCNetSdkLogin;
import com.bmw.dinosaursmall.presenter.VideoCutPresenter;
import com.bmw.dinosaursmall.presenter.impl.ControlPresentImpl;
import com.bmw.dinosaursmall.presenter.impl.HCNetSdkLoginImpl;
import com.bmw.dinosaursmall.presenter.impl.VideoCutPresentImpl;
import com.bmw.dinosaursmall.utils.NetUtil;
import com.bmw.dinosaursmall.utils.singleThreadUtil.RunnablePriority;
import com.bmw.dinosaursmall.view.view.ConnectStateDialog;
import com.bmw.dinosaursmall.view.view.DirectionButton;
import com.bmw.dinosaursmall.view.view.EnvironmentDialog;
import com.bmw.dinosaursmall.view.view.FireControlAlarmDialog;
import com.bmw.dinosaursmall.view.view.ModelControlDialog;
import com.bmw.dinosaursmall.view.view.MutiRadioGroup;
import com.bmw.dinosaursmall.view.view.PwLightSpeed;
import com.bmw.dinosaursmall.view.view.RockerButton;
import com.bmw.dinosaursmall.view.view.RockerView;
import com.bmw.dinosaursmall.view.view.RollButton;
import com.bmw.dinosaursmall.view.view.SettingDialog;
import com.bmw.dinosaursmall.view.view.VerticalPageSeekBar;
import com.bmw.dinosaursmall.view.viewImpl.PreviewImpl;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 *
 */
public class MainActivity extends BaseActivity implements PreviewImpl,
        View.OnTouchListener,
        View.OnClickListener,
        MutiRadioGroup.OnCheckedChangeListener,
        VerticalPageSeekBar.OnSeekBarStopTouchListener {

    @Bind(R.id.preview_surface)
    SurfaceView previewSurface;
    @Bind(R.id.preview_battery)
    ImageView battery;
    @Bind(R.id.capture)
    ImageView capture;
    @Bind(R.id.record)
    ImageView record;
    @Bind(R.id.car_roll_angle)
    ImageView carRollImg;
    @Bind(R.id.swing_angle)
    ImageView swingImg;
    @Bind(R.id.carAndswing)
    FrameLayout carAndswing;
    @Bind(R.id.preview_img)
    LinearLayout previewImg;
    @Bind(R.id.preview_connect)
    ImageView previewConn;
    @Bind(R.id.preview_setting)
    ImageButton previewSetting;
    //    @Bind(R.id.preview_aim)
//    ImageView previewAim;
    @Bind(R.id.main)
    RelativeLayout main;
    @Bind(R.id.rollButton_SwingMove)
    RollButton swingMoveRollBtn;
    @Bind(R.id.ptzControl_rockerBtn)
    RockerButton ptzControlBtn;
    @Bind(R.id.moveControl_RockerButton)
    RockerButton moveControlRockBtn;
    @Bind(R.id.preview_fire_Control)
    ImageView img_fire_control;
    @Bind(R.id.preview_bigLight_Control)
    ImageView img_light_big_control;

    private HCNetSdkLogin hcNetSdkLogin;
    private VideoCutPresenter videoCutPresenter;
    private ControlPresenter controlPresenter;
    private int foward_light_num, back_light_num, ptz_light_num, car_speed_num = 2500, swing_speed_num = 1500; //灯光，车速，摆速
    private Model_info model_info = Model_info.video_four;  //视频模式
    private Animation appear, disapear, shake;  //显示，消失，震动动画
    private Vibrator vibrator;  //震动
    private int key_back; //按两次返回计数；
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private boolean isGuide;
    private boolean isVideoIpConnect, isControlIpConnect;
    private ScheduledExecutorService scheduledExecutorService;
    private int car_turnSpeed_num = 600;
    private boolean isOpenFireControl;
    private boolean isOpenLightBig;
    private boolean mFireControlSet;
    private EnvironmentDialog mEnvironmentWindow;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    initCreate();
                    break;
                case 0x02:
                    if (previewConn != null)
                        previewConn.setImageResource(R.mipmap.connect);
                    break;
                case 0x03:
                    if (previewConn != null)
                        previewConn.setImageResource(R.mipmap.disconnect);
                    break;
                case 0x04:
                    if (record != null)
                        record.setImageResource(R.mipmap.record_press);
                    break;
                case 0x05:
                    if (record != null)
                        record.setImageResource(R.mipmap.record);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);    //屏蔽home键
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //屏幕常亮
//        initCreate();
        setContentView(R.layout.wlcome);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initCreate();
                    }
                });
            }
        }, 1500);


    }

    private void initCreate() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        LoginInfo.getInstance().initLoginInfo(this);
        hcNetSdkLogin = new HCNetSdkLoginImpl(this, this, previewSurface);
        appear = AnimationUtils.loadAnimation(this, R.anim.boom_focuse_appear);
        disapear = AnimationUtils.loadAnimation(this, R.anim.boom_focuse_disappear);
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        videoCutPresenter = new VideoCutPresentImpl(this);
        controlPresenter = new ControlPresentImpl(this, getApplicationContext());
        controlPresenter.setCarSpeed(car_speed_num);
        controlPresenter.setSwingSpeed(swing_speed_num);
        controlPresenter.moveTurnSpeed(car_turnSpeed_num);
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        scheduledExecutorService = Executors.newScheduledThreadPool(3);

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int videoPing = NetUtil.pingHost(LoginInfo.getInstance().getIp());
                int controlPing = NetUtil.pingHost(LoginInfo.getInstance().getSocket_ip());

                isVideoIpConnect = videoPing == 0 ? true : false;
                isControlIpConnect = controlPing == 0 ? true : false;

                int handlerOprator = (isVideoIpConnect && isControlIpConnect) ? 0x02 : 0x03;
                handler.sendEmptyMessage(handlerOprator);

            }
        }, 0, 5, TimeUnit.SECONDS);

        initDirectionControl();

        setSurfaceTouchEvent();
    }

    private void setSurfaceTouchEvent() {
        previewSurface.setOnTouchListener(new View.OnTouchListener() {
            int lastXLength = -1;
            PtzAutoThread ptzAutoThread;
            boolean isChangeSize = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (model_info == Model_info.video_ptz && event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1) {
                    ptzAutoThread = new PtzAutoThread(event.getX(), event.getY(), previewSurface.getWidth() / 2, previewSurface.getHeight() / 2, controlPresenter);
                    ptzAutoThread.start();
                }
                if (model_info == Model_info.video_ptz && event.getPointerCount() >= 2) {
                    if (ptzAutoThread != null)
                        ptzAutoThread.isNotRunning(true);
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        isChangeSize = true;
                        int currentXLength = (int) (event.getX(0) - event.getX(1));
                        currentXLength = Math.abs(currentXLength);
                        if (lastXLength != -1 && currentXLength > lastXLength)
                            controlPresenter.set_ptz_Vcamera_boom_max();
                        if (currentXLength < lastXLength) {
                            controlPresenter.set_ptz_Vcamera_boom_min();
                        }
                        lastXLength = currentXLength;
                    }

                }

                if (isChangeSize && event.getAction() == MotionEvent.ACTION_UP)
                    controlPresenter.set_ptz_stop();
                return true;
            }
        });
    }


    private void initDirectionControl() {

        moveControlRockBtn.setOnRockerRollTouchListerner(new RockerButton.OnRockerRollTouchListerner() {
            @Override
            public void rolling(double bate, int angle) {
                LoginInfo.getInstance().setKeepSendMoveStop(false);
                if (angle >= 45 && angle < 135) {
                    controlPresenter.moveBottomBate(bate);
                } else if (angle >= 135 && angle < 225) {
                    controlPresenter.moveLeftBate(bate);
                } else if (angle >= 225 && angle < 315) {
                    controlPresenter.moveUpBate(bate);
                } else {
                    controlPresenter.moveRightBate(bate);
                }
            }

            @Override
            public void stopRoll() {
                controlPresenter.stopMove();
                LoginInfo.getInstance().setKeepSendMoveStop(true);
            }
        });

       /* directionButton.setOnItemClickListener(new DirectionButton.OnItemClickListener() {
            @Override
            public void topClick() {
                controlPresenter.moveUp();
            }

            @Override
            public void bottomClick() {
                controlPresenter.moveBottom();
            }

            @Override
            public void leftClick() {
                controlPresenter.moveLeft();
            }

            @Override
            public void rightClick() {
                controlPresenter.moveRight();
            }

            @Override
            public void stopTouch() {
                controlPresenter.stopMove();
            }
        });*/

        swingMoveRollBtn.setMaxProgress(swing_speed_num);

        swingMoveRollBtn.setOnRollBtnChangeListener(new RollButton.OnRollBtnChangeListener() {
            @Override
            public void onTouchChange(int direction, int progress) {
                LoginInfo.getInstance().setKeepSendTurnStop(false);
                if (direction == 0) {
                    controlPresenter.turnBack(progress);
                }
                if (direction == 1) {
                    controlPresenter.turnFoward(progress);
                }
            }

            @Override
            public void stopTouch() {
                controlPresenter.stopSwing();
                LoginInfo.getInstance().setKeepSendTurnStop(true);
            }
        });
/*
        ptzControlBtn.setOnRockerRollTouchListerner(new RockerButton.OnRockerRollTouchListerner() {
            @Override
            public void rolling(double bate, int angle) {
                LoginInfo.getInstance().setKeepSendPtzStop(false);
                if (angle >= 45 && angle < 135) {
//                    controlPresenter.moveBottomBate(bate);
                    controlPresenter.set_ptz_down();
                } else if (angle >= 135 && angle < 225) {
//                    controlPresenter.moveLeftBate(bate);
                    controlPresenter.set_ptz_left();
                } else if (angle >= 225 && angle < 315) {
//                    controlPresenter.moveUpBate(bate);
                    controlPresenter.set_ptz_up();
                } else {
//                    controlPresenter.moveRightBate(bate);
                    controlPresenter.set_ptz_right();
                }
            }

            @Override
            public void stopRoll() {
                LoginInfo.getInstance().setKeepSendPtzStop(true);
                controlPresenter.set_ptz_stop();
            }
        });*/

        ptzControlBtn.setOnDirectionClickListener(new RockerButton.OnDirectionClickListener() {
            @Override
            public void topClick(double bate) {
                LoginInfo.getInstance().setKeepSendPtzStop(false);
                controlPresenter.set_ptz_up();
            }

            @Override
            public void bottomClick(double bate) {
                LoginInfo.getInstance().setKeepSendPtzStop(false);
                controlPresenter.set_ptz_down();
            }

            @Override
            public void leftClick(double bate) {
                LoginInfo.getInstance().setKeepSendPtzStop(false);
                controlPresenter.set_ptz_left();
            }

            @Override
            public void rightClick(double bate) {
                LoginInfo.getInstance().setKeepSendPtzStop(false);
                controlPresenter.set_ptz_right();
            }

            @Override
            public void stopTouch() {
                controlPresenter.set_ptz_stop();
                LoginInfo.getInstance().setKeepSendPtzStop(true);
            }

        });

    }

    //震动线程
    private void vibratorThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                long[] pattern = {100, 400, 100, 400}; // 停止 开启 停止 开启
                vibrator.vibrate(pattern, 1); //重复两次上面的pattern 如果只想震动一次，index设为-1

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                vibrator.cancel();
            }
        }).start();

    }

    //sdk初始化失败回调
    @Override
    public void stop() {
//        startActivity(new Intent(this, MainActivity.class));
        MainActivity.this.finish();
    }

    @Override
    public void setEnvironmentData(final EnvironmentInfo environmentData) {
        if (mEnvironmentWindow != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEnvironmentWindow.setAdapterData(environmentData);
                }
            });
        }
    }

    @Override
    public void isConnect(final boolean isConnect) {

        /*handler.post(new Runnable() {
            @Override
            public void run() {
                if (previewConn != null)
                    if (isConnect) {
                        previewConn.setImageResource(R.mipmap.connect);
                    } else {
                        previewConn.setImageResource(R.mipmap.disconnect);
                    }
            }
        });*/


    }

    //释放资源
    @Override
    protected void onDestroy() {
//        previewPresenter.release();
//        isGoLogin = false;
        videoCutPresenter.release();
        scheduledExecutorService.shutdownNow();
        controlPresenter.release();
        hcNetSdkLogin.release();
        ButterKnife.unbind(this);
        super.onDestroy();
    }


    //录像回调
    @Override
    public void record(int i, boolean isRecord) {
        switch (i) {
            case 0:
                if (isRecord) {
                    toast("开始录像");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            record.setImageResource(R.mipmap.record_press);
                        }
                    });
                } else {
                    toast("开始录像失败");
                }
                break;
            case 1:
                if (isRecord) {
                    toast("停止录像");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            record.setImageResource(R.mipmap.record);
                        }
                    });
                } else {
                    toast("停止录像失败");
                }
                break;
        }

    }

    @Override
    public void capture(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        final ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
//        layoutParams.setMargins(0,R.dimen.top_menu,R.dimen.control_right_contain,0);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(bitmap);
        main.addView(imageView);

//        Animation animation = AnimationUtils.loadAnimation(this,R.anim.capture);
//        animation.setFillAfter(true);
//        imageView.startAnimation(animation);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setVisibility(View.GONE);
//                        bitmap.recycle();
                    }
                });
            }
        }).start();
    }


    //电量显示回调
    @Override
    public void showBattery(int pic) {
        if (battery != null)
            battery.setImageResource(pic);
    }


    //获取摆臂以及车身角度回调
    @Override
    public void setAngleAnimation(RotateAnimation ra_swing, RotateAnimation ra_picth, RotateAnimation ra_roll, RotateAnimation ra_head) {

        if (carRollImg != null && carAndswing != null && swingImg != null && previewImg != null) {
            carRollImg.setAnimation(ra_roll);
            ra_roll.startNow();
            carAndswing.setAnimation(ra_picth);
            ra_picth.startNow();
//            swingImg.setAnimation(ra_swing);
//            ra_swing.startNow();
            carAndswing.invalidate();
            previewImg.invalidate();
        }

    }


    //触摸事件
    @OnTouch({
            R.id.img_size_add, R.id.img_size_sub,
            R.id.img_zoom_add, R.id.img_zoom_sub})
    public boolean onTouch(View v, MotionEvent event) {

//        if (event.getAction() == MotionEvent.ACTION_DOWN)
//            controlPresenter.isSendGetCommand(false);

        switch (v.getId()) {
            case R.id.img_size_add:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LoginInfo.getInstance().setKeepSendPtzStop(false);
                    controlPresenter.set_ptz_Vcamera_focus_high();
                }
                stop_ptz(event);
                break;
            case R.id.img_size_sub:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LoginInfo.getInstance().setKeepSendPtzStop(false);
                    controlPresenter.set_ptz_Vcamera_focus_low();
                }
                stop_ptz(event);
                break;
            case R.id.img_zoom_add:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LoginInfo.getInstance().setKeepSendPtzStop(false);
                    if (model_info.toString().contains("video_four") || model_info.toString().contains("video_ptz"))
                        controlPresenter.set_ptz_Vcamera_boom_max();
                    else if (model_info.toString().contains("video_ir")) {
                        controlPresenter.set_ptz_Rcamera_boom_max();
                    }
                }
                stop_ptz(event);
                break;
            case R.id.img_zoom_sub:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LoginInfo.getInstance().setKeepSendPtzStop(false);
                    if (model_info.toString().contains("video_ptz") || model_info.toString().contains("video_four"))
                        controlPresenter.set_ptz_Vcamera_boom_min();
                    else if (model_info.toString().contains("video_ir"))
                        controlPresenter.set_ptz_Rcamera_boom_min();
                }
                stop_ptz(event);
                break;

        }

//        if (event.getAction() == MotionEvent.ACTION_UP)
//            controlPresenter.isSendGetCommand(true);
        return false;
    }

    //摆臂停止行动
    private void swingStopMove(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            controlPresenter.stopSwing();
        }
    }

    //云台停止
    private void stop_ptz(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            LoginInfo.getInstance().setKeepSendPtzStop(true);
            controlPresenter.set_ptz_stop();
        }
    }

    //点击事件
    @OnClick({R.id.preview_picture, R.id.preview_video,
            R.id.preview_connect, R.id.preview_shutdown,
            R.id.record, R.id.capture,
            R.id.preview_setting, R.id.environment,
            R.id.preview_model, R.id.preview_change_camera,
            R.id.preview_speed, R.id.preview_light,
            R.id.preview_fire_Control, R.id.preview_bigLight_Control})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview_fire_Control:

                if (!mFireControlSet) {
                    setFireControlOpen();
                    break;
                }

                if (!isOpenFireControl) {
                    isOpenFireControl = true;
                    img_fire_control.setImageResource(R.mipmap.fire_control_open);
                    controlPresenter.openFireExtingguisher();
                } else {
                    isOpenFireControl = false;
                    mFireControlSet = false;
                    img_fire_control.setImageResource(R.mipmap.fire_control_close);
                    controlPresenter.closeFireExtingguisher();
                }
                break;
            case R.id.preview_bigLight_Control:
                if (!isOpenLightBig) {
                    isOpenLightBig = true;
                    img_light_big_control.setImageResource(R.mipmap.light);
                    controlPresenter.openBigLight();
                } else {
                    isOpenLightBig = false;
                    img_light_big_control.setImageResource(R.mipmap.light_big_close);
                    controlPresenter.closeBigLight();
                }
                break;
            case R.id.preview_setting:  //设置
                SettingDialog settingDialog = new SettingDialog(this);
                previewSetting.setClickable(false);
                settingDialog.setOnSettingChangeListener(new SettingDialog.OnSettingChangeListener() {
                    @Override
                    public void changeReporter(boolean isChange) {
                        if (isChange) {
                            resetConnect();
                        }
                        previewSetting.setClickable(true);
                    }
                });
                break;
            case R.id.environment:  //环境
                if (mEnvironmentWindow == null) {
                    mEnvironmentWindow = new EnvironmentDialog(this);
                } else {
                    mEnvironmentWindow.getDialog().show();
                }
//                controlPresenter.getGasInfo();
//                controlPresenter.getHumidityInfo();
                break;
            case R.id.preview_model:    //模式
                ModelControlDialog modelDialog = new ModelControlDialog(this, model_info);
                modelDialog.setOnCheckedChangeListener(this);
                break;
            case R.id.preview_speed:    //速度
                setSpeed();
                break;
            case R.id.preview_change_camera:    //摄像转换
                rechange_camero_model();
                break;
            case R.id.preview_light:    //灯光调节
                setLight();
                break;
            case R.id.record:   //录像
                record.startAnimation(shake);
                videoCutPresenter.record();
                break;
            case R.id.capture:  //截图
                capture.startAnimation(shake);
                videoCutPresenter.capture();
                break;
            case R.id.preview_shutdown:  //关闭
                finish();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                            System.exit(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.preview_connect:


                new ConnectStateDialog(this, isVideoIpConnect, isControlIpConnect);

                break;
            case R.id.preview_picture:
                Intent picIntent = new Intent(this, FileActivity.class);
                picIntent.putExtra("isPicture", true);
                startActivity(picIntent);
                break;
            case R.id.preview_video:
                Intent videoIntent = new Intent(this, FileActivity.class);
                videoIntent.putExtra("isPicture", false);
                startActivity(videoIntent);
                break;

        }
    }

    private void setFireControlOpen() {
        new FireControlAlarmDialog(this).setOnSwitchChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFireControlSet = isChecked;
            }
        });
    }

    private void resetConnect() {
        hcNetSdkLogin.logout();
        hcNetSdkLogin.connectDevice();
        controlPresenter = new ControlPresentImpl(this, getApplicationContext());
    }

    //摄像转换
    private void rechange_camero_model() {

        switch (model_info) {
            case video_four:
                camera_model_processor(R.id.video_ptz_front_ir_rear);
                break;
            case video_ptz_front_ir_rear:
                camera_model_processor(R.id.video_front);
                break;
            case video_front:
                camera_model_processor(R.id.video_rear);
                break;
            case video_rear:
                camera_model_processor(R.id.video_ptz);
                break;
            case video_ptz:
                camera_model_processor(R.id.video_ir);
                break;
            case video_ir:
                camera_model_processor(R.id.video_ptz_ir);
                break;
            case video_ptz_ir:
                camera_model_processor(R.id.video_ptz_ir_front);
                break;
            case video_ptz_ir_front:
                camera_model_processor(R.id.video_ptz_front_ir);
                break;

            case video_ptz_front_ir:
                camera_model_processor(R.id.video_ptz_ir_v);
                break;

            case video_ptz_ir_v:
                camera_model_processor(R.id.video_four);
                break;

        }
    }

    //摄像模式切换
    private void camera_model_processor(int checkedId) {
        switch (checkedId) {
            case R.id.video_four:
                controlPresenter.setVideo_model(0x05);
                model_info = Model_info.video_four;
                break;
            case R.id.video_front:
                controlPresenter.setVideo_model(0x03);
                model_info = Model_info.video_front;
                break;
            case R.id.video_rear:
                controlPresenter.setVideo_model(0x04);
                model_info = Model_info.video_rear;
                break;
            case R.id.video_ir:
                controlPresenter.setVideo_model(0x02);
                model_info = Model_info.video_ir;
                break;
            case R.id.video_ptz:
                controlPresenter.setVideo_model(0x01);
                model_info = Model_info.video_ptz;
                break;
            case R.id.video_ptz_ir:
                controlPresenter.setVideo_model(0x09);
                model_info = Model_info.video_ptz_ir;
                break;
            case R.id.video_ptz_ir_front:
                controlPresenter.setVideo_model(0x08);
                model_info = Model_info.video_ptz_ir_front;
                break;
            case R.id.video_ptz_front_ir_rear:
                controlPresenter.setVideo_model(0x06);
                model_info = Model_info.video_ptz_front_ir_rear;
                break;
            case R.id.video_ptz_front_ir:
                controlPresenter.setVideo_model(0x07);
                model_info = Model_info.video_ptz_front_ir;
                break;
            case R.id.video_ptz_ir_v:
                controlPresenter.setVideo_model(0x0a);
                model_info = Model_info.video_ptz_ir_v;
                break;
        }

        showAndHideBoom();
    }

    //灯光popuewindow设置
    private void setLight() {

        PwLightSpeed ls = new PwLightSpeed(this, R.layout.ppw_light_control);
        ls.setMax(0x0a, 0x0a, 0xff);
        ls.setCurrentProgress(foward_light_num, back_light_num, ptz_light_num);
        ls.setOnSeekBarStopTouchListener(this);
        log("灯光！！！！");
//        ls.setLightWhich(model_info);
    }


    //速度popuewindow设置
    private void setSpeed() {

        PwLightSpeed ls = new PwLightSpeed(this, R.layout.ppw_speed_control);
        ls.setMax(5000, 5000);
        ls.setCurrentSpeedProgress(car_speed_num, swing_speed_num, car_turnSpeed_num);
        ls.setOnSeekBarStopTouchListener(this);
    }

    //seekbar变动监听
    @Override
    public void setSeekBarChanged(int progress, int id) {
        sb_progressChange_processor(id, progress);
    }

    private void sb_progressChange_processor(int id, int i) {
        switch (id) {
            case R.id.foward_light_seekBar:
                foward_light_num = i;

                if (i == 0) {
                    controlPresenter.close_foward_light();
                } else {
                    controlPresenter.open_foward_light(foward_light_num);
                }
                log("foward_light: " + i);
                break;
            case R.id.back_light_seekBar:
                back_light_num = i;
                if (i == 0) {
                    controlPresenter.close_back_light();
                } else {
                    controlPresenter.open_back_light(back_light_num);
                }
                log("back_light: " + i);
                break;
            case R.id.ptz_light_seekBar:
                ptz_light_num = i;
                if (i == 0) {
                    controlPresenter.set_ptz_closeLight();
                } else {
                    controlPresenter.set_ptz_openLight(ptz_light_num);
                }
                log("ptz_light: " + i);
                break;
            case R.id.car_speed:
                car_speed_num = i;
                controlPresenter.setCarSpeed(car_speed_num);
                log("car_speed: " + i);
                break;
            case R.id.swing_speed:
                swing_speed_num = i;
                controlPresenter.setSwingSpeed(swing_speed_num);
                swingMoveRollBtn.setMaxProgress(swing_speed_num);
                log("swing_speed: " + i);
                break;
            case R.id.car_turnSpeed:
                car_turnSpeed_num = i;
                controlPresenter.moveTurnSpeed(car_turnSpeed_num);
                break;
        }
    }

    //摄像模式dialog的radiobutton监听
    @Override
    public void onCheckedChanged(MutiRadioGroup group, int checkedId) {
        camera_model_processor(checkedId);
    }


    //聚焦以及变倍的显示以及隐藏设置
    private void showAndHideBoom() {

        if (model_info == Model_info.video_ptz ||
                model_info == Model_info.video_front ||
                model_info == Model_info.video_rear ||
                model_info == Model_info.video_ir ||
                model_info == Model_info.video_ptz_ir_front) {
//            previewAim.setVisibility(View.VISIBLE);
        } else {
//            previewAim.setVisibility(View.INVISIBLE);
        }
    }


    //信息显示
    @Override
    public void ilog(String msg) {
        log(msg);
    }

    //错误显示
    @Override
    public void ierror(String msg) {
        error(msg);
    }

    //显示
    @Override
    public void iToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast(msg);
            }
        });
    }

    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            if (key_back == 1) {
                finish();
            }
            key_back++;
            toast("再按一次返回键退出！");
            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {


        return super.onKeyUp(keyCode, event);
    }
}
