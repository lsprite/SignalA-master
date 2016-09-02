package com.zsoft.hubdemo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnDisconnectionRequestedListener,
        ConnectionFragment.OnConnectionRequestedListener,
        CalculatorFragment.ShowAllListener,
        CalculatorFragment.OnCalculationRequestedListener {
    protected static final String TAG_CONNECTION_FRAGMENT = "connection";
    protected static final String TAG_CALCULATION_FRAGMENT = "calculation";

    protected HubConnection con = null;
    protected IHubProxy hub = null;
    protected TextView tvStatus = null;
    private Boolean mShowAll = false;
    private UserBean mySelfBean;
    private List<UserBean> userBeanList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = (TextView) findViewById(R.id.connection_status);

        ChangeFragment(new ConnectionFragment(), false, TAG_CONNECTION_FRAGMENT);

        mySelfBean = new UserBean();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void ConnectionRequested(Uri address) {

        con = new HubConnection(address.toString(), this, new LongPollingTransport()) {
            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                tvStatus.setText(oldState.getState() + " -> " + newState.getState());

                switch (newState.getState()) {
                    case Connected:

                        ///上线通知服务端
                        HubInvokeCallback connectCallback = new HubInvokeCallback() {
                            @Override
                            public void OnResult(boolean succeeded, String response) {
                                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void OnError(Exception ex) {
                                Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        };
                        List<String> args = new ArrayList<String>(3);
                        args.add("2323244");//随机id:先随机生成一个10位数字
                        args.add("Android");//用户名
                        args.add("android client");//组

                        hub.Invoke("connect", args, connectCallback);
                        /////

                        CalculatorFragment fragment = new CalculatorFragment();
                        ChangeFragment(fragment, true, TAG_CALCULATION_FRAGMENT);
                        break;
                    case Disconnected:
                        Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_CALCULATION_FRAGMENT);
                        if (f != null && f.isVisible()) {
                            getSupportFragmentManager().popBackStackImmediate();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void OnError(Exception exception) {
                Toast.makeText(MainActivity.this, "On error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            }

        };

        try {
            hub = con.CreateHubProxy("systemHub");//创建hub
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

            }
        });


        //监听用户下线
        hub.On("onUserDisconnected", new HubOnDataCallback()

        {
            @Override
            public void OnReceived(JSONArray args) {

                String connectID = args.opt(0).toString();
                String userName = args.opt(1).toString();

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

            }
        });


        con.Start();
    }

    @Override
    public void calculate(int value1, int value2, String operator) {
//		int answer = operator.equalsIgnoreCase("Plus") ? value1+value2 : value1-value2;
//			
//		StringBuilder sb = new StringBuilder();
//		sb.append(value1);
//		sb.append(operator=="plus" ? "+":"-");
//		sb.append(value2);
//		sb.append(" = ");
//		sb.append(answer);

        HubInvokeCallback callback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(Exception ex) {
                Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        List<Integer> args = new ArrayList<Integer>(2);
        args.add(value1);
        args.add(value2);
        hub.Invoke(operator, args, callback);

    }

    protected void ChangeFragment(Fragment fragment, Boolean addToBackstack, String tag) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragment_container, fragment, tag);
        if (addToBackstack)
            trans.addToBackStack(null);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.commit();

    }

    @Override
    public void setShowAll(Boolean value) {
        mShowAll = value;
    }

    @Override
    public Boolean getShowAll() {
        return mShowAll;
    }

    @Override
    public void DisconnectionRequested() {
        if (con != null) {
            con.Stop();
        }

    }

    ///发送消息
    private void sendMessage(IHubProxy hubProxy, UserBean userBean, String message) {
        ///发送消息
        HubInvokeCallback messageSendCallback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(Exception ex) {
                Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        List<String> args = new ArrayList<String>(5);
        args.add(mySelfBean.getConnectionId());
        args.add(userBean.getConnectionId());
        args.add(message);
        args.add(userBean.getUserName());
        args.add(mySelfBean.getUserName());

        hubProxy.Invoke("sendAppPrivateMessage", args, messageSendCallback);
        /////
    }


    ///退出登录
    private void Logout(IHubProxy hubProxy) {
        ///发送消息
        HubInvokeCallback logoutCallback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(Exception ex) {
                Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        List<Object> args = new ArrayList<Object>(4);
        args.add(true);
        args.add(mySelfBean.getConnectionId());
        args.add(mySelfBean.getUserName());
        args.add(mySelfBean.getUserID());

        hubProxy.Invoke("onAppDisconnected", args, logoutCallback);
        /////
    }

}



