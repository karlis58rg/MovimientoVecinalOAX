package mx.gob.sspo.movimientovecinal.ServiceShake;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import mx.gob.sspo.movimientovecinal.R;
import mx.gob.sspo.movimientovecinal.TransporteSeguro;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static mx.gob.sspo.movimientovecinal.Shake.App.CHANNEL_ID;

public class Service911TS extends Service implements SensorEventListener {
    public Service911TS(Context applicationContext) {
        super();
        Log.i("HERE", "HERE I AM SERVICE911TS!");
    }

    /**********VARIABLES PARA EL SHAKE***************/
    public static MyTask2 miTareaSuperTS;
    public SensorManager mSensorManager;
    public Sensor mAccelerometer;
    public float mAccel; // acceleration apart from gravity
    public float mAccelCurrent = 0; // current acceleration including gravity
    public float mAccelLast; // last acceleration including gravity
    public int sacudidas = 0;
    public long tiempoActual = 0;
    public long tiempoAnterior = 0;
    public long diferencia;
    public String cargarInfoSDK;
    public int cargarInfoValorShake = 0;
    public String serbar = "sincrear";
    int bandera = 0;
    int banderaInsert = 0;
    String version;
    int comparar;
    private static final int NOTIFICATION_ID = 200;
    private static final String CHANNEL_NAME = "PANIC_BUTTON";

    /************UBICACION Y VARIABLES A INSERTAR AL SERVER***************/
    private Activity activity;
    String mensaje1, mensaje2, direccion, municipio, estado;
    String valorRandom,codigoVerifi,randomCodigoVerifi,fecha,hora;
    Double lat, lon;
    int numberRandom,cargarInfoWtransporteMW,cargarInfoValorWidget;
    String cargarInfoTelefono,cargarInfoNombre,cargarInfoApaterno,cargarInfoAmaterno,cargarInfoPlaca,cargarInfoDireccion,cargarInfoMunicipio,cargarInfoEstado,cargarInfoLat,cargarInfoLong;

    /************GUARDAR PREFERENCIAS DEL SISTEMA***************/
    SharedPreferences shared;
    SharedPreferences.Editor editor;

    public Service911TS() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("HERE", "SERVICIO CREADO");
        miTareaSuperTS = new MyTask2();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        miTareaSuperTS.execute();
        cargar();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        //startForeground();

        Intent notificationIntent = new Intent(this, TransporteSeguro.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Movilidad Segura")
                .setContentText("Su alerta ha sido enviada al Servicio de Emergencias 9-1-1")
                .setSmallIcon(R.drawable.ic_logo_app)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        //All notifications should go through NotificationChannel on Android 26 & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("HEY", "onDestroy!");
        miTareaSuperTS.cancel(true);
    }

    public class MyTask2 extends AsyncTask<String, String, String> {
        private boolean aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            aux = true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            serbar = "creado";
            guardarServicio();
            //Toast.makeText(getApplicationContext(), "EJECUTANDO SERVICIO CADA 15 SEGUNDOS", Toast.LENGTH_SHORT).show();
            Log.i("HERE","Localización cada 15 segundos");
            locationStart();
            if(banderaInsert == 1){
                getDatosMapaRobos();
            }else if(bandera == 1){
                locationStart();
                getDatosMapaRobos();
            }else if(bandera == 3){
                bandera = 0;
                Log.i("HEY", "BANDERA 4!");
                eliminarServicio();
                stopSelf();
                stopForeground(true);
                miTareaSuperTS.cancel(true);
                stopService(new Intent(Service911TS.this, Service911TS.class));
                onDestroy();
            }else{
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1500);
                Random();
                insertBdEventoTransportePublicoIOS();
                Toast.makeText(getApplicationContext(), "EMERGENCIA ENVIADA", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            miTareaSuperTS.onCancelled();
            aux = false;
        }

