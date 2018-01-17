package com.bmw.dinosaursmall.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.bmw.dinosaursmall.model.All_id_Info;
import com.bmw.dinosaursmall.model.LoginInfo;
import com.bmw.dinosaursmall.view.ui.MainActivity;
import com.bmw.dinosaursmall.view.view.PlaySurfaceView;
import com.bmw.dinosaursmall.presenter.PreviewPresenter;
import com.bmw.dinosaursmall.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;

/**
 * Created by admin on 2016/9/29.
 */
public class PreviewPresentImpl implements PreviewPresenter, SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private Context context;
    private PreviewImpl preview;
    private static PlaySurfaceView[] playView = new PlaySurfaceView[16];
    private All_id_Info all_id_info;
    private LoginInfo loginInfo;


    public PreviewPresentImpl(Context context, PreviewImpl preview, SurfaceView surfaceView) {
        this.context = context;
        loginInfo = LoginInfo.getInstance();
        this.surfaceView = surfaceView;
        this.preview = preview;
        all_id_info = All_id_Info.getInstance();
    }

    @Override
    public void surfaceAddCallback() {
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void startSingle() {
        int m_iLogID = all_id_info.getM_iLogID();
        if (m_iLogID < 0) {
            return;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null) {
            preview.ierror("PreviewPresentImpl: fRealDataCallBack object is failed!");
            return;
        }
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = all_id_info.getM_iStartChan();
        previewInfo.dwStreamType = 0; // substream
        previewInfo.bBlocked = 1;
//         NET_DVR_CLIENTINFO struClienInfo = new NET_DVR_CLIENTINFO();
//         struClienInfo.lChannel = m_iStartChan;
//         struClienInfo.lLinkMode = 0;
        // HCNetSDK start preview
        int m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID,
                previewInfo, fRealDataCallBack);
//         m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V30(m_iLogID,
//         struClienInfo, fRealDataCallBack, false);
        if (m_iPlayID < 0) {
            preview.ierror("PreviewPresentImpl: NET_DVR_RealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        all_id_info.setM_iPlayID(m_iPlayID);
        preview.ilog("PreviewPresentImpl: NetSdk Play sucess");
    }

    @Override
    public void stopSingle() {
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (m_iPlayID < 0) {
            preview.ierror("PreviewPresentImpl: stopSingle:m_iPlayID < 0");
            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            preview.ierror("PreviewPresentImpl: StopRealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        all_id_info.setM_iPlayID(-1);
        stopSinglePlayer();

        preview.ilog("退出预览成功！");
    }

    @Override
    public void startMulti() {
        int m_iLogID = all_id_info.getM_iLogID();
        if (m_iLogID < 0) {
            preview.ierror("PreviewPresentImpl: please login on device first");
            return;
        }
        int multi_chan_num = all_id_info.getMulti_chan_num();
        DisplayMetrics metric = new DisplayMetrics();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        int i = 0;
        for (i = 0; i < multi_chan_num; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(context);
                playView[i].setParam(metric.widthPixels);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView[i].getCurHeight() - (i / 2)
                        * playView[i].getCurHeight();
                params.leftMargin = (i % 2) * playView[i].getCurWidth();
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                ((Activity) context).addContentView(playView[i], params);
            }
            playView[i].startPreview(m_iLogID, all_id_info.getM_iStartChan() + i);
        }
        all_id_info.setM_iPlayID(playView[0].m_iPreviewHandle);
    }

    @Override
    public void stopMulti() {
        int i = 0;
        for (i = 0; i < all_id_info.getMulti_chan_num(); i++) {
            playView[i].stopPreview();
        }
        all_id_info.setM_iPlayID(-1);
    }


    @Override
    public void release() {

    }

    // @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        int m_iPort = all_id_info.getM_iPort();
        preview.ilog("PreviewPresentImpl: surface is created" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        Surface surface = holder.getSurface();
        if (true == surface.isValid()) {
            if (false == Player.getInstance()
                    .setVideoWindow(m_iPort, 0, holder)) {
                preview.ierror("PreviewPresentImpl: Player setVideoWindow failed!");
            }
        }
    }

    // @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    // @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        int m_iPort = all_id_info.getM_iPort();
        preview.ilog("PreviewPresentImpl: Player setVideoWindow release!" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        if (true == holder.getSurface().isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                preview.ierror("PreviewPresentImpl: Player setVideoWindow failed!");
            }
        }
    }

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType,
                                          byte[] pDataBuffer, int iDataSize) {
                // player channel 1
                processRealData(1, iDataType, pDataBuffer,
                        iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }


    public void processRealData(int iPlayViewNo, int iDataType,
                                byte[] pDataBuffer, int iDataSize, int iStreamMode) {

        int m_iPort = all_id_info.getM_iPort();

        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (m_iPort >= 0) {
                return;
            }
            m_iPort = Player.getInstance().getPort();
            if (m_iPort == -1) {
                preview.ierror("PreviewPresentImpl: getPort is failed with: "
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            preview.ilog("PreviewPresentImpl: getPort succ with: " + m_iPort);
            all_id_info.setM_iPort(m_iPort);
            if (iDataSize > 0) {
                if (!Player.getInstance().setStreamOpenMode(m_iPort,
                        iStreamMode)) // set stream mode
                {
                    preview.ilog("PreviewPresentImpl: setStreamOpenMode failed");
                    return;
                }
                if (!Player.getInstance().openStream(m_iPort, pDataBuffer,
                        iDataSize, loginInfo.getStream_buf() * 1024)) // open stream
                {
                    preview.ierror("PreviewPresentImpl: openStream failed");
                    return;
                }


                if (!Player.getInstance().setDisplayBuf(m_iPort, 1)) {
                    preview.ierror("海康：设置播放缓冲区最大缓冲帧数！" + Player.getInstance().getLastError(m_iPort));
                }

                preview.ilog("缓冲区剩余数据：" + Player.getInstance().getSourceBufferRemain(m_iPort));
                if (Player.getInstance().resetSourceBuffer(m_iPort)) {
                    preview.ilog("清空缓冲区所有剩余数据！");
                }
                if (loginInfo.isHardDecode()) {
                    if(Player.getInstance().setMaxHardDecodePort(1)){
                        preview.ilog("设置最大硬解码路数为16！");

                    }
                    if (Player.getInstance().setHardDecode(m_iPort, 1)) {
                        preview.ilog("启用硬解码优先！");
                    }
                }

//                if (Player.getInstance().setDisplayBuf(m_iPort, 30)) {
//
//                    preview.ilog("设置播放缓冲区最大缓冲帧数20帧！");
//                }
                if (!Player.getInstance().play(m_iPort,
                        surfaceView.getHolder())) {
                    preview.ierror("PreviewPresentImpl: play failed");
                    return;
                }
                if (!Player.getInstance().playSound(m_iPort)) {
                    preview.ierror("PreviewPresentImpl: playSound failed with ierror code:"
                            + Player.getInstance().getLastError(m_iPort));
                    return;
                }
            }
        } else {
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer,
                    iDataSize)) {
                // Log.e(TAG, "inputData failed with: " +
                // Player.getInstance().getLastError(m_iPort));
                for (int i = 0; i < 4000; i++) {
                    if (Player.getInstance().resetSourceBuffer(m_iPort)) {
                        preview.ilog("清空缓冲区所有剩余数据！");
                    }
                    if (Player.getInstance().inputData(m_iPort,
                            pDataBuffer, iDataSize)) {
                        break;
                    }

                    if (i % 100 == 0) {
                        preview.ierror("PreviewPresentImpl: inputData failed with: "
                                + Player.getInstance()
                                .getLastError(m_iPort) + ", i:" + i);
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                }
            }
        }

    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        int m_iPort = all_id_info.getM_iPort();
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            preview.ierror("PreviewPresentImpl: stopSinglePlayer is failed!");
            return;
        }

        if (!Player.getInstance().setHardDecode(m_iPort, 0)) {
            preview.ierror("PreviewPresentImpl: stopHardDecode is failed!");
            return;
        }

        if (!Player.getInstance().closeStream(m_iPort)) {
            preview.ierror("PreviewPresentImpl: stopSinglePlayer closeStream is failed!");
            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            preview.ierror("PreviewPresentImpl: stopSinglePlayer freePort is failed!" + m_iPort);
            return;
        }

        all_id_info.setM_iPort(-1);
    }
}
