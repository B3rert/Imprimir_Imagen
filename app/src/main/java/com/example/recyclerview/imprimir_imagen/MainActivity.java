package com.example.recyclerview.imprimir_imagen;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int ANCHO_IMG_58_MM = 384;
    private static final int MODE_PRINT_IMG = 0;
    private static final int INTENT_CAMARA = 123;
    private static final int INTENT_GALERIA = 321;
    private static final int COD_PERMISOS = 872;

    // Para el flujo de datos de entrada y salida del socket bluetooth
    private OutputStream outputStream;

    private String rutaFoto, rutaFoto_print, rutaPDF;
    private Button btnPrint;
    private Button btnPrintPicture;
    private Button btnPrintText;
    private Button btnPrintPDF;
    private Button btnSync;
    private Button btnTomarFoto;
    private Button btnGenerarPDF;
    private EditText editText;
    private Button btnCerrarC;
    private TextView txtLabel;
    private ImageView imgFoto;
    private ImageView imgPDF;
    private FotoDeCamara cameraPhoto;
    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private GalleryPhoto galleryPhoto;

    private Bitmap imageWithBG;
    private int indexingpages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rutaFoto = null;
        rutaFoto_print = null;
        rutaPDF = null;

        //btnPrint = (Button)findViewById(R.id.btn_imprimir_todo);
        btnSync = (Button)findViewById(R.id.btn_buscar_dispositivos);
        btnPrintText = (Button) findViewById(R.id.btn_imprimir_texto);
        btnPrintPicture = (Button)findViewById(R.id.btnImprimir_img);
        //btnCerrarC = (Button)findViewById(R.id.btn_cerrar_conexion);
        imgFoto = (ImageView) findViewById(R.id.img_foto);
        imgPDF = (ImageView) findViewById(R.id.img_pdf);
        editText = (EditText) findViewById(R.id.edt_texto);
        //txtLabel = (TextView) findViewById(R.id.txt_label);
        btnTomarFoto = (Button) findViewById(R.id.btn_tomar_foto);
        btnPrintPDF = (Button) findViewById(R.id.btn_imprimir_pdf);
        btnGenerarPDF = (Button) findViewById(R.id.btn_tomar_pdf);

        btnGenerarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerarPDF();
            }
        });

        /*0
        btnCerrarC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llama al metodo para cerrar la conexion bluetooth
                onDestroy();
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Llama al metodo que imprime primero el texto y luego la imagen seguidas
                printDemo();
            }
        });

         */

        btnPrintPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llama al metodo para imprimir PDF
                imprimirPDF();
            }
        });


        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llama al metodo que busca y lista los dispositivos bluetooth disponibles
                buscardispositivos();
            }
        });

        btnPrintText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llama al metodo que imprime texto
                ImprimirTexto();
            }
        });

        btnPrintPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llama al metodo que imprime imagen
                imprimirImagen();
            }
        });

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llama al metodo que permite seleccianar imagenes
                Tomar_foto();
            }
        });
    }

    //Crea un documento PDF,lo convierte a Bitmap, Bitmap a PNG y lo guarda en una carpeta.
    private void GenerarPDF() {
        //URL Del PDF que se va a descargar

        //String urlPDF = "http://www.posgrado.unam.mx/filosofiadelaciencia/media/uploaded_files/2012/04/guia_digit_conacyt.pdf"; //prueba multiples hojas
        String urlPDF = "https://oficinavirtual.ugr.es/apli/solicitudPAU/test.pdf"; //prueba una hoja

        //Llama al metodo que recibe la direccion URL para efectuar la descarga
        DecargarPDF(urlPDF);


    }


    void DecargarPDF(String Url){

        //inicializamos progressdialog para verificar el proceso de descaga y conversion
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Generando Documento...");


        //nueva instancia par ala clase "DescargarPDFAsyncTAsk", descarga el docuemnto PDF
        new DescargarPDFAsyncTask(progressDialog).execute(Url);

    }

    class DescargarPDFAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        //instanciamos el progresdialog
        DescargarPDFAsyncTask(ProgressDialog progressDialog){
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {

            String urlADescargar = url[0];

            HttpURLConnection conexion = null;
            InputStream input = null;
            OutputStream output = null;

            try {

                URL url1 = new URL(urlADescargar);

                conexion = (HttpURLConnection) url1.openConnection();
                conexion.connect();

                if (conexion.getResponseCode() != HttpURLConnection.HTTP_OK){
                    return "Error, verifique su conexion a internet";
                }

                byte[] data = new byte[1024];

                //guarda en espacio de almacenamiento externo (publico)
                //String rutaPDFGuardar = Constantes.getRutaDestinoPDF().toString();

                //guarda en espacio de almacenamiento interno (privado)

                input = conexion.getInputStream();

                String rutaPDFGuardar = getFilesDir()+"/prueba.pdf";
                output = new FileOutputStream(rutaPDFGuardar);


                int count;

                while ((count = input.read(data)) != -1){

                    output.write(data, 0,count);

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Error, compruebe su conexion a internet";
            } catch (IOException e) {
                e.printStackTrace();
                return "Error, compruebe su conexion a internet";
            } finally {

                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null){
                        output.close();
                    }
                    if (conexion != null){
                        conexion.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            //se instancia la libreria para lograr la "impresion del pdf", (no es indispensable)
            PDFBoxResourceLoader.init(getApplicationContext());

            //Busca el documento PDF
            rutaPDF = getFilesDir()+"/prueba.pdf";



            PDDocument pd = null;
            try {
                //carga el documento pdf
                pd = PDDocument.load (new File(rutaPDF));
                //obtiene el numero de paginas del documento
                indexingpages = pd.getNumberOfPages();

            } catch (IOException e) {
                e.printStackTrace();
                return "Documento no encontrado";
            }


            for (int count =0; count<indexingpages; count++){

                //Renderiza el documento PDF, (Conversion PDF a Bitmap)
                PDFRenderer pr = new PDFRenderer (pd);
                try {
                    Bitmap bitmap = pr.renderImageWithDPI(count, 300);
                    imageWithBG = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),bitmap.getConfig());  // Create another image the same size

                    //Cambia el fonfo transparente a blanco
                    imageWithBG.eraseColor(Color.WHITE);  // set its background to white, or whatever color you want
                    Canvas canvas = new Canvas(imageWithBG);  // create a canvas to draw on the new image
                    canvas.drawBitmap(bitmap, 0f, 0f, null); // draw old image on the background
                    bitmap.recycle();

                    String rutaPDFimg = getFilesDir()+"/";


                    //Llama al metodo para guardar el Bitmap em formato PNG
                    //Recibe el nombre con el que se va a gardar la imagen y el Bitmap del PDF
                    BitmapToPNG.saveBitmap("prueba("+count+").png",imageWithBG,rutaPDFimg);

                    //Impresion de prueba
                    //outputStream.write(PrintBitmap.POS_PrintBMP(bitmap, ANCHO_IMG_58_MM, MODE_PRINT_IMG));

                    //Carga el bitmap del PDF en un ImageView
                    //imgPDF.setImageBitmap(imageWithBG);

                } catch (IOException e) {
                    e.printStackTrace();
                    return "No se pudo generar el documento";
                }


            }

            return "Documento listo para imprimir ";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String mensaje) {
            super.onPostExecute(mensaje);
            imgPDF.setImageBitmap(imageWithBG);
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
        }
    }


    //Metodo que imprime PDF
    private void imprimirPDF() {
        //Verifica si hay una impresora conectada
        if(btsocket == null){
            Toast.makeText (getApplicationContext (), "Error, ninguna impresora conectada" , Toast.LENGTH_SHORT) .show ();
        }
        else{

            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;
            //print command
            try {
                outputStream = btsocket.getOutputStream();
                byte[] printformat = { 0x1B, 0x21,FONT_TYPE};
                //se imprime una linea en blanco
                outputStream.write(printformat);
             } catch (IOException e) {
                e.printStackTrace();
            }

            String PdfR = getFilesDir()+"/prueba.pdf";
            PDDocument pd = null;
            try {
                //carga el documento pdf
                pd = PDDocument.load (new File(PdfR));
                //obtiene el numero de paginas del documento
                indexingpages = pd.getNumberOfPages();

            } catch (IOException e) {
                e.printStackTrace();
            }
            //

            for(int count= 0;count<indexingpages;count++){

                try {
                    //Busca la imagen que se guardo del pdf, la convierte a bitmap y l aimprime
                    rutaPDF = getFilesDir()+"/prueba("+count+").png";

                    Bitmap bitmap = BitmapFactory.decodeFile(rutaPDF);
                    printCustom("",2,1);
                    outputStream.write(PrintBitmap.POS_PrintBMP(bitmap, ANCHO_IMG_58_MM, MODE_PRINT_IMG));
                } catch (IOException e) {
                    Toast.makeText(this, "Error al intentar imprimir PDF", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    //Metodo para abrir la galeria
    private void Tomar_foto() {
        if (pedirPermisosFaltantes()) { //Verifica si el usuario concedió todos los permisos

            //Abre la galeria
            galleryPhoto= new GalleryPhoto(getApplicationContext());
            Intent intentGaleria = galleryPhoto.openGalleryIntent();
            startActivityForResult(intentGaleria, INTENT_GALERIA);
        }
    }

    //Metodo que verifica si los permisos necesarios fueron dados.
    private boolean pedirPermisosFaltantes() {
        boolean todosConsedidos = true;
        ArrayList<String> permisosFaltantes = new ArrayList<>();

        boolean permisoCamera = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED);

        boolean permisoEscrituraSD = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);

        boolean permisoLecturaSD = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);

        if (!permisoCamera) {
            todosConsedidos = false;
            permisosFaltantes.add(Manifest.permission.CAMERA);
        }

        if (!permisoEscrituraSD) {
            todosConsedidos = false;
            permisosFaltantes.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permisoLecturaSD) {
            todosConsedidos = false;
            permisosFaltantes.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!todosConsedidos) {
            String[] permisos = new String[permisosFaltantes.size()];
            permisos = permisosFaltantes.toArray(permisos);

            ActivityCompat.requestPermissions(this, permisos, COD_PERMISOS);
        }
        return todosConsedidos;
    }

    //Metodo que imprimme texto
    private void ImprimirTexto() {
        if(btsocket == null){
            Toast.makeText (getApplicationContext (), "Error, ninguna impresora conectada" , Toast.LENGTH_SHORT) .show ();
        }
        else{
            String texto = editText.getText().toString() + "\n";
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;
            //print command
            try {
                outputStream = btsocket.getOutputStream();
                byte[] printformat = { 0x1B, 0x21,FONT_TYPE};
                //se imprime una linea en blanco
                outputStream.write(printformat);
                //El metodo Custom imprime el texto conviertiendo en bitmap y se le concatena alineacion
                printCustom(texto,2,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void imprimirImagen() {
        if(btsocket == null){
            Toast.makeText (getApplicationContext (), "Error, ninguna impresora conectada" , Toast.LENGTH_SHORT) .show ();
        }
        else{
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try {
                rutaFoto_print = getFilesDir()+"/Empresa_log.png";
                ;
                Bitmap bitmap = BitmapFactory.decodeFile(rutaFoto_print);
                printCustom("",2,1);
                outputStream.write(PrintBitmap.POS_PrintBMP(bitmap, ANCHO_IMG_58_MM, MODE_PRINT_IMG));
            } catch (IOException e) {
                Toast.makeText(this, "Error al intentar imprimir imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Metodo para buscar los dispositivos bluetooth disponibles, inicia la clase DeviceList
    private void buscardispositivos() {
        Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
        this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
    }

    //Imprime el texto y luego la imagen seguido
    protected void printDemo() {
        if(btsocket == null){
            Toast.makeText (getApplicationContext (), "Error, ninguna impresora conectada" , Toast.LENGTH_SHORT) .show ();
        }
        else{
            String texto = editText.getText().toString() + "\n";
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;
            //print command
            try {
                outputStream = btsocket.getOutputStream();
                byte[] printformat = { 0x1B, 0x21,FONT_TYPE};
                //se imprime una linea en blanco
                outputStream.write(printformat);
                //El metodo Custom imprime el texto conviertiendo en bitmap y se le concatena alineacion
                printCustom(texto,2,1);

                //Imprime la imagen
                try {
                    File rutaDestino = Constantes.getRutaDestinoImg();
                    rutaFoto_print = rutaDestino.getAbsolutePath();
                    Bitmap bitmap = BitmapFactory.decodeFile(rutaFoto_print);
                    outputStream.write(PrintBitmap.POS_PrintBMP(bitmap, ANCHO_IMG_58_MM, MODE_PRINT_IMG));
                } catch (IOException e) {
                    Toast.makeText(this, "Error al intentar imprimir imagen", Toast.LENGTH_SHORT).show();
                }
                //ImprimePDF
                imprimirPDF();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Cierra conexiones actualmente activas.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(btsocket!= null){
                outputStream.close();
                btsocket.close();
                btsocket = null;
            } else{
                Toast.makeText(this,"No hay dispositivos conectados",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"No hay dispositivos conectados",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verifica si Blutooth está disponoble para iniciar Device List
        try {
            btsocket = DeviceList.getSocket();
            if(btsocket != null){
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //Guarda las fotos tomadas con la camara
                case INTENT_CAMARA:
                    try {
                        rutaFoto = cameraPhoto.getPhotoPath();
                        btnTomarFoto.setText("CAMBIAR IMAGEN");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar la foto, intente de nuevo.", Toast.LENGTH_SHORT).show();
                        btnTomarFoto.setText("TOMAR IMAGEN");
                    }
                    break;
                 //guarda la imagen seleccionada de galera
                case INTENT_GALERIA:
                    galleryPhoto.setPhotoUri(data.getData());
                    // Si el peso es 0 Kb es porque la imagen no existe
                    if (ManipuladorImagen.pesoKBytesFile(galleryPhoto.getPath()) != 0) {
                        rutaFoto = galleryPhoto.getPath();
                        btnTomarFoto.setText("CAMBIAR IMAGEN");
                    } else {
                        Toast.makeText(this, "La imagen que eligió no es valida.", Toast.LENGTH_SHORT).show();
                        btnTomarFoto.setText("CAMBIAR IMAGEN");
                        return;// para que no ejecute el codigo siguiente
                    }
                    break;
            }

            if(rutaFoto != null){
                //Crea un directorio nuevo
                Constantes.crearRutaCarpetaImg();
                //Nombre de la imagen que va a guardarse
                //String nombreImg = "Empresa_log";
                try {
                    String rutaPDFGuardar = getFilesDir()+"/Empresa_log.png";
                    OutputStream output = new FileOutputStream(rutaPDFGuardar);

                    //Guarda l aimagen en el directorio que se acaba de crear
                    File rutaDestino = new File(rutaPDFGuardar);
                    FileUtils.copyFile(new File(rutaFoto), rutaDestino);
                    //carga la imagen en el ImageView
                    Glide.with(getApplicationContext())
                            .load(rutaFoto)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imgFoto);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No se pudo guardar la imagen.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //Tipos de alineaciones para texto que a la vez se encapsula en un case por cada alineacion
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }
            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}