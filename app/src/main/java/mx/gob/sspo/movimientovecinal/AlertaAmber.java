package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    String mensaje1,mensaje2,direccion, municipio, estado;
    Double lat,lon;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    private String cargarInfoLat,cargarInfoLong,cargarInfoTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_amber);
        Random();
        locationStart();

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
            private String ddmmyyyy = "DDMMYYYY";
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
                        clean = clean + ddmmyyyy.substring(clean.length());
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
                        clean = String.format("%02d%02d%02d", day,mon, year);
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
            private String ddmmyyyy = "DDMMYYYY";
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
                        clean = clean + ddmmyyyy.substring(clean.length());
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
                        clean = String.format("%02d%02d%02d",day,mon,year);
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
                }else if(varSexo == null){
                    Toast.makeText(getApplicationContext(), "EL SEXO ES NECESARIO", Toast.LENGTH_LONG).show();
                }
                /*else if(txtNacionalidad.getText().length() < 3 || txtNacionalidad.getText().toString().isEmpty()) {
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
                }*/
                else if(banderaFoto == 0){
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
        cargarLatLong();
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fechaHechos = dateFormat.format(date);

        nombreAlerta = txtNombreAlerta.getText().toString();
        aPaternoAlerta = txtApaternoAlerta.getText().toString();
        aMaternoAlerta = txtAmaternoAlerta.getText().toString();
        edad = txtEdad.getText().toString();
        nacionalidad = cargarInfoLat;
        colorOjos = cargarInfoLong;
        /*estatura = txtEstatura.getText().toString();
        complexion = txtComplexion.getText().toString();
        fechaNacimiento = txtFechaNacimiento.getText().toString();
        fechaHechos = txtFechaHechos.getText().toString();
        lugarHechos = txtLugarHechos.getText().toString();
        descripcionHechos = txtDescripcionHechos.getText().toString();*/
        estatura = "1.50";
        fechaNacimiento = "01/01/2000";
        lugarHechos = "OAXACA"; //poner el numero de telefono
        descripcionHechos = "DESAPARECIDO";


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
                .add("Complexion",cargarInfoTelefono)
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

    /*********************Apartir de aqui empezamos a obtener la direciones y coordenadas************************/
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        AlertaAmber.Localizacion Local = new AlertaAmber.Localizacion();
        Local.setAlertaAmber(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        mensaje1 = "Localizacion agregada";
        mensaje2 = "";
        Log.i("HERE", mensaje1);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        /***********Obtener la direccion de la calle a partir de la latitud y la longitud******************************/
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion = DirCalle.getAddressLine(0);
                    municipio = DirCalle.getLocality();
                    estado = DirCalle.getAdminArea();
                    if (municipio != null) {
                        municipio = DirCalle.getLocality();
                    } else {
                        municipio = "SIN INFORMACION";
                    }
                    Log.i("HERE", "dir" + direccion + "mun" + municipio + "est" + estado);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**************************Aqui empieza la Clase Localizacion********************************/
    public class Localizacion implements LocationListener {
        AlertaAmber alertaAmber;

        public AlertaAmber getAltoALaViolencia() {
            return alertaAmber;
        }

        public void setAlertaAmber(AlertaAmber alertaAmber1) {
            this.alertaAmber = alertaAmber1;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            guardarCoor();
            String Text = "Lat = " + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            mensaje1 = Text;
            Log.i("HERE", mensaje1);
            this.alertaAmber.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1 = "GPS Desactivado";
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1 = "GPS Activado";
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    /****************SE GUARDA LA INFO DE LAS COORDENADAS****************/

    private void guardarCoor() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        editor = share.edit();
        editor.putString("LATITUDE", lat.toString());
        editor.putString("LONGITUDE", lon.toString());
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }
    //*********************************************************************//
    public void cargarLatLong() {
        share = getApplication().getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoLat = share.getString("LATITUDE", "SIN INFORMACION");
        cargarInfoLong = share.getString("LONGITUDE", "SIN INFORMACION");
        cargarInfoTelefono = share.getString("TELEFONO", "SIN INFORMACION");
    }
    public void Random(){
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = "OAX2021"+codigoVerifi;
    }


}
