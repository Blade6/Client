package com.example.jianhong.note.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.jianhong.note.R;
import com.example.jianhong.note.ui.activity.MainActivity;
import com.example.jianhong.note.ui.adapter.NoteBookAdapter;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.provider.NoteProvider;
import com.example.jianhong.note.utils.LogUtils;
import com.example.jianhong.note.utils.PreferencesUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by jianhong on 2018/3/16.
 */

public class NoteBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        NoteBookAdapter.ItemLongPressedListener, NoteBookAdapter.OnItemClickListener {
    private static final String TAG = NoteBookFragment.class.getSimpleName();

    private static final int MODE_NOTHING = 0;
    private static final int MODE_DELETE = 1;
    private static int mode = MODE_NOTHING;

    private Context mContext;
    private Activity activity;
    private NoteBookAdapter noteBookAdapter;
    private LayoutInflater inflater;
    private ListView listView;

    private LoaderManager loaderManager;
    private static final int NOTEBOOK_LOADER_ID = 112;

    private Button folder_delete;
    private Button folder_new;

    private void init_values() {
        activity = this.getActivity();
        mContext = activity.getApplicationContext();
        inflater =  LayoutInflater.from(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_values();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //引入我们的布局
        View v = inflater.inflate(R.layout.fragment_notebook, container, false);
        folder_delete = (Button) v.findViewById(R.id.btn_folder_delete);
        folder_new = (Button) v.findViewById(R.id.btn_folder_new);

        listView = (ListView) v.findViewById(R.id.notebook_list);
        noteBookAdapter = new NoteBookAdapter(mContext, null, 0, this, this);
        listView.setAdapter(noteBookAdapter);

        loaderManager = getLoaderManager();
        loaderManager.initLoader(NOTEBOOK_LOADER_ID, null, this);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        folder_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == MODE_NOTHING) {
                    ((MainActivity)activity).showCreateFolderDialog();
                } else if (mode == MODE_DELETE) {
                    if (noteBookAdapter.isChecked())
                        ((MainActivity)activity).trash(NoteBookFragment.this);
                }
            }
        });

        folder_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFooter();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(mContext, NoteProvider.NOTEBOOK_URI,
                NoteProvider.NOTEBOOK_PROJECTION, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        noteBookAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        noteBookAdapter.swapCursor(null);
    }

    @Override
    public void startActionMode() {

    }

    @Override
    public void onLongPress(NoteBook noteBook) {
        if (null != noteBook) {
            if (noteBook.getId() != 0) ((MainActivity) activity).showRenameFolderDialog(noteBook);
        } else {
            LogUtils.d(TAG, "onLongPress:noteBook is null");
        }
    }

    @Override
    public void onItemClick(View view) {
        // 进行删除操作时无视之
        if (mode == MODE_DELETE) return;

        NoteBook noteBook = (NoteBook) view.getTag(R.string.notebook_data);
        if (null != noteBook) {
            int newId = noteBook.getId();
            String newName = noteBook.getName();
            changeToBook(newId, newName);
            refresh_UI();
        } else {
            LogUtils.d(TAG, "onItemClick:view.getTag is null");
        }
    }

    public void changeToBook(int newId, String newName) {
        PreferencesUtils.putInt(PreferencesUtils.NOTEBOOK_ID, newId);
        PreferencesUtils.putString(PreferencesUtils.NOTEBOOK_NAME, newName);
    }

    public void refresh_UI() {
        // 点击文件夹后跳转该文件页面
        // 跳转前别忘了打开抽屉
        ((MainActivity) activity).goToNoteRVFragment();
    }

    public void trash() {
        int quickId = PreferencesUtils.getInt(PreferencesUtils.LIGHTNING_EXTRACT_SAVE_LOCATION);

        HashMap<Integer, NoteBook> map = noteBookAdapter.getCheckedItems();
        if (null == map || 0 == map.size()) {
            return;
        } else {
            Set<Integer> keys = map.keySet();
            for (Integer key : keys) {
                NoteBook noteBook = map.get(key);
                int noteBookId = noteBook.getId();
                if (noteBookId == quickId) {
                    PreferencesUtils.putInt(PreferencesUtils.LIGHTNING_EXTRACT_SAVE_LOCATION, 0);
                }
                if (noteBookId == PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID)) {
                    changeToBook(0, getString(R.string.default_notebook));
                }
                noteBookAdapter.deleteNoteBook(noteBook);
            }
            noteBookAdapter.destroyCheckedItems();
        }
        changeFooter();
    }

    public void changeFooter() {
        if (mode == MODE_NOTHING) {
            mode = MODE_DELETE;
            if (null != noteBookAdapter) {
                noteBookAdapter.setCheckMode(true);
            }
        } else if (mode == MODE_DELETE) {
            mode = MODE_NOTHING;
            if (null != noteBookAdapter) {
                noteBookAdapter.setCheckMode(false);
            }
        }
        setFooter();
    }

    public void setFooter() {
        if (mode == MODE_NOTHING) {
            folder_new.setText(R.string.folder_new);
            folder_delete.setText(R.string.folder_delete);
        } else if (mode == MODE_DELETE) {
            folder_new.setText(R.string.folder_delete);
            folder_delete.setText(R.string.folder_cancel);
        }
    }

}
