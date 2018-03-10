package com.example.jianhong.note.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.jianhong.note.R;
import com.example.jianhong.note.entity.HttpCallbackListener;
import com.example.jianhong.note.entity.Response;
import com.example.jianhong.note.entity.User;
import com.example.jianhong.note.utils.CommonUtils;
import com.example.jianhong.note.utils.HttpUtils;
import com.example.jianhong.note.utils.JSONUtils;
import com.example.jianhong.note.utils.MD5Utils;
import com.example.jianhong.note.utils.UrlUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText accountEt;
    @BindView(R.id.et_password)
    EditText pwdEt;
    @BindView(R.id.et_email)
    EditText twicePwdEt;
    @BindView(R.id.btn_register)
    Button registerBtn;
    @BindView(R.id.img_back)
    ImageButton imgBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       // requestWindowFeature(Window.FEATURE_NO_TITLE);
//        /*set it to be full screen*/
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.img_back)
    public void back() {
        finish();
    }


    @OnClick(R.id.btn_register)
    public void register(){
        String name =accountEt.getText().toString();
        String password = pwdEt.getText().toString();
        String pwd_again = twicePwdEt.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Snackbar.make(registerBtn,"帐号不能为空",Snackbar.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Snackbar.make(registerBtn,"密码不能为空",Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!pwd_again.equals(password)) {
            Snackbar.make(registerBtn,"两次密码输入不一致",Snackbar.LENGTH_LONG).show();
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            Snackbar.make(registerBtn,"网络连接出错",Snackbar.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        final User user = new User();
        user.setUsername(name);
        String pwd = MD5Utils.MD5(password);
        user.setPassword(pwd);

        String address = UrlUtils.RegisterURL + "usr/" + name + "/pwd/" + pwd;
        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Response res = JSONUtils.handleResponse(response);
                if (!res.getReturnCode()) {
                    progress.dismiss();
                    Snackbar.make(registerBtn,"注册失败:",Snackbar.LENGTH_LONG).show();
                }
                else {
                    progress.dismiss();
                    Snackbar.make(registerBtn,"注册成功",Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onError(Exception e) {
                progress.dismiss();
                Snackbar.make(registerBtn,"网络错误:",Snackbar.LENGTH_LONG).show();
            }
        });

    }
}
