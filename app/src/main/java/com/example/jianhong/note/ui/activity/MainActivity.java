package com.example.jianhong.note.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jianhong.note.R;
import com.example.jianhong.note.app.NoteApplication;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.net.BooksData;
import com.example.jianhong.note.data.net.NetBroker;
import com.example.jianhong.note.data.net.NetData;
import com.example.jianhong.note.data.net.NotesData;
import com.example.jianhong.note.entity.HttpCallbackListener;
import com.example.jianhong.note.entity.Response;
import com.example.jianhong.note.ui.fragment.NoteBookFragment;
import com.example.jianhong.note.ui.fragment.SearchFragment;
import com.example.jianhong.note.ui.widget.MySwipeRefreshLayout;
import com.example.jianhong.note.utils.AccountUtils;
import com.example.jianhong.note.utils.CommonUtils;
import com.example.jianhong.note.utils.HttpUtils;
import com.example.jianhong.note.utils.JSONUtils;
import com.example.jianhong.note.utils.LogUtils;
import com.example.jianhong.note.utils.PreferencesUtils;
import com.example.jianhong.note.utils.ProviderUtils;
import com.example.jianhong.note.utils.SynStatusUtils;
import com.example.jianhong.note.utils.SystemUtils;
import com.example.jianhong.note.utils.TimeUtils;
import com.example.jianhong.note.ui.fragment.ChangeBgFragment;
import com.example.jianhong.note.ui.fragment.NoteRVFragment;
import com.example.jianhong.note.utils.UrlUtils;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    // version code
    private int versionCode;
    private Context mContext;
    private Calendar today;

    private SearchFragment searchFragment;
    private NoteRVFragment noteRVFragment;

    public DrawerLayout drawer;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOverflowShowingAlways();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        today = Calendar.getInstance();
        mContext = MainActivity.this;
        versionCode = CommonUtils.getVersionCode(mContext);

        LogUtils.d(TAG, "以防忘记，这里打印一下，告诉你，此为本地版本，无服务器");
        LogUtils.d(TAG, "带服务器方式：增加105-108行注释；更改WelcomeActivity为authLogin");
        AccountUtils.setUserId(1000);
        AccountUtils.setUserName("hjh");
        first_use(AccountUtils.getUserName());

        initBgPic(); // 感觉这个要废掉
        goToNoteRVFragment();

        // / 配置全局Handler
        NoteApplication application = (NoteApplication) getApplication();
        application.setHandler(new SyncHandler());

        LogUtils.d(TAG, "NoteBook:");
        List<NoteBook> list = NoteDB.getInstance(mContext).loadRawNoteBook();
        for (NoteBook nb : list) {
            LogUtils.d(TAG, nb.toString());
        }
        LogUtils.d(TAG, "Note:");
        List<Note> nlist = NoteDB.getInstance(mContext).loadRawNote();
        for (Note note : nlist) {
            LogUtils.d(TAG, note.toString());
        }
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

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ComponentName componentName = getComponentName();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint(getString(R.string.search_note));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                LogUtils.d(TAG, "search:"+s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //recyclerAdapter.getFilter().filter(s);
                if (null != searchFragment) {
                    if (0 != s.length()) {
                        searchFragment.startSearch(new String[]{"%" + s + "%"});
                    } else {
                        searchFragment.clearResult();
                    }
                }
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                goToNoteRVFragment();
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                searchFragment = new SearchFragment();
                changeFragment(searchFragment);
                return true;  // Return true to expand action view
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sync) {
            onRefresh();
        } else if (id == R.id.action_setting) {
            SettingsActivity.actionStart(mContext);
        } else if (id == R.id.action_about) {
            AboutActivity.activityStart(mContext);
        } else if (id == R.id.menu_search) {

        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            goToNoteRVFragment();
        } else if (id == R.id.nav_mgr_note) {
            setTitle(R.string.mgr_note);
            NoteBookFragment noteBookFragment = new NoteBookFragment();
            changeFragment(noteBookFragment);
        } else if (id == R.id.nav_change_bg) {
            setTitle(R.string.change_bg);
            ChangeBgFragment changeBgFragment = new ChangeBgFragment();
            changeFragment(changeBgFragment);
        } else if (id == R.id.nav_exit) {
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        AccountUtils.clearAllInfos();
        finish();
        //System.exit(0);
    }

    private void changeFragment(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fraglayout, fragment, null);
        ft.commit();
    }

    public void goToNoteRVFragment() {
        noteRVFragment = new NoteRVFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fraglayout, noteRVFragment).commit();

        navigationView.getMenu().getItem(0).setChecked(true); // 修改导航栏选中项
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


