package com.example.jianhong.note.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jianhong.note.R;
import com.example.jianhong.note.utils.CommonUtils;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;
        TextView version = (TextView) findViewById(R.id.tv_version);
        version.setText(CommonUtils.getVersionName(mContext));

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
                // CommonUtils.feedback(mContext);
                // ignore
                break;
            case R.id.btn_donate:
                // ignore
                break;
        }
    }


    private void evaluate(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Couldn't launch the market!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
