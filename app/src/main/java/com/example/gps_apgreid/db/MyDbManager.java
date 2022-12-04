package com.example.gps_apgreid.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gps_apgreid.DBHelper;
import com.example.gps_apgreid.adapter.ListItem;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyDbManager {
    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public MyDbManager(Context context){
        this.context = context;
        dbHelper = new DBHelper(context);

    }

    public void openDb(){
        db = dbHelper.getWritableDatabase();
    }

    public void insertToDb(String title, String speed){
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_NAME, title);
        cv.put(DBHelper.KEY_SPEED, speed);
        db.insert(DBHelper.TABLE_CONTACTS, null, cv);
    }

    public void delete(int id){
        String selection = dbHelper.KEY_ID + "=" + id;
        db.delete(dbHelper.TABLE_CONTACTS, selection, null);
    }

    public List<ListItem> getFromDb(){
        List<ListItem> tempList = new ArrayList<>();
        Cursor cursor = db.query(dbHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            ListItem item = new ListItem();
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME));
            @SuppressLint("Range") String speed = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEED));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID));
            item.setTitle(title);
            item.setSpeed(speed);
            item.setId(_id);
            tempList.add(item);
        }
        cursor.close();
        return tempList;
    }

    public void closeDb(){
        dbHelper.close();
    }
}
