package com.bmw.dinosaursmall.view.view;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.model.LoginInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SettingDialog implements View.OnClickListener {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private LoginInfo loginInfo;
    private EditText eIp, ePort, eAccount, ePassword, eSocket_ip, eSocket_port, eStream_buf,eControl_addr;
    private SwitchButton sHardDecode;
    private TextView sure, cancel, reset;
    private Context context;
    private String tMsg;
    private boolean isChoose;


    public SettingDialog(Context context) {


        this.context = context;
        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
        dialog.setView(new EditText(context));//实现弹出虚拟键盘
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
        window.setContentView(R.layout.dialog_setting);
        eIp = (EditText) window.findViewById(R.id.haikang_ip);
        ePort = (EditText) window.findViewById(R.id.haikang_port);
        ePassword = (EditText) window.findViewById(R.id.haikang_password);
        eAccount = (EditText) window.findViewById(R.id.haikang_account);
        eSocket_ip = (EditText) window.findViewById(R.id.socket_ip);
        eSocket_port = (EditText) window.findViewById(R.id.socket_port);
        sure = (TextView) window.findViewById(R.id.set_sure);
        cancel = (TextView) window.findViewById(R.id.set_cancel);
        reset = (TextView) window.findViewById(R.id.reset_setting);
        eStream_buf = (EditText) window.findViewById(R.id.haikang_buf);
        sHardDecode = (SwitchButton) window.findViewById(R.id.haikang_hardDecode);
        eControl_addr = (EditText) window.findViewById(R.id.control_addr);
        loginInfo =  LoginInfo.getInstance();
        eIp.setText(loginInfo.getIp());
        ePort.setText(loginInfo.getPort() + "");
        eAccount.setText(loginInfo.getAccount());
        ePassword.setText(loginInfo.getPassword());
        eSocket_ip.setText(loginInfo.getSocket_ip());
        eSocket_port.setText(loginInfo.getSocket_port() + "");
        eStream_buf.setText(loginInfo.getStream_buf() + "");
        eControl_addr.setText(loginInfo.getControl_addr()+"");
        isChoose = loginInfo.isHardDecode();
        sHardDecode.setChecked(isChoose);

        eIp.setSelection(loginInfo.getIp().length());
        ePort.setSelection((loginInfo.getPort() + "").length());
        eAccount.setSelection(loginInfo.getAccount().length());
        ePassword.setSelection(loginInfo.getPassword().length());
        eSocket_ip.setSelection(loginInfo.getSocket_ip().length());
        eSocket_port.setSelection((loginInfo.getSocket_port() + "").length());
        eStream_buf.setSelection((loginInfo.getStream_buf() + "").length());
        eControl_addr.setSelection((loginInfo.getControl_addr() + "").length());

        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
        reset.setOnClickListener(this);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                    listener.changeReporter(false);
                }
                return false;
            }
        });

        sHardDecode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChoose = b;
            }
        });
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        listener.changeReporter(false);
        dialog.dismiss();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_sure:
                sureProsessor();
                break;
            case R.id.set_cancel:
                dismiss();
                break;
            case R.id.reset_setting:
                resetData();
                break;
        }
    }

    private void resetData() {
        eIp.setText(loginInfo.BASIC_IP);
        ePort.setText(loginInfo.BASIC_PORT + "");
        eAccount.setText(loginInfo.BASIC_ACCOUNT);
        ePassword.setText(loginInfo.BASIC_PASSWORD);
        eSocket_ip.setText(loginInfo.BASIC_SOCKET_IP);
        eSocket_port.setText(loginInfo.BASIC_SOCKET_PORT + "");
        eStream_buf.setText(loginInfo.BASIC_BUFFER + "");
        sHardDecode.setChecked(loginInfo.BASIC_HARDDECODE);
        eControl_addr.setText(loginInfo.BASIC_CONTROL_ADDR+"");

        eIp.setSelection(loginInfo.BASIC_IP.length());
        ePort.setSelection((loginInfo.BASIC_PORT + "").length());
        eAccount.setSelection(loginInfo.BASIC_ACCOUNT.length());
        ePassword.setSelection(loginInfo.BASIC_PASSWORD.length());
        eSocket_ip.setSelection(loginInfo.BASIC_SOCKET_IP.length());
        eSocket_port.setSelection((loginInfo.BASIC_SOCKET_PORT + "").length());
        eStream_buf.setSelection((loginInfo.BASIC_BUFFER + "").length());
        eControl_addr.setSelection((loginInfo.BASIC_CONTROL_ADDR + "").length());
    }

    private void sureProsessor() {
        String tIp = eIp.getText().toString();
        String tPort = ePort.getText().toString();
        String tAccount = eAccount.getText().toString();
        String tPassword = ePassword.getText().toString();
        String tSocket_ip = eSocket_ip.getText().toString();
        String tSocket_port = eSocket_port.getText().toString();
        String tStream_buf = eStream_buf.getText().toString();
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        String tControl_addr = eControl_addr.getText().toString();
        Pattern p = Pattern.compile(regex);
        Matcher is_tIp = p.matcher(tIp);
        Matcher is_socketIp = p.matcher(tSocket_ip);
        if (!is_tIp.matches()) {
            tIp = loginInfo.BASIC_IP;
        }
        if (!is_socketIp.matches()) {
            tSocket_ip = loginInfo.BASIC_SOCKET_IP;
        }
        if (isInputRight(is_tIp.matches(), is_socketIp.matches(), tPort, tSocket_port)) {
            loginInfo.setAll_UrlData(tIp,
                    Integer.valueOf(tPort),
                    tAccount, tPassword,
                    tSocket_ip,
                    Integer.valueOf(tSocket_port),
                    Integer.valueOf(tStream_buf),
                    isChoose,Integer.valueOf(tControl_addr));
            if (listener != null)
                listener.changeReporter(true);
            dismiss();
        } else {
            Toast.makeText(context, tMsg + " 输入格式错误，请重新输入！", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInputRight(boolean isIP, boolean isSocketIp, String tPort, String tS_port) {
        if (eIp.getText().toString().equals("") || !isIP) {
            tMsg = "视频地址";
            return false;
        }
        int port = Integer.valueOf(tPort);
        if (tPort.equals("") || port <= 0 || port > 65535) {
            tMsg = "视频端口";
            return false;
        }
        if (eAccount.getText().toString().equals("")) {
            tMsg = "用户名";
            return false;
        }
        if (ePassword.getText().toString().equals("")) {
            tMsg = "密码";
            return false;
        }
        if (eStream_buf.getText().toString().equals("")) {
            tMsg = "播放缓存";
            return false;
        }
        if (eSocket_ip.getText().toString().equals("") || !isSocketIp) {
            tMsg = "控制地址";
            return false;
        }
        int socket_port = Integer.valueOf(tS_port);
        if (tS_port.equals("") || socket_port <= 0 || socket_port > 65535) {
            tMsg = "控制端口";
            return false;
        }
        if(eControl_addr.getText().toString().equals("")){
            tMsg = "数据地址";
            return false;
        }
        return true;
    }

    private OnSettingChangeListener listener;

    public void setOnSettingChangeListener(OnSettingChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSettingChangeListener {
        void changeReporter(boolean isChange);
    }


}