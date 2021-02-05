package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlertaAmber extends AppCompatActivity {

    EditText txtNombreAlerta,txtApaternoAlerta,txtAmaternoAlerta,txtEdad,txtNacionalidad,txtColorOjos,txtEstatura,txtComplexion,txtFechaNacimiento,txtFechaHechos,txtLugarHechos,txtDescripcionHechos;
    RadioGroup rgSexo;
    Button btnEnviarAlertaAmber;
    AlertDialog alert = null;
    ImageView home,pickFotoAvatar;
    CircleImageView avatar2;
    int banderaFoto = 0;
    String nombreCompleto,nombreAlerta,aPaternoAlerta,aMaternoAlerta,varSexo,edad,nacionalidad,colorOjos,estatura,complexion,fechaNacimiento,fechaHechos,lugarHechos,descripcionHechos,cadena;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int dia,mes,año,dia1,mes1,año1;
    int numberRandom;
    String randomCodigoVerifi,codigoVerifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_amber);
        Random();

        home = findViewById(R.id.imgHomeAlertaAmber);
        btnEnviarAlertaAmber = findViewById(R.id.btnEnviarAlertaAmber);
        rgSexo = findViewById(R.id.rgSexo);
        txtNombreAlerta = findViewById(R.id.txtNombreAlerta);
        txtApaternoAlerta = findViewById(R.id.txtApaternoAlerta);
        txtAmaternoAlerta = findViewById(R.id.txtAmaternoAlerta);
        txtEdad = findViewById(R.id.txtEdad);
        txtNacionalidad = findViewById(R.id.txtNacionalidad);
        txtColorOjos = findViewById(R.id.txtColorOjos);
        txtEstatura = findViewById(R.id.txtEstatura);
        txtComplexion = findViewById(R.id.txtComplexion);
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);
        txtFechaHechos = findViewById(R.id.txtFechaHechos);
        txtLugarHechos = findViewById(R.id.txtLugarHechos);
        txtDescripcionHechos = findViewById(R.id.txtDescripcionHechos);
        pickFotoAvatar = findViewById(R.id.pickFoto_Amber);
        avatar2 = findViewById(R.id.profile_image_Amber);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /*********************** CALENDARIO PARA FECHAS EN FORMULARIOS *********************************/

        txtFechaNacimiento.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String mmddyyyy = "MMDDYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + mmddyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);


                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    txtFechaNacimiento.setText(current);
                    txtFechaNacimiento.setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        txtFechaHechos.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String mmddyyyy = "MMDDYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + mmddyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);


                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    txtFechaHechos.setText(current);
                    txtFechaHechos.setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rgSexo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbMasculino) {
                    varSexo = "MASCULINO";
                } else if (checkedId == R.id.rbFemenino) {
                    varSexo = "FEMENINO";
                }
            }
        });

        pickFotoAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(AlertaAmber.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    banderaFoto = 1;
                    llamarItemAvatar();
                }
            }
        });

        btnEnviarAlertaAmber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtNombreAlerta.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "EL NOMBRE ES NECESARIO", Toast.LENGTH_LONG).show();
                }else if (txtNombreAlerta.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, EL NOMBRE NO PUEDE SER MENOR A 3 LETRAS", Toast.LENGTH_LONG).show();
                }else if(txtApaternoAlerta.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "EL A. PATERNO ES NECESARIO", Toast.LENGTH_LONG).show();
                }else if(txtApaternoAlerta.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, EL A. PATERNO NO PUEDE SER MENOR A 3 LETRAS", Toast.LENGTH_LONG).show();
                }else if(txtAmaternoAlerta.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "EL A. MATERNO ES NECESARIO", Toast.LENGTH_LONG).show();
                }else if(txtAmaternoAlerta.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, EL A. MATERNO NO PUEDE SER MENOR A 3 LETRAS", Toast.LENGTH_LONG).show();
                }else if(txtEdad.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "LA EDAD ES NECESARIA", Toast.LENGTH_LONG).show();
                }else if(txtNacionalidad.getText().length() < 3 || txtNacionalidad.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, LA NACIONALIDAD NO PUEDE SER MENOR A 3 LETRAS", Toast.LENGTH_LONG).show();
                }else if(txtColorOjos.getText().length() < 3 || txtColorOjos.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, EL COLOR DE OJOS NO PUEDE SER MENOR A 3 LETRAS NI CONTENER ESPACIOS EN BLANCO", Toast.LENGTH_LONG).show();
                }else if(txtEstatura.getText().length() < 2 || txtEstatura.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, LA ESTATURA NO PUEDE SER MENOR A 1 LETRA NI CONTENER ESPACIOS EN BLANCO", Toast.LENGTH_LONG).show();
                }else if(txtComplexion.getText().length() < 3 || txtComplexion.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, LA COMPLEXCIÓN NO PUEDE SER MENOR A 3 LETRA NI CONTENER ESPACIOS EN BLANCO", Toast.LENGTH_LONG).show();
                }else if(txtFechaNacimiento.getText().length() < 2 || txtFechaNacimiento.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, LA FECHA DE NACIMIENTO NO PUEDE SER MENOR A 3 LETRA NI CONTENER ESPACIOS EN BLANCO", Toast.LENGTH_LONG).show();
                }else if(txtFechaHechos.getText().length() < 2 || txtFechaHechos.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, LA FECHA DE LOS HECHOS NO PUEDE SER MENOR A 3 LETRA NI CONTENER ESPACIOS EN BLANCO", Toast.LENGTH_LONG).show();
                }else if(txtLugarHechos.getText().length() < 3 || txtLugarHechos.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, EL LUGAR DE LOS HECHOS NO PUEDE SER MENOR A 3 LETRA NI CONTENER ESPACIOS EN BLANCO", Toast.LENGTH_LONG).show();
                }else if(txtDescripcionHechos.getText().length() < 3 || txtDescripcionHechos.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, LA DESCRIPCIÓN DE LOS HECHOS NO PUEDE SER MENOR A 3 LETRA NI CONTENER ESPACIOS EN BLANCO", Toast.LENGTH_LONG).show();
                }else if(banderaFoto == 0){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, LA FOTOGRAFIA ES NECESARIA PARA LA BUSQUEDA DE LA PERSONA DESAPARECIDA", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_LONG).show();
                    insertImagenDesaparecido();
                    insertUserDesaparecido();
                }
            }
        });
    }

    //********************************** SE CONVIERTE LA IMAGEN A BASE64 Y SE ENVIA AL SERVIDOR ***********************************//
    private void llamarItemAvatar() {
       /* Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takePictureIntent.setType("image/");
        startActivityForResult(takePictureIntent.createChooser(takePictureIntent,"Selecciona la aplicación"),REQUEST_IMAGE_CAPTURE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            avatar2.setImageBitmap(imageBitmap);*/
            Uri path = data.getData();
            avatar2.setImageURI(path);
            imagen2();
            pickFotoAvatar.setVisibility(View.GONE);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Tu acción ha sido cancelada", Toast.LENGTH_SHORT).show();
            avatar2.clearAnimation();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void imagen2() {
        avatar2.buildDrawingCache();
        Bitmap bitmap = avatar2.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imgBytes = baos.toByteArray();
        String imgString = android.util.Base64.encodeToString(imgBytes, android.util.Base64.NO_WRAP);
        cadena = imgString;

        imgBytes = android.util.Base64.decode(imgString, android.util.Base64.NO_WRAP);
        Bitmap decoded = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        avatar2.setImageBitmap(decoded);
        System.out.print("IMAGEN" + avatar2);
    }
    //********************************** INSERTA IMAGEN AL SERVIDOR ***********************************//
    public void insertImagenDesaparecido() {
        nombreAlerta = txtNombreAlerta.getText().toString();
        aPaternoAlerta = txtApaternoAlerta.getText().toString();
        aMaternoAlerta = txtAmaternoAlerta.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Description", randomCodigoVerifi+".jpg")
                .add("ImageData", cadena)
                .build();
        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/MultimediaFotoUserDesaparecidos/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO, FAVOR DE VERIFICAR SU CONEXCIÓN A INTERNET", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/
                    AlertaAmber.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    //********************************** INSERTA REGISTROP AL SERVIDOR ***********************************//
    public void insertUserDesaparecido(){
        nombreAlerta = txtNombreAlerta.getText().toString();
        aPaternoAlerta = txtApaternoAlerta.getText().toString();
        aMaternoAlerta = txtAmaternoAlerta.getText().toString();
        edad = txtEdad.getText().toString();
        nacionalidad = txtNacionalidad.getText().toString();
        colorOjos = txtColorOjos.getText().toString();
        estatura = txtEstatura.getText().toString();
        complexion = txtComplexion.getText().toString();
        fechaNacimiento = txtFechaNacimiento.getText().toString();
        fechaHechos = txtFechaHechos.getText().toString();
        lugarHechos = txtLugarHechos.getText().toString();
        descripcionHechos = txtDescripcionHechos.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Nombre",nombreAlerta)
                .add("APaterno",aPaternoAlerta)
                .add("AMaterno",aMaternoAlerta)
                .add("Genero",varSexo)
                .add("Edad",edad)
                .add("Nacionalidad",nacionalidad)
                .add("ColorOjos",colorOjos)
                .add("Estatura",estatura)
                .add("Complexion",complexion)
                .add("FechaNacimiento",fechaNacimiento)
                .add("FechaHechos",fechaHechos)
                .add("LugarHechos",lugarHechos)
                .add("DescripcionHechos",descripcionHechos)
                .add("UrlaFoto","http://187.174.102.142/AppMovimientoVecinal/FotoDesaparecidos/"+randomCodigoVerifi+".jpg")
                .add("StatusDesaparicion","1")
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/AlertaAmber/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO, FAVOR DE VERIFICAR SU CONEXCIÓN A INTERNET", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();
                    AlertaAmber.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(AlertaAmber.this,MensajeEnviadoAlertaAmber.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        });
    }

    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    public void Random(){
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = "OAX2021"+codigoVerifi;
    }


}
