package com.example.one_drop_cruds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    EditText add_value_gly, add_notes_gly, add_date_gly;
    AdminSQLiteOpenHelper admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_value_gly = findViewById(R.id.add_value_gly);
        add_notes_gly = findViewById(R.id.add_notes_gly);
        add_date_gly = findViewById(R.id.add_date_gly);
        admin = new AdminSQLiteOpenHelper(this, "base_datos_vehiculos", null, 1); // version es para las futuras modificaciones de la estructura de la bd
    }
    public void add_new_reg_gly(View v){
        SQLiteDatabase bd = admin.getWritableDatabase();// abre la bd
        ContentValues new_reg_gly = new ContentValues(); // crea un objeto que luego sera un nuevo registro en la bd

        // agrego datos al objeto registro
        if(add_date_gly.getText().toString().equals("") ){
            System.out.println("CREO NUEVA FECHA NOW PORQUE VINO VACIA");
            new_reg_gly.put("date", new Date().toString());
        } else {
            System.out.println("TOMO FECHA ELEGIDA POR USER");
            new_reg_gly.put("date", add_date_gly.getText().toString());
        }
        new_reg_gly.put("value", add_value_gly.getText().toString());
        new_reg_gly.put("notes", add_notes_gly.getText().toString());
        bd.insert("glycemia", null, new_reg_gly);// inserta en tabla "glycemia"

        add_value_gly.setText("");// limpio pantalla
        add_notes_gly.setText("");
        add_date_gly.setText("");
        bd.close();// cierro conexion bd

        Toast.makeText(this,"Se agrego registro de glucemia", Toast.LENGTH_SHORT).show();
    }




}