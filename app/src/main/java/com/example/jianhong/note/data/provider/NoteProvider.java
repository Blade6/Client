package com.example.jianhong.note.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.db.NoteOpenHelper;
import com.example.jianhong.note.utils.AccountUtils;
import com.example.jianhong.note.utils.SynStatusUtils;

public class NoteProvider extends ContentProvider {
    private static final int NOTE_DIR = 1;
    private static final int NOTE_ITEM = 2;
    private static final int NOTEBOOK_DIR = 3;
    private static final int NOTEBOOK_ITEM = 4;
    private static final int SYN = 5;

    public static final String AUTHORITY = "com.example.jianhong.provider"; // 与AndroidManifest.xml中的一致
    public static final String TABLE_USER = "table_user";
    public static final String TABLE_NOTE = NoteDB.TABLE_NOTE;
    public static final String TABLE_NOTEBOOK = NoteDB.TABLE_NOTEBOOK;

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NOTE);
    public static final Uri NOTEBOOK_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NOTEBOOK);
    public static final Uri SYN_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_USER);

    public static final String[] STANDARD_PROJECTION = {NoteDB.ID + " AS _id", NoteDB.CONTENT,
            NoteDB.EDIT_TIME, NoteDB.CREATE_TIME, NoteDB.SYN_STATUS, NoteDB.GUID,
            NoteDB.BOOK_GUID, NoteDB.DELETED, NoteDB.NOTEBOOK_ID};
    public static final String STANDARD_SORT_ORDER = NoteDB.EDIT_TIME + " desc";

    public static final String[] NOTEBOOK_PROJECTION = {NoteDB.ID + " AS _id", NoteDB.NAME,
            NoteDB.SYN_STATUS, NoteDB.NOTEBOOK_GUID, NoteDB.DELETED, NoteDB.NOTES_NUM};

    public static final String[] USER_PROJECTION = {"user_id", "syn_status", "syn_uid"};

    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TABLE_NOTE, NOTE_DIR);
        uriMatcher.addURI(AUTHORITY, TABLE_NOTE + "/#", NOTE_ITEM);
        uriMatcher.addURI(AUTHORITY, TABLE_NOTEBOOK, NOTEBOOK_DIR);
        uriMatcher.addURI(AUTHORITY, TABLE_NOTEBOOK + "/#", NOTEBOOK_ITEM);
        uriMatcher.addURI(AUTHORITY, TABLE_USER, SYN);
    }

    private NoteOpenHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new NoteOpenHelper(getContext(), NoteDB.DB_NAME, null, NoteDB.VERSION);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
                queryBuilder.setTables(TABLE_NOTE);
                queryBuilder.appendWhere(NoteDB.DELETED + "!='" + SynStatusUtils.TRUE + "'"
                        + " and " + NoteDB.USER_ID + "='" + AccountUtils.getUserId() + "'");
                break;
            case NOTE_ITEM:
                queryBuilder.setTables(TABLE_NOTE);
                queryBuilder.appendWhere(NoteDB.ID + "=" + uri.getLastPathSegment());
                break;
            case NOTEBOOK_DIR:
                queryBuilder.setTables(TABLE_NOTEBOOK);
                queryBuilder.appendWhere(NoteDB.DELETED + "!='" + SynStatusUtils.TRUE + "'"
                        + " and " + NoteDB.USER_ID + "='" + AccountUtils.getUserId() + "'");
                break;
            case NOTEBOOK_ITEM:
                queryBuilder.setTables(TABLE_NOTEBOOK);
                queryBuilder.appendWhere(NoteDB.ID + "=" + uri.getLastPathSegment());
                break;
            case SYN:
                queryBuilder.setTables(TABLE_USER);
                queryBuilder.appendWhere("user_id" + "=" + AccountUtils.getUserId());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updateCount = 0;
        String where = "";

        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
                updateCount = db.update(TABLE_NOTE, values, selection, selectionArgs);
                break;
            case NOTE_ITEM:
                if (!TextUtils.isEmpty(selection)) {
                    where += " and " + selection;
                }
                updateCount = db.update(TABLE_NOTE, values, NoteDB.ID + "=" + uri
                        .getLastPathSegment() + where, selectionArgs);
                break;
            case NOTEBOOK_DIR:
                updateCount = db.update(TABLE_NOTEBOOK, values, selection, selectionArgs);
                break;
            case NOTEBOOK_ITEM:
                if (!TextUtils.isEmpty(selection)) {
                    where += " and " + selection;
                }
                updateCount = db.update(TABLE_NOTEBOOK, values, NoteDB.ID + "=" + uri
                        .getLastPathSegment() + where, selectionArgs);
                break;
            case SYN:
                updateCount = db.update(TABLE_USER, values, "user_id" + "=" +
                        values.get("user_id"), selectionArgs);
                break;

            default:
                break;
        }
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (values.containsKey(NoteDB.ID)) {
            values.remove(NoteDB.ID);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri itemUri = null;
        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
                // / Begin 此部分代码仅适用于 对 Note 判定
                String content = values.getAsString(NoteDB.CONTENT);
                if (null == content || content.trim().length() == 0) {
                    return null;
                }
                // / End
                long newID = db.insert(TABLE_NOTE, null, values);
                if (newID > 0) {
                    itemUri = ContentUris.withAppendedId(uri, newID);
                    getContext().getContentResolver().notifyChange(itemUri, null);
                }
                break;
            case NOTEBOOK_DIR:
                long newBookID = db.insert(TABLE_NOTEBOOK, null, values);
                if (newBookID > 0) {
                    itemUri = ContentUris.withAppendedId(uri, newBookID);
                    getContext().getContentResolver().notifyChange(itemUri, null);
                }
                break;
            default:
                break;
        }

        return itemUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowAffected = 0;

        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
                rowAffected = db.delete(TABLE_NOTE, selection, selectionArgs);
                break;
            case NOTE_ITEM:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowAffected = db.delete(TABLE_NOTE, NoteDB.ID + " = ?", new String[]{"" + id});
                } else {
                    rowAffected = db.delete(TABLE_NOTE, selection + " and " + NoteDB.ID + "=" +
                            id, selectionArgs);
                }
                break;
            case NOTEBOOK_DIR:
                rowAffected = db.delete(TABLE_NOTEBOOK, selection, selectionArgs);
                break;
            case NOTEBOOK_ITEM:
                String bookId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowAffected = db.delete(TABLE_NOTEBOOK, NoteDB.ID + " = ?", new String[]{""
                            + bookId});
                } else {
                    rowAffected = db.delete(TABLE_NOTEBOOK, selection + " and " + NoteDB.ID +
                            "=" + bookId, selectionArgs);
                }
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowAffected;
    }
}
