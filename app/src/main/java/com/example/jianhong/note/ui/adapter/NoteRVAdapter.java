package com.example.jianhong.note.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jianhong.note.R;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.ui.activity.NoteActivity;
import com.example.jianhong.note.utils.CommonUtils;
import com.example.jianhong.note.utils.LogUtils;
import com.example.jianhong.note.utils.NoteBookUtils;
import com.example.jianhong.note.utils.SPUtils;
import com.example.jianhong.note.utils.TimeUtils;
import com.example.jianhong.note.utils.ProviderUtils;

import java.util.HashMap;
import java.util.Set;

public class NoteRVAdapter extends RecyclerView.Adapter<NoteRVAdapter.NoteItemHolder>
        implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = NoteRVAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private Cursor mCursor;
    private boolean mDataValid;
    private Context mContext;
    private boolean mCheckMode;
    private HashMap<Integer, Note> mCheckedItems;
    private ItemLongPressedListener mItemLongPressedListener;
    private OnItemSelectListener mOnItemSelectListener;

    public interface ItemLongPressedListener {
        void startActionMode();
    }

    public interface OnItemSelectListener {
        void onSelect();

        void onCancelSelect();
    }

    public void setmItemLongPressedListener(ItemLongPressedListener mItemLongPressedListener) {
        this.mItemLongPressedListener = mItemLongPressedListener;
    }

    public void setmOnItemSelectListener(OnItemSelectListener mOnItemSelectListener) {
        this.mOnItemSelectListener = mOnItemSelectListener;
    }

    public NoteRVAdapter(Context context, Cursor cursor) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCursor = cursor;
        boolean cursorPresent = (null != cursor);
        mDataValid = cursorPresent;
    }

    public NoteRVAdapter(Context context, Cursor cursor, OnItemSelectListener
            onItemSelectListener, ItemLongPressedListener itemLongPressedListener) {
        this(context, cursor);
        mOnItemSelectListener = onItemSelectListener;
        mItemLongPressedListener = itemLongPressedListener;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public NoteItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.note_rv_item, viewGroup, false);
        NoteItemHolder noteItemHolder = new NoteItemHolder(view);
        return noteItemHolder;
    }

    @Override
    public void onBindViewHolder(NoteItemHolder noteItemHolder, int i) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(i)) {
            throw new IllegalStateException("couldn't move cursor to position " + i);
        }

        Note note = NoteDB.initNote(mCursor);
        noteItemHolder.itemLayout.setTag(R.string.note_data, note);
        noteItemHolder.itemLayout.setOnClickListener(this);
        noteItemHolder.itemLayout.setOnLongClickListener(this);
//      noteItemHolder.title.setText(note.getContentFromHtml().toString().trim());
        noteItemHolder.title.setText(note.getContent());

        // 默认按照最后修改时间排序
        if ((Boolean) SPUtils.get(mContext, "CREATE_ORDER", true)) {
            noteItemHolder.editTime.setText(CommonUtils.timeStamp(note));
        } else {
            noteItemHolder.editTime.setText(TimeUtils.getConciseTime(note.getUpdTime(), mContext));
        }

