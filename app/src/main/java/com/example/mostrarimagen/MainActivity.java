package com.example.mostrarimagen;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    EditText ID, Des;
    Button load, New, save;
    ImageView imgV;
    TextView URL;
    String Dirección;

    final int REQUEST_CODE_GALLERY = 999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        New.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });





    }

    public void init()
    {
        ID = (EditText)findViewById(R.id.Id);
        Des = (EditText)findViewById(R.id.Des);
        load = (Button)findViewById(R.id.btn_load);
        save = (Button)findViewById(R.id.btn_save);
        New = (Button)findViewById(R.id.btn_new);
        imgV = (ImageView)findViewById(R.id.imgV);
        URL = (TextView)findViewById(R.id.URL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE_GALLERY)
        {
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){

                Intent intentt = new Intent(Intent.ACTION_PICK);
                intentt.setType("image/");

                startActivityForResult(intentt, REQUEST_CODE_GALLERY);
            }
            else{
                Toast.makeText(this, "No tienes los permisos necesarios", Toast.LENGTH_SHORT).show();}
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==REQUEST_CODE_GALLERY && resultCode==RESULT_OK && data!=null){

            Uri url = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(url);
                URL.setText(url.toString());
                Dirección = URL.getText().toString();
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                imgV.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }



    //Guardar.
    public void GuardarFoto(View view){
        try{
            //Conexión.

            Database admin = new Database(this, "ImagesDatabase", null, 1);

            //Abres la database en modo lectura y escritura.

            SQLiteDatabase ImagesDatabase = admin.getWritableDatabase();

            String Id = ID.getText().toString();
            String D = Des.getText().toString();
            String UD = Dirección;

            if (!Id.isEmpty() && !D.isEmpty()){

                ContentValues Datos = new ContentValues();
                Datos.put("Id", Id);
                Datos.put("Descripción", D);
                //Datos.put("Image", imgVToBye(imgV));
                Datos.put("Image", UD);

                ImagesDatabase.insert("Images", null, Datos);

                ID.setText("");
                Des.setText("");
                URL.setText("");
                imgV.setImageResource(R.drawable.ic_launcher_background);
                ImagesDatabase.close();

                Toast.makeText(this, "Imágen guardada", Toast.LENGTH_SHORT).show();

            }

        }
        catch (Exception e){Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();}


    }

    //Convertir a byte. Ya no es necesario, ahora lo mostramos por ID
    private byte[] imgVToBye(ImageView imgV) {

        Bitmap bitmap = ((BitmapDrawable)imgV.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //Cargar.
    public void CargarFoto(View view){
        try{
            //Conexión.

            Database admin = new Database(this, "ImagesDatabase", null, 1);

            //Abrir en modo lectura y escritura.

            SQLiteDatabase db = admin.getWritableDatabase();

            String ID_Validation = ID.getText().toString();

            String Image_URL = Dirección;

            if (!ID_Validation.isEmpty()){
                Cursor fila =db.rawQuery(
                        "select Descripción, Image from Images where Id='"+ ID_Validation + "'", null);

                if (fila.moveToFirst()){
                    Des.setText(fila.getString(0));
                    //URL.setText(fila.getString(2));
                    URL.setText(fila.getString(1));
                    InputStream inputStream = getContentResolver().openInputStream(Uri.parse(URL.getText().toString()));
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    imgV.setImageBitmap(bmp);
                    //imgV.setImageURI(Uri.parse(Dirección));
                    //imgV.setImageBitmap(ByteArrayToBitmap(fila.getBlob(1)));
                    db.close();

                }
                else {Toast.makeText(this, "No se encontró fotografía", Toast.LENGTH_SHORT).show(); db.close();}
            }


        }

        catch (Exception e){Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();}



    }

    //Convertir de byte a Bitmap. Ya no es necesario, ahora lo mostramos por ID
    public Bitmap ByteArrayToBitmap(byte[] byteArray)
    {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }

    public void EliminarFoto(View view){

        //Con el nombre de tu BDD.
        Database admin = new Database(this, "ImagesDatabase", null, 1);

        //Abro la BDD.
        SQLiteDatabase db = admin.getWritableDatabase();

        String id_Delete = ID.getText().toString();
        String des_Delete = Des.getText().toString();
         //¿Cómo vamos a tomar el objeto Bye?
    }
}
