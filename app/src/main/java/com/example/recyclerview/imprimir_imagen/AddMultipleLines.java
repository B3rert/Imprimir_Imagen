package com.example.recyclerview.imprimir_imagen;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMultipleLines {

    public static void main(String nombre_E, String direccion_E,String nit_E,
                            String vendedor, String ID_doc, String serie,
                            String nombr_C, String nit_C, String direccion_C) {

   try {

       //Busca el documento que va a ser editado
            File file = Constantes.getRutaDestinoPDF();
            PDDocument doc = PDDocument.load(file);

            //Crea un nuevo documento
            PDPage page = doc.getPage(0);//numero de pagina

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);

            //Inicia la secuencia de contenid
            contentStream.beginText();

            //Funete de letra
            contentStream.setFont( PDType1Font.COURIER_BOLD, 25);

            contentStream.setLeading(14.5f);

            //Posision de inico de linea
            contentStream.newLineAtOffset(25, 865);

            //obtiene la fecha actual y le da un formato

       Date date = new Date();
       DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
       String fecha = hourdateFormat.format(date);


       //Texto

       //DATOS
       String text1 = "Lineas multiples";


       //Ecritura en pdf Encabezado

       contentStream.showText("-- INICIO --");
       contentStream.newLine();
       contentStream.newLine();
       contentStream.newLine();
       contentStream.showText(nombre_E);
       contentStream.newLine();
       contentStream.newLine();
       contentStream.showText(direccion_E);
       contentStream.newLine();
       contentStream.newLine();
       contentStream.showText(nit_E);
       contentStream.newLine();
       contentStream.newLine();
       //datos vcendedor
       contentStream.newLine();
       contentStream.showText("Vendedor: "+vendedor);
       contentStream.newLine();
       contentStream.newLine();
       contentStream.showText("Fecha: "+fecha);
       contentStream.newLine();
       contentStream.newLine();
       contentStream.showText("ID Doc: "+ID_doc);
       contentStream.newLine();
       contentStream.newLine();
       contentStream.showText("Serie: "+serie);
       contentStream.newLine();
       contentStream.newLine();
       //datos cliente
       contentStream.showText("Cliente: "+nombr_C);
       contentStream.newLine();
       contentStream.newLine();
       contentStream.showText("Nit: "+nit_C);
       contentStream.newLine();
       contentStream.newLine();
       contentStream.showText("Direccion:  "+direccion_C);
       contentStream.newLine();
       contentStream.newLine();
       contentStream.newLine();

       //mas Datos
       for (int i=1; i<=12; i++){

                //Agregar texto en fomra de cadena
                contentStream.showText("Bloque: "+i);
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText(text1);
                contentStream.newLine();
                contentStream.newLine();
            }

       //Final del secuencia de contenido
            contentStream.endText();

            //Cierre de secuencia de contenido
            contentStream.close();

            //Se guarda el documento
            doc.save(Constantes.getRutaDestinoPDF());

            //Se cierra el documento
            doc.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
