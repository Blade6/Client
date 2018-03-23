package com.example.jianhong.note.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.jianhong.note.R;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.provider.NoteProvider;
import com.example.jianhong.note.ui.activity.MainActivity;
import com.example.jianhong.note.ui.adapter.NoteRVAdapter;
import com.example.jianhong.note.ui.view.FloatingActionButton;
import com.example.jianhong.note.utils.PreferencesUtils;

import java.util.List;

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        NoteRVAdapter.ItemLongPressedListener, NoteRVAdapter.OnItemSelectListener {
    public static final String TAG = SearchFragment.class.getSimpleName();

    private static final int LOADER_ID = 113 + 1;//以示区分
    //MODE_LIST相关
    private Context mContext;
    private LoaderManager loaderManager;

    //nice FloatingButton
    private FloatingActionButton fabButton;
    private NoteRVAdapter mAdapter;
    private RecyclerView recyclerView;

    private void initValues() {
        mContext = getActivity();
    }

    public void configLayoutManager() {
        int columnNum = 2;
        if (PreferencesUtils.getBoolean(PreferencesUtils.ONE_COLUMN)) {
            columnNum = 1;
        }
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(columnNum,
                StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_recycler, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_notes);
        configLayoutManager();

        mAdapter = new NoteRVAdapter(mContext, null, null, null);
        recyclerView.setAdapter(mAdapter);

        loaderManager = getLoaderManager();

        // We don't need it begin load immediately when we enter SearchMode
        // loaderManager.initLoader(LOADER_ID, null, this);

        fabButton = (FloatingActionButton) view.findViewById(R.id.fab);
        fabButton.setVisibility(View.GONE);

        // In this fragment,we don't need it
        view.findViewById(R.id.refresher).setEnabled(false);
        return view;
    }

    public void clearResult() {
        if (null == mAdapter) return;
        mAdapter.swapCursor(null);
    }

    public static final String SELECTION = NoteDB.CONTENT + " LIKE ?";
    private String selection;
    private String[] selectionArgs;

    public void startSearch(String[] selectionArgs) {
        this.selection = SELECTION;
        this.selectionArgs = selectionArgs;
        refreshUI();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(mContext, NoteProvider.BASE_URI,
                NoteProvider.STANDARD_PROJECTION, selection, selectionArgs, NoteProvider.STANDARD_SORT_ORDER);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void refreshUI() {
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDestroy() {
//        if (null != mAdapter && null != mAdapter.getCursor()) {
//            mAdapter.getCursor().close();
//        }
        super.onDestroy();
    }

    /**
     *-----------------------------------------选中行为管理---------------------------------------------
     */

    private Menu mContextMenu;
    private ActionMode mActionMode;
    private int tmpNoteBookId;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onActionItemClicked(ActionMode arg0, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete:
                    deleteNotes();
                    break;
                case R.id.i_move:
                    moveNotes();
                    break;
                case R.id.i_select_all:
                    selectAll();
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode arg0) {
            ((MainActivity) mContext).unlockDrawerLock();

            showFAB();

            mActionMode = null;
            mContextMenu = null;
            mAdapter.setCheckMode(false);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode arg0, Menu menu) {
            ((MainActivity) mContext).lockDrawerLock();

            dismissFAB();

            mContextMenu = menu;
            updateActionMode();
            return false;
        }

    };

    @Override
    public void startActionMode() {
        if (mActionMode != null) {
            return;
        }
        mActionMode = ((MainActivity) mContext).startActionMode(mActionModeCallback);
    }

    public void updateActionMode() {
        if (mAdapter.getSelectedCount() <= 1) {
            mContextMenu.findItem(R.id.selected_counts).setTitle(mContext.getString(R.string
                    .selected_one_count, mAdapter.getSelectedCount()));
        } else {
            mContextMenu.findItem(R.id.selected_counts).setTitle(mContext.getString(R.string
                    .selected_more_count, mAdapter.getSelectedCount()));
        }
    }

    @Override
    public void onSelect() {
        updateActionMode();
    }

    @Override
    public void onCancelSelect() {
        updateActionMode();
    }

    private void selectAll() {
        mAdapter.selectAllNotes();
    }

    /**
     *-----------------------------------------FAB管理---------------------------------------------
     */

    public void dismissFAB() {
        fabButton.hide(true);
    }

    public void showFAB() {
        fabButton.hide(false);
    }

    /**
     *-----------------------------------------笔记管理---------------------------------------------
     */

    private void moveNotes() {
        if (mAdapter.getSelectedCount() == 0) {
            Toast.makeText(mContext, R.string.delete_select_nothing, Toast
                    .LENGTH_SHORT).show();
        } else {
            View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout
                    .dialog_radiogroup, (ViewGroup) ((Activity) mContext).getWindow()
                    .getDecorView(), false);

            final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id
                    .rg_dialog);
            RadioButton note = (RadioButton) view.findViewById(R.id.rb_note);
            NoteDB db = NoteDB.getInstance(mContext);
            List<NoteBook> list = db.loadNoteBooks();
            for (final NoteBook noteBook : list) {
                if (noteBook.getId() == 0) {
                    // ignore
                } else {
                    RadioButton tempButton = new RadioButton(mContext);
                    tempButton.setText(noteBook.getName());
                    radioGroup.addView(tempButton, LinearLayout.LayoutParams
                            .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    tempButton.setOnCheckedChangeListener(new CompoundButton
                            .OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton,
                                                     boolean b) {
                            if (b) {
                                tmpNoteBookId = noteBook.getId();
                            }
                        }
                    });
                }
            }

            note.setChecked(true);
            tmpNoteBookId = 0;
            note.setOnCheckedChangeListener(new CompoundButton
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        tmpNoteBookId = 0;
                    }
                }
            });

            final Dialog dialog = new AlertDialog.Builder(mContext).setTitle(R.string
                    .action_move).setView(view).setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.moveSelectedNotes(tmpNoteBookId);
                            if (mActionMode != null) {
                                mActionMode.finish();
                            }

                        }
                    }).setNegativeButton(android.R.string.cancel, null).create();
            dialog.show();
        }
    }

    private void deleteNotes() {
        if (mAdapter.getSelectedCount() == 0) {
            Toast.makeText(mContext, R.string.delete_select_nothing, Toast
                    .LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(R.string.delete_all_confirm)
                    .setPositiveButton(android.R.string.ok, new
                        DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.deleteSelectedNotes();
                                if (mActionMode != null) {
                                    mActionMode.finish();
                                }

                            }
                        }).setNegativeButton(android.R.string.cancel, null).create().show();
        }
    }

}
