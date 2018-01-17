package com.bmw.dinosaursmall.presenter.impl;

import com.bmw.dinosaursmall.jna.HCNetSDKJNAInstance;
import com.bmw.dinosaursmall.model.All_id_Info;
import com.bmw.dinosaursmall.presenter.VideoCutPresenter;
import com.bmw.dinosaursmall.utils.UrlUtil;
import com.bmw.dinosaursmall.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by admin on 2016/9/30.
 */
public class VideoCutPresentImpl implements VideoCutPresenter {

    private PreviewImpl preview;
    private boolean isRecord;
    private All_id_Info all_id_info;

    public VideoCutPresentImpl(PreviewImpl preview) {
        this.preview = preview;
        all_id_info = All_id_Info.getInstance();
        pathIsExist();
    }

    @Override
    public void record() {
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (!isRecord) {
            int clibrary = HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID, 2, UrlUtil.getLocal_video_path() + UrlUtil.getFileName() + ".mp4");
            if (clibrary == 0) {
                preview.ierror("NET_DVR_SaveRealData failed! error: "
                        + HCNetSDK.getInstance().NET_DVR_GetLastError());
                preview.record(0, false);
                return;
            } else {
                preview.ilog("NET_DVR_SaveRealData succ!");
                preview.record(0, true);
            }
            /*
            if (!HCNetSDK.getInstance().NET_DVR_SaveRealData(m_iPlayID,
                    UrlUtil.getLocal_video_path()+UrlUtil.getFileName()+".avi")) {
               preview.ierror("NET_DVR_SaveRealData failed! error: "
                        + HCNetSDK.getInstance().NET_DVR_GetLastError());
                preview.record(0,false);
                return;
            } else {
                preview.ilog("NET_DVR_SaveRealData succ!");
                preview.record(0,true);
            }*/
            isRecord = true;
        } else {
            if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID)) {
                preview.ierror("NET_DVR_StopSaveRealData failed! error: "
                        + HCNetSDK.getInstance()
                        .NET_DVR_GetLastError());
                preview.record(1, false);
            } else {
                preview.ilog("NET_DVR_StopSaveRealData succ!");
                preview.record(1, true);
            }
            isRecord = false;
        }
    }

    @Override
    public void capture() {
        try {
            int m_iPort = all_id_info.getM_iPort();
            if (m_iPort < 0) {
                preview.ierror("please start preview first");
                preview.iToast("截图失败！");
                return;
            }
            Player.MPInteger stWidth = new Player.MPInteger();
            Player.MPInteger stHeight = new Player.MPInteger();
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth,
                    stHeight)) {
                preview.ierror("getPictureSize failed with error code:"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            Player.MPInteger stSize = new Player.MPInteger();
            if (!Player.getInstance()
                    .getJPEG(m_iPort, picBuf, nSize, stSize)) {
                preview.ierror("getBMP failed with error code:"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }

            String path = UrlUtil.getLocal_picture_path()
                    + UrlUtil.getFileName() + ".jpg";
            FileOutputStream file = new FileOutputStream(path);
            file.write(picBuf, 0, stSize.value);
            file.close();
            preview.capture(path);
//            preview.iToast("截图");
        } catch (Exception err) {
            preview.ierror("error: " + err.toString());
        }
    }

    /**
     * 路径是否存在，不能存在则创建
     */
    private void pathIsExist() {
        File file = new File(UrlUtil.getLocal_video_path());
        if (!file.exists())
            file.mkdirs();

        File file1 = new File(UrlUtil.getLocal_picture_path());
        if (!file1.exists())
            file1.mkdirs();
    }

}
