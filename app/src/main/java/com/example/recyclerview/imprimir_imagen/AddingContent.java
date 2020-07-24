package com.example.recyclerview.imprimir_imagen;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;

public class AddingContent {
    public static void main ()  {

        try {

            //Carga un documento existente
            File file = Constantes.getRutaDestinoPDF();
            PDDocument document = PDDocument.load(file);

            //Busca la pagina en la que se escribirá.
            PDPage page = document.getPage(0);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            //Inicia la secuencia de contenido
            contentStream.beginText();

            //Fuente de letra del documento
            contentStream.setFont(PDType1Font.COURIER_OBLIQUE, 25);

            //posicion de la linea
            contentStream.newLineAtOffset(25, 500);

            //Linea de texto
            String text = "Cantidad";

            //Agrega texto en forma de cadena
            contentStream.showText(text);

            //Finaliza la secuencia de contenido
            contentStream.endText();

            //Cierra la secuencia de contenido
            contentStream.close();

            //guarda el documento
            document.save(Constantes.getRutaDestinoPDF());

            //Cierra el docuemnto
            document.close();


        } catch (Exception e) {
            e.printStackTrace();
        }}
}