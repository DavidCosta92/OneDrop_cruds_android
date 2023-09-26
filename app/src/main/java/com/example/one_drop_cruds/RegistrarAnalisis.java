package com.example.one_drop_cruds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrarAnalisis extends AppCompatActivity {

    ImageView iv1;
    final int CAPTURA_IMAGEN=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_analisis);

        iv1 = findViewById(R.id.iv1);



    }

    // --- CODIGO PARA CAPTURA LA IMAGEN Y MOSTRARLA ---
    // --- CODIGO PARA CAPTURA LA IMAGEN Y MOSTRARLA ---




    // METODO PARA CAPTURAR IMAGEN
    public void tomarFoto(View v){
        // este intent nos devuelve la imagen que se toma
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAPTURA_IMAGEN);
    }//





    // METODO PARA EXTRAER LA IMAGEN, ESTE SE EJECUTA CUANDO SE CIERRA LA CAMARA.
    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        // si devuelve la imagen y esta to do bien entonces se imprime la imagen en el ImageView
        if(requestCode==CAPTURA_IMAGEN && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap1=(Bitmap)extras.get("data");
            iv1.setImageBitmap(bitmap1);

            // ahora para grabar la imagen en la memoria interna hacemos
            try{
                // la foto va a tener como nombre la fecha y hora
                FileOutputStream fos=openFileOutput(crearNombreArchivoJPG(), Context.MODE_PRIVATE);

                // guardamos la captura de la imagen
                bitmap1.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.close();

            }catch (Exception e){}
        }
    } //Termina metodo

    // METODO crearNombreArchivoJPG
    private String crearNombreArchivoJPG(){
        String fecha= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return fecha+".jpg";
    }




    // METODO PARA IR A GALERIA

    public void aGaleria(View v){
        Intent siguiente = new Intent(this, Galeria_Analisis.class);
        startActivity(siguiente);
    }//


    public void volver(View v){
        finish();
    }




}// FINAL