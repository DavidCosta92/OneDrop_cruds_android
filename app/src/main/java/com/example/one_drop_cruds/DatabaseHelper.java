package com.example.one_drop_cruds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos
    public static final String databaseName = "SignLog.db";

    // Constructor de la clase
    public DatabaseHelper(@Nullable Context context) {
        // Llama al constructor de la clase base SQLiteOpenHelper
        super(context, "SignLog.db", null, 1);
    }

    // Método llamado cuando se crea la base de datos por primera vez
    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        // Ejecuta una sentencia SQL para crear la tabla "users"
        MyDatabase.execSQL("create Table users(email TEXT primary key, password TEXT)");
    }

    // Método llamado cuando se necesita actualizar la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        // Elimina la tabla "users" si existe una versión anterior
        MyDB.execSQL("drop Table if exists users");
        // Llama a onCreate para recrear la tabla
        onCreate(MyDB);
    }

    // Método para insertar datos en la tabla "users"
    public Boolean insertData(String email, String password){
        // Obtiene una instancia de la base de datos en modo escritura
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        // Crea un objeto ContentValues para almacenar los valores a insertar
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        // Realiza la inserción de datos en la tabla "users"
        long result = MyDatabase.insert("users", null, contentValues);

        // Verifica si la inserción fue exitosa
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    // Método para verificar si un correo electrónico existe en la tabla "users"
    public Boolean checkEmail(String email){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        // Ejecuta una consulta SQL para buscar registros con el email proporcionado
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ?", new String[]{email});

        // Verifica si se encontraron registros
        if(cursor.getCount() > 0) {
            return true; // Existe el correo electrónico
        }else {
            return false; // No existe el correo electrónico
        }
    }

    // Método para verificar si un correo electrónico y contraseña coinciden en la tabla "users"
    public Boolean checkEmailPassword(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        // Ejecuta una consulta SQL para buscar registros con el email y contraseña proporcionados
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ? and password = ?", new String[]{email, password});

        // Verifica si se encontraron registros
        if (cursor.getCount() > 0) {
            return true; // Coinciden el correo electrónico y contraseña
        }else {
            return false; // No coinciden el correo electrónico y contraseña
        }
    }
}
