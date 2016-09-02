package com.singala2android.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/2.
 */
public class MsgBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String inout = "";
    private String msg = "";
    private String sender = "";
    private String receiver = "";
    private String sender_name = "";
    private String receiver_name = "";

    public MsgBean() {
    }

    public MsgBean(String inout, String msg, String sender, String receiver, String sender_name, String receiver_name) {
        this.inout = inout;
        this.msg = msg;
        this.sender = sender;
        this.receiver = receiver;
        this.sender_name = sender_name;
        this.receiver_name = receiver_name;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getInout() {
        return inout;
    }

    public void setInout(String inout) {
        this.inout = inout;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }
}
