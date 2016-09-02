package com.singala2android.event;

import com.singala2android.bean.MsgBean;
import com.singala2android.bean.UserBean;

/**
 * Created by Administrator on 2016/8/2.
 */
public class MsgEvent {

    private MsgBean msgBean;

    public MsgEvent(MsgBean msgBean) {
        this.msgBean = msgBean;
    }

    public MsgBean getMsgBean() {
        return msgBean;
    }
}
