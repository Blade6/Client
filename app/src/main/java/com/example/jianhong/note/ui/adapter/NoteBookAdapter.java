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
import com.example.jianhong.note.utils.PrefrencesUtils;
import com.example.jianhong.note.utils.ProviderUtils;

import java.util.HashMap;
import java.util.Set;

public class NoteBookAdapter extends CursorAdapter implements View.OnClickListener,
        View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = NoteBookAdapter.class.getSimpleName();

    public static final int SPECIAL_ITEM_NUM = 1;
    private LayoutInflater mLayoutInflater;
    private boolean mCheckMode;
    private HashMap<Integer, NoteBook> mCheckedItems;
    private ItemLongPressedListener mItemLongPressedListener;
    private OnItemSelectListener mOnItemSelectListener;
    private OnItemClickListener mOnItemClickListener;

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

    public void checkBoxChanged(int _id, NoteBook noteBook, boolean isChecked) {
        if (isChecked) {
            mCheckedItems.put(_id, noteBook);

            if (null != mOnItemSelectListener) {
                mOnItemSelectListener.onSelect();
            }
        } else {
            mCheckedItems.remove(_id);

            if (null != mOnItemSelectListener) {
                mOnItemSelectListener.onCancelSelect();
            }
        }
    }

    public interface ItemLongPressedListener {
        void startActionMode();

        void onLongPress(NoteBook noteBook);
    }

    public interface OnItemSelectListener {
        void onSelect();

        void onCancelSelect();
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public void setItemLongPressedListener(ItemLongPressedListener mItemLongPressedListener) {
        this.mItemLongPressedListener = mItemLongPressedListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener mOnItemSelectListener) {
        this.mOnItemSelectListener = mOnItemSelectListener;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public NoteBookAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
    }

    public NoteBookAdapter(Context context, Cursor c, int flags, ItemLongPressedListener
            itemLongPressedListener, OnItemSelectListener onItemSelectListener) {
        this(context, c, flags);
        mItemLongPressedListener = itemLongPressedListener;
        mOnItemSelectListener = onItemSelectListener;
    }

    public NoteBookAdapter(Context context, Cursor c, int flags, ItemLongPressedListener
            itemLongPressedListener, OnItemSelectListener onItemSelectListener,
                           OnItemClickListener onItemClickListener) {
        this(context, c, flags, itemLongPressedListener, onItemSelectListener);
        mOnItemClickListener = onItemClickListener;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public HashMap<Integer, NoteBook> getCheckedItems() {
        return mCheckedItems;
    }

    @Override
    public int getCount() {
//        注意当 未配置 cursor 时，需要返回0
        if (null != mCursor) {
            return super.getCount() + SPECIAL_ITEM_NUM;
        }
        return 0;
    }

    private View mView;
    private Holder mHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        mDataValid 等疑似 父类 域,仅 v4 包中有效
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        if (convertView == null) {
//            newView()中实际并未使用 mCursor，所以没有问题
            mView = newView(mContext, mCursor, parent);
            mHolder = new Holder();
            mHolder.itemLayout = (LinearLayout) mView.findViewById(R.id.ll_folder_unit);
            mHolder.flag = (ImageView) mView.findViewById(R.id.iv_folder_unit_flag);
            mHolder.name = (TextView) mView.findViewById(R.id.tv_folder_unit_name);
            mHolder.num = (TextView) mView.findViewById(R.id.tv_folder_unit_num);
            mHolder.checkBox = (CheckBox) mView.findViewById(R.id.cb_folder_unit);
            mHolder.divider = mView.findViewById(R.id.v_divider);
            mView.setTag(mHolder);
        } else {
            mView = convertView;
            mHolder = (Holder) mView.getTag();
        }

        if (position == 0) {
            bindFirstView(mView);
        } else {
            int newPosition = position - 1;
            if (!mCursor.moveToPosition(newPosition)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            }
            bindView(mView, mContext, mCursor);
        }
        return mView;
    }

    private void bindFirstView(View mView) {
        int bookId = PrefrencesUtils.getInt(PrefrencesUtils.NOTEBOOK_ID);
        if (0 == bookId) {
//            mHolder.flag.setVisibility(View.VISIBLE);
            mHolder.itemLayout.setBackgroundResource(R.drawable.abc_list_pressed_holo_dark);
        } else {
//            mHolder.flag.setVisibility(View.INVISIBLE);
            mHolder.itemLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }

        mHolder.name.setText(R.string.all_notes);
        mHolder.checkBox.setVisibility(View.INVISIBLE);
//        mHolder.divider.setVisibility(View.VISIBLE);
        mHolder.divider.setVisibility(View.GONE);
        mHolder.itemLayout.setTag(R.string.notebook_data, null);//似乎很有必要
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View commonView = mLayoutInflater.inflate(R.layout.drawer_folder_item, parent, false);
        final View hover = commonView.findViewById(R.id.ll_folder_unit);
        hover.setOnClickListener(this);
        hover.setOnLongClickListener(this);
        return commonView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NoteBook noteBook = NoteDB.initNoteBook(cursor);
        if (noteBook.getId() == PrefrencesUtils.getInt(PrefrencesUtils.NOTEBOOK_ID)) {
//            mHolder.flag.setVisibility(View.VISIBLE);
            mHolder.itemLayout.setBackgroundResource(R.drawable.abc_list_pressed_holo_dark);
        } else {
//            mHolder.flag.setVisibility(View.INVISIBLE);
            mHolder.itemLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }
        mHolder.name.setText(noteBook.getName());
        mHolder.num.setText("" + noteBook.getNotesNum());
//        mHolder.divider.setVisibility(View.INVISIBLE);
        mHolder.divider.setVisibility(View.GONE);
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
        View divider;
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
//            由于牵连甚广，退出删除模式后，这里我们让它维持原状态
//            mCheckedItems = null;
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

    public void deleteNoteBook(NoteBook noteBook) {
//        仍旧以改代删
        deleteNotesByBookId(noteBook.getId());
        noteBook.setNotesNum(0);
        noteBook.setDeleted(NoteBook.TRUE);
        ProviderUtils.updateNoteBook(mContext, noteBook);
    }

    private void deleteNotesByBookId(int bookId) {
        Cursor cursor = mContext.getContentResolver().query(NoteProvider.BASE_URI, NoteProvider
                        .STANDARD_PROJECTION, NoteDB.NOTEBOOK_ID + " = ?", new String[]{"" + bookId},
                null);

        if (cursor.moveToFirst()) {
            do {
                Note note = NoteDB.initNote(cursor);
                note.setDeleted(Note.TRUE);
                ProviderUtils.updateNote(mContext, note);
            } while (cursor.moveToNext());
        }

        if (null != cursor) {
            cursor.close();
        }

    }

}
