package com.singala2android.event;

import com.singala2android.bean.UserBean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class UserConnectEvent {
    public interface UserConnectResult {
        int ONCONNECTED = 0;//连接上后获取在线用户列表
        int ONNEWUSERCONNECTED = 1;//监听新用户上线
        int ONUSERDISCONNECTED = 2;//监听用户下线
    }

    private int result;
    private UserBean userBean;
    private List<UserBean> userBeanList;

    public UserConnectEvent(int result, UserBean userBean, List<UserBean> userBeanList) {
        this.result = result;
        this.userBean = userBean;
        this.userBeanList = userBeanList;
    }

    public int getResult() {
        return result;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public List<UserBean> getUserBeanList() {
        return userBeanList;
    }
}
