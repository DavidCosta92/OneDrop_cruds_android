package com.example.one_drop_cruds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText add_value_gly, add_notes_gly, add_date_gly;
    AdminSQLiteOpenHelper admin;

    // RECICLER VIEW
    RecyclerView rv1;
    List<Integer> reg_gly_ids = new ArrayList<Integer>();
    List<String> reg_gly_dates = new ArrayList<String>();
    List<Double> reg_gly_values = new ArrayList<Double>();
    List<String> reg_gly_notes = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        admin = new AdminSQLiteOpenHelper(this, "bd_one_drop", null, 1); // version es para las futuras modificaciones de la estructura de la bd
        add_value_gly = findViewById(R.id.add_value_gly);
        add_notes_gly = findViewById(R.id.add_notes_gly);
        add_date_gly = findViewById(R.id.add_date_gly);
        this.getRegGly();

        // RECICLER VIEW
        rv1 = findViewById(R.id.recyclerView_reg_gly);
        LinearLayoutManager linearLayoutManager_reg_gly = new LinearLayoutManager(this);
        rv1.setLayoutManager(linearLayoutManager_reg_gly);
        rv1.setAdapter(new AdapterRegGly());

    }

    // Clase que se encargara de CREAR todos los elementos
    private class AdapterRegGly extends RecyclerView.Adapter<AdapterRegGly.AdapterRegGlyHolder> {
        @NonNull
        @Override
        public AdapterRegGlyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdapterRegGlyHolder(getLayoutInflater().inflate(R.layout.layout_reg_gly_view,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterRegGlyHolder holder, int position) {
            holder.printItem(position);
        }

        @Override
        public int getItemCount() {
            return reg_gly_ids.size(); // debe retornar la cantidad de registros..
        }
        // Clase que se encargara de IMPRIMIR todos los elementos
        private class AdapterRegGlyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView reg_id;
            TextView reg_date;
            TextView reg_value;
            TextView reg_note;
            public AdapterRegGlyHolder(@NonNull View itemView) {
                super(itemView);
                reg_id = itemView.findViewById(R.id.recycler_reg_gly_id);
                reg_date = itemView.findViewById(R.id.recycler_reg_gly_date);
                reg_value = itemView.findViewById(R.id.recycler_reg_gly_value);
                reg_note = itemView.findViewById(R.id.recycler_reg_gly_note);
                itemView.setOnClickListener(this);
            }
            public void printItem(int position) {
                reg_id.setText(String.valueOf(reg_gly_ids.get(position)));
                reg_date.setText(reg_gly_dates.get(position));
                reg_value.setText(String.valueOf(reg_gly_values.get(position)));
                reg_note.setText(reg_gly_notes.get(position));
            }
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, String.valueOf(reg_gly_ids.get(getLayoutPosition())),Toast.LENGTH_SHORT).show();
            }
        }
    }

      public void getRegGly(){
        SQLiteDatabase bd = admin.getWritableDatabase(); // abre la bd
        // este obj ejecuta la consulta a bd
        Cursor fila = bd.rawQuery("SELECT * FROM glycemia", null);

        if (fila.moveToFirst()){
            do {
                System.out.println(Integer.valueOf(fila.getInt(0)));
                reg_gly_ids.add(Integer.valueOf(fila.getInt(0)));
                reg_gly_dates.add(fila.getString(1));
                reg_gly_values.add(fila.getDouble(2));
                reg_gly_notes.add(fila.getString(3));
            }
            while (fila.moveToNext());
            // this.renderRegGly();

        } else{
            Toast.makeText(this, "Aun no hay registros guardados..", Toast.LENGTH_LONG).show();
        }
        bd.close(); // cierro conexion bd
    }
    public void getRegGly (View v){
        this.getRegGly();
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