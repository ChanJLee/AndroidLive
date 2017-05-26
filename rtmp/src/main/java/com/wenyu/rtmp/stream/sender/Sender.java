package com.wenyu.rtmp.stream.sender;

public interface Sender {
    void start();
    void onData(byte[] data, int type);
    void stop();
}
