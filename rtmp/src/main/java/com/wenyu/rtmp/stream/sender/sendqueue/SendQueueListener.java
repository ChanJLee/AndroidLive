package com.wenyu.rtmp.stream.sender.sendqueue;

/**
 * @Title: SendQueueListener
 * @Package com.wenyu.rtmp.stream.sender.sendqueue
 * @Description:
 * @Author Jim
 * @Date 2016/11/21
 * @Time 下午3:19
 * @Version
 */

public interface SendQueueListener {
    void good();
    void bad();
}
