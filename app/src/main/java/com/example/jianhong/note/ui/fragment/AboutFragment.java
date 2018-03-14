package com.example.jianhong.note.ui.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jianhong.note.R;
import com.example.jianhong.note.utils.PhoneUtils;

/**
 * Created by jianhong on 2018/3/11.
 */
public class AboutFragment extends Fragment implements View.OnClickListener  {

    private Context mContext;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView version = (TextView) view.findViewById(R.id.tv_version);
        mContext = this.getActivity().getApplicationContext();
        version.setText(PhoneUtils.getVersionName(mContext));

        View comment = view.findViewById(R.id.btn_comment);
        comment.setOnClickListener(this);
        View feedback = view.findViewById(R.id.btn_feedback);
        feedback.setOnClickListener(this);
        View donate = view.findViewById(R.id.btn_donate);
        donate.setOnClickListener(this);
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
