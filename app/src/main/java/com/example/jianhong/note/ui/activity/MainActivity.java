package com.example.jianhong.note.ui.activity;

import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
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

import com.example.jianhong.note.R;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.entity.Common;
import com.example.jianhong.note.utils.CommonUtils;
import com.example.jianhong.note.utils.LogUtils;
import com.example.jianhong.note.utils.SPUtils;
import com.example.jianhong.note.utils.SystemUtils;
import com.example.jianhong.note.utils.TimeUtils;
import com.example.jianhong.note.ui.fragment.ChangeBgFragment;
import com.example.jianhong.note.ui.fragment.AboutFragment;
import com.example.jianhong.note.ui.view.NoteRecyclerView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    // version code
    private int versionCode;
    private Context mContext;
    private Calendar today;

    public static final int MODE_LIST = 0;
    public static final int MODE_GRID = 1;
    private int mode;

    private NoteRecyclerView noteRecyclerView;
    //private FiltratePage filtratePage;

    protected FloatingActionButton fab;
    public DrawerLayout drawer;
    Toolbar toolbar;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteActivity.writeTodayNewNote(MainActivity.this);
            }
        });

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
        boolean first = (Boolean) SPUtils.get(mContext, "first", true);
        LogUtils.d(TAG, "first:" + first);
        if (first) {
            firstLaunch();
            setVersionCode();
        }

        changeContent();

        initBgPic();

        // 设置当前文件夹
        Common.setNoteBookId(0);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mgr_note) {

        } else if (id == R.id.nav_change_bg) {
            setTitle(R.string.change_bg);
            ChangeBgFragment changeBgFragment=new ChangeBgFragment();
            changeFragment(changeBgFragment);
            fab.hide();
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_about) {
            setTitle(R.string.about_app);
            AboutFragment aboutAppFragment=new AboutFragment();
            changeFragment(aboutAppFragment);
            fab.hide();
        } else if (id == R.id.nav_exit) {
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        SPUtils.remove(MainActivity.this, "user_name");
        SPUtils.remove(MainActivity.this, "pwd");
        finish();
        System.exit(0);
    }

    protected void changeFragment(Fragment fragment)
    {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.main_fraglayout, fragment,null);
        //    ft.addToBackStack(fragment.toString());
        ft.commit();
    }

    private void initBgPic()
    {
        SystemUtils systemUtils = new SystemUtils(this);
        String path=systemUtils.getPath();
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
        SPUtils.put(mContext, "version_code", versionCode);
    }

    private void firstLaunch() {
        SPUtils.put(mContext, "first", false);

        //如果是第一次启动应用，首先创建笔记本表，
        NoteBook noteBook = new NoteBook();
        noteBook.setName(getString(R.string.default_notebook));
        noteBook.setNotesNum(0);
        noteBook.setNotebookGuid(0L);
        NoteDB.getInstance(mContext).saveNoteBook_initDB(noteBook);

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

    public void changeContent() {
        if (mode == MODE_LIST) {
            if (null == noteRecyclerView) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fraglayout, new NoteRecyclerView()).commit();
            }
            unlockDrawerLock();//打开手势滑动
        } else if (mode == MODE_GRID) {//暂时弃用

        }
    }

    public void lockDrawerLock() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭手势滑动
    }

    public void unlockDrawerLock() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//关闭手势滑动
    }

    @Override
    public void onRefresh() {
        // todo 同步数据
    }
}
