package com.bmw.dinosaursmall.presenter.impl;

import com.bmw.dinosaursmall.jna.HCNetSDKJNAInstance;
import com.bmw.dinosaursmall.jna.SystemTransformByJNA;
import com.bmw.dinosaursmall.jna.SystemTransformJNAInstance;
import com.bmw.dinosaursmall.model.All_id_Info;
import com.bmw.dinosaursmall.presenter.VideoCutPresenter;
import com.bmw.dinosaursmall.utils.UrlUtil;
import com.bmw.dinosaursmall.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;

import static com.bmw.dinosaursmall.view.ui.BaseActivity.error;
import static com.bmw.dinosaursmall.view.ui.BaseActivity.log;

/**
 * Created by admin on 2016/9/30.
 */
public class VideoCutPresentImpl implements VideoCutPresenter {

    private PreviewImpl preview;
    private boolean isRecord;
    private All_id_Info all_id_info;
    private SystemTransformByJNA.SYS_TRANS_PARA sys_trans_para;

    public VideoCutPresentImpl(PreviewImpl preview) {
        this.preview = preview;
        all_id_info = All_id_Info.getInstance();
        pathIsExist();
        initStructure();
    }

    @Override
    public void record() {
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (!isRecord) {

            String desPath = UrlUtil.getLocal_video_path() + UrlUtil.getFileName() + ".avi";




            int create_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Create(SystemTransformByJNA.handle, sys_trans_para);
            if (create_state != 0) {
                startFailed();
                error("录像：创建句柄失败！错误码：" + Integer.toHexString(create_state));
                return;
            }

           /* SystemTransformByJNA.OutputDataCallBack outputDataCallBack = new FileTurnStreamTest.OutPutDataCallBack();
            Pointer  p = Pointer.NULL;
            int callback_state = SystemTransformJNAInstance.getInstance().SYSTRANS_RegisterOutputDataCallBack(SystemTransformByJNA.handle.getValue(), outputDataCallBack, p);
            if (callback_state != 0) {
                error("SYSTRANS_RegisterOutputDataCallBack error！" + Integer.toHexString(callback_state));
                return;
            }*/


            int start_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Start(SystemTransformByJNA.handle.getValue(), null, desPath);
            if (start_state != 0) {
                startFailed();
                error("录像：开始转换失败！错误码：" + Integer.toHexString(start_state));
                return;
            }

            all_id_info.setRecordPause(false);

            log("海康：抓拍：开始录制成功！");
            if (preview != null) {
                log("NET_DVR_SaveRealData succ!");
                preview.record(0, true);
            }

            isRecord = true;
        } else {
            all_id_info.setRecordPause(true);
//            WriteInfoUtil.getInstance().release();
            int stop_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
            int release_state = SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
            if (stop_state != 0 || release_state != 0) {
                error("海康：抓拍：结束录制失败！" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                if (preview != null) {

                    preview.record(1, false);
                }
            } else {
                log("海康：抓拍：结束录制成功！");
                if (preview != null) {
                    log("NET_DVR_StopSaveRealData succ!");
                    preview.record(1, true);
                }
            }
            isRecord = false;
        }
            /*
            int clibrary = HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID, 2,desPath );
            if (clibrary == 0) {
                preview.ierror("NET_DVR_SaveRealData failed! error: "
                        + HCNetSDK.getInstance().NET_DVR_GetLastError());
                preview.record(0, false);
                return;
            } else {
                preview.ilog("NET_DVR_SaveRealData succ!");
                preview.record(0, true);
            }
            *//*
            if (!HCNetSDK.getInstance().NET_DVR_SaveRealData(m_iPlayID,
                    UrlUtil.getLocal_video_path()+UrlUtil.getFileName()+".avi")) {
               preview.ierror("NET_DVR_SaveRealData failed! error: "
                        + HCNetSDK.getInstance().NET_DVR_GetLastError());
                preview.record(0,false);
                return;
            } else {
                preview.ilog("NET_DVR_SaveRealData succ!");
                preview.record(0,true);
            }*//*
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
        }*/
    }


    private void startFailed() {
        error("海康：抓拍：开始录制失败：" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        if (preview != null) {
            preview.record(0, false);
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



    public void initStructure() {

        sys_trans_para = new SystemTransformByJNA.SYS_TRANS_PARA();
        sys_trans_para.dwSrcInfoLen = 40;
        sys_trans_para.dwTgtPackSize = 8 * 1024;
        sys_trans_para.dwSrcDemuxSize = 0;
        sys_trans_para.enTgtType = 0x07;

        SystemTransformByJNA.PLAYSDK_MEDIA_INFO.ByReference media_info = SystemTransformByJNA.media_info;

        media_info.media_fourcc = 0x484B4D49;
        media_info.media_version = 0x0101;
        media_info.device_id = 0;
        media_info.audio_bits_per_sample = 16;                 //16bit
        media_info.audio_bitrate = 8000;
        media_info.system_format = 4;
        media_info.video_format = 0x0100;
        media_info.audio_format = 0x1000;
        media_info.audio_channels = 0;      //single channel
        media_info.audio_samplesrate = 0;
        media_info.write();
        sys_trans_para.pSrcInfo = media_info;
        sys_trans_para.write();


    }

    @Override
    public void release() {
//        SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
//        SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
        if (isRecord) {
            log("海康：抓拍：视图退出，停止录像");
//            preview = null;
            record();
        }
    }

}
