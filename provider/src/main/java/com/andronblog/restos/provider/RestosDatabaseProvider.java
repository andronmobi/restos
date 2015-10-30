package com.andronblog.restos.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.andronblog.restos.contract.RestosContract;
import com.andronblog.restos.contract.RestosContract.Cities;
import com.andronblog.restos.provider.RestosDatabaseHelper.Tables;

public class RestosDatabaseProvider extends ContentProvider {

    private static final String TAG = "RestosDatabaseProvider";

    private static final int CITIES = 110;
    private static final int CITIES_ID = 111;

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(RestosContract.AUTHORITY, "cities", CITIES);
        sUriMatcher.addURI(RestosContract.AUTHORITY, "cities/#", CITIES_ID);
    }

    private SQLiteDatabase mDB;

    @Override
    public boolean onCreate() {
        try {
            return initialize();
        } catch (RuntimeException e) {
            Log.e(TAG, "Cannot start provider", e);
        }
        return false;
    }

    private boolean initialize() {
        Context context = getContext();
        RestosDatabaseHelper dbHelper = new RestosDatabaseHelper(context);
        mDB = dbHelper.getWritableDatabase();
        return (mDB == null) ? false : true;
    }
  
    /**
     * Inserts an argument at the beginning of the selection arg list.
     */
    private String[] insertSelectionArg(String[] selectionArgs, String arg) {
        if (selectionArgs == null) {
            return new String[] {arg};
        } else {
            int newLength = selectionArgs.length + 1;
            String[] newSelectionArgs = new String[newLength];
            newSelectionArgs[0] = arg;
            System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, selectionArgs.length);
            return newSelectionArgs;
        }
    }
  
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
    
        Uri groupUri = null;
        String table;
        final int match = sUriMatcher.match(uri);
    
        switch (match) {
        case CITIES:
            table = Tables.CITIES;
            break;
        case CITIES_ID: {
            table = Tables.CITIES;
            long id = ContentUris.parseId(uri);
            whereArgs = insertSelectionArg(whereArgs, String.valueOf(id));
            where = Cities._ID + "=?" + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
            groupUri = Cities.CONTENT_URI;
            break;
        }
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Log.d(TAG, "delete where: " + where + ", whereArgs: " + whereArgs);
        int count = mDB.delete(table, where, whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            if (groupUri != null) {
                getContext().getContentResolver().notifyChange(groupUri, null);
            }
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        String type;
        final int match = sUriMatcher.match(uri);
    
        switch (match) {
        case CITIES:
            type = Cities.CONTENT_TYPE;
            break;
        case CITIES_ID: {
            type = Cities.CONTENT_ITEM_TYPE;
            break;
        }
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return type;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String table;
        final int match = sUriMatcher.match(uri);
        switch (match) {
        case CITIES:
            table = Tables.CITIES;
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Log.d(TAG, "insert values: " + values);
        long rowID = mDB.insert(table, null, values);
        // Return a URI to the newly inserted row on success.
        if (rowID > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }
  
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CITIES:
            qb.setTables(Tables.CITIES);
            break;
        case CITIES_ID: {
            long id = ContentUris.parseId(uri);
            qb.setTables(Tables.CITIES);
            selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(id));
            qb.appendWhere(Cities._ID + "=?");
          break;
        }
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Log.d(TAG, "query projection: " + projection + ", selection: " + selection + ", selectionArgs: " + selectionArgs);
        Cursor c = qb.query(mDB, projection, selection, selectionArgs, null, null, sortOrder);
        // Register the contexts ContentResolver to be notified if
        // the cursor result set changes.
        if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    
        String table;
        final int match = sUriMatcher.match(uri);
    
        switch (match) {
        case CITIES:
            table = Tables.CITIES;
            break;
        case CITIES_ID: {
            table = Tables.CITIES;
            long id = ContentUris.parseId(uri);
            selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(id));
            selection = Cities._ID + "=?" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
            break;
        }
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Log.d(TAG, "update values: " + values + ", selection: " + selection + ", selectionArgs: " + selectionArgs);
        int count = mDB.update(table, values, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    
        return count;
    }

}
