package com.example.jianhong.note.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.content.Context;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;

import java.lang.reflect.Field;

import com.example.jianhong.note.R;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.entity.Originator;
import com.example.jianhong.note.entity.Memo;
import com.example.jianhong.note.utils.CommonUtils;
import com.example.jianhong.note.utils.LogUtils;
import com.example.jianhong.note.utils.PreferencesUtils;
import com.example.jianhong.note.utils.SynStatusUtils;
import com.example.jianhong.note.utils.TimeUtils;
import com.example.jianhong.note.utils.ProviderUtils;
import com.example.jianhong.note.utils.NoteBookUtils;

import butterknife.BindView;

/**
 * Created by jianhong on 2018/3/12.
 */

public class NoteActivity extends AppCompatActivity implements TextWatcher {

    public static final String TAG = NoteActivity.class.getSimpleName();

    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 2;
    public static final int MODE_TODAY = 3;

    /**
     * 退出时应对数据库进行操作的标志位
     */
    private int dbFlag;
    public static final int DB_SAVE = 1;
    public static final int DB_UPDATE = 2;
    public static final int DB_DELETE = 3;

    private int mode;
    private Note note;
    private NoteDB db;
    private android.support.v7.app.ActionBar actionBar;
    private Context mContext;

    /**
     * 数据解析器，备忘录模式，轻松实现撤消、重做
     */
    private Originator mDataParser;
    /**
     * TextWatcher 中使用，标注是否是使用undo或redo引起的内容改变
     */
    private boolean isUndoOrRedo;
    private MenuItem undoItem;
    private MenuItem redoItem;

    @BindView(R.id.et_note_edit)
    EditText editText;

