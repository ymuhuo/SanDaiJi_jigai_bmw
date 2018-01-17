package com.bmw.dinosaursmall.jna;

import com.sun.jna.Native;

/**
 * Created by admin on 2017/7/25.
 */

public enum SystemTransformJNAInstance {


    CLASS;
    private static SystemTransformByJNA transform = null;
    /**
     * get the instance of HCNetSDK
     * @return the instance of HCNetSDK
     */
    public static SystemTransformByJNA getInstance()
    {
        if (null == transform)
        {
            synchronized (HCNetSDKByJNA.class)
            {
                transform = (SystemTransformByJNA) Native.loadLibrary("SystemTransform",
                        SystemTransformByJNA.class);
            }
        }
        return transform;
    }
}
