package com.example.jianhong.note.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jianhong.note.R;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.provider.NoteProvider;
import com.example.jianhong.note.utils.PreferencesUtils;
import com.example.jianhong.note.utils.ProviderUtils;
import com.example.jianhong.note.utils.SynStatusUtils;

import java.util.HashMap;
import java.util.Set;

public class NoteBookAdapter extends CursorAdapter implements View.OnClickListener,
        View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = NoteBookAdapter.class.getSimpleName();

    private boolean mCheckMode;
    private HashMap<Integer, NoteBook> mCheckedItems;
    private ItemLongPressedListener mItemLongPressedListener;
    private OnItemClickListener mOnItemClickListener;

    public NoteBookAdapter(Context context, Cursor c, int flags, ItemLongPressedListener
            itemLongPressedListener) {
        super(context, c, flags);
        mItemLongPressedListener = itemLongPressedListener;
    }

    public NoteBookAdapter(Context context, Cursor c, int flags, ItemLongPressedListener
            itemLongPressedListener, OnItemClickListener onItemClickListener) {
        this(context, c, flags, itemLongPressedListener);
        mOnItemClickListener = onItemClickListener;
    }

    /**
     *-----------------------------------------view相关---------------------------------------------
     */

    private View mView;
    private Holder mHolder;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View commonView = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false);
        final View hover = commonView.findViewById(R.id.ll_folder_unit);
        hover.setOnClickListener(this);
        hover.setOnLongClickListener(this);
        return commonView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // mDataValid 等疑似 父类 域,仅 v4 包中有效
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        if (convertView == null) {
            // newView()中实际并未使用 mCursor，所以没有问题
            mView = newView(mContext, mCursor, parent);
            mHolder = new Holder();
            mHolder.itemLayout = (LinearLayout) mView.findViewById(R.id.ll_folder_unit);
            mHolder.flag = (ImageView) mView.findViewById(R.id.iv_folder_unit_flag);
            mHolder.name = (TextView) mView.findViewById(R.id.tv_folder_unit_name);
            mHolder.num = (TextView) mView.findViewById(R.id.tv_folder_unit_num);
            mHolder.checkBox = (CheckBox) mView.findViewById(R.id.cb_folder_unit);
            mView.setTag(mHolder);
        } else {
            mView = convertView;
            mHolder = (Holder) mView.getTag();
        }

        // Cursor下标从-1开始计数，但是第一个值在下标0处
        if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
        } else {
            if (position == 0) {
                bindFirstView(mCursor);
            } else {
                bindView(mView, mContext, mCursor);
            }
        }
        return mView;
    }

    private void bindFirstView(Cursor cursor) {
        NoteBook noteBook = NoteDB.initNoteBook(cursor);
        if (noteBook.getId() == PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID)) {
            mHolder.itemLayout.setBackgroundResource(R.drawable.abc_list_pressed_holo_dark);
        } else {
            mHolder.itemLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }

        mHolder.name.setText(R.string.default_notebook);
        mHolder.num.setText("" + PreferencesUtils.getInt(PreferencesUtils.JIAN_NUM));
        mHolder.checkBox.setVisibility(View.INVISIBLE);
        mHolder.itemLayout.setTag(R.string.notebook_data, noteBook);//似乎很有必要
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NoteBook noteBook = NoteDB.initNoteBook(cursor);
        if (noteBook.getId() == PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID)) {
            mHolder.itemLayout.setBackgroundResource(R.drawable.abc_list_pressed_holo_dark);
        } else {
            mHolder.itemLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }

        mHolder.name.setText(noteBook.getName());
        mHolder.num.setText("" + noteBook.getNotesNum());
        mHolder.itemLayout.setTag(R.string.notebook_data, noteBook);

        mHolder.checkBox.setOnCheckedChangeListener(null);
        if (mCheckMode) {
            if (null != mCheckedItems && mCheckedItems.containsKey(noteBook.getId())) {
                mHolder.checkBox.setChecked(true);
            } else {
                mHolder.checkBox.setChecked(false);
            }
            mHolder.checkBox.setVisibility(View.VISIBLE);
            mHolder.checkBox.setOnCheckedChangeListener(this);
        } else {
            mHolder.checkBox.setVisibility(View.INVISIBLE);
        }
    }

    class Holder {
        LinearLayout itemLayout;
        ImageView flag;
        TextView name;
        TextView num;
        CheckBox checkBox;
    }

    /**
     *-----------------------------------------手势操作相关-------------------------------------------
     */

    public interface ItemLongPressedListener {
        void startActionMode();

        void onLongPress(NoteBook noteBook);
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    @Override
    public void onClick(View v) {
        if (R.id.ll_folder_unit == v.getId()) {
            if (!mCheckMode) {
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClick(v);
                }
            } else {
                v.findViewById(R.id.cb_folder_unit).performClick();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!mCheckMode) {
            if (null != mItemLongPressedListener) {
                NoteBook noteBook = (NoteBook) v.getTag(R.string.notebook_data);
                mItemLongPressedListener.onLongPress(noteBook);
            }
        }

        return true;
    }

    /**
     *-----------------------------------------check相关---------------------------------------------
     */

    public boolean isChecked() {
        return !(null == mCheckedItems || 0 == mCheckedItems.size());
    }

    public void setCheckMode(boolean check) {
        if (!check) {
            // 由于牵连甚广，退出删除模式后，这里我们让它维持原状态
            // ignore
        } else {
            if (mCheckedItems == null) {
                mCheckedItems = new HashMap<Integer, NoteBook>();
            }
        }
        if (check != mCheckMode) {
            mCheckMode = check;
            notifyDataSetChanged();
        }
    }

    public void destroyCheckedItems() {
        mCheckedItems = null;
    }

    public void deleteItems() {
        if (null == mCheckedItems || 0 == mCheckedItems.size()) {
            return;
        } else {
            Set<Integer> keys = mCheckedItems.keySet();
            for (Integer key : keys) {
                NoteBook noteBook = mCheckedItems.get(key);
                deleteNoteBook(noteBook);
            }

            mCheckedItems = null;
        }
    }

    public void checkBoxChanged(int _id, NoteBook noteBook, boolean isChecked) {
        if (isChecked) {
            mCheckedItems.put(_id, noteBook);
        } else {
            mCheckedItems.remove(_id);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        View view = (View) buttonView.getParent();
        NoteBook noteBook = (NoteBook) view.getTag(R.string.notebook_data);
        if (null != noteBook) {
            checkBoxChanged(noteBook.getId(), noteBook, isChecked);
        } else {
            Log.e(TAG, "Error in onCheckedChanged(CompoundButton buttonView, boolean isChecked),"
                    + "null==noteBook");
        }
    }

    public HashMap<Integer, NoteBook> getCheckedItems() {
        return mCheckedItems;
    }

    /**
     *-----------------------------------------删除笔记本---------------------------------------------
     */

    public void deleteNoteBook(NoteBook noteBook) {
        deleteNotesByBookId(noteBook.getId());
        noteBook.setNotesNum(0);
        noteBook.setDeleted(SynStatusUtils.TRUE);
        ProviderUtils.updateNoteBook(mContext, noteBook);

        SynStatusUtils.setSyn(mContext);
    }

    private void deleteNotesByBookId(int bookId) {
        Cursor cursor = mContext.getContentResolver().query(NoteProvider.BASE_URI, NoteProvider
                        .STANDARD_PROJECTION, NoteDB.NOTEBOOK_ID + " = ?", new String[]{"" + bookId},
                null);

        if (cursor.moveToFirst()) {
            do {
                Note note = NoteDB.initNote(cursor);
                note.setDeleted(SynStatusUtils.TRUE);
                ProviderUtils.updateNote(mContext, note);
            } while (cursor.moveToNext());
        }

        if (null != cursor) {
            cursor.close();
        }

        SynStatusUtils.setSyn(mContext);

    }

}
