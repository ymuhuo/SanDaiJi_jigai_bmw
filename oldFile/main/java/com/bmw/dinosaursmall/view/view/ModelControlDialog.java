package com.bmw.dinosaursmall.view.view;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.model.Model_info;


public class ModelControlDialog {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private MutiRadioGroup radioGroup;
    private Model_info model_info;
    private ImageButton confirm;
    private RadioButton video_four,video_front,video_rear,video_ir,video_ptz,video_ptz_ir,video_ptz_ir_front,video_ptz_front_ir_rear,video_ptz_front_ir,video_ptz_ir_v;

    public ModelControlDialog(Context context, Model_info model_info) {


        this.model_info = model_info;
        dialog = new AlertDialog.Builder(context).create();
//        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
        dialog.show();
        WindowManager manager = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
//        p.height = (int) (dm.heightPixels() * 0.3);   //高度设置为屏幕的0.3
//        p.width = (int) (dm.widthPixels);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

//        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.dialog_model_choose);
        radioGroup = (MutiRadioGroup) window.findViewById(R.id.model_radioGroup);
        getRadioButton(window);
        setGroup();

    }

    private void getRadioButton(Window window) {
        video_four = (RadioButton) window.findViewById(R.id.video_four);
        video_front = (RadioButton) window.findViewById(R.id.video_front);
        video_rear = (RadioButton) window.findViewById(R.id.video_rear);
        video_ir = (RadioButton) window.findViewById(R.id.video_ir);
        video_ptz = (RadioButton) window.findViewById(R.id.video_ptz);
        video_ptz_ir = (RadioButton) window.findViewById(R.id.video_ptz_ir);
        video_ptz_ir_front = (RadioButton) window.findViewById(R.id.video_ptz_ir_front);
        video_ptz_front_ir_rear = (RadioButton) window.findViewById(R.id.video_ptz_front_ir_rear);
        video_ptz_front_ir = (RadioButton) window.findViewById(R.id.video_ptz_front_ir);
        video_ptz_ir_v = (RadioButton) window.findViewById(R.id.video_ptz_ir_v);
    }


    private void setGroup() {
        switch (model_info){
            case video_four:
                video_four.setChecked(true);
                radioGroup.setCurrent_id(video_four);
                break;
            case video_front:
                video_front.setChecked(true);
                radioGroup.setCurrent_id(video_front);
                break;
            case video_rear:
                video_rear.setChecked(true);
                radioGroup.setCurrent_id(video_rear);
                break;
            case video_ptz:
                video_ptz.setChecked(true);
                radioGroup.setCurrent_id(video_ptz);
                break;
            case video_ir:
                video_ir.setChecked(true);
                radioGroup.setCurrent_id(video_ir);
                break;
            case video_ptz_ir:
                video_ptz_ir.setChecked(true);
                radioGroup.setCurrent_id(video_ptz_ir);
                break;
            case video_ptz_front_ir_rear:
                video_ptz_front_ir_rear.setChecked(true);
                radioGroup.setCurrent_id(video_ptz_front_ir_rear);
                break;
            case video_ptz_ir_front:
                video_ptz_ir_front.setChecked(true);
                radioGroup.setCurrent_id(video_ptz_ir_front);
                break;
            case video_ptz_front_ir:
                video_ptz_front_ir.setChecked(true);
                radioGroup.setCurrent_id(video_ptz_front_ir);
                break;
            case video_ptz_ir_v:
                video_ptz_ir_v.setChecked(true);
                radioGroup.setCurrent_id(video_ptz_ir_v);
                break;
           /* case video_front_ir:
                video_front_ir.setChecked(true);
                break;
            case video_front_rear:
                video_front_rear.setChecked(true);
                break;
            case video_front_ptz:
                video_front_ptz.setChecked(true);
                break;
            case video_ir_front:
                video_ir_front.setChecked(true);
                break;
            case video_ir_ptz:
                video_ir_ptz.setChecked(true);
                break;
            case video_ir_rear:
                video_ir_rear.setChecked(true);
                break;
            case video_ptz_front:
                video_ptz_front.setChecked(true);
                break;
            case video_ptz_rear:
                video_ptz_rear.setChecked(true);
                break;
            case video_rear_front:
                video_rear_front.setChecked(true);
                break;
            case video_rear_ir:
                video_rear_ir.setChecked(true);
                break;
            case video_rear_ptz:
                video_rear_ptz.setChecked(true);
                break;*/
        }
    }

    public void setOnclickListener(View.OnClickListener listener){
    }
    public void setOnCheckedChangeListener(MutiRadioGroup.OnCheckedChangeListener listener){
        radioGroup.setOnCheckedChangeListener(listener);
    }



    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }



}