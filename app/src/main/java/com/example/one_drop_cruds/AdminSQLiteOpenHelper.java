package com.example.one_drop_cruds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREACION DE TABLAS se ejecuta una vez
        // REG GLUCEMIA
        db.execSQL("CREATE TABLE glycemia (\n"+
                " id_reg_glucemia INTEGER PRIMARY KEY AUTOINCREMENT, \n"+
                " date DATETIME NOT NULL, \n"+
                " value REAL, \n"+
                " notes TEXT\t\n"+
                ")");
        //USERS
        db.execSQL("CREATE TABLE users (\n"+
                " email TEXT PRIMARY KEY, \n"+
                " password TEXT NOT NULL\t\n"+
                ")");
        //PRESSURE
        db.execSQL("CREATE TABLE pressure (\n"+
                " id_reg_pressure INTEGER PRIMARY KEY AUTOINCREMENT, \n"+
                " date DATETIME NOT NULL, \n"+
                " value REAL, \n"+
                " notes TEXT\t\n"+
                ")");
        //WEIGHT
        db.execSQL("CREATE TABLE weight (\n"+
                " id_reg_weight INTEGER PRIMARY KEY AUTOINCREMENT, \n"+
                " date DATETIME NOT NULL, \n"+
                " value REAL, \n"+
                " notes TEXT\t\n"+
                ")");
    }
    public Boolean createUser(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);

        // deberia hashearse password
        contentValues.put("password", password);

        long result = MyDatabase.insert("users", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Boolean checkEmail(String email){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});

        if(cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    public Boolean checkEmailPassword(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ? and password = ?", new String[]{email, password});

        if (cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // cuando se va modificando la app
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS glycemia");
    }

}
