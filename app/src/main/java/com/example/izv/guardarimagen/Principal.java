package com.example.izv.guardarimagen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Principal extends Activity {

    private EditText eturl, etnombre;
    private RadioGroup rg;
    private String extension, nombre;
    private URLConnection urlCon;
    private File f;
    private Bitmap myBitmap;
    private ImageView iv;
    private int pulsado=0;
    private boolean textoguardado;

    /***********************************************************************/
    /*                                                                     */
    /*                             METODOS ON                              */
    /*                                                                     */
    /***********************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        eturl=(EditText)findViewById(R.id.eturl);
        etnombre=(EditText)findViewById(R.id.etnombre);
        rg=(RadioGroup)findViewById(R.id.grupo);
        iv = (ImageView)findViewById(R.id.ivimagen);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.privada:
                        pulsado=1;
                    break;
                    case R.id.publica:
                        pulsado=2;
                    break;
                }
            }
        });
    }

    /***********************************************************************/
    /*                                                                     */
    /*                          METODOS ONCLICK                            */
    /*                                                                     */
    /***********************************************************************/

    public void descarga (View view){
        nombre=etnombre.getText().toString();
        if (nombre.compareTo("")!=0) {
            textoguardado=true;
                Hilo hilo = new Hilo();
                hilo.start();
        }else{
            tostada("Debes selecionar donde guardar el archivo");
        }
    }

    /***********************************************************************/
    /*                                                                     */
    /*                               HILOS                                 */
    /*                                                                     */
    /***********************************************************************/

    private class Hilo extends Thread{
        @Override
        public void run(){
            String url=eturl.getText().toString();
            try {
                URL intlLogoURL = new URL(url);
                urlCon = intlLogoURL.openConnection();
                extension=urlCon.getContentType();
                extension=extension.substring(6, extension.length());
                if(extension.compareTo("png")==0 || extension.compareTo("git")==0 || extension.compareTo("jpeg")==0) {
                    if (pulsado == 1) {
                        f = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), nombre + "." + extension);
                        guardarimagen();
                        myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                        Verimagen ver = new Verimagen();
                        Principal.this.runOnUiThread(ver);
                    } else {
                        if (pulsado == 2) {
                            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), nombre + "." + extension);
                            guardarimagen();
                            myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                            Verimagen ver = new Verimagen();
                            Principal.this.runOnUiThread(ver);
                        } else {
                            Seleccionaradiobuton boton=new Seleccionaradiobuton();
                            Principal.this.runOnUiThread(boton);
                        }
                    }
                }else {
                    Urlnoesimagen no=new Urlnoesimagen();
                    Principal.this.runOnUiThread(no);
                }
             } catch (MalformedURLException e) {
                e.printStackTrace();
                Urlnoesimagen no=new Urlnoesimagen();
                Principal.this.runOnUiThread(no);
             } catch (IOException e) {
                e.printStackTrace();
                Urlnoesimagen no=new Urlnoesimagen();
                Principal.this.runOnUiThread(no);
            }
        }
    }

    private class Nombrearchivonocorrecto extends Thread{
        @Override
        public void run(){
            tostada("El nombre del archivo no es valido");
        }
    }

    private class Seleccionaradiobuton extends Thread{
        @Override
        public void run(){
            tostada("Tienes que seleccionar un boton redondo");
        }
    }

    private class Urlnoesimagen extends Thread{
        @Override
        public void run(){
            tostada("La URL no es una imagen");
        }
    }

    private class Verimagen extends Thread{
        @Override
        public void run(){
            iv.setImageBitmap(myBitmap);
            if (textoguardado) {
                tostada("Imagen guardada");
            }
        }
    }

    /***********************************************************************/
    /*                                                                     */
    /*                         METODOS AUXILIARES                          */
    /*                                                                     */
    /***********************************************************************/

    public void guardarimagen(){
        try {
            FileOutputStream fos = new FileOutputStream(f);
            InputStream is = urlCon.getInputStream();
            byte[] array = new byte[1000];
            int leido = is.read(array);
            while (leido > 0) {
                fos.write(array, 0, leido);
                leido = is.read(array);
            }
            is.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Nombrearchivonocorrecto n=new Nombrearchivonocorrecto();
            Principal.this.runOnUiThread(n);
            textoguardado=false;
        } catch (IOException e) {
            e.printStackTrace();
            Nombrearchivonocorrecto n=new Nombrearchivonocorrecto();
            Principal.this.runOnUiThread(n);
            textoguardado=false;
        }
    }

    public void tostada(String c){
        Toast.makeText(this,c,Toast.LENGTH_SHORT).show();
    }
}