        @Override
        protected String doInBackground(String... strings) {
            while (aux) {
                try {
                    publishProgress();
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    /*********************Apartir de aqui empezamos a obtener la direciones y coordenadas************************/
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Service911TS.Localizacion Local = new Service911TS.Localizacion();
        Local.setServiceShake(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
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
                    guardar();
                    Log.i("HERE", "dir" + direccion + "mun" + municipio + "est" + estado);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**************************Aqui empieza la Clase Localizacion********************************/
    public class Localizacion implements LocationListener {
        Service911TS serviceShake;

        public Service911TS getServiceShake() {
            return serviceShake;
        }

        public void setServiceShake(Service911TS serviceShake1) {
            this.serviceShake = serviceShake1;
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
            this.serviceShake.setLocation(loc);
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

    //*********************** METODO QUE INSERTA A LA BASE DE DATOS DESPUES DE INSERTAR AL CAD ***********************//
    public void insertBdEventoTransportePublicoIOS(){
        banderaInsert = 1;
        cargar();
        valorRandom = "OAX2021"+ randomCodigoVerifi;
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("FolioRobo",valorRandom)
                .add("Telefono", cargarInfoTelefono)
                .add("NombreUsuario", cargarInfoNombre)
                .add("ApaternoUsuario", cargarInfoApaterno)
                .add("AmaternoUsuario", cargarInfoAmaterno)
                .add("Placa", cargarInfoPlaca)
                .add("Direccion", cargarInfoDireccion)
                .add("Municipio", cargarInfoMunicipio)
                .add("Estado", cargarInfoEstado)
                .add("Latitud", cargarInfoLat)
                .add("Longitud", cargarInfoLong)
                .add("Hora", hora)
                .add("Fecha", fecha)
                //.add("idTipoEmergencia", "6000") /*********¿QUÉ TIPO DE EMERGENCIA ES**************/
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/EventosTransportePublicoApp/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/
                    insertBdEventoTransportePublicoRobosIOS();

                }
            }
        });
    }

    //**************** INSERTA A LA TABLA DE ROBOS CON LAS COORDENADAS EN TIEMPO REAL *****************//
    public void insertBdEventoTransportePublicoRobosIOS() {
        cargar();
        valorRandom = "OAX2021"+ randomCodigoVerifi;
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("FolioRobo",valorRandom)
                .add("Telefono", cargarInfoTelefono)
                .add("Longitud", cargarInfoLong)
                .add("Latitud", cargarInfoLat)
                .add("Status", "1")
                .add("Fecha", fecha)
                .add("Hora", hora)
                .add("Placa", cargarInfoPlaca)
                .add( "Direccion",cargarInfoDireccion)
                .add( "Revisado", "1")
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/EventosTransportePublicoRobosApp/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();
                    bandera = 1;/********** ME REGRESA LA RESPUESTA DEL WS ****************/
                    //startTimer();
                }
            }
        });
    }

    //************************ STSTUS DEL INCIDENTE ***************************************//

    public void getDatosMapaRobos(){
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/EventosTransportePublicoRobosApp?folioRobo=OAX2021"+randomCodigoVerifi+"&statusRobo=1")
                //.url("http://c5.hidalgo.gob.mx:58/api/EventosTransportePublicoRobos?folioRobo=C5I2019891&statusRobo=1")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    final String resp = myResponse;
                    final String valor = "true";
                    if(resp.equals(valor)){
                        Log.i("HERE", "BANDERA 1 CON CICLO");
                        Log.i("HERE", resp);
                        insertBdEventoTransportePublicoRobosIOS();
                    }else{
                        bandera = 3;
                        Log.i("HERE", resp);
                        Log.i("HERE", "PROCESO TERMINADO");
                    }
                    Log.i("HERE", resp);

                }
            }
        });
    }

    private void runOnUiThread(Runnable runnable) {
        if(bandera == 1){
            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
        }else if (bandera == 2){
            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
        }else if(bandera == 3){
            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
        }
    }

    //**************************************************************************************//
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void guardarServicio() {
        shared = getSharedPreferences("main", MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("servicio", serbar);
        editor.putInt("bandera", bandera);
        editor.apply();
    }

    private void cargar() {
        shared = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoTelefono = shared.getString("TELEFONO", "SIN INFORMACION");
        cargarInfoNombre = shared.getString("NOMBRE", "SIN INFORMACION");
        cargarInfoApaterno = shared.getString("APATERNO", "SIN INFORMACION");
        cargarInfoAmaterno = shared.getString("AMATERNO", "SIN INFORMACION");
        cargarInfoPlaca = shared.getString("PLACA","SIN INFORMACION");
        cargarInfoDireccion = shared.getString("DIRECCION","SIN INFORMACION");
        cargarInfoMunicipio = shared.getString("MUNICIPIO","SIN INFORMACION");
        cargarInfoEstado = shared.getString("ESTADO","SIN INFORMACION");
        cargarInfoLat = shared.getString("LATITUDE","SIN INFORMACION");
        cargarInfoLong = shared.getString("LONGITUDE","SIN INFORMACION");
        cargarInfoWtransporteMW = shared.getInt("WTRANSPORTE", 0);
        cargarInfoValorWidget = shared.getInt("OPRIMIR", 0);
    }

    private void eliminarServicio(){
        shared = getSharedPreferences("main", MODE_PRIVATE);
        shared.edit().remove("servicio").commit();
        //Toast.makeText(getApplicationContext(),"Dato Eliminado",Toast.LENGTH_LONG).show();
    }

    /****************SE GUARDA LA INFO DE LAS COORDENADAS****************/
    private void guardar() {
        shared = getSharedPreferences("main", MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("DIRECCION", direccion);
        editor.putString("MUNICIPIO", municipio);
        editor.putString("ESTADO", estado);
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    private void guardarCoor() {
        shared = getSharedPreferences("main", MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("LATITUDE", lat.toString());
        editor.putString("LONGITUDE", lon.toString());
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    public void Random(){
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = codigoVerifi;
    }
}
