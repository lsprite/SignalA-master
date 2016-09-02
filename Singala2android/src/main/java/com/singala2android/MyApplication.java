package com.singala2android;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.singala2android.bean.UserBean;
import com.zsoft.signala.hubs.IHubProxy;

public class MyApplication extends Application {
    private static MyApplication instance;
    private UserBean mySelfBean;
    private IHubProxy hub = null;
    private int CurNotificationID = 0;

    public UserBean getMySelfBean() {
        return mySelfBean;
    }

    public void setMySelfBean(UserBean mySelfBean) {
        this.mySelfBean = mySelfBean;
    }

    public IHubProxy getHub() {
        return hub;
    }

    public void setHub(IHubProxy hub) {
        this.hub = hub;
    }

    public int getCurNotificationID() {
        return CurNotificationID++;
    }

    //
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

}