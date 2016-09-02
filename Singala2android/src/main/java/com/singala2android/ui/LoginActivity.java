package com.singala2android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.singala2android.BaseActivity;
import com.singala2android.R;

public class LoginActivity extends BaseActivity {
    //
    private TextView tv_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        tv_account = (TextView) findViewById(R.id.tv_account);
    }

    public void myClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (tv_account.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("account", tv_account.getText().toString().trim());
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }

                break;
        }
    }

}
