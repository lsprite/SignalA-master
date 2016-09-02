package com.singala2android.bean;

import java.io.Serializable;

/**
 * Created by alphaguan on 16/8/2.
 */
public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;
    String ConnectionId;
    String UserID;
    String UserName;

    public String getConnectionId() {
        return ConnectionId;
    }

    public void setConnectionId(String connectionId) {
        ConnectionId = connectionId;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getDeptName() {
        return DeptName;
    }

    public void setDeptName(String deptName) {
        DeptName = deptName;
    }

    public String getLoginTime() {
        return LoginTime;
    }

    public void setLoginTime(String loginTime) {
        LoginTime = loginTime;
    }

    String DeptName;
    String LoginTime;
}
