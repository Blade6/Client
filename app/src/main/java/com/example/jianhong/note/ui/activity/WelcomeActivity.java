package com.example.jianhong.note.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.jianhong.note.R;
import com.example.jianhong.note.utils.SPUtils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //autoLogin();
                goToHomeActivity();
            }
        }, 2000);
    }

    private void autoLogin()
    {
        if(SPUtils.contains(this,"user_name"))
        {
            goToHomeActivity();
        }
        else
        {
            goToLoginActivity();
        }
    }
    private void goToHomeActivity()
    {
        Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }
    private void goToLoginActivity()
    {
        Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }
}
