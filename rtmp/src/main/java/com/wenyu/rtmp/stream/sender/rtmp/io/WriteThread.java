package com.wenyu.rtmp.stream.sender.rtmp.io;

import com.wenyu.rtmp.entity.Frame;
import com.wenyu.rtmp.stream.sender.rtmp.packets.Command;
import com.wenyu.rtmp.stream.sender.rtmp.packets.Chunk;
import com.wenyu.rtmp.stream.sender.sendqueue.ISendQueue;

import java.io.IOException;
import java.io.OutputStream;

public class WriteThread extends Thread {

    private static final String TAG = "WriteThread";
    private OutputStream out;
    private SessionInfo sessionInfo;
    private OnWriteListener listener;
    private ISendQueue mSendQueue;
    private volatile boolean startFlag;

    public WriteThread(OutputStream out, SessionInfo sessionInfo) {
        this.out = out;
        this.sessionInfo = sessionInfo;
        this.startFlag = true;
    }

    public void setWriteListener(OnWriteListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (startFlag) {
            try {
                Frame<Chunk> frame = mSendQueue.takeFrame();
                if(frame != null) {
                    Chunk chunk = frame.data;
                    chunk.writeTo(out, sessionInfo);
                    if (chunk instanceof Command) {
                        Command command = (Command) chunk;
                        sessionInfo.addInvokedCommand(command.getTransactionId(), command.getCommandName());
                    }
                    out.flush();
                }
            } catch (IOException e) {
                startFlag = false;
                if(listener != null) {
                    listener.onDisconnect();
                }
            }
        }
    }

    public void setSendQueue(ISendQueue sendQueue) {
        mSendQueue = sendQueue;
    }

    public void shutdown() {
        listener = null;
        startFlag = false;
        this.interrupt();
    }
}
