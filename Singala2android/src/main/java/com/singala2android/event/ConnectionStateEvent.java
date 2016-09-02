package com.singala2android.event;

/**
 * Created by Administrator on 2016/8/2.
 */
public class ConnectionStateEvent {
    public interface ConnectResult {
        int LOGIN_SUCCEED = 0;//登录成功
        int CONNECTED = 1;//连接到服务器
        int DISCONNECTED = 2;//与服务器断开
        int CONNECT_ERROR = -1;//链接错误
        int LOGIN_ERROR = -2;//登录错误
    }

    private int result;

    private String exception;

    public ConnectionStateEvent(int result, String exception) {
        this.result = result;
        this.exception = exception;
    }

    public int getResult() {
        return result;
    }

    public String getException() {
        return exception;
    }

}
