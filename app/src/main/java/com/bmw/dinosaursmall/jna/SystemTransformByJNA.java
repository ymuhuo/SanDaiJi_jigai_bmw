package com.bmw.dinosaursmall.jna;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2017/7/25.
 */

public interface SystemTransformByJNA extends Library {


    public static IntByReference handle = new IntByReference();
    public static IntByReference file_handle = new IntByReference();

    public static SystemTransformByJNA.PLAYSDK_MEDIA_INFO.ByReference media_info = new SystemTransformByJNA.PLAYSDK_MEDIA_INFO.ByReference();


    int SYSTRANS_Create(IntByReference pointerByReference, SYS_TRANS_PARA sys_trans_para);

    int SYSTRANS_CreateEx(IntByReference phTrans, int eType, SYS_TRANS_PARA pstInfo);

    long SYSTRANS_OpenStreamAdvanced(IntByReference phTrans, int nProtocolType, SYS_TRANS_SESSION_PARA pstTransSessionInfo);

    int SYSTRANS_RegisterOutputDataCallBack(int hTrans, OutputDataCallBack outputDataCallBack, int pUser);

    int SYSTRANS_Start(int hTrans, String szSrcPath, String szTgtPath);

    int SYSTRANS_InputData(int hTrans, int enType, byte[] pData, int dwDataLen);

    int SYSTRANS_Stop(int hTrans);

    int SYSTRANS_Release(int hTrans);

    int SYSTRANS_GetTransPercent(int hTrans, int pdwPercent);


    public static interface OutputDataCallBack extends Callback {
       void outputDataCallBack(OUTPUTDATA_INFO outputdata_info, int pUser);
    }


    public static class SYS_TRANS_SESSION_PARA extends Structure {