//        主要用于批量操作时，notifyDataSetChanged()之后改变背景
        if (mCheckMode) {
            if (isChecked(note.getId())) {
                noteItemHolder.itemLayout.setBackgroundResource(R.drawable.hover_multi_background_normal);
            } else {
                noteItemHolder.itemLayout.setBackgroundResource(R.drawable.hover_border_normal);
            }
        } else {
            noteItemHolder.itemLayout.setBackgroundResource(R.drawable.hover_background);
        }
    }


    @Override
    public int getItemCount() {
        if (mDataValid && null != mCursor) return mCursor.getCount();
        return 0;
    }


    class NoteItemHolder extends RecyclerView.ViewHolder {
        View itemLayout;
        TextView title;
        TextView editTime;

        public NoteItemHolder(View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.rv_item_container);
            title = (TextView) itemView.findViewById(R.id.note_content);
            editTime = (TextView) itemView.findViewById(R.id.edit_time);
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.rv_item_container == v.getId()) {
            LogUtils.d(TAG, "mCheckMode:" + mCheckMode);
            if (!mCheckMode) {
                NoteActivity.actionStart(mContext, (Note) v.getTag(R.string.note_data), NoteActivity.MODE_EDIT);
            } else {
                Note note = (Note) v.getTag(R.string.note_data);
                toggleCheckedId(note.getId(), note, v);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!mCheckMode) {
            if (null != mItemLongPressedListener) {
                mItemLongPressedListener.startActionMode();
            }
            setCheckMode(true);
        }
        Note note = (Note) v.getTag(R.string.note_data);
        toggleCheckedId(note.getId(), note, v);
        return true;
    }

    private boolean isChecked(int id) {
        if (null == mCheckedItems) {
            return false;
        }
        return mCheckedItems.containsKey(id);
    }

    public int getSelectedCount() {
        if (mCheckedItems == null) {
            return 0;
        } else {
            return mCheckedItems.size();
        }
    }

    public void setCheckMode(boolean check) {
        if (!check) {
            mCheckedItems = null;
        }
        if (check != mCheckMode) {
            mCheckMode = check;
            notifyDataSetChanged();
        }
    }

    public void toggleCheckedId(int _id, Note note, View v) {
        if (mCheckedItems == null) {
            mCheckedItems = new HashMap<Integer, Note>();
        }
        if (!mCheckedItems.containsKey(_id)) {
            mCheckedItems.put(_id, note);

            if (null != mOnItemSelectListener) {
                mOnItemSelectListener.onSelect();
            }
        } else {
            mCheckedItems.remove(_id);

            if (null != mOnItemSelectListener) {
                mOnItemSelectListener.onCancelSelect();
            }
        }
        notifyDataSetChanged();
    }

    public void deleteSelectedNotes() {
        if (mCheckedItems == null || mCheckedItems.size() == 0) {
            return;
        } else {
            Set<Integer> keys = mCheckedItems.keySet();
            SparseIntArray affectedNotebooks = new SparseIntArray(mCheckedItems.size());
            for (Integer key : keys) {
                Note note = mCheckedItems.get(key);
                note.setSynStatus(Note.DELETE);
                note.setDeleted(Note.TRUE);
                ProviderUtils.updateNote(mContext, note);

//                更新受到影响的笔记本的应删除数值
                if (0 != note.getNoteBookId()) {
                    int num = affectedNotebooks.get(note.getNoteBookId());
                    affectedNotebooks.put(note.getNoteBookId(), num + 1);
                }
            }
            NoteBookUtils.updateNoteBook(mContext, 0, -mCheckedItems.size());
            for (int i = 0; i < affectedNotebooks.size(); i++) {
                int key = affectedNotebooks.keyAt(i);
                int value = affectedNotebooks.valueAt(i);
                NoteBookUtils.updateNoteBook(mContext, key, -value);
            }

            mCheckedItems.clear();

            if (null != mOnItemSelectListener) {
                mOnItemSelectListener.onCancelSelect();
            }

//            new Evernote(mContext).sync(true, false, null);
        }
    }

    public void moveSelectedNotes(int toNotebookId) {
        if (mCheckedItems == null || mCheckedItems.size() == 0) {
            return;
        } else {
            Set<Integer> keys = mCheckedItems.keySet();
            SparseIntArray affectedNotebooks = new SparseIntArray(mCheckedItems.size());
            for (Integer key : keys) {
                Note note = mCheckedItems.get(key);

                // 更新受到影响的笔记本中的数值
                if (0 != note.getNoteBookId()) {
                    int num = affectedNotebooks.get(note.getNoteBookId());
                    affectedNotebooks.put(note.getNoteBookId(), num + 1);
                }

                note.setNoteBookId(toNotebookId);
                ProviderUtils.updateNote(mContext, note);

            }
            if (0 != toNotebookId) {
                NoteBookUtils.updateNoteBook(mContext, toNotebookId, +mCheckedItems.size());
            }
            for (int i = 0; i < affectedNotebooks.size(); i++) {
                int key = affectedNotebooks.keyAt(i);
                int value = affectedNotebooks.valueAt(i);
                NoteBookUtils.updateNoteBook(mContext, key, -value);
            }

            mCheckedItems.clear();

            if (null != mOnItemSelectListener) {
                mOnItemSelectListener.onCancelSelect();
            }

        }
    }

    public void selectAllNotes() {
        for (int i = 0; i < mCursor.getCount(); i++) {
            mCursor.moveToPosition(i);
            Note note = NoteDB.initNote(mCursor);

            if (mCheckedItems == null) {
                mCheckedItems = new HashMap<Integer, Note>();
            }
            int _id = note.getId();
            if (!mCheckedItems.containsKey(_id)) {
                mCheckedItems.put(_id, note);
            }

        }

        if (null != mOnItemSelectListener) {
            mOnItemSelectListener.onSelect();
        }

        notifyDataSetChanged();
    }

}
