package com.bmw.dinosaursmall.view.view;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.bmw.dinosaursmall.Constant;
import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.adapter.EnvironmentAdapter;
import com.bmw.dinosaursmall.model.Environment;
import com.bmw.dinosaursmall.model.EnvironmentInfo;

import java.util.ArrayList;
import java.util.List;


public class EnvironmentDialog {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private RecyclerView eRecyclerview;
    private EnvironmentAdapter adapter;
    private SharedPreferences sharedPreferences;


    public EnvironmentDialog(Context context) {


        sharedPreferences = context.getSharedPreferences(Environment.ENVIRONMENT_INFO,Context.MODE_PRIVATE);
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
        window.setContentView(R.layout.dialog_environment);
        eRecyclerview = (RecyclerView) window.findViewById(R.id.environment_recyclerview);
        init(context);
    }

    public AlertDialog getDialog(){
        return dialog;
    }

    public void init(Context context){
        Log.d(TAG, "init: getdatainit");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eRecyclerview.setLayoutManager(layoutManager);
        adapter = new EnvironmentAdapter(context);
        eRecyclerview.setAdapter(adapter);
        setAdapterData(new EnvironmentInfo());
    }

    public void setAdapterData(EnvironmentInfo environmentInfo){

        List<Environment> list = new ArrayList<>();
//        Environment CO = getSingleEnvironment("CO", sharedPreferences.getFloat(Environment.CO,0), 0, 30);
//        Environment CH4 = getSingleEnvironment("CH4", sharedPreferences.getFloat(Environment.CH4,0), 0, 10);
//        Environment O2 = getSingleEnvironment("O2", sharedPreferences.getFloat(Environment.O2,0), 0, 30);
//        Environment CO2 = getSingleEnvironment("CO2", sharedPreferences.getFloat(Environment.CO2,0), 0, 2000);
//        Environment Temparature = getSingleEnvironment("温度", sharedPreferences.getFloat(Environment.TEMPERATURE,0), -15, 100);
//        Environment Humidity = getSingleEnvironment("湿度", sharedPreferences.getFloat(Environment.HUMIDITY,0), 0, 100);


        Environment CO = getSingleEnvironment(Constant.CO, environmentInfo.getmCO(), 0, 10);
        Environment CH4 = getSingleEnvironment(Constant.CH4,environmentInfo.getmCH4(), 0, 5);
        Environment O2 = getSingleEnvironment(Constant.O2, environmentInfo.getmO2(), 0, 21);
        Environment CO2 = getSingleEnvironment(Constant.CO2, environmentInfo.getmCO2(), 0, 1000);
//        Environment Temparature = getSingleEnvironment("温度", environmentInfo.getmTEMPERATURE(), -15, 100);
//        Environment Humidity = getSingleEnvironment("湿度", environmentInfo.getmHUMIDITY(), 0, 100);

        list.add(CO2);
        list.add(CO);
        list.add(CH4);
        list.add(O2);
//        list.add(Temparature);
//        list.add(Humidity);
        adapter.setList(list);
        if (eRecyclerview.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                && !eRecyclerview.isComputingLayout()) {
            adapter.notifyDataSetChanged();
            Log.d(TAG, "setAdapterData: ");
        }
    }

    private Environment getSingleEnvironment(String name, float num, float min, float max) {
        Environment environment = new Environment();
        environment.setName(name);
        environment.setCurrent_num(num);
        environment.setMax_num(max);
        environment.setMin_num(min);
        return environment;
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }



}