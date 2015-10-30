package com.andronblog.restos;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.andronblog.restos.contract.RestosContract.Cities;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";

    /** Cursor of available cities */
    private Cursor mCursor;
    private SimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] columns = {Cities._ID, Cities.CITY_NAME, Cities.POPULATION};
        String orderBy = Cities._ID;

        mCursor = getContentResolver().query(Cities.CONTENT_URI, columns, null, null, orderBy);
        mCursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, mCursor,
                new String[] {Cities.CITY_NAME, Cities.POPULATION},
                new int[] {android.R.id.text1, android.R.id.text2});
        ListView lv = (ListView) findViewById(R.id.lv_cities);
        lv.setAdapter(mCursorAdapter);

        Button btn = (Button) findViewById(R.id.btn_add_city);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CityInsertActivity.class);
                startActivity(intent);
            }
        });

        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context_listview, menu);
        menu.setHeaderTitle("Database record");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_update: {
                Intent intent = new Intent(this, CityUpdateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Cities._ID, mCursor.getInt(mCursor.getColumnIndex(Cities._ID)));
                bundle.putString(Cities.CITY_NAME, mCursor.getString(mCursor.getColumnIndex(Cities.CITY_NAME)));
                bundle.putInt(Cities.POPULATION, mCursor.getInt(mCursor.getColumnIndex(Cities.POPULATION)));
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
            case R.id.menu_item_delete: {
                int count = deleteSelectedCityFromDB();
                Toast toast = Toast.makeText(this, "Deleted rows: " + count, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            }
        }
        return true;
    }

    private int deleteSelectedCityFromDB() {
        ContentResolver cr = getContentResolver();
        String cityId = mCursor.getString(mCursor.getColumnIndex(Cities._ID));
        // method #1: without whereArgs
        //int count = cr.delete(Cities.CONTENT_URI, Cities._ID + "=" + cityId, null);
        // method #2: with whereArgs
        //int count = cr.delete(Cities.CONTENT_URI, Cities._ID + "=?", new String[]{cityId});
        // method #3: with uri id
        Uri uri = Uri.withAppendedPath(Cities.CONTENT_URI, "/" + cityId);
        int count = cr.delete(uri, null, null);
        return count;
    }
}