//    /**
//     * 返回键方法，切换更换壁纸或者笔记管理时，点击返回键会返回主页
//     */
//    private void back(MenuItem item) {
//        item.setChecked(false); // 关闭选中
//        closeDrawer();
//
//        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openDrawer();
//
//                goToNoteRVFragment();
//            }
//        });
//    }
//
//    /**
//     * 开启抽屉，隐藏返回键
//     */
//    public void openDrawer() {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        toggle.setDrawerIndicatorEnabled(true);
//    }
//
//    /**
//     * 关闭抽屉，出现返回键
//     */
//    public void closeDrawer() {
//        toggle.setDrawerIndicatorEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }

    public void lockDrawerLock() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭手势滑动
    }

    public void unlockDrawerLock() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//关闭手势滑动
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
        PreferencesUtils.putInt(PreferencesUtils.VERSION_CODE, versionCode);
    }

    private void firstLaunch() {
        //如果是第一次启动应用，首先创建笔记本表，
        NoteBook noteBook = new NoteBook();
        noteBook.setSynStatus(SynStatusUtils.NEW);
        noteBook.setName(getString(R.string.default_notebook));
        noteBook.setNotesNum(2);
        noteBook.setNotebookGuid(0L);
        NoteDB.getInstance(mContext).insertDefaultNoteBook(noteBook);
        PreferencesUtils.putInt(PreferencesUtils.JIAN_NUM, 2);

        // 然后在数据库中添加note
        Note one = new Note();
        one.setSynStatus(SynStatusUtils.NEW);
        one.setContent(getString(R.string.tip1));
        one.setCreateTime(TimeUtils.getCurrentTimeInLong());
        one.setEditTime(TimeUtils.getCurrentTimeInLong());
        NoteDB.getInstance(mContext).insertNote(one);

        Calendar tmpCal = (Calendar) today.clone();
        tmpCal.add(Calendar.DAY_OF_MONTH, -1);

        Note two = new Note();
        two.setSynStatus(SynStatusUtils.NEW);
        two.setContent(getString(R.string.tip2));
        two.setCreateTime(TimeUtils.getCurrentTimeInLong());
        two.setEditTime(TimeUtils.getCurrentTimeInLong());
        NoteDB.getInstance(mContext).insertNote(two);

        NoteDB.getInstance(mContext).insertSyn(AccountUtils.getUserId(), 1);
    }

    /**
     *-----------------------------------------同步相关---------------------------------------------
     */

    @Override
    public void onRefresh() {
        // todo hejianhong 同步数据
        setRefreshing(true);
        needSyn(new SyncHandler());
    }

    private void setRefreshing(boolean b) {
        if (null == noteRVFragment || !noteRVFragment.isVisible()) return;
        MySwipeRefreshLayout refreshLayout = noteRVFragment.getRefreshLayout();
        refreshLayout.setRefreshing(b);

        if (b) {
            refreshLayout.setEnabled(false);
        } else {
            refreshLayout.setEnabled(true);
        }
    }

    /**
     * 0 不用同步
     * 1 upload
     * 2 download
     * @return
     */
    public void needSyn(final SyncHandler syncHandler) {
        String address = UrlUtils.GETSYNURL + "user_id/" + AccountUtils.getUserId();
        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Response res = JSONUtils.handleResponse(response);
                if (!res.getReturnCode()) {
                    LogUtils.d(TAG, "syn fail");
                }
                else {
                    long server_uid = res.getSynUid();
                    long local_uid = NoteDB.getInstance(mContext).loadSyn();
                    LogUtils.d(TAG, "server syn_id:" + server_uid + ", local syn_id:" + local_uid);

                    if (local_uid > server_uid) {
                        upload(server_uid, syncHandler);
                    } else if (local_uid < server_uid) {
                        download(server_uid, syncHandler);
                    } else {
                        // ignore
                        LogUtils.d(TAG, "No need to sync");
                        syncHandler.sendEmptyMessage(SYN_NO_NEED);
                    }
                }
            }
            @Override
            public void onError(Exception e) {
                LogUtils.d(TAG, "exception");
            }
        });
    }

    public void upload(Long server_uid, final SyncHandler syncHandler) {
        List<NoteBook> synNoteBooks = NoteDB.getInstance(mContext).loadSynNoteBooks();
        List<Note> synNotes = NoteDB.getInstance(mContext).loadSynNotes();

        if (synNoteBooks.size() > 0 || synNotes.size() > 0) {
            LogUtils.d(TAG, "synNoteBooks:");
            for (NoteBook nb : synNoteBooks) {
                LogUtils.d(TAG, nb.toString());
            }

            LogUtils.d(TAG, "synNotes:");
            for (Note n: synNotes) {
                LogUtils.d(TAG, n.toString());
            }

            final BooksData booksData = SynStatusUtils.booksToServer(synNoteBooks);
            final NotesData notesData = SynStatusUtils.notesToServer(synNotes);
            NetData netData = new NetData(AccountUtils.getUserId(), booksData, notesData);

            final String jsonData = JSONUtils.converJavaBeanToJson(netData);
            LogUtils.d(TAG, jsonData);

            new Thread() {
                @Override
                public void run() {
                    String result = HttpUtils.doJsonPost(UrlUtils.SynuURL, jsonData);
                    LogUtils.d(TAG, result);
                    if (result != null && result != "") {
                        Response res = JSONUtils.handleResponse(result);

                        NetBroker.handleUploadResult(mContext, res.getData(), booksData, notesData);
                    }
                    syncHandler.sendEmptyMessage(SYN_SUCC);
                }
            }.start();

            NoteDB.getInstance(mContext).updateSyn(AccountUtils.getUserId(), server_uid+2);
        }
    }

    public void download(final long server_uid, final SyncHandler syncHandler) {
        String address = UrlUtils.SyndURL + "user_id/" + AccountUtils.getUserId();
        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Response res = JSONUtils.handleResponse(response);
                if (!res.getReturnCode()) {
                    LogUtils.d(TAG, "同步失败");
                    syncHandler.sendEmptyMessage(SYN_ERROR);
                }
                else {
                    NetBroker.handleDownloadResult(mContext, res.getData(), AccountUtils.getUserId());
                    NoteDB.getInstance(mContext).updateSyn(AccountUtils.getUserId(), server_uid);
                    syncHandler.sendEmptyMessage(SYN_SUCC);
                }

            }

            @Override
            public void onError(Exception e) {
                LogUtils.d(TAG, "程序故障");
                syncHandler.sendEmptyMessage(SYN_ERROR);
            }
        });
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
        NoteBook noteBook = new NoteBook();
        noteBook.setSynStatus(SynStatusUtils.NEW);
        noteBook.setName(name);
        ProviderUtils.insertNoteBook(mContext, noteBook);

        SynStatusUtils.setSyn(mContext);
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
                            noteBook.setSynStatus(SynStatusUtils.UPDATE);
                            ProviderUtils.updateNoteBook(mContext, noteBook);

                            SynStatusUtils.setSyn(mContext);
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

    /**
     *-----------------------------------------handler相关---------------------------------------------
     */

    public static final int NEED_CONFIG_LAYOUT = 0x0010;
    public static final int NEED_RECREATE = 0x0011;
    public static final int SYN_SUCC = 0x0012;
    public static final int SYN_ERROR = 0x0013;
    public static final int SYN_NO_NEED = 0x0014;

    public class SyncHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NEED_CONFIG_LAYOUT:
                    noteRVFragment.configLayoutManager();
                    break;
                case NEED_RECREATE:
                    recreate();
                    break;
                case SYN_SUCC:
                    setRefreshing(false);
                    goToNoteRVFragment();
                    Toast.makeText(mContext, "同步完成", Toast.LENGTH_SHORT).show();
                    break;
                case SYN_ERROR:
                    setRefreshing(false);
                    Toast.makeText(mContext, "同步失败！", Toast.LENGTH_SHORT).show();
                    break;
                case SYN_NO_NEED:
                    setRefreshing(false);
                    Toast.makeText(mContext, "数据已是最新！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     *-----------------------------------------SharedPreferences------------------------------------
     */

    private void first_use(String user_name) {
        SharedPreferences sp = this.getSharedPreferences("note", Context.MODE_PRIVATE);
        if(sp.getBoolean(user_name + " first_use", true)) {
            firstLaunch();
            setVersionCode();

            Editor editor = sp.edit();
            editor.putBoolean(user_name + " first_use", false);
            editor.commit();
        }
    }

}
