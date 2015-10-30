package com.andronblog.restos.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andronblog.restos.contract.RestosContract.Cities;

public class RestosDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "assorti.db";
    public static final int DB_VERSION = 1;

    public interface Tables {
        String CITIES = "cities";
    }

    public RestosDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int _oldVersion, int _newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CITIES);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.CITIES + " (" +
                Cities._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Cities.CITY_NAME + " TEXT NOT NULL, " +
                Cities.POPULATION + " INTEGER NOT NULL" +
        ");");
    }
  
}
