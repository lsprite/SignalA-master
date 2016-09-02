package com.singala2android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.singala2android.MyApplication;
import com.singala2android.R;
import com.singala2android.bean.MsgBean;
import com.singala2android.bean.UserBean;
import com.singala2android.event.ConnectionStateEvent;
import com.singala2android.event.MsgEvent;
import com.singala2android.event.UserConnectEvent;
import com.singala2android.ui.chat.ChatActivity;
import com.singala2android.util.URLUtil;
import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/8/2.
 */
public class SignalAService extends Service {
    private IHubProxy hub = null;
    private HubConnection con = null;
    private List<UserBean> userBeanList;
    private UserBean mySelfBean = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mySelfBean = new UserBean();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent.getStringExtra("account") != null && !intent.getStringExtra("account").equals("")) {
            connect(intent.getStringExtra("account"));
        }
    }

    private void connect(final String account) {
        if (con != null) {
            con.Stop();
            con = null;
        }
        con = new HubConnection(URLUtil.SIGNALA_URL, this, new LongPollingTransport()) {
            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                switch (newState.getState()) {
                    case Connected:

                        ///上线通知服务端
                        HubInvokeCallback connectCallback = new HubInvokeCallback() {
                            @Override
                            public void OnResult(boolean succeeded, String response) {
                                System.out.println("++++OnStateChanged.OnResult" + succeeded + "," + response);
//                                Toast.makeText(SignalAService.this, response, Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new ConnectionStateEvent(ConnectionStateEvent.ConnectResult.LOGIN_SUCCEED, null));
                            }

                            @Override
                            public void OnError(Exception ex) {
                                System.out.println("++++OnStateChanged.OnError");
//                                Toast.makeText(SignalAService.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        };
                        List<String> args = new ArrayList<String>(3);
                        args.add(getRadom());//随机id:先随机生成一个10位数字
                        args.add(account);//用户名
                        args.add("android");//组

                        hub.Invoke("connect", args, connectCallback);
                        /////
//                        Toast.makeText(SignalAService.this, "连接成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(
                                new ConnectionStateEvent(ConnectionStateEvent.ConnectResult.CONNECTED, null));
                        break;
                    case Disconnected:
//                        System.out.println("连接已断开");
                        EventBus.getDefault().post(
                                new ConnectionStateEvent(ConnectionStateEvent.ConnectResult.DISCONNECTED, null));
//                        Toast.makeText(SignalAService.this, "登录失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void OnError(Exception exception) {
                EventBus.getDefault().post(
                        new ConnectionStateEvent(ConnectionStateEvent.ConnectResult.CONNECT_ERROR, "" + exception.getMessage()));
            }

        };
        try {
            hub = con.CreateHubProxy("systemHub");//创建hub
            MyApplication.getInstance().setHub(hub);
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ///连接上后获取在线用户列表
        hub.On("onConnected", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                userBeanList = new ArrayList<UserBean>();
                ///返回三个参数，第一个是登陆者的connectid，第二个是登陆者的用户名，第三个是用户列表
                mySelfBean.setConnectionId(args.opt(0).toString());
                mySelfBean.setUserName(args.opt(1).toString());
                JSONArray users = (JSONArray) args.opt(2);
                for (int i = 0; i < users.length(); i++) {
                    JSONObject oneObj = (JSONObject) users.opt(i);
                    UserBean bean = new UserBean();
                    try {
                        bean.setConnectionId(oneObj.getString("ConnectionId"));
                        bean.setUserID(oneObj.getString("UserID"));
                        bean.setUserName(oneObj.getString("UserName"));
                        bean.setDeptName(oneObj.getString("DeptName"));
                        bean.setLoginTime(oneObj.getString("LoginTime"));
                        userBeanList.add(bean);
                    } catch (JSONException e) {
                    }
                }
                EventBus.getDefault().post(new UserConnectEvent(UserConnectEvent.UserConnectResult.ONCONNECTED, mySelfBean, userBeanList));
            }
        });

        //监听新用户上线
        hub.On("onNewUserConnected", new HubOnDataCallback()

        {
            @Override
            public void OnReceived(JSONArray args) {
//				for(int i=0; i<args.length(); i++)
//				{
//					Toast.makeText(MainActivity.this, args.opt(i).toString(), Toast.LENGTH_SHORT).show();
//				}
                UserBean userBean = new UserBean();
                userBean.setConnectionId(args.opt(0).toString());
                userBean.setUserID(args.opt(1).toString());
                userBean.setUserName(args.opt(2).toString());
                userBean.setDeptName(args.opt(3).toString());
                userBean.setLoginTime(args.opt(4).toString());
                EventBus.getDefault().post(new UserConnectEvent(UserConnectEvent.UserConnectResult.ONNEWUSERCONNECTED, userBean, null));
            }
        });


        //监听用户下线
        hub.On("onUserDisconnected", new HubOnDataCallback()

        {
            @Override
            public void OnReceived(JSONArray args) {
                String connectID = args.opt(0).toString();
                String userName = args.opt(1).toString();
                UserBean userBean = new UserBean();
                userBean.setConnectionId(connectID);
                userBean.setUserName(userName);
                EventBus.getDefault().post(new UserConnectEvent(UserConnectEvent.UserConnectResult.ONUSERDISCONNECTED, userBean, null));
            }
        });

        //监听消息
        hub.On("receivePrivateMessage", new HubOnDataCallback()

        {
            @Override
            public void OnReceived(JSONArray args) {
                String connectID = args.opt(0).toString();
                String userName = args.opt(1).toString();
                String message = args.opt(2).toString();
                MsgBean msgBean = new MsgBean("in", message, connectID, MyApplication.getInstance().getMySelfBean().getConnectionId(), userName, MyApplication.getInstance().getMySelfBean().getUserName());
                EventBus.getDefault().post(new MsgEvent(msgBean));
                createNotification(msgBean);
            }
        });
        con.Start();
    }

    @Override
    public void onDestroy() {
        System.out.println("+++SignalAService.onDestroy");
        if (con != null) {
            con.Stop();
            con = null;
        }
        super.onDestroy();
    }

    Notification notification;
    NotificationManager notificationManager;
    int curNotificationID;

    public void createNotification(MsgBean bean) {
        curNotificationID = MyApplication.getInstance().getCurNotificationID();
        UserBean userBean = new UserBean();
        userBean.setConnectionId(bean.getSender());
        userBean.setUserName(bean.getSender_name());
        System.out.println("+++createNotification:" + userBean.getConnectionId() + "," + userBean.getUserName() + "," + userBean);
        Intent intent = new Intent(getApplicationContext(),
                ChatActivity.class); // 设置任务栏拉下点击的链接
        intent.putExtra("chatUser", userBean);
        intent.putExtra("msgBean", bean);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                curNotificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new
                Notification.Builder(getApplicationContext())
                .setAutoCancel(true)
                .setTicker(bean.getSender_name())
                .setContentTitle(bean.getSender_name())
                .setContentText(bean.getMsg())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(true);
        notification = builder.getNotification();
        notification.vibrate = new long[]{100,
                250, 100, 500};
        notification.ledARGB = 0x00FF00;
        notification.ledOnMS = 100;
        notification.ledOffMS = 100;
        notification.flags = Notification.FLAG_SHOW_LIGHTS
                | Notification.FLAG_AUTO_CANCEL;
        notificationManager
                .notify(curNotificationID, notification);
    }

    public String getRadom() {
        int[] randoms = new int[10];

        //产生10个随机数：
        Random rnd = new Random();
        for (int i = 0; i < randoms.length; i++)
            randoms[i] = rnd.nextInt();
        String str = "";
        for (int i = 0; i < randoms.length; i++) {
            str = str + randoms[i];//拼接成字符串，最终放在变量str中
        }
        return str;
    }
}
