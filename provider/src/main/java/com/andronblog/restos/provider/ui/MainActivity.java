package com.andronblog.restos.provider.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.andronblog.restos.provider.R;
import com.andronblog.restos.provider.RestosDatabaseHelper;
import com.andronblog.restos.provider.RestosDatabaseHelper.Tables;

public class MainActivity extends Activity {

    private SimpleCursorAdapter mCursorAdapter;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RestosDatabaseHelper dbHelper = new RestosDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] columns = {Cities._ID, Cities.CITY_NAME, Cities.POPULATION};
        String orderBy = Cities._ID;

        mCursor = db.query(Tables.CITIES, columns, null, null, null, null, orderBy, null);
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

    /**
     * Since we work directly with DB without ContentProvider which should send
     * notifications to ContentResolver we have to take care to update manually
     * the cursor and its adapter.
     */
    private void requery() {
        mCursor.requery(); // It's not a good way to call it from (Main) UI thread.
        mCursorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        requery();
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
                requery();
                break;
            }
        }
        return true;
    }

    private int deleteSelectedCityFromDB() {
        RestosDatabaseHelper dbHelper = new RestosDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String cityId = mCursor.getString(mCursor.getColumnIndex(Cities._ID));
        // method #1: without whereArgs
        //int count = db.delete(Tables.CITIES, Cities._ID + "=" + cityId, null);
        // method #2: with whereArgs
        int count = db.delete(Tables.CITIES, Cities._ID + "=?", new String[]{cityId});
        return count;
    }
}
