package com.singala2android.ui.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.singala2android.BaseActivity;
import com.singala2android.MyApplication;
import com.singala2android.R;
import com.singala2android.adapter.MsgAdapter;
import com.singala2android.bean.MsgBean;
import com.singala2android.bean.UserBean;
import com.singala2android.event.MsgEvent;
import com.zsoft.signala.hubs.HubInvokeCallback;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Administrator on 2016/8/2.
 */
public class ChatActivity extends BaseActivity {
    private LinearLayout backBtn;
    private TextView title;
    private SmoothProgressBar progressbar;
    //
    private ListView listview;
    private MsgAdapter adapter;
    private ArrayList<MsgBean> msgs = new ArrayList<MsgBean>();
    //
    private EditText et_msg;
    private UserBean chatUser;//聊天对象
    //
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    String text = (String) msg.obj;
                    Toast.makeText(ChatActivity.this, text, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case LISTVIEW_REFRESH:
                    try {
                        adapter.setList(msgs);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (MyApplication.getInstance().getHub() == null) {
            postMsg(handler, "连接不存在", SHOW_TOAST);
            finish();
        }
        chatUser = (UserBean) getIntent().getSerializableExtra("chatUser");
        System.out.println("+++onCreate:" + chatUser.getConnectionId() + "," + chatUser.getUserName() + "," + chatUser);
        initView();
        //
        MsgBean msgBean = (MsgBean) getIntent().getSerializableExtra("msgBean");
        if (msgBean != null) {
            msgs.add(msgBean);
            handler.sendEmptyMessage(LISTVIEW_REFRESH);
        }
    }

    private void initView() {
        //
        listview = (ListView) findViewById(R.id.listview);
        adapter = new MsgAdapter(this);
        listview.setAdapter(adapter);
        //
        et_msg = (EditText) findViewById(R.id.et_msg);
        //
        backBtn = (LinearLayout) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                overridePendingTransition(0, 0);
            }
        });
        title = (TextView) findViewById(R.id.title);
        title.setText(chatUser.getUserName());
        progressbar = (SmoothProgressBar) findViewById(R.id.progressbar);
    }

    public void myClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (!et_msg.getText().toString().trim().equals("")) {
                    sendMessage(et_msg.getText().toString().trim());
                    et_msg.setText("");
                }
                break;
        }
    }

    //接受登录连接的情况
    @Subscribe
    public void onEventMainThread(MsgEvent event) {
        if (event.getMsgBean().getSender().equals(chatUser.getConnectionId())) {
            msgs.add(event.getMsgBean());
            handler.sendEmptyMessage(LISTVIEW_REFRESH);
        }
    }

    private MsgBean msgBean;

    ///发送消息
    private void sendMessage(String message) {
        progressbar.setVisibility(View.VISIBLE);
        ///发送消息
        HubInvokeCallback messageSendCallback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                //发送成功
                System.out.println("发送成功");
                msgs.add(msgBean);
                handler.sendEmptyMessage(LISTVIEW_REFRESH);
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void OnError(Exception ex) {
                postMsg(handler, "发送失败:" + ex.getMessage(), SHOW_TOAST);
                progressbar.setVisibility(View.GONE);
            }
        };
        List<String> args = new ArrayList<String>(5);
        args.add(MyApplication.getInstance().getMySelfBean().getConnectionId());
        args.add(chatUser.getConnectionId());
        args.add(message);
        args.add(chatUser.getUserName());
        args.add(MyApplication.getInstance().getMySelfBean().getUserName());
        msgBean = new MsgBean("out", message, MyApplication.getInstance().getMySelfBean().getConnectionId(), chatUser.getConnectionId(), MyApplication.getInstance().getMySelfBean().getUserName(), chatUser.getUserName());
        MyApplication.getInstance().getHub().Invoke("sendAppPrivateMessage", args, messageSendCallback);
        /////
    }

}
