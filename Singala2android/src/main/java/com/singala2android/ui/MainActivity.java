package com.singala2android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.singala2android.BaseActivity;
import com.singala2android.MyApplication;
import com.singala2android.R;
import com.singala2android.adapter.UserAdapter;
import com.singala2android.bean.UserBean;
import com.singala2android.event.ConnectionStateEvent;
import com.singala2android.event.UserConnectEvent;
import com.singala2android.service.SignalAService;
import com.singala2android.ui.chat.ChatActivity;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.IHubProxy;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public LinearLayout ll_tip;
    public TextView tv_tip;
    //
    private ArrayList<UserBean> userBeanList;
    private ListView listView;
    private UserAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    String text = (String) msg.obj;
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case LISTVIEW_REFRESH:
                    adapter.setList(userBeanList);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initChat();
        Intent intent = new Intent(MainActivity.this, SignalAService.class);
        intent.putExtra("account", getIntent().getStringExtra("account"));
        startService(intent);
        showLoadingDialog("登陆中...");
    }


    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
        adapter = new UserAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (userBeanList.get(i).getConnectionId().equals(MyApplication.getInstance().getMySelfBean().getConnectionId())) {
                        postMsg(handler, "你自己", SHOW_TOAST);
                    } else {
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra("chatUser", userBeanList.get(i));
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void initChat() {
        ll_tip = (LinearLayout) findViewById(R.id.ll_tip);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //接受登录连接的情况
    @Subscribe
    public void onEventMainThread(ConnectionStateEvent event) {
        switch (event.getResult()) {
            case ConnectionStateEvent.ConnectResult.LOGIN_SUCCEED:
                postMsg(handler, "登录成功", SHOW_TOAST);
                showLoadingDialog("刷新在线用户中..");
                break;
            case ConnectionStateEvent.ConnectResult.CONNECT_ERROR:
                postMsg(handler, "连接失败:" + event.getException(), SHOW_TOAST);
                break;
        }
        dismissLoadingDialog();
    }

    //用户情况
    @Subscribe
    public void onEventMainThread(UserConnectEvent event) {
        switch (event.getResult()) {
            case UserConnectEvent.UserConnectResult.ONCONNECTED://连接上后获取在线用户列表
                MyApplication.getInstance().setMySelfBean(event.getUserBean());
                userBeanList = (ArrayList<UserBean>) event.getUserBeanList();
                handler.sendEmptyMessage(LISTVIEW_REFRESH);
                break;
            case UserConnectEvent.UserConnectResult.ONNEWUSERCONNECTED://监听新用户上线
                addUser(event.getUserBean());
                handler.sendEmptyMessage(LISTVIEW_REFRESH);
                break;
            case UserConnectEvent.UserConnectResult.ONUSERDISCONNECTED://监听用户下线
                removeUser(event.getUserBean());
                handler.sendEmptyMessage(LISTVIEW_REFRESH);
                break;
        }
        dismissLoadingDialog();
    }

    public void addUser(UserBean bean) {
        boolean needAdd = true;
        for (int i = 0; i < userBeanList.size(); i++) {
            if (userBeanList.get(i).getConnectionId().equals(bean.getConnectionId())) {
                needAdd = false;
                break;
            }
        }
        if (needAdd) {
            userBeanList.add(bean);
        }
    }


    public void removeUser(UserBean bean) {
        for (int i = 0; i < userBeanList.size(); i++) {
            if (userBeanList.get(i).getConnectionId().equals(bean.getConnectionId())) {
                userBeanList.remove(i);
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isServiceWork(this, "com.singala2android.service.SignalAService")) {
                Intent intent = new Intent(MainActivity.this, SignalAService.class);
                stopService(intent);
            }
            signOut(MyApplication.getInstance().getHub());
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
        }
        return false;
    }

    ///退出登录
    private void signOut(IHubProxy hubProxy) {
        ///发送消息
        HubInvokeCallback logoutCallback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
//                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }

            @Override
            public void OnError(Exception ex) {
                Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        };
        List<Object> args = new ArrayList<Object>(4);
        args.add(true);
        args.add(MyApplication.getInstance().getMySelfBean().getConnectionId());
        args.add(MyApplication.getInstance().getMySelfBean().getUserName());
        args.add(MyApplication.getInstance().getMySelfBean().getUserID());

        hubProxy.Invoke("onAppDisconnected", args, logoutCallback);
        /////
    }
}
