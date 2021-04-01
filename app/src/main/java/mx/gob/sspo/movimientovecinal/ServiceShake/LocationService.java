package mx.gob.sspo.movimientovecinal.ServiceShake;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import mx.gob.sspo.movimientovecinal.R;
import mx.gob.sspo.movimientovecinal.receiver.LocationActionsReceiver;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationService extends Service {

    private static String TAG = "LocationService";
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    /************UBICACION Y VARIABLES A INSERTAR AL SERVER***************/
    String direccion, municipio, estado;
    String valorRandom,codigoVerifi,randomCodigoVerifi,fecha,hora;
    Double lat, lon;
    int numberRandom,cargarInfoWtransporteMW,cargarInfoValorWidget,bandera = 0,banderaInsert = 0;
    String cargarInfoTelefono,cargarInfoNombre,cargarInfoApaterno,cargarInfoAmaterno,cargarInfoPlaca,cargarInfoDireccion,cargarInfoMunicipio,cargarInfoEstado,cargarInfoLat,cargarInfoLong,serbar = "sincrear";

    /************GUARDAR PREFERENCIAS DEL SISTEMA***************/
    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Intent receiverIntent = new Intent(this,
                LocationActionsReceiver.class)
                .setAction(LocationActionsReceiver.ACTION_CANCEL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, receiverIntent, 0);

        String GENERAL_CHANNEL_ID = getString(R.string.default_notification_channel_id);
        Notification notification = new NotificationCompat.Builder(this, GENERAL_CHANNEL_ID)
                .setContentTitle("Localizando")
                .setContentText("Enviando ubicación...")
                .setSmallIcon(R.drawable.ic_logo_app)
                .addAction(R.drawable.ic_baseline_cancel_24, "Detener", pendingIntent)
                .build();

        startForeground(1, notification);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                serbar = "creado";
                guardarServicio();
                lat = locationResult.getLastLocation().getLatitude();
                lon = locationResult.getLastLocation().getLongitude();
                Geocoder geocoder = new Geocoder(LocationService.this, Locale.getDefault());
                try {
                    List<Address> list = geocoder.getFromLocation(lat,lon, 1);
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
                        //guardar();
                        Log.i("DIRECCION", "dir" + direccion + "mun" + municipio + "est" + estado);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Location : " +
                        locationResult.getLastLocation().getLatitude() +
                        " , " + locationResult.getLastLocation().getLongitude());
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    /******************************************************************************************************************/
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

    private void guardarServicio() {
        shared = getSharedPreferences("main", MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("servicio", serbar);
        //editor.putInt("bandera", bandera);
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
        //cargarInfoLat = shared.getString("LATITUDE","SIN INFORMACION");
        //cargarInfoLong = shared.getString("LONGITUDE","SIN INFORMACION");
        //cargarInfoWtransporteMW = shared.getInt("WTRANSPORTE", 0);
        //cargarInfoValorWidget = shared.getInt("OPRIMIR", 0);
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
