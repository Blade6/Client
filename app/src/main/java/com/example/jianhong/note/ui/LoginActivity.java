package com.example.jianhong.note.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jianhong.note.R;
import com.example.jianhong.note.entity.Response;
import com.example.jianhong.note.entity.User;
import com.example.jianhong.note.entity.HttpCallbackListener;
import com.example.jianhong.note.utils.AccountUtils;
import com.example.jianhong.note.utils.UrlUtils;
import com.example.jianhong.note.utils.HttpUtils;
import com.example.jianhong.note.utils.MD5Utils;
import com.example.jianhong.note.utils.SPUtils;
import com.example.jianhong.note.utils.JSONUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_login)
    Button loginBtn;
    @BindView(R.id.btn_register)
    TextView registerBtn;
    @BindView(R.id.login_name)
    EditText loginName;
    @BindView(R.id.login_pwd)
    EditText loginPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initDrawableSize();

    }


    private void initDrawableSize()
    {
        Drawable accountDraw=getResources().getDrawable(R.drawable.login_icon_account);
        accountDraw.setBounds(0,0,45,45);
        Drawable passwordDraw=getResources().getDrawable(R.drawable.login_icon_password);
        passwordDraw.setBounds(0,0,45,45);
        loginName.setCompoundDrawables(accountDraw,null,null,null);
        loginPwd.setCompoundDrawables(passwordDraw,null,null,null);
    }
    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                goToRegisterActivity();
                break;
        }
    }


    private void login() {
        final String name=loginName.getText().toString();
        final String pwd=MD5Utils.MD5(loginPwd.getText().toString());
        final ProgressDialog progress = new ProgressDialog(
                LoginActivity.this);
        progress.setMessage("正在登陆...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        final User user = new User();
        user.setUsername(name);
        user.setPassword(pwd);

        String address = UrlUtils.LoginURL + "usr/" + name + "/pwd/" + pwd;
        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Response res = JSONUtils.handleResponse(response);
                if (!res.getReturnCode()) {
                    progress.dismiss();
                    //Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                    Snackbar.make(loginBtn,"用户名或密码错误",Snackbar.LENGTH_LONG).show();
                }
                else {
                    progress.dismiss();
                    //将用户信息保存至本地
                    SPUtils.put(LoginActivity.this,"user_name",name);
                    SPUtils.put(LoginActivity.this,"pwd", pwd);
                    //将登陆信息保存本地
                    AccountUtils.saveUserInfos(LoginActivity.this, user, pwd);
                    goToHomeActivity();
                }
            }
            @Override
            public void onError(Exception e) {
                progress.dismiss();
                Snackbar.make(loginBtn,"登录失败",Snackbar.LENGTH_LONG).show();
                //Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void goToHomeActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void goToRegisterActivity() {
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
