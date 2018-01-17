package com.bmw.dinosaursmall.utils;

import java.io.IOException;

/**
 * Created by yMuhuo on 2017/1/6.
 */
public class NetUtil {

    public static int pingHost(String str){
        int resault= -1;
        try {
            // TODO: Hardcoded for now, make it UI configurable
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 3 " +str);
            int status = p.waitFor();
            if (status == 0) {
                //  mTextView.setText("success") ;
                resault= 0;
            }
            else
            {
                resault= 1;
                //  mTextView.setText("fail");
            }
        } catch (IOException e) {
            //  mTextView.setText("Fail: IOException"+"\n");
        } catch (InterruptedException e) {
            //  mTextView.setText("Fail: InterruptedException"+"\n");
        }

        return resault;
    }

}
