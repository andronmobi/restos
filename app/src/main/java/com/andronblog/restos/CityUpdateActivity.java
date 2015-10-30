package com.andronblog.restos;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andronblog.restos.contract.RestosContract.Cities;

public class CityUpdateActivity extends Activity {

    private EditText mEditCityName;
    private EditText mEditCityPopulation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_update);

        Bundle bundle = getIntent().getExtras();
        final int cityId = bundle.getInt(Cities._ID);
        String cityName = bundle.getString(Cities.CITY_NAME);
        int population = bundle.getInt(Cities.POPULATION);

        mEditCityName = (EditText) findViewById(R.id.et_city);
        mEditCityName.setText(cityName);
        mEditCityPopulation = (EditText) findViewById(R.id.et_population);
        mEditCityPopulation.setText(Integer.toString(population));

        Button btn = (Button) findViewById(R.id.btn_insert_city_in_db);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cityName = mEditCityName.getText().toString();
                int population = Integer.parseInt(mEditCityPopulation.getText().toString());

                ContentValues values = new ContentValues();
                values.put(Cities.CITY_NAME, cityName);
                values.put(Cities.POPULATION, population);
                int count = updateCityInDB(cityId, values);

                String text = "Updated rows: " + count;
                Toast toast = Toast.makeText(CityUpdateActivity.this, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    private int updateCityInDB(int cityId, ContentValues values) {
        ContentResolver cr = getContentResolver();
        // method #1: without whereArgs
        //int count = cr.update(Cities.CONTENT_URI, values, Cities._ID + "=" + cityId, null);
        // method #2: with whereArgs
        //int count = cr.update(Cities.CONTENT_URI, values, Cities._ID + "=?", new String[]{Integer.toString(cityId)});
        // method #3: with uri id
        Uri uri = Uri.withAppendedPath(Cities.CONTENT_URI, "/" + cityId);
        int count = cr.update(uri, values, null, null);
        return count;
    }
}
