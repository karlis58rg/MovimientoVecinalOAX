package mx.gob.sspo.movimientovecinal.ServiceShake;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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

import mx.gob.sspo.movimientovecinal.AlertaAmber;
import mx.gob.sspo.movimientovecinal.MensajeEnviadoAlertaAmber;
import mx.gob.sspo.movimientovecinal.MensajeError;
import mx.gob.sspo.movimientovecinal.R;
import mx.gob.sspo.movimientovecinal.Transporte.TransporteExistente;
import mx.gob.sspo.movimientovecinal.Transporte.TransporteNoExiste;
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
    int numberRandom,banderaInsert = 0;
    String cargarInfoTelefono,cargarInfoNombre,cargarInfoApaterno,cargarInfoAmaterno,cargarInfoPlaca,serbar = "sincrear",datoEncontrado = "ENCONTRADO",datoNoEncontrado="NOENCONTRADO";
    String cargarPlacaJson,cargarPlaca,cargarNucJson,cargarNuc,cargarSitio,cargarMarca,cargarTipo,cargarDato,sinInformacion="SIN INFORMACION",placaCad="";
    String tituloNotificacion = "Localizando..";
    String cuerpoNotificacion = "Verificando información.";
    String tituloCancelar = "Detener";
    String enviando = "";

    boolean folioEncontrado = false;
    boolean alertaLanzada = false;
    String mensaje = "Cuando concluyas tu emergencia para detener tu alertamiento es necesario: 1.Entrar al App “Oaxaca Seguro / Movilidad Segura” y oprimir “Finalizar Viaje” 2.Ir a las notificaciones “Enviando tu ubicación” y oprimir “Detener”";
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
        Random();
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
        final Notification notification = new NotificationCompat.Builder(this, GENERAL_CHANNEL_ID)
                .setContentTitle(tituloNotificacion)
                .setContentText(cuerpoNotificacion)
                .setSmallIcon(R.drawable.ic_logo_app)
                .addAction(R.drawable.ic_baseline_cancel_24, tituloCancelar, pendingIntent)
                .build();

        startForeground(1, notification);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(60000)
                .setFastestInterval(60000);

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
                        Log.i(TAG, "dir" + direccion + "mun" + municipio + "est" + estado);
                    }
                    if(banderaInsert == 1 && folioEncontrado == true){
                        if(alertaLanzada == false){
                            cuerpoNotificacion = "Enviando ubicación..";
                            enviando = cuerpoNotificacion;
                            pushCad();
                            getDatosMapaRobos();
                            if(cargarDato.equals(datoNoEncontrado)){
                                TransporteNoExiste.lblTituloListos.setText(mensaje);
                            }else if(cargarDato.equals(datoEncontrado)){
                                TransporteExistente.lblTituloListos.setText(mensaje);
                            }
                        }else {
                            getDatosMapaRobos();
                            if(cargarDato.equals(datoNoEncontrado)){
                                TransporteNoExiste.lblTituloListos.setText(mensaje);
                            }else if(cargarDato.equals(datoEncontrado)){
                                TransporteExistente.lblTituloListos.setText(mensaje);
                            }
                        }
                    }else if(banderaInsert == 1 && folioEncontrado == false){
                        if(alertaLanzada == false){
                            tituloNotificacion = "¡Alerta!";
                            cuerpoNotificacion = "Su emergencia no pudo ser enviada";
                            pushCad();
                            if(cargarDato.equals(datoNoEncontrado)){
                                TransporteNoExiste.lblTituloListos.setText(mensaje);
                            }else if(cargarDato.equals(datoEncontrado)){
                                TransporteExistente.lblTituloListos.setText(mensaje);
                            }
                        }else{
                            if(cargarDato.equals(datoNoEncontrado)){
                                TransporteNoExiste.lblTituloListos.setText(mensaje);
                            }else if(cargarDato.equals(datoEncontrado)){
                                TransporteExistente.lblTituloListos.setText(mensaje);
                            }
                        }

                    } else if(banderaInsert != 1){
                        insertBdEventoTransportePublicoIOS();
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
        cargar();
        if(cargarPlaca != sinInformacion){
            placaCad = cargarPlaca;
        }else if(cargarPlacaJson != sinInformacion){
            placaCad = cargarPlacaJson;
        }else if(cargarNuc != sinInformacion){
            placaCad = cargarNuc;
        }else if (cargarNucJson != sinInformacion){
            placaCad = cargarNucJson;
        }

        String sitio = cargarSitio != "SIN INFORMACION" ? cargarSitio : "SIN INFORMACION";
        String marca = cargarMarca != "SIN INFORMACION" ? cargarMarca : "SIN INFORMACION" ;
        String modelo = cargarTipo != "SIN INFORMACION" ? cargarTipo : "SIN INFORMACION" ;

        banderaInsert = 1;
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
                .add("Placa", placaCad)
                .add("Sitio", sitio)
                .add("Marca", marca)
                .add("Modelo", modelo)
                .add("Direccion", direccion)
                .add("Municipio", municipio)
                .add("Estado", estado)
                .add("Latitud", lat.toString())
                .add("Longitud", lon.toString())
                .add("Hora", hora)
                .add("Fecha", fecha)
                //.add("idTipoEmergencia", "6000") /*********¿QUÉ TIPO DE EMERGENCIA ES**************/
                .build();

        Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/EventosTransportePublicoApp/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    final String resp = myResponse;
                    String respCad = resp;
                    final String valor = "\"false\"";
                    if(respCad.equals(valor)){
                        folioEncontrado = false;
                    }else{
                        folioEncontrado= true;
                    }
                    Log.i(TAG, resp);
                }
            }
        });
    }
    //**************** INSERTA A LA TABLA DE ROBOS CON LAS COORDENADAS EN TIEMPO REAL *****************//
    public void insertBdEventoTransportePublicoRobosIOS() {
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
                .add("Longitud", lon.toString())
                .add("Latitud", lat.toString())
                .add("Status", "1")
                .add("Fecha", fecha)
                .add("Hora", hora)
                .add("Placa", cargarInfoPlaca)
                .add( "Direccion",direccion)
                .add( "Revisado", "1")
                .build();

        Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/EventosTransportePublicoRobosApp/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();
                }
            }
        });
    }

    //************************ STSTUS DEL INCIDENTE ***************************************//

    public void getDatosMapaRobos(){
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/EventosTransportePublicoRobosApp?folioRobo=OAX2021"+randomCodigoVerifi+"&statusRobo=1")
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
                        insertBdEventoTransportePublicoRobosIOS();
                    }
                    Log.i("HERE", resp);
                }
            }
        });
    }

    private void cargar() {
        shared = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoTelefono = shared.getString("TELEFONO", "SIN INFORMACION");
        cargarInfoNombre = shared.getString("NOMBRE", "SIN INFORMACION");
        cargarInfoApaterno = shared.getString("APATERNO", "SIN INFORMACION");
        cargarInfoAmaterno = shared.getString("AMATERNO", "SIN INFORMACION");
        cargarInfoPlaca = shared.getString("PLACA","SIN INFORMACION");
        cargarPlacaJson = shared.getString("placaJson", "SIN INFORMACION");
        cargarNucJson = shared.getString("nucJson", "SIN INFORMACION");
        cargarPlaca = shared.getString("PLACA", "SIN INFORMACION");
        cargarNuc = shared.getString("NUC", "SIN INFORMACION");
        cargarSitio = shared.getString("sitioJson", "SIN INFORMACION");
        cargarMarca = shared.getString("marcaJson", "SIN INFORMACION");
        cargarTipo = shared.getString("tipoJson", "SIN INFORMACION");
        cargarDato = shared.getString("DATO", "SIN INFORMACION");
    }
    private void guardarServicio() {
        shared = getSharedPreferences("main", MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("servicio", serbar);
        editor.apply();
    }

    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    public void Random(){
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = codigoVerifi;
    }

    public void pushCad (){
        alertaLanzada = true;
        NotificationManager notificationManager = (NotificationManager)
                getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent receiverIntent1 = new Intent(getApplication(),
                LocationActionsReceiver.class)
                .setAction(LocationActionsReceiver.ACTION_CANCEL);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplication(),
                0, receiverIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
        String GENERAL_CHANNEL_ID = getString(R.string.default_notification_channel_id);
        Notification notification = new NotificationCompat.Builder(getApplication(), GENERAL_CHANNEL_ID)
                .setContentTitle(tituloNotificacion)
                .setContentText(cuerpoNotificacion)
                .setSmallIcon(R.drawable.ic_logo_app)
                .addAction(R.drawable.ic_baseline_cancel_24, tituloCancelar, pendingIntent1)
                .build();
        notificationManager.notify(1, notification);
    }

}
