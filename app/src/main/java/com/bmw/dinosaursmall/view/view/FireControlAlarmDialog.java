package com.bmw.dinosaursmall.view.view;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.model.LoginInfo;


public class FireControlAlarmDialog {
    private AlertDialog dialog;
    private SwitchButton aSwitch;
    public FireControlAlarmDialog(Context context) {


        dialog = new AlertDialog.Builder(context).create();
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
//        dialog.setView(new EditText(context));//实现弹出虚拟键盘
        dialog.show();
        WindowManager manager = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
//p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels*0.5);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.dialog_fire_control_set);
        aSwitch = (SwitchButton) window.findViewById(R.id.fire_control_set_switch);

    }

    public void setOnSwitchChangedListener(CompoundButton.OnCheckedChangeListener listener){
        aSwitch.setOnCheckedChangeListener(listener);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }

}