package com.bmw.dinosaursmall.presenter.impl;

import android.content.Context;
import android.view.SurfaceView;

import com.bmw.dinosaursmall.model.All_id_Info;
import com.bmw.dinosaursmall.model.LoginInfo;
import com.bmw.dinosaursmall.presenter.HCNetSdkLogin;
import com.bmw.dinosaursmall.presenter.PreviewPresenter;
import com.bmw.dinosaursmall.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2016/9/28.
 */
public class HCNetSdkLoginImpl implements HCNetSdkLogin {

    private PreviewImpl viewImpl;
    private All_id_Info all_id_info;
    private PreviewPresenter previewPresenter;
    private boolean isGoLogin,isfirst;
    private LoginInfo loginInfo;
    private ExecutorService cachedThreadPool;

    public HCNetSdkLoginImpl(Context context, PreviewImpl viewImpl, SurfaceView surfaceView) {
        this.viewImpl = viewImpl;
        loginInfo = LoginInfo.getInstance();
        all_id_info = All_id_Info.getInstance();
        cachedThreadPool = Executors.newCachedThreadPool();
        initSDK();
        previewPresenter = new PreviewPresentImpl(context, viewImpl, surfaceView);
        previewPresenter.surfaceAddCallback();
        connectDevice();

    }



    private void initSDK() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            viewImpl.ierror("HCNetSDK init is failed!");

            viewImpl.stop();
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",
                true);
        viewImpl.ilog("HCNetSDK init is success!");
    }


    public void login() {
        int m_iLogID = loginNormalDevice();
        if (m_iLogID < 0) {
            viewImpl.iToast("登录失败！请检查网络以及配置信息是否正确！");
            return;
        }
        viewImpl.ilog("登录成功！");
        viewImpl.isConnect(true);
        all_id_info.setM_iLogID(m_iLogID);
    }

    @Override
    public void logout() {
        previewPresenter.stopSingle();
        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(all_id_info.getM_iLogID())) {
            viewImpl.ierror(" 退出登录失败!");
            return;
        }
        viewImpl.ilog("退出登录成功!");
        all_id_info.setM_iLogID(-1);
        all_id_info.resetData();
    }

    @Override
    public void connectDevice() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (All_id_Info.getInstance().getM_iLogID() < 0 ) {
                    login();
                    previewPresenter.startSingle();
                    try {
                        Thread.sleep(1000 * 15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    viewImpl.ilog("已经登录过，不需重复登录！");
                    viewImpl.isConnect(true);
                }
            }
        });
    }

    @Override
    public void release() {
        // release net SDK resource
        isGoLogin = false;
        logout();
        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }

    private int loginNormalDevice() {

        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            viewImpl.ierror("HKNetDvrDeviceInfoV30对象创建失败!");
            return -1;
        }

        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(loginInfo.getIp(), loginInfo.getPort(),
                loginInfo.getAccount(), loginInfo.getPassword(), m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            viewImpl.ierror("登录失败!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError() + " " + iLogID);
            return -1;
        }

        all_id_info.setM_oNetDvrDeviceInfoV30(m_oNetDvrDeviceInfoV30);

        return iLogID;
    }
}
