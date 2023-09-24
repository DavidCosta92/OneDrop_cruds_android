package com.example.one_drop_cruds;

import android.content.Context;
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
        //
        db.execSQL("CREATE TABLE glycemia (\n"+
                " id_reg_glucemia INTEGER PRIMARY KEY AUTOINCREMENT, \n"+
                " date DATETIME NOT NULL, \n"+
                " value REAL, \n"+
                " notes TEXT\t\n"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // cuando se va modificando la app

    }

}
