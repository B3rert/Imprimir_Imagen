package com.example.recyclerview.imprimir_imagen;

import android.graphics.Bitmap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapToPNG {


    public static void saveBitmap(String bitName, Bitmap mBitmap, String rutaGuardado) {//  ww  w.j  a va 2s.c  o  m



        //Crea directorio donde se guardará la imagen

        String f = rutaGuardado+(bitName);

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Convierte bitmap a PNG
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
