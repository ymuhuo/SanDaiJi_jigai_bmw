package com.bmw.dinosaursmall.utils;


import com.bmw.dinosaursmall.model.LoginInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2017/2/17.
 */

public class UdpSocketUtil {

    private DatagramSocket socket;
    private DatagramPacket datagramPacket;
    private DatagramPacket datagramPacketRead;
    private boolean isStop;
    private final int RESULT_COUNT = 50;
    private final byte[] stopMoveCommands = new byte[]{};
    private final byte[] stopPtzCommands = new byte[]{};
    private final byte[] stopTurnCommands = new byte[]{};

    public UdpSocketUtil() {

        read();
        sendStopThread();

    }

    public void socketLogin(String ip, int port, int myPort) {
        try {
            if (socket == null) {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(myPort));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (datagramPacket == null)
            try {
                InetAddress address = InetAddress.getByName(ip);
                datagramPacket = new DatagramPacket(new byte[1], 1, address, port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        else {
            try {
                InetAddress address = InetAddress.getByName(ip);
                datagramPacket.setAddress(address);
                datagramPacket.setPort(port);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        if (datagramPacketRead == null) {

            byte[] bytes = new byte[RESULT_COUNT];
            datagramPacketRead = new DatagramPacket(bytes, bytes.length);
        }
    }

    private void socketLoginOut() {
        if (socket != null) {
            socket.close();
        }
        socket = null;
    }


    private void initSocket() {
//        if (fixThreadPool != null) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket == null && !isStop) {
                    if (socket != null) {
//                            log("udp: socket连接成功");
                        break;
                    }
                }
            }
        }).start();
//        }
    }

    public void sendCommandInThread(final byte[] commands) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendCommand(commands);
                sleep(200);
            }
        }).start();

    }

    private void sendCommand(byte[] commands) {
        if (socket == null || datagramPacket == null) {
            return;
        }
        datagramPacket.setData(commands);
        datagramPacket.setLength(commands.length);
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStopThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    if (LoginInfo.getInstance().isKeepSendMoveStop())
                        sendCommand(stopMoveCommands);
                    sleep(200);
                    if (LoginInfo.getInstance().isKeepSendTurnStop())
                        sendCommand(stopTurnCommands);
                    sleep(200);
                    if (LoginInfo.getInstance().isKeepSendPtzStop())
                        sendCommand(stopPtzCommands);
                    sleep(200);
                }
            }
        }).start();
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void read() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    if (socket != null && datagramPacketRead != null) {
                        try {
//                            log("等待接收数据！");
//                            sleep(100);
                            socket.receive(datagramPacketRead);
                            byte[] bytesResult = datagramPacketRead.getData();

                            byte[] byteEndInfo = getRealByteResult(bytesResult);

                            if (isGetThrRightResult(byteEndInfo) && listener != null)
                                listener.result(byteEndInfo);

                        } catch (IOException e) {
//                            error("数据读取错误：Error：" + e.toString());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private byte[] getRealByteResult(byte[] bytesResult) {
        List<Byte> list = new ArrayList<>();
        boolean startGetData = false;
        for (int i = bytesResult.length - 1; i >= 0; i--) {

            if (startGetData = true) {
                list.add(bytesResult[i]);
            }

            if (!startGetData && bytesResult[i] != 0) {
                list.add(bytesResult[i]);
                startGetData = true;
            }

        }
        byte[] bytesInfo = new byte[list.size()];
        for (int i = 0; i < bytesInfo.length; i++) {
            bytesInfo[i] = list.get(list.size() - 1 - i);
        }
        return bytesInfo;
    }

    private boolean isGetThrRightResult(byte[] bytes) {
//        if (getIntFromBytes(bytes[0]) == 0xa5 && getIntFromBytes(bytes[1]) == 0x5a) {
//            int length = getIntFromBytes(bytes[2]) | (getIntFromBytes(bytes[3]) << 8);
//            length += 2;
//            if (length == bytes.length)
//                return true;
//        }
        return true;
    }

    public void release() {
//        fixThreadPool.shutdownNow();
        isStop = true;
        if (socket != null) {
            socket.close();
        }
//        log("socket连接：释放内存！");

    }

    public interface OnCommandResultListener {
        void result(byte[] bytes);
    }

    private OnCommandResultListener listener;

    public void setOnCommandResultListener(OnCommandResultListener listener) {
        this.listener = listener;
    }
}
