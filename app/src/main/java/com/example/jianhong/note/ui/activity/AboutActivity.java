package com.example.jianhong.note.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.jianhong.note.R;
import com.example.jianhong.note.utils.CommonUtils;

/**
 * Created by jianhong on 2018/3/11.
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG = AboutActivity.class.getSimpleName();

    private Context mContext;

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView version = (TextView) findViewById(R.id.tv_version);
        mContext = getApplicationContext();
        version.setText(CommonUtils.getVersionName(mContext));

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View comment = findViewById(R.id.btn_comment);
        comment.setOnClickListener(this);
        View feedback = findViewById(R.id.btn_feedback);
        feedback.setOnClickListener(this);
        View donate = findViewById(R.id.btn_donate);
        donate.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_comment:
                // evaluate(mContext);
                // ignore
                break;
            case R.id.btn_feedback:
                // PhoneUtils.feedback(mContext);
                // ignore
                break;
            case R.id.btn_donate:
                // ignore
                break;
            case R.id.btn_how_to_use:
                // ignore
                break;
        }
    }

}
