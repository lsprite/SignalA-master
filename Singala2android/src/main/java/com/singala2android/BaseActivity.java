package com.singala2android;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.singala2android.event.ConnectionStateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/8/2.
 */
public class BaseActivity extends Activity {
    public static final int LISTVIEW_REFRESH = 110;//
    public static final int SHOW_TOAST = 100;//
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    //接受登录连接的情况
    @Subscribe
    public void onEventMainThread(Objects event) {
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void postMsg(Handler handler, String s, int what) {
        Message msg = Message.obtain();
        msg.obj = s;
        msg.what = what;
        handler.sendMessage(msg);
    }

    /**
     * @param msg
     * @return
     */
    public boolean showLoadingDialog(String msg) {
        try {
            if (loadingDialog == null) {
                LayoutInflater inflater = LayoutInflater.from(this);
                View v = inflater.inflate(R.layout.progresswheel_lay, null);// 得到加载view
                LinearLayout layout = (LinearLayout) v
                        .findViewById(R.id.progresswheel_bg);// 加载布局
                // final ProgressWheel progressWheel = (ProgressWheel)
                // layout.findViewById(R.id.progresswheel);
                TextView tipTextView = (TextView) v
                        .findViewById(R.id.tipTextView);// 提示文字
                if (msg.length() != 0) {
                    tipTextView.setText(msg);// 设置加载信息
                } else {
                    tipTextView.setVisibility(View.GONE);
                }
                // progressWheel.setBarColor(context.getResources().getColor(R.color.title_default_color));
                loadingDialog = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
                loadingDialog.setCancelable(true);// 可以用“返回键”取消
                loadingDialog.setCanceledOnTouchOutside(false);// 点击外面的时候 不关闭
                loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
                loadingDialog
                        .setOnShowListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(DialogInterface dialog) {
                                // progressWheel.refreshDrawableState();
                                // hyperspaceJumpAnimation.reset();
                                // hyperspaceJumpAnimation.startNow();
                                // 使用ImageView显示动画
                                // spaceshipImage.startAnimation(hyperspaceJumpAnimation);
                            }
                        });
                loadingDialog
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                // progressWheel.stopSpinning();
                                // hyperspaceJumpAnimation.cancel();
                            }
                        });
            }
            loadingDialog.show();
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public void dismissLoadingDialog() {
        try {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        System.out.println(serviceName + ":" + isWork);
        return isWork;
    }

}