        public int nSessionInfoType;
        public  int nSessionInfoLen;
        public byte[] pSessionInfoData;
        public int eTgtType;
        public int nTgtPackSize;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("nSessionInfoType","nSessionInfoLen","pSessionInfoData","eTgtType","nTgtPackSize");
        }
    }


    public static class OUTPUTDATA_INFO extends Structure {
        public byte[] pData;
        public int dwDataLen;
        public int dwDataType;
        public int dwFlag;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("pData","dwDataLen","dwDataType","dwFlag");
        }
    }


    public static class SYSTEM_TYPE extends Structure {
        public int TRANS_SYSTEM_NULL;  // ES
        public int TRANS_SYSTEM_HIK; //海康文件层，可用于传输和存储
        public int TRANS_SYSTEM_MPEG2_PS;  //PS文件层，主要用于存储，也可用于传输
        public int TRANS_SYSTEM_MPEG2_TS;  //TS文件层，主要用于传输，也可用于存储
        public int TRANS_SYSTEM_RTP; //RTP文件层，用于传输
        public int TRANS_SYSTEM_MPEG4;  //MPEG4文件层，用于存储（索引后置方式）
        public int TRANS_SYSTEM_AVI; //AVI文件层，用于存储
        public int TRANS_SYSTEM_GB_PS;  //国标PS，主要用于国标场合
        public int TRANS_SYSTEM_HLS_TS;  //符合HLS的TS封装，区分普通TS封装
        public int TRANS_SYSTEM_FLV; //FLV封装
        public int TRANS_SYSTEM_RAW; //es流前有参数信息的裸码流(源封装)（很老的版本，现在不推荐使用）
        public int TRANS_SYSTEM_MPEG4_FRONT;//MPEG4文件层（索引前置方式）

        public SYSTEM_TYPE() {
            TRANS_SYSTEM_NULL = 0x0;  // ES
            TRANS_SYSTEM_HIK = 0x1;  //海康文件层，可用于传输和存储
            TRANS_SYSTEM_MPEG2_PS = 0x2; //PS文件层，主要用于存储，也可用于传输
            TRANS_SYSTEM_MPEG2_TS = 0x3;  //TS文件层，主要用于传输，也可用于存储
            TRANS_SYSTEM_RTP = 0x4; //RTP文件层，用于传输
            TRANS_SYSTEM_MPEG4 = 0x5;  //MPEG4文件层，用于存储（索引后置方式）
            TRANS_SYSTEM_AVI = 0x7;  //AVI文件层，用于存储
            TRANS_SYSTEM_GB_PS = 0x8; //国标PS，主要用于国标场合
            TRANS_SYSTEM_HLS_TS = 0x9; //符合HLS的TS封装，区分普通TS封装
            TRANS_SYSTEM_FLV = 0x0a; //FLV封装
            TRANS_SYSTEM_RAW = 0x10; //es流前有参数信息的裸码流(源封装)（很老的版本，现在不推荐使用）
            TRANS_SYSTEM_MPEG4_FRONT = 0x0b;//MPEG4文件层（索引前置方式）
        }

        public static class ByReference extends SYSTEM_TYPE implements Structure.ByReference {
        }

        public static class ByValue extends SYSTEM_TYPE implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList();
        }
    }

    public static class PLAYSDK_MEDIA_INFO extends Structure {

        public int media_fourcc;            // "HKMI": 0x484B4D49 Hikvision Media Information
        public short media_version;            // 版本号：指本信息结构版本号，目前为0x0101,即1.01版本，01：主版本号；01：子版本号。
        public short device_id;                // 设备ID，便于跟踪/分析

        public short system_format;          // 系统封装层封装对应值(10进制):0-raw, 1-hik, 2-ps, 3-ts, 4-rtp, 5-mp4, 6-asf, 7-avi, 8-GB_ps, 9-hls_ts, 10-flv, 11-mp4_front, 13-rtmp
        public short video_format;           // 视频编码类型视频编码类型: 0x0001-hik264, 0x0002-mpeg2, 0x0003-mpeg4, 0x0004-mjpeg, 0x0005-h265, 0x0006-svac, 0x0100-h264

        public short audio_format;           // 音频编码类型
        public char audio_channels;         // 通道数
        public char audio_bits_per_sample;  // 样位率
        public int audio_samplesrate;      // 采样率
        public int audio_bitrate;          // 压缩音频码率,单位：bit

        public int[] reserved = new int[4];            // 保留

        public static class ByReference extends PLAYSDK_MEDIA_INFO implements Structure.ByReference {
        }

        public static class ByValue extends PLAYSDK_MEDIA_INFO implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("media_fourcc","media_version","device_id"
                    ,"system_format","video_format","audio_format"
                    ,"audio_channels","audio_bits_per_sample","audio_samplesrate"
                    ,"audio_bitrate","reserved");
        }
    }


    public static class SYS_TRANS_PARA extends Structure {

        public PLAYSDK_MEDIA_INFO.ByReference pSrcInfo;           //海康设备出的媒体信息头（源码流信息）
        public int dwSrcInfoLen;       //当前固定为40
        public int enTgtType;          //目标封装
        public int dwTgtPackSize;      //设置为0时，使用库内默认值。如果目标为RTP，PS/TS等封装格式时，设定每个包大小的上限。如果目标为AVI，设定最大帧长
        public int dwSrcDemuxSize;     //暂用于RTMP格式：设置无效时，使用库内默认值。
        public int dwAggregate;        //暂用于RTMP格式：设置为0时，表示不使用聚合包，大于0时表示聚合包中视频Tag的数量。
        public int dwMessageStreamID;  //仅RTMP打包使用：服务器返回的MessageStreamID
        public int dwChunkStreamID;    //仅RTMP打包使用：预览设置为4，回放设置为5

        public static class ByReference extends SYS_TRANS_PARA implements Structure.ByReference {
        }

        public static class ByValue extends SYS_TRANS_PARA implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("pSrcInfo","dwSrcInfoLen"
                    ,"enTgtType","dwTgtPackSize"
                    ,"dwSrcDemuxSize","dwAggregate"
                    ,"dwMessageStreamID","dwChunkStreamID"
            );
        }
    }

}