    /**
     * 启动NoteActivity活动的静态方法，
     * 需给出Note实例及启动模式NoteActivity.MODE_SHOW or NoteActivity.MODE_NEW
     * or NoteActivity.MODE_EDIT
     *
     * @param context
     * @param note
     * @param mode
     */
    public static void actionStart(Context context, Note note, int mode) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra("note_data", note);
        intent.putExtra("mode", mode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        setOverflowShowingAlways();
        initValues();

        actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (MODE_NEW == mode) {
                actionBar.setTitle(R.string.mode_new);
            } else if (MODE_EDIT == mode) {
                updateAppBar();
            }
        }
    }

    private void updateAppBar() {
        String stamp = TimeUtils.getTime(note.getUpdTime());
        LogUtils.d(TAG, "stamp:" + stamp);
        actionBar.setTitle(stamp);
    }

    /**
     * 令Overflow菜单永远显示
     */
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mode == MODE_NEW || MODE_TODAY == mode) {
            getMenuInflater().inflate(R.menu.new_note_menu, menu);
        } else if (mode == MODE_EDIT) {
            getMenuInflater().inflate(R.menu.edit_note_menu, menu);
        }
        undoItem = menu.findItem(R.id.action_undo);
        redoItem = menu.findItem(R.id.action_redo);
        updateUndoAndRedo();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitOperation();
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_delete:
                deleteNoteWithDialog();
                return true;
            case R.id.word_count:
                showWordCount();
                return true;
            case R.id.action_redo:
                redo();
                return true;
            case R.id.action_undo:
                undo();
                return true;
            default:
                return true;
        }
    }

    private void undo() {
        isUndoOrRedo = true;
        mDataParser.undo();
        setDataToNoteContent();
    }

    private void redo() {
        isUndoOrRedo = true;
        mDataParser.redo();
        setDataToNoteContent();
    }

    private void initValues() {
        mContext = this;
        note = getIntent().getParcelableExtra("note_data");
        LogUtils.d(TAG, note.toString());
        mDataParser = new Originator(new Memo(note.getContent(), 0));
        isUndoOrRedo = false;
        db = NoteDB.getInstance(this);

        editText = (EditText) findViewById(R.id.et_note_edit);
        editText.setHint(R.string.write_something);
        initMode(mDataParser.getState().getContent());
        editText.addTextChangedListener(this);

        if (mode == MODE_NEW) {
            editText.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else if (mode == MODE_EDIT) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        editText.setCursorVisible(true);
                    }
                }
            });
        } else if (mode == MODE_TODAY) {
            note.setCreateTime(TimeUtils.getCurrentTimeInLong());
            note.setNoteBookId(PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID));
            editText.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void initMode(String content) {
        mode = getIntent().getIntExtra("mode", 0);
        if (mode == MODE_NEW || mode == MODE_EDIT || MODE_TODAY == mode) {
            setNoteContent(content, content.length());
        }
    }

    public static void writeNewNote(Context mContext) {
        Note note = new Note();
        note.setCreateTime(TimeUtils.getCurrentTimeInLong());

        Intent intent = new Intent(mContext, NoteActivity.class);
        intent.putExtra("note_data", note);
        intent.putExtra("mode", MODE_NEW);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(0, 0);
    }

    private void setNoteContent(String content, int length) {
        editText.setText(content);
        editText.setSelection(length);
    }

    private void setDataToNoteContent() {
        String content = mDataParser.getState().getContent();
        int selectionEnd = mDataParser.getState().getSelectionEnd();
        setNoteContent(content, selectionEnd);
    }

    private void showWordCount() {
        int[] res = new int[3];
        CommonUtils.wordCount(editText.getText().toString(), res);
        String msg = getString(R.string.words) + "\n" + res[0] + "\n\n"
                + getString(R.string.characters_no_spaces) + "\n" + res[1] + "\n\n"
                + getString(R.string.characters_with_spaces) + "\n" + res[2] + "\n";
        new AlertDialog.Builder(mContext).setTitle(R.string.word_count)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!isUndoOrRedo) {
            int selectionLocation = editText.getSelectionEnd();
            if (-1 == selectionLocation) {
                selectionLocation = 0;
            }
            mDataParser.newState(new Memo(s.toString(), selectionLocation));
        } else {
            isUndoOrRedo = false;
        }

        updateUndoAndRedo();
    }

    private void updateUndoAndRedo() {
        undoItem.setEnabled(mDataParser.lastSize() > 0);
        redoItem.setEnabled(mDataParser.nextSize() > 0);
    }

    /**
     * 分享按钮
     */
    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.action_share);
        String text = editText.getText().toString();
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    /**
     * 捕获Back按键
     */
    @Override
    public void onBackPressed() {
        exitOperation();
    }

    /**
     * 退出时的操作，用于 重写Back键 与 导航键
     */
    private void exitOperation() {
        String tmp = editText.getText().toString();

        if (mode == MODE_NEW || MODE_TODAY == mode) {
            if (tmp.length() > 0) {
                dbFlag = DB_SAVE;
            }
        } else if (mode == MODE_EDIT) {
            if (tmp.length() > 0) {
                if (!tmp.equals(note.getContent())) {
                    dbFlag = DB_UPDATE;
                }
            } else {
                dbFlag = DB_DELETE;
            }
        }

        if (dbFlag == DB_SAVE) {
            createNote();
        } else if (dbFlag == DB_UPDATE) {
            updateNote();
        } else if (dbFlag == DB_DELETE) {
            deleteNote();
        }
        finish();
    }

    private void deleteNote() {
        // 物理数据存储，以改代删
        note.setDeleted(Note.TRUE);
        if (note.getGuid() != 0) {
            note.setSynStatus(SynStatusUtils.DELETE);
        }
        ProviderUtils.updateNote(mContext, note);

        // 更新笔记本状态
        int notebookId = note.getNoteBookId();
        NoteBookUtils.updateNoteBook(mContext, notebookId, -1);
    }

    private void createNote() {
        // 不允许新建一条空的笔记
        if (0 == editText.getText().toString().trim().length()) {
            return;
        }

        note.setContent(editText.getText().toString());
        note.setSynStatus(SynStatusUtils.NEW);
        int groupId = PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID);
        note.setNoteBookId(groupId);
        note.setUpdTime(TimeUtils.getCurrentTimeInLong());

        // 物理数据存储
        ProviderUtils.insertNote(mContext, note);

        // 更新笔记本
        NoteBookUtils.updateNoteBook(mContext, groupId, +1);
    }

    private void updateNote() {
        note.setContent(editText.getText().toString());
        note.setSynStatus(SynStatusUtils.UPDATE);

        note.setUpdTime(TimeUtils.getCurrentTimeInLong());
        // 物理数据存储
        ProviderUtils.updateNote(mContext, note);
    }

    private void deleteNoteWithDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_all_confirm)
                .setPositiveButton(android.R.string.ok, new
                        DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteNote();
                                finish();
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

}
