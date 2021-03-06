package com.andronblog.restos.provider.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andronblog.restos.provider.R;
import com.andronblog.restos.provider.RestosDatabaseHelper;
import com.andronblog.restos.provider.RestosDatabaseHelper.Tables;
import com.andronblog.restos.contract.RestosContract.Cities;

public class CityInsertActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_insert);

        Button btn = (Button) findViewById(R.id.btn_insert_city_in_db);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText et = (EditText) findViewById(R.id.et_city);
                String cityName = et.getText().toString();
                et = (EditText) findViewById(R.id.et_population);
                int population = Integer.parseInt(et.getText().toString());

                ContentValues values = new ContentValues();
                values.put(Cities.CITY_NAME, cityName);
                values.put(Cities.POPULATION, population);
                long rowID = insertCityInDB(values);

                String text = "The city is inserted at " + rowID;
                Toast toast = Toast.makeText(CityInsertActivity.this, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    private long insertCityInDB(ContentValues values) {
        RestosDatabaseHelper dbHelper = new RestosDatabaseHelper(CityInsertActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID = db.insert(Tables.CITIES, null, values);
        return rowID;
    }
}
