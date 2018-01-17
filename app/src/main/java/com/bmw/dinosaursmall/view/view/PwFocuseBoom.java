package com.bmw.dinosaursmall.view.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bmw.dinosaursmall.R;


/**
 * Created by yMuhuo on 2017/1/9.
 */
public class PwFocuseBoom {

    private final PopupWindow popupWindow;
    private  ImageView b_add;
    private  ImageView b_sub;

    public PwFocuseBoom(Context context , int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popue_anim_bf);

        if(layoutId == R.layout.ppw_focus) {
            b_add = (ImageView) view.findViewById(R.id.add);
            b_sub = (ImageView) view.findViewById(R.id.sub);
        }else {
            b_add = (ImageView) view.findViewById(R.id.boom_add);
            b_sub = (ImageView) view.findViewById(R.id.boom_sub);
        }

    }

    public void show(TextView textView,int width, int height){
        if (textView == null)
            return;
        popupWindow.showAsDropDown(textView,width,height);
    }

    public void setOnTouchListener(View.OnTouchListener listener){
        if(listener == null)
            return;
        b_add.setOnTouchListener(listener);
        b_sub.setOnTouchListener(listener);
    }

    public void setOnDismissLayout(final LinearLayout layout, final Animation appear){
        if(layout == null && appear == null)
            return;
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                layout.startAnimation(appear);
                layout.setVisibility(View.VISIBLE);
            }
        });
    }
}
