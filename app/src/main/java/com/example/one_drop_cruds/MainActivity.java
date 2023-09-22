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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText add_value_gly, add_notes_gly, add_date_gly;

    Button btn_add_reg_gly;
    AdminSQLiteOpenHelper admin;

    // RECICLER VIEW
    RecyclerView rv1;
    AdapterRegGly adapterRegGly;
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
        btn_add_reg_gly = findViewById(R.id.btn_add_reg_gly);
        this.updateRegGly();

        // RECICLER VIEW
        rv1 = findViewById(R.id.recyclerView_reg_gly);
        LinearLayoutManager linearLayoutManager_reg_gly = new LinearLayoutManager(this);
        rv1.setLayoutManager(linearLayoutManager_reg_gly);
        adapterRegGly = new AdapterRegGly();
        rv1.setAdapter(adapterRegGly);

    }

    @Override
    public void onClick(View view) {

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

            Button btn_edit_reg_gly, btn_delete_reg_gly;
            public AdapterRegGlyHolder(@NonNull View itemView) {
                super(itemView);
                reg_id = itemView.findViewById(R.id.recycler_reg_gly_id);
                reg_date = itemView.findViewById(R.id.recycler_reg_gly_date);
                reg_value = itemView.findViewById(R.id.recycler_reg_gly_value);
                reg_note = itemView.findViewById(R.id.recycler_reg_gly_note);
                btn_edit_reg_gly = itemView.findViewById(R.id.recycler_btn_edit_reg_gly);
                btn_delete_reg_gly = itemView.findViewById(R.id.recycler_btn_delete_reg_gly);
                itemView.setOnClickListener(this);
            }
            public void printItem(int position) {
                reg_id.setText(String.valueOf(reg_gly_ids.get(position)));
                reg_date.setText(reg_gly_dates.get(position));
                reg_value.setText(String.valueOf(reg_gly_values.get(position)));
                reg_note.setText(reg_gly_notes.get(position));

                btn_edit_reg_gly.setOnClickListener(view -> {
                    btnEditOnClick(reg_gly_ids.get(getLayoutPosition()));
                });
                btn_delete_reg_gly.setOnClickListener(view -> {
                    btnDeleteOnClick(reg_gly_ids.get(getLayoutPosition()));
                });
            }
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, String.valueOf(reg_gly_ids.get(getLayoutPosition())),Toast.LENGTH_SHORT).show();
            }
            public void btnEditOnClick(int valor){
                edit_reg_gly(valor);
            }
            public void btnDeleteOnClick(int valor){
                delete_reg_gly(valor);
            }
        }
    }

      public void updateRegGly(){
        SQLiteDatabase bd = admin.getWritableDatabase(); // abre la bd
        // este obj ejecuta la consulta a bd
        Cursor fila = bd.rawQuery("SELECT * FROM glycemia", null);

        reg_gly_ids.clear(); // limpio arrays para recibir los datos nuevos..
        reg_gly_dates.clear();
        reg_gly_values.clear();
        reg_gly_notes.clear();

        if (fila.moveToFirst()){
            do {
                reg_gly_ids.add(Integer.valueOf(fila.getInt(0)));
                reg_gly_dates.add(fila.getString(1));
                reg_gly_values.add(fila.getDouble(2));
                reg_gly_notes.add(fila.getString(3));
            }
            while (fila.moveToNext());
        } else{
            Toast.makeText(this, "Aun no hay registros guardados..", Toast.LENGTH_LONG).show();
        }
        bd.close(); // cierro conexion bd
    }
    public void updateRegGly(View v){
        this.updateRegGly();
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

        this.updateRegGly(); // actualiza array de reg
        adapterRegGly.notifyDataSetChanged(); // refresca pantalla del recycler
        rv1.smoothScrollToPosition(reg_gly_ids.size()-1); // mueve la vista al ultimo elemento agregado

        Toast.makeText(this,"Se agrego registro de glucemia", Toast.LENGTH_SHORT).show();

    }
    public void edit_reg_gly(int id){
        // cargar datos en campos visuales de registro para que se puedan ver y modificar
        SQLiteDatabase bd = admin.getWritableDatabase();// abre la bd
        Cursor reg_gly = bd.rawQuery("SELECT * FROM glycemia WHERE id_reg_glucemia = " +id, null);

        if(reg_gly.moveToFirst()){
            add_date_gly.setText(reg_gly.getString(1));
            add_value_gly.setText(reg_gly.getString(2));
            add_notes_gly.setText(reg_gly.getString(3));

            btn_add_reg_gly.setText("¡ EDITAR REGISTRO !"); // cambio texto de btn para que user sepa que va a EDITAR el registro
            // Pongo una funcion para que cuando haga click en el btn editar, llame a mi funcion update..
            btn_add_reg_gly.setOnClickListener(view -> {
                update_edited_reg_gly(id);
            });
        } else {
            Toast.makeText(this,"Click en EDIT id= pero hubo un error pareceeeee", Toast.LENGTH_SHORT).show();
        }
    }
    public void update_edited_reg_gly (int id){
        // tomar datos re ingresados en campos y hacer el update definitivo con los nuevos campos
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues edited_reg_gly = new ContentValues(); // crea un objeto que luego actualizara

        edited_reg_gly.put("date", add_date_gly.getText().toString());// agrego datos al objeto registro
        edited_reg_gly.put("value", Double.valueOf(add_value_gly.getText().toString()));
        edited_reg_gly.put("notes", add_notes_gly.getText().toString());

        int editedRows = bd.update("glycemia", edited_reg_gly, "id_reg_glucemia = "+id, null);
        if (editedRows == 1){
            // mandar a refrescar recicler y vaciar textos..
            add_value_gly.setText("");// limpio pantalla
            add_notes_gly.setText("");
            add_date_gly.setText("");
            this.updateRegGly(); // actualiza array de reg
            adapterRegGly.notifyDataSetChanged(); // refresca pantalla del recycler
            Toast.makeText(this, "Registro actualizado!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "ERROR AL EDITAR REGISTRO", Toast.LENGTH_LONG).show();
        }
        // luego deberia poner un condicional, que vuelva el btn a su estado normal

        btn_add_reg_gly.setText(""); // intento ver si el btn se restaura su texto original...
        //btn_add_reg_gly.setText("¡Agregar nuevo registro!");

        // Elimino onClick creado para editar y seteo el nuevo on click
        btn_add_reg_gly.setOnClickListener(null); //
        //btn_add_reg_gly.setOnClickListener(add_new_reg_gly);
    }

    public void delete_reg_gly(int id){
        SQLiteDatabase bd = admin.getWritableDatabase();
        int deletedRow = bd.delete("glycemia", "id_reg_glucemia = "+id, null);
        if(deletedRow == 1){
            Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error eliminando registro", Toast.LENGTH_LONG).show();
        }
        bd.close(); // cierro conexion bd
        this.updateRegGly(); // actualiza array de reg
        adapterRegGly.notifyDataSetChanged(); // refresca pantalla del recycler
    }


}