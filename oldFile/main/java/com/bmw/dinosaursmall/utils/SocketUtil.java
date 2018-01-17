package com.bmw.dinosaursmall.utils;

import android.content.Context;

import com.bmw.dinosaursmall.model.LoginInfo;
import com.bmw.dinosaursmall.presenter.SocketReaderListener;
import com.bmw.dinosaursmall.utils.singleThreadUtil.FinalizableDelegatedExecutorService;
import com.bmw.dinosaursmall.utils.singleThreadUtil.RunnablePriority;
import com.bmw.dinosaursmall.view.viewImpl.PreviewImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016/9/19.
 */
public class SocketUtil {

    private PreviewImpl preview;
    private Socket socket;
    private OutputStream socketWriter;
    private InputStream socketReader;
    private boolean isFinish;
    private int digui_num;
    private LoginInfo loginInfo;
    public ExecutorService singleThreadExecutor = new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>()));
    private long current_time;
    private String lastActionName;
    private byte[] lastCommand;

//    private int all_reader_num;
//    private ExecutorService singleThreadExecutor = null;


    public SocketUtil(PreviewImpl preview, Context context) {
        this.preview = preview;
        loginInfo = LoginInfo.getInstance();
        initSocket();
//        singleThreadExecutor = Executors.newSingleThreadExecutor();

    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //初始化socket
    public void initSocket() {
        isFinish = true;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                int i = 1;
                while (isFinish && socket == null) {
                    try {

                        socket = new Socket(loginInfo.getSocket_ip(), loginInfo.getSocket_port());
                        preview.ilog("connect: " + socket.isBound());
                        socket.setSoTimeout(100);
                        socketWriter = socket.getOutputStream();
                        socketReader = socket.getInputStream();
                        preview.ilog("run: socket 连接成功!");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (socket == null)
                        try {
                            preview.ierror("run: 第 " + i + " 次socket连接失败");
                            Thread.sleep(1000 * 10);
                            i++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            }

        };
        thread.start();
    }

    //发送命令（控制方向、速度的命令）
    public void sendcmd(byte[] commands, String action_name) {
        if (socket == null) {
            preview.ierror("发送" + action_name + "命令失败：sendcmd: socket is null");
            return;
        }
        if (socketWriter == null)
            try {
                socketWriter = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            socketWriter.write(commands);
            socketWriter.flush();
            preview.ilog("sendcmd: 已经发送 " + action_name + " 命令！");
        } catch (IOException e) {
            if (socket != null && !socket.isClosed()) {
                preview.ierror("socketService is already closed!  ");
                preview.isConnect(false);
                socket = null;
            }
            e.printStackTrace();
        }
    }

    public void release() {
        isFinish = false;
        if (socket != null) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socketReader.close();
                socketWriter.close();
                if (socket != null)
                    socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        preview.ilog("release: Socket已经释放内存！");
    }

    public void threadShutdown() {
        singleThreadExecutor.shutdownNow();
    }

    public void getReader(final byte[] commands,
                          final byte[] result,
                          final SocketReaderListener listener,
                          final int priority,
                          final String action_name,
                          final int which) {
        if (socket == null) {
            preview.ierror("发送" + action_name + "命令失败：sendcmd: socket is null");
            return;
        }
        if (socketReader == null)
            try {
                socketReader = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (socket != null)
            singleThreadExecutor.execute(new RunnablePriority(priority) {
                @Override
                public void run() {
                    if (which == 1)
                        readAll();
                    while (action_name.contains("停止") && System.currentTimeMillis() - current_time <= 150) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    sendcmd(commands, action_name);
                    if(action_name.contains("停止") ){
                        sleep(50);
                    }

                    if (which == 1) {
                        if(lastActionName != null && lastActionName.contains("停止")){
                            sendcmd(lastCommand,lastActionName);
                        }
                        sleep(140);
                        try {
                            socketReader.read(result);
                            listener.Result(result);
                            preview.ilog("run: 已经接收 " + action_name + " 命令： " + Integer.toHexString(result[2] & 0xff));
                            digui_num = 0;
                        } catch (SocketTimeoutException e) {
                            preview.ierror("接收" + action_name + "数据超时！");
//                            sleep(100);
                            e.printStackTrace();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    current_time = System.currentTimeMillis();
                    lastActionName = action_name;
                    lastCommand = commands;
                }
            });
    }


    public void readAll() {
        int len;
        byte[] bytes = new byte[1];
        try {
            int i = 0;
            while ((len = socketReader.read(bytes)) != -1) {
                preview.ilog("正在清空流数据：byte[" + i + "] = " + Integer.toHexString(bytes[0] & 0xff));
                i++;
            }
        } catch (SocketTimeoutException e) {
            preview.ilog("流数据清空完成！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

