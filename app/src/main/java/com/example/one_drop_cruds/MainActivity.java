package com.example.one_drop_cruds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    AdminSQLiteOpenHelper admin;
    EditText add_value_gly, add_notes_gly, add_date_gly;
    EditText edit_value_gly, edit_notes_gly, edit_date_gly;
    // Button btn_add_reg_gly;
    FloatingActionButton float_btn_add_reg_gly;

    // RECICLER VIEW
    RecyclerView rv1;
    AdapterRegGly adapterRegGly;

    //DATA
    ArrayList<Integer> reg_gly_ids = new ArrayList<Integer>();
    ArrayList<String> reg_gly_dates = new ArrayList<String>();
    ArrayList<Double> reg_gly_values = new ArrayList<Double>();
    ArrayList<String> reg_gly_notes = new ArrayList<String>();

    // GRAPHS
    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        admin = new AdminSQLiteOpenHelper(this, "bd_one_drop", null, 1); // version es para las futuras modificaciones de la estructura de la bd

        this.updateRegGly(); // CARGAR ARRAYS CON DATA

        // btn float add
        float_btn_add_reg_gly = findViewById(R.id.float_btn_add_reg_gly);

        lineChart = findViewById(R.id.lineChart);
        this.updateChartRegGly(); // sobreescribe chart

        // RECICLER VIEW
        rv1 = findViewById(R.id.recyclerView_reg_gly);
        LinearLayoutManager linearLayoutManager_reg_gly = new LinearLayoutManager(this);
        rv1.setLayoutManager(linearLayoutManager_reg_gly);
        adapterRegGly = new AdapterRegGly();
        rv1.setAdapter(adapterRegGly);

    }
    private void updateChartRegGly(){
        LineDataSet lineDataSet = new LineDataSet(createLineChartDataSet(), "Glucemia");
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        //Edito los datos de fecha al formato corto
        ArrayList<String> formatedDates = new ArrayList<String>();
        reg_gly_dates.forEach(date ->{
            formatedDates.add(formatDate(date));
        });
        //Seteo el formateador para leyendas del eje X
        LineData lineData = new LineData(iLineDataSets);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DateAxisValueFormatter(formatedDates));

        lineChart.setData(lineData);
        lineChart.invalidate();
        // PERSONALIZACION
        lineDataSet.setColor(Color.BLUE); // COLOR LINEA
        lineDataSet.setCircleColor(Color.RED); // COLOR PUNTOS?
        lineDataSet.setDrawCircles(true); // HABILITA QUE SE MUESTRE LOS PUNTOS
        lineDataSet.setDrawCircleHole(true); // LOS PUNTOS LOS MUESTRA COMO ARANDELAS
        lineDataSet.setLineWidth(5); // GROSOR LINEA
        lineDataSet.setCircleRadius(10); // diametro ext de punto
        lineDataSet.setCircleHoleRadius(5); // diam interno punto
        lineDataSet.setValueTextSize(10); // tamaño texxto valot
        lineDataSet.setValueTextColor(Color.BLACK); // COLOR TEXTO

        lineChart.setBackgroundColor(Color.LTGRAY); // COLOR FONDO OPCION
        lineChart.setNoDataText("Aun no hay registros guardados.."); // TEXTO SI NO HAY INFO
        lineChart.setNoDataTextColor(Color.RED); // TEXTO SI NO HAY INFO
        lineChart.setTouchEnabled(true); // permite tactil
        lineChart.setPinchZoom(true); // permite zoom tactil
    }
    public class DateAxisValueFormatter extends IndexAxisValueFormatter {
        private List<String> mValues;
        public DateAxisValueFormatter(List<String> values){
            this.mValues = values;
        }
        @Override
        public String getFormattedValue(float value) {
            int index = (int) value;
            if(index <0 || index>= mValues.size()){
                return "";
            }
            return mValues.get(index);
        }

    }
    private ArrayList<Entry> createLineChartDataSet(){
        ArrayList<Entry> dataSet = new ArrayList<Entry>();
        reg_gly_dates.forEach(date ->{
            Double value = reg_gly_values.get(reg_gly_dates.indexOf(date));
            int index = reg_gly_dates.indexOf(date);
            dataSet.add(new Entry (index, Float.valueOf(String.valueOf(value))));
        });
        return dataSet;
    }

    public String formatDate (String inputDate){
        Date date = null;
        try {
            // Creo un formateador que reciba un string del fomrato "E MMM dd HH:mm:ss z yyyy" y lo cree un obj date
            SimpleDateFormat inputPatternFormatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            date = inputPatternFormatter.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Creo un formateador que reciba un date y lo pase al formato deseado "E dd '-' HH:mm'hs'"
        SimpleDateFormat outputPatternFormatter = new SimpleDateFormat("E dd '-' HH:mm'hs'", Locale.ENGLISH);
        return outputPatternFormatter.format(date);
    }
    public void openPopupBtnEdit(int id_reg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Editar registro de glucemia");
        View popupEditReg = getLayoutInflater().inflate(R.layout.popup_form_edit_reg_gly, null);
        builder.setView(popupEditReg); // ESTO ES PARA QUE PUEDA OBTENER LAS REFERENCIAS DESDE popupEditReg Y PODER OBTENER EL CONTROL DE LOS ELEMENTOS
        edit_value_gly = popupEditReg.findViewById(R.id.edit_value_gly);
        edit_notes_gly = popupEditReg.findViewById(R.id.edit_notes_gly);
        edit_date_gly = popupEditReg.findViewById(R.id.edit_date_gly);

        set_text_edit_reg_popup(id_reg); // esto es para setear los campos del popup con la info de la bd
        builder.setPositiveButton("¡Editar!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                update_edited_reg_gly(id_reg); // toma los campos modificados y actualiza la bd
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    public void openPopupBtnDel(int id_reg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Eliminar este registro?");
        builder.setPositiveButton("¡Eliminar!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delete_reg_gly(id_reg);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    public void openPopupAddReg(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Agregar registro de glucemia");
        View popupAddReg = getLayoutInflater().inflate(R.layout.popup_form_add_reg_gly, null);
        builder.setView(popupAddReg); // ESTO ES PARA QUE PUEDA OBTENER LAS REFERENCIAS DESDE popupAddReg Y PODER OBTENER EL CONTROL DE LOS ELEMENTOS
        add_value_gly = popupAddReg.findViewById(R.id.edit_value_gly);
        add_notes_gly = popupAddReg.findViewById(R.id.edit_notes_gly);

        // add_date_gly = popupAddReg.findViewById(R.id.edit_date_gly);
        add_date_gly = popupAddReg.findViewById(R.id.edit_date_gly_pickdater); // ESTE ES PARA EL PICKDATER
        // ESTA FORMA AGREGA A ESTA MISMA CLASE COMO LISTENER Y LUEGO EN UN SWITCH SE ELIJE EL EVENTO SEGUN SU ID..
        add_date_gly.setOnClickListener(this);

        builder.setPositiveButton("¡Agregar!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                add_new_reg_gly();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
    @Override
    public void onClick(View view) {

    }

    // RECICLER VIEW
    // RECICLER VIEW Clase que se encargara de CREAR todos los elementos de lista
    private class AdapterRegGly extends RecyclerView.Adapter<AdapterRegGly.AdapterRegGlyHolder> {
        @NonNull
        @Override
        public AdapterRegGlyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdapterRegGlyHolder(getLayoutInflater().inflate(R.layout.layout_reg_gly_view,parent,false));
        }
        @Override
        public void onBindViewHolder(@NonNull AdapterRegGlyHolder holder, int position) {
            try {
                holder.printItem(position);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        public int getItemCount() {
            return reg_gly_dates.size(); // debe retornar la cantidad de registros..
        }
        // Clase que se encargara de IMPRIMIR todos los elementos
        private class AdapterRegGlyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            TextView reg_date;
            TextView reg_value;
            TextView reg_note;
            Button btn_edit_reg_gly, btn_delete_reg_gly;
            public AdapterRegGlyHolder(@NonNull View itemView) {
                super(itemView);
                reg_date = itemView.findViewById(R.id.recycler_reg_gly_date);
                reg_value = itemView.findViewById(R.id.recycler_reg_gly_value);
                reg_note = itemView.findViewById(R.id.recycler_reg_gly_note);
                btn_edit_reg_gly = itemView.findViewById(R.id.recycler_btn_edit_reg_gly);
                btn_delete_reg_gly = itemView.findViewById(R.id.recycler_btn_delete_reg_gly);
                itemView.setOnClickListener(this);
            }
            public void printItem(int position) throws ParseException {
                reg_date.setText(formatDate(reg_gly_dates.get(position)));
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
            public void btnEditOnClick(int id){
                openPopupBtnEdit(id);
            }
            public void btnDeleteOnClick(int id){
                openPopupBtnDel(id);
            }
        }
    }

    //USO DE BD
    //USO DE BD
    //USO DE BD
    //USO DE BD
    public void updateRegGly(){
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE

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
    public void set_text_edit_reg_popup(int id){
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE

        // cargar datos en campos visuales para que sea visible y editable lo que estaba en la bd
        SQLiteDatabase bd = admin.getWritableDatabase(); // abre la bd
        Cursor reg_gly = bd.rawQuery("SELECT * FROM glycemia WHERE id_reg_glucemia = " +id, null); // Busco el registro por id

        if(reg_gly.moveToFirst()) {
            edit_date_gly.setText(reg_gly.getString(1)); // obtengo la primera columna del resultado, y el texto lo seteo en el campo date
            edit_value_gly.setText(reg_gly.getString(2));
            edit_notes_gly.setText(reg_gly.getString(3));
        } else {
            Toast.makeText(this,"Click en EDIT  pero hubo un error pareceeeee", Toast.LENGTH_SHORT).show();
        }
    }

    public void update_edited_reg_gly (int id){

        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE

        // tomar datos re ingresados en campos y hacer el update definitivo con los nuevos campos
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues edited_reg_gly = new ContentValues(); // crea un objeto que luego actualizara

        edited_reg_gly.put("date", edit_date_gly.getText().toString());// agrego datos al objeto registro
        edited_reg_gly.put("value", Double.valueOf(edit_value_gly.getText().toString()));
        edited_reg_gly.put("notes", edit_notes_gly.getText().toString());

        int editedRows = bd.update("glycemia", edited_reg_gly, "id_reg_glucemia = "+id, null);

        if (editedRows == 1){
            // Si edite alguna fila, mandar a refrescar recicler, vaciar textos y setear btn a estado inicial..
            edit_value_gly.setText("");// limpio pantalla
            edit_notes_gly.setText("");
            edit_date_gly.setText("");
            this.updateRegGly(); // actualiza array de reg
            this.updateChartRegGly(); // actualiza chart
            adapterRegGly.notifyDataSetChanged(); // refresca pantalla del recycler

            // btn_add_reg_gly.setText("¡Agregar nuevo registro!"); // Coloco el btn a su texto inicial
            // btn_add_reg_gly.setBackgroundColor(getColor(R.color.green)); // Coloco el btn a su color inicial
            // sobre escribo el click para que vuelva a su estado original (agregar registros)
            // ESTO ES LA FORMA CON LAMBDA
            /*
            btn_add_reg_gly.setOnClickListener(view -> {
                add_new_reg_gly(view);
            });
            */
            // ESTO ES LA FORMA COMPLETA DE HACERLO
            // btn_add_reg_gly.setOnClickListener(new View.OnClickListener() {
            //     @Override
            //     public void onClick(View view) {
            //
            //     }
            // });
            Toast.makeText(this, "Registro actualizado!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "ERROR AL EDITAR REGISTRO", Toast.LENGTH_LONG).show();
        }
    }
    public void add_new_reg_gly(){
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE

        SQLiteDatabase bd = admin.getWritableDatabase();// abre la bd
        ContentValues new_reg_gly = new ContentValues(); // crea un objeto que luego sera un nuevo registro en la bd

        // agrego datos al objeto registro
        if(add_date_gly.getText().toString().equals("") ){
            System.out.println("CREO NUEVA FECHA NOW PORQUE VINO VACIA");
            new_reg_gly.put("date", new Date().toString());
        } else {
            new_reg_gly.put("date", add_date_gly.getText().toString());
        }
        new_reg_gly.put("value", add_value_gly.getText().toString());
        new_reg_gly.put("notes", add_notes_gly.getText().toString());
        bd.insert("glycemia", null, new_reg_gly);// inserta en tabla "glycemia"

        add_value_gly.setText("");// limpio pantalla
        add_notes_gly.setText("");
        add_date_gly.setText("");
        bd.close();// cierro conexion bd

        this.updateRegGly();
        this.updateChartRegGly(); // sobreescribe chart
        adapterRegGly.notifyDataSetChanged(); // refresca pantalla del recycler
        rv1.smoothScrollToPosition(reg_gly_ids.size()-1); // mueve la vista al ultimo elemento agregado
        Toast.makeText(this,"Se agrego registro de glucemia", Toast.LENGTH_SHORT).show();
    }
    public void delete_reg_gly(int id){

        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE
        // DEBO SEPARAR EN RESPONSABILIDADES con admin sqlite helper ANTES DE AGREGAR LOS OTROS CRUDS, NO VA A SER ESCALABLE

        SQLiteDatabase bd = admin.getWritableDatabase();
        int deletedRow = bd.delete("glycemia", "id_reg_glucemia = "+id, null);
        bd.close(); // cierro conexion bd

        if(deletedRow == 1){
            this.updateRegGly(); // actualiza array de reg
            this.updateChartRegGly(); // sobreescribe chart
            adapterRegGly.notifyDataSetChanged(); // refresca pantalla del recycler
            Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error eliminando registro", Toast.LENGTH_LONG).show();
        }
    }
}