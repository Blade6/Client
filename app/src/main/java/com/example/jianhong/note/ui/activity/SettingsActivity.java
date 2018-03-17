package com.example.jianhong.note.ui.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.jianhong.note.R;
import com.example.jianhong.note.app.NoteApplication;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.service.ExtractService;
import com.example.jianhong.note.utils.CommonUtils;
import com.example.jianhong.note.utils.PreferencesUtils;

import java.util.List;

/**
 * Created by jianhong on 2018/3/17.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static final String SERVICE = "com.example.jianhong.note.service.ExtractService";

    private Context mContext;
    
    private int tmpLocation;
    private String tmpLocationName;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_settings);

        initButtons();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.action_setting);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.ll_lightning_container).setOnClickListener(this);
        findViewById(R.id.ll_extract_location_container).setOnClickListener(this);
        findViewById(R.id.ll_one_column).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        exitOperation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitOperation();
                break;
            default:
                break;
        }
        return true;
    }

    public void exitOperation() {
        finish();
    }

    private Switch lightningExtract;
    private TextView extractLocationSummary;
    private CheckBox oneColumn;

    private void initButtons() {
        oneColumn = (CheckBox) findViewById(R.id.cb_one_column);
        oneColumn.setChecked(PreferencesUtils.getBoolean(PreferencesUtils.ONE_COLUMN));
        oneColumn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.putBoolean(PreferencesUtils.ONE_COLUMN, isChecked);
                refreshMainActivity(MainActivity.NEED_CONFIG_LAYOUT);
            }
        });

        lightningExtract = (Switch) findViewById(R.id.s_lightning_extract);
        lightningExtract.setChecked(PreferencesUtils.getBoolean(PreferencesUtils.LIGHTNING_EXTRACT));
        lightningExtract.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.putBoolean(PreferencesUtils.LIGHTNING_EXTRACT, isChecked);
                if (isChecked) {
                    if (CommonUtils.isServiceWork(mContext, SERVICE)) {
                        ExtractService.startExtractTask(mContext);
                    } else {
                        // todo hejianhong
                    }
                } else {
                    if (CommonUtils.isServiceWork(mContext, SERVICE)) {
                        ExtractService.stopExtractTask(mContext);
                    }
                }
            }
        });

        extractLocationSummary = (TextView) findViewById(R.id.tv_extract_location_summary);
        extractLocationSummary.setText(PreferencesUtils.getString(PreferencesUtils.LIGHTNING_EXTRACT_SAVE_NAME));
        extractLocationSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectSaveLocationDialog(extractLocationSummary);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_lightning_container:
                lightningExtract.toggle();
                break;
            case R.id.ll_extract_location_container:
                extractLocationSummary.performClick();
                break;
            case R.id.ll_one_column:
                oneColumn.performClick();
                break;
            default:
                break;
        }
    }

    private void showSelectSaveLocationDialog(final TextView tv) {
        View view = getLayoutInflater().inflate(R.layout.dialog_radiogroup, (ViewGroup) getWindow().getDecorView(), false);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_dialog);

        boolean radioChecked = false;
        List<NoteBook> list = NoteDB.getInstance(mContext).loadNoteBooks();

        tmpLocation = PreferencesUtils.getInt(PreferencesUtils.LIGHTNING_EXTRACT_SAVE_LOCATION);
        tmpLocationName = mContext.getString(R.string.default_notebook);

        for (final NoteBook noteBook : list) {
            if (noteBook.getId() == 0) continue;

            RadioButton tempButton = new RadioButton(mContext);
            tempButton.setText(noteBook.getName());
            radioGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (noteBook.getId() == tmpLocation) {
                tempButton.setChecked(true);
                radioChecked = true;
                tmpLocationName = noteBook.getName();
            }

            tempButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        tmpLocation = noteBook.getId();
                        tmpLocationName = noteBook.getName();
                    }
                }
            });
        }

        RadioButton note = (RadioButton) view.findViewById(R.id.rb_note);
        if (!radioChecked) {
            note.setChecked(true);
        }
        note.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tmpLocation = 0;
                    tmpLocationName = mContext.getString(R.string.default_notebook);
                }
            }
        });

        final Dialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.lightning_extract_save_location)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferencesUtils.putInt(PreferencesUtils.LIGHTNING_EXTRACT_SAVE_LOCATION, tmpLocation);
                        PreferencesUtils.putString(PreferencesUtils.LIGHTNING_EXTRACT_SAVE_NAME, tmpLocationName);
                        tv.setText(tmpLocationName);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void refreshMainActivity(int message) {
        NoteApplication application = (NoteApplication) getApplication();
        MainActivity.SyncHandler handler = application.getHandler();
        handler.sendEmptyMessage(message);
    }

}
