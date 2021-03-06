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
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.example.jianhong.note.ui.activity.MainActivity;
import com.example.jianhong.note.ui.activity.NoteActivity;
import com.example.jianhong.note.ui.adapter.NoteRVAdapter;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.provider.NoteProvider;
import com.example.jianhong.note.ui.view.FloatingActionButton;
import com.example.jianhong.note.ui.widget.MySwipeRefreshLayout;
import com.example.jianhong.note.utils.PreferencesUtils;
import com.example.jianhong.note.utils.SynStatusUtils;

import java.util.List;

public class NoteRVFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        NoteRVAdapter.ItemLongPressedListener, NoteRVAdapter.OnItemSelectListener {
    public static final String TAG = NoteRVFragment.class.getSimpleName();

    private static final int LOADER_ID = 113;
    private LoaderManager loaderManager;

    private Context mContext;

    private RecyclerView mRecyclerView;
    private NoteRVAdapter mAdapter;
    private MySwipeRefreshLayout refreshLayout;

    private void initValues() {
        mContext = getActivity();
    }

    public void configLayoutManager() {
        int columnNum = 2;
        if (PreferencesUtils.getBoolean(PreferencesUtils.ONE_COLUMN)) {
            columnNum = 1;
        }
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(columnNum,
                StaggeredGridLayoutManager.VERTICAL));
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public MySwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_notes);
        configLayoutManager();
        mAdapter = new NoteRVAdapter(mContext, null, this, this);
        mRecyclerView.setAdapter(mAdapter);

        loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, this);

        refreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.refresher);
        refreshLayout.setOnRefreshListener((MainActivity) mContext);

        getActivity().setTitle(PreferencesUtils.getString(PreferencesUtils.NOTEBOOK_NAME));

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteActivity.writeNewNote(getActivity());
            }
        });

        return view;
    }

    public boolean setRefresherEnabled(boolean b) {
        if (null == refreshLayout) {
            return false;
        }
        refreshLayout.setEnabled(b);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     *-----------------------------------------Loader---------------------------------------------
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int bookId = PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID);
        String selection = NoteDB.NOTEBOOK_ID + " = ?";
        String[] selectionArgs = {"" + bookId};

        String sortOrder = NoteProvider.STANDARD_SORT_ORDER;

        CursorLoader cursorLoader = new CursorLoader(mContext, NoteProvider.BASE_URI,
                NoteProvider.STANDARD_PROJECTION, selection, selectionArgs, sortOrder);
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

    /**
     *-----------------------------------------选中行为管理---------------------------------------------
     */

    private Menu mContextMenu;
    private int tmpNoteBookId;
    private ActionMode mActionMode;
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
            menu.clear();
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode arg0, Menu menu) {
            mContextMenu = menu;
            updateActionMode();

            setRefresherEnabled(false);
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mContextMenu.clear();
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.main, mContextMenu);

            mActionMode = null;
            mContextMenu = null;
            mAdapter.setCheckMode(false);
        }

    };

    @Override
    public void startActionMode() {
        // mActionMode 在Destroy中重赋为了 null
        if (mActionMode != null) {
            return;
        }
        mActionMode = ((MainActivity) mContext).startSupportActionMode(mActionModeCallback);
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
            RadioButton radioButton = (RadioButton) view.findViewById(R.id.rb_note);
            NoteDB db = NoteDB.getInstance(mContext);
            List<NoteBook> list = db.loadNoteBooks();
            for (final NoteBook noteBook : list) {
                if (noteBook.getId() == 0) {
                    if (0 == PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID)) {
                        radioButton.setChecked(true);
                    }
                } else {
                    RadioButton tempButton = new RadioButton(mContext);
                    tempButton.setText(noteBook.getName());
                    if (noteBook.getId() == PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID)) {
                        tempButton.setChecked(true);
                    }

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


            tmpNoteBookId = 0;
            radioButton.setOnCheckedChangeListener(new CompoundButton
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
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create().show();
        }
    }

}
