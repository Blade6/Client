package com.example.jianhong.note.ui.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jianhong.note.R;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.ui.fragment.NoteBookFragment;
import com.example.jianhong.note.utils.AccountUtils;
import com.example.jianhong.note.utils.CommonUtils;
import com.example.jianhong.note.utils.LogUtils;
import com.example.jianhong.note.utils.PrefrencesUtils;
import com.example.jianhong.note.utils.ProviderUtils;
import com.example.jianhong.note.utils.SystemUtils;
import com.example.jianhong.note.utils.TimeUtils;
import com.example.jianhong.note.ui.fragment.ChangeBgFragment;
import com.example.jianhong.note.ui.fragment.AboutFragment;
import com.example.jianhong.note.ui.fragment.NoteRecyclerView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    // version code
    private int versionCode;
    private Context mContext;
    private Calendar today;

    //private FiltratePage filtratePage;

    public DrawerLayout drawer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        today = Calendar.getInstance();
        mContext = MainActivity.this;
        versionCode = CommonUtils.getVersionCode(mContext);
        first_use();

        initBgPic(); // 感觉这个要废掉

        List<NoteBook> list = NoteDB.getInstance(mContext).loadNoteBooks();
        for (NoteBook nb : list) {
            LogUtils.d(TAG, nb.toString());
        }

        goToNoteRecyclerViewFragment();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {

        } else if (id == R.id.action_about) {
            setTitle(R.string.action_about);
            AboutFragment aboutAppFragment=new AboutFragment();
            changeFragment(aboutAppFragment);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mgr_note) {
            setTitle(R.string.mgr_note);
            NoteBookFragment noteBookFragment = new NoteBookFragment();
            changeFragment(noteBookFragment);
        } else if (id == R.id.nav_change_bg) {
            setTitle(R.string.change_bg);
            ChangeBgFragment changeBgFragment = new ChangeBgFragment();
            changeFragment(changeBgFragment);
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_exit) {
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void first_use() {
        SharedPreferences sp = this.getSharedPreferences("note", Context.MODE_PRIVATE);
        if(sp.getBoolean("first_use", true)) {
            firstLaunch();
            setVersionCode();

            Editor editor = sp.edit();
            editor.putBoolean("first_use", false);
            editor.commit();
        }
    }

    private void logout() {
        AccountUtils.clearAllInfos();
        finish();
        System.exit(0);
    }

    private void changeFragment(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fraglayout, fragment, null);
        ft.commit();
    }

    public void goToNoteRecyclerViewFragment() {
        LogUtils.d(TAG, "goToNoteRe...Fragment");
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fraglayout, new NoteRecyclerView()).commit();
    }

    private void initBgPic()
    {
        SystemUtils systemUtils = new SystemUtils(this);
        String path = systemUtils.getPath();
        if(path!=null) {
            Bitmap bitmap = systemUtils.getBitmapByPath(this, path);
            if (bitmap != null) {
                drawer.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            }
        }
    }

    /*
    标注版本号
     */
    private void setVersionCode() {
        PrefrencesUtils.putInt(PrefrencesUtils.VERSION_CODE, versionCode);
    }

    private void firstLaunch() {
        //如果是第一次启动应用，首先创建笔记本表，
        NoteBook noteBook = new NoteBook();
        noteBook.setName(getString(R.string.default_notebook));
        noteBook.setNotesNum(2);
        noteBook.setNotebookGuid(0L);
        NoteDB.getInstance(mContext).saveNoteBook_initDB(noteBook);
        PrefrencesUtils.putInt(PrefrencesUtils.JIAN_NUM, 2);

        // 然后在数据库中添加note
        Note one = new Note();
        one.setCalToTime(today);
        one.setContent(getString(R.string.tip1));
        one.setUpdTime(TimeUtils.getCurrentTimeInLong());
        NoteDB.getInstance(mContext).saveNote(one);

        Calendar tmpCal = (Calendar) today.clone();
        tmpCal.add(Calendar.DAY_OF_MONTH, -1);

        Note two = new Note();
        two.setCalToTime(tmpCal);
        two.setContent(getString(R.string.tip2));
        two.setUpdTime(TimeUtils.getCurrentTimeInLong());
        NoteDB.getInstance(mContext).saveNote(two);
    }

    @Override
    public void onRefresh() {
        // todo 同步数据
    }

    /**
     *-----------------------------------------会话相关---------------------------------------------
     */

    public void showCreateFolderDialog() {
        LogUtils.d(TAG, "showCreateFolderDialog");
        View view = getLayoutInflater().inflate(R.layout.dialog_edittext, (ViewGroup) getWindow()
                .getDecorView(), false);
        final EditText editText = (EditText) view.findViewById(R.id.et_in_dialog);

        final Dialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.create_folder_title)
                .setView(view)
                .setPositiveButton(R.string.create_folder, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().trim().length() == 0) {
                            Toast.makeText(mContext, R.string.create_folder_err, Toast.LENGTH_SHORT).show();
                        } else {
                            createFolder(editText.getText().toString().trim());
                        }
                    }
                })
                .setNegativeButton(R.string.folder_cancel, null)
                .create();

        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void createFolder(String name) {
        LogUtils.d(TAG, "folder_name:" + name);
        NoteBook noteBook = new NoteBook();
        noteBook.setName(name);
        ProviderUtils.insertNoteBook(mContext, noteBook);
    }

    public void showRenameFolderDialog(final NoteBook noteBook) {
        View view = getLayoutInflater().inflate(R.layout.dialog_edittext, (ViewGroup) getWindow()
                .getDecorView(), false);
        final EditText editText = (EditText) view.findViewById(R.id.et_in_dialog);
        editText.setText(noteBook.getName());
        editText.setSelection(noteBook.getName().length());
        final Dialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.rename_folder_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().trim().length() == 0) {
                            Toast.makeText(mContext, R.string.rename_folder_err, Toast.LENGTH_SHORT).show();
                        } else {
                            noteBook.setName(editText.getText().toString().trim());
                            ProviderUtils.updateNoteBook(mContext, noteBook);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void trash(final NoteBookFragment noteBookFragment) {
        new AlertDialog.Builder(this).
                setMessage(R.string.delete_folder_title).
                setNegativeButton(android.R.string.cancel, null).
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteBookFragment.trash();
                    }
                }).show();
    }

}
