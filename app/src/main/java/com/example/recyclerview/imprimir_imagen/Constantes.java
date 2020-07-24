package com.example.recyclerview.imprimir_imagen;

import android.os.Environment;

import java.io.File;

public class Constantes {

    //Crea directorio "File" para almacenar la imagen que se selecciona.
    public static void crearRutaCarpetaImg(){
        File dir = new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/ImagenesImprimir");
        if(!dir.exists()){
            dir.mkdir();
        }
    }



    public static File getRutaDestinoPDFIMG(String nombrePDF){
        return new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/ImagenesImprimir/"+nombrePDF+".png");
    }

    public static File getRutaDestinoImgPDFDef(int numeroPagina){
        return new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/ImagenesImprimir/prueba("+numeroPagina+").png");
    }

    //Guarda nueva imagen
    public static File getRutaDestino(String nombreImg){
        return new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/ImagenesImprimir/"+nombreImg+".png");
    }

    //Devuelve la ruta y el nombre de la imagen que va a imprimirse.
    public static File getRutaDestinoImg(){
        return new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/ImagenesImprimir/Empresa_log.png");
    }



    public static File getRutaDestinoPDF(){
        return new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/ImagenesImprimir/prueba.pdf");
    }
}