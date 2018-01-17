package com.bmw.dinosaursmall.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2016/9/29.
 */
public class LoginInfo {
    public static final String ALL_URL_INFOMATION = "ALL_URL_INFOMATION";
    public static final String HAIKANG_IP = "HAIKANG_IP";
    public static final String HAIKANG_PORT = "HAIKANG_PORT";
    public static final String HAIKANG_ACCOUNT = "HAIKANG_ACCOUNT";
    public static final String HAIKANG_PASSWORD = "HAIKANG_PASSWORD";
    public static final String HAIKANG_BUFFER = "HAIKANG_BUFFER";
    public static final String SOCKET_IP = "SOCKET_IP";
    public static final String SOCKET_PORT = "SOCKET_PORT";
    public static final String HARDDECODE = "HARDDECODE";
    public static final String CONTROL_ADDR = "CONTROL_ADDR";




    public static final String BASIC_IP = "172.169.10.65";
    public static final int BASIC_PORT = 8000;
    public static final String BASIC_ACCOUNT = "admin";
    public static final String BASIC_PASSWORD = "bmw12345";
    public static final int BASIC_BUFFER = 600;
    public static final String BASIC_SOCKET_IP = "172.169.10.7";
    public static final int BASIC_SOCKET_PORT = 20108;
    public static final boolean BASIC_HARDDECODE = false;
    public static final int BASIC_CONTROL_ADDR = 1;


    private String ip;
    private int port;
    private String account;
    private String password;
    private String socket_ip;
    private int socket_port;
    private int stream_buf;
    private boolean isHardDecode;
    private int control_addr;
    private boolean isKeepSendMoveStop = true;
    private boolean isKeepSendPtzStop = true;
    private boolean isKeepSendTurnStop = true;

    private SharedPreferences sharedPreferences;
    private static LoginInfo instance = null;

    private LoginInfo() {
    }
    public static LoginInfo getInstance(){
        if(instance == null){
            synchronized (All_id_Info.class){
                if(instance == null)
                    instance = new LoginInfo();
            }
        }
        return instance;
    }



    public void  initLoginInfo(Context context) {
        sharedPreferences = context.getSharedPreferences(ALL_URL_INFOMATION, Context.MODE_PRIVATE);
        initData();
    }

    private void initData() {
        ip = sharedPreferences.getString(HAIKANG_IP, BASIC_IP);
        port = sharedPreferences.getInt(HAIKANG_PORT, BASIC_PORT);
        account = sharedPreferences.getString(HAIKANG_ACCOUNT, BASIC_ACCOUNT);
        password = sharedPreferences.getString(HAIKANG_PASSWORD, BASIC_PASSWORD);
        socket_ip = sharedPreferences.getString(SOCKET_IP, BASIC_SOCKET_IP);
        socket_port = sharedPreferences.getInt(SOCKET_PORT, BASIC_SOCKET_PORT);
        stream_buf = sharedPreferences.getInt(HAIKANG_BUFFER,BASIC_BUFFER);
        isHardDecode = sharedPreferences.getBoolean(HARDDECODE,BASIC_HARDDECODE);
        control_addr = sharedPreferences.getInt(CONTROL_ADDR,BASIC_CONTROL_ADDR);

    }

    public void setAll_UrlData(String sIp, int sPort, String sAccount, String sPassword, String sSocket_ip, int sSocket_port,int sStream_buf,boolean is_SHarddecode,int sControl_addr) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HAIKANG_IP, sIp);
        editor.putInt(HAIKANG_PORT, sPort);
        editor.putString(HAIKANG_ACCOUNT, sAccount);
        editor.putString(HAIKANG_PASSWORD, sPassword);
        editor.putString(SOCKET_IP, sSocket_ip);
        editor.putInt(SOCKET_PORT, sSocket_port);
        editor.putInt(HAIKANG_BUFFER,sStream_buf);
        editor.putBoolean(HARDDECODE,is_SHarddecode);
        editor.putInt(CONTROL_ADDR,sControl_addr);
        editor.commit();
        editor.clear();
        initData();
    }


    public boolean isKeepSendMoveStop() {
        return isKeepSendMoveStop;
    }

    public void setKeepSendMoveStop(boolean keepSendMoveStop) {
        isKeepSendMoveStop = keepSendMoveStop;
    }

    public boolean isKeepSendPtzStop() {
        return isKeepSendPtzStop;
    }

    public void setKeepSendPtzStop(boolean keepSendPtzStop) {
        isKeepSendPtzStop = keepSendPtzStop;
    }

    public boolean isKeepSendTurnStop() {
        return isKeepSendTurnStop;
    }

    public void setKeepSendTurnStop(boolean keepSendTurnStop) {
        isKeepSendTurnStop = keepSendTurnStop;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSocket_ip() {
        return socket_ip;
    }

    public void setSocket_ip(String socket_ip) {
        this.socket_ip = socket_ip;
    }

    public int getSocket_port() {
        return socket_port;
    }

    public void setSocket_port(int socket_port) {
        this.socket_port = socket_port;
    }

    public int getStream_buf() {
        return stream_buf;
    }

    public boolean isHardDecode() {
        return isHardDecode;
    }

    public int getControl_addr() {
        return control_addr;
    }
}
