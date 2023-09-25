package com.example.one_drop_cruds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    // METODOS DE NAVEGACION
    public void aRegistrarGlucemia(View v){
        Intent siguiente = new Intent(this, MainActivity.class);
        startActivity(siguiente);
    }//

    public void aRegistrarAnalisis(View v){
        Intent siguiente = new Intent(this, RegistrarAnalisis.class);
        startActivity(siguiente);
    }//


    /////////////////////////////
}