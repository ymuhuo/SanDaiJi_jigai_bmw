package com.bmw.dinosaursmall.view.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();
    public static String TAG = "dinosaur_debug";
//    private static   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss::SSS");
    private static   SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss::SSS");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public static void log(String msg) {
        Log.i(TAG,"--------------------------------------"+sdf.format(System.currentTimeMillis())+"-----------------------------------------");
        Log.i(TAG, msg);
    }

    public static void error(String msg){
        Log.e(TAG,"======================================"+sdf.format(System.currentTimeMillis())+"=========================================");
        Log.e(TAG, msg);
    }

    /**
     * 请求权限
     * @param id 请求授权的id 唯一标识即可
     * @param permission 请求的权限
     * @param allowableRunnable 同意授权后的操作
     * @param disallowableRunnable 禁止权限后的操作
     */
    protected void requestPermission(int id, String permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }

        allowablePermissionRunnables.put(id, allowableRunnable);
        if (disallowableRunnable != null) {
            disallowablePermissionRunnables.put(id, disallowableRunnable);
        }

        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, id);
                return;
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Runnable allowRun = allowablePermissionRunnables.get(requestCode);
            allowRun.run();
        } else {
            Runnable disallowRun = disallowablePermissionRunnables.get(requestCode);
            disallowRun.run();
        }
    }
}
