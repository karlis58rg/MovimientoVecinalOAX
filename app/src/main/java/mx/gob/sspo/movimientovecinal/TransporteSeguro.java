package mx.gob.sspo.movimientovecinal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.gob.sspo.movimientovecinal.ServiceShake.Service911TS;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransporteSeguro extends AppCompatActivity {
    LinearLayout lyTransporte,lyIntroduce,lyPlaca,lyEnviarPlaca,lyPlacaEnviada,lyEncasoDe,lyDetenerServicio,lyDetenerServicioEjecución,lyEmergenciaEnviada;
    EditText txtPlaca;
    TextView lblNoPlaca,coordenadas;
    Button btnIniciar,btnDetenerServicio;
    String placa;
    ImageView home;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    Activity activity;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;
    private LocationManager locationManager;
    private Context context;
    int acceso = 0;
    AlertDialog alert = null;
    String cargarInfoServicio,cargarInfoPlaca,cargarInfoTelefono,cargarInfoPlacaGuardada = "SIN INFORMACION";
    String cargarInfoServicioShake = "creado";
    String mensaje1,mensaje2;
    String direc;
    Double lat,lon;
    int cargarInfoWtransporteSeguro = 0;
    int wTransporteSeguro = 0;

    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private Service911TS mSensorService;
    Context ctx;
    AppWidgetManager manager;
    View view;

    public Context getCtx() {
        return ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_seguro);
        cargarServicio();
        cargarPlaca();
        if(cargarInfoServicio.equals(cargarInfoServicioShake)){
            Log.i("HEY", "CON SERVICIO INICIADO");
        }else{
            getDatosPlaca();
        }

        home = findViewById(R.id.imgHomeTransporte);

        /***************FASE 1********************/
        lyTransporte = findViewById(R.id.lyTransporte);
        lyIntroduce = findViewById(R.id.lyIntroduce);
        lyPlaca = findViewById(R.id.lyPlaca);
        lyEnviarPlaca = findViewById(R.id.lyEnviarPlaca);
        txtPlaca = findViewById(R.id.txtPlaca);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnDetenerServicio = findViewById(R.id.btnDetenerServicio);
        coordenadas = (TextView)findViewById(R.id.lblCoordenadasSensorPlaca);

        /*************FASE 2********************/
        lyPlacaEnviada = findViewById(R.id.lyPlacaEnviada);
        lyEncasoDe = findViewById(R.id.lyEncasoDe);
        lyDetenerServicio = findViewById(R.id.lyDetenerServicio);
        lyDetenerServicioEjecución = findViewById(R.id.lyDetenerServicioEjecución);
        lblNoPlaca = findViewById(R.id.lblNoPlaca);
        lyEmergenciaEnviada = findViewById(R.id.lyEmergenciaEnviada);

        lyPlacaEnviada.setVisibility(View.INVISIBLE);
        lyEncasoDe.setVisibility(View.INVISIBLE);
        lyDetenerServicio.setVisibility(View.INVISIBLE);
        lyDetenerServicioEjecución.setVisibility(View.INVISIBLE);
        lblNoPlaca.setVisibility(View.INVISIBLE);
        lyEmergenciaEnviada.setVisibility(View.INVISIBLE);
        txtPlaca.setEnabled(false);

        if(cargarInfoWtransporteSeguro == 1){
            txtPlaca.setEnabled(true);
        }else {
            txtPlaca.setEnabled(false);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("LO SENTIMOS, ES NECESARIO TENER UN ACCESO DIRECTO CONFIGURADO")
                    .setCancelable(false)
                    .setPositiveButton("CONFIGURAR ACCESO DIRECTO", new DialogInterface.OnClickListener() {
                        @SuppressLint("NewApi")
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            wTransporteSeguro = 1;
                            guardarActividad();
                            AppWidgetManager mAppWidgetManager = getSystemService(AppWidgetManager.class);
                            ComponentName myProvider = new ComponentName(getApplication(), MiWidgetT.class);
                            if(mAppWidgetManager.isRequestPinAppWidgetSupported()){
                                mAppWidgetManager.requestPinAppWidget(myProvider,null,null);
                            }
                            finish();
                        }
                    })
                    .setNegativeButton("EN OTRO MOMENTO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            finish();
                        }
                    });
            alert = builder.create();
            alert.show();
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //************************* SERVICIO ********************************//
        mSensorService = new Service911TS(getCtx());
        ctx = this;
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        //************************* SERIVCIO ********************************//
        context = getApplicationContext();
        activity = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(acceso == 0){
            solicitarPermisoLocalizacion();
        } else {
            Toast.makeText(getApplicationContext()," **GPS** ES OBLIGATORIO PARA EL CORRECTO FUNCIONAMIENTO DEL APLICATIVO",Toast.LENGTH_LONG).show();
        }
        if(cargarInfoPlaca.equals(cargarInfoPlacaGuardada)){
        }else{
            lblNoPlaca.setText(cargarInfoPlaca);
            lyTransporte.setVisibility(View.INVISIBLE);
            lyIntroduce.setVisibility(View.INVISIBLE);
            lyPlaca.setVisibility(View.INVISIBLE);
            lyEnviarPlaca.setVisibility(View.INVISIBLE);

            lyPlacaEnviada.setVisibility(View.VISIBLE);
            lyEncasoDe.setVisibility(View.VISIBLE);
            lyDetenerServicio.setVisibility(View.VISIBLE);
            lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
            lblNoPlaca.setVisibility(View.VISIBLE);
        }

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPlaca.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"EL CAMPO **PLACA** ES OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else{
                    placa = txtPlaca.getText().toString();
                    lblNoPlaca.setText(placa);
                    guardar();
                    insertPlacaTransporte();
                    lyTransporte.setVisibility(View.INVISIBLE);
                    lyIntroduce.setVisibility(View.INVISIBLE);
                    lyPlaca.setVisibility(View.INVISIBLE);
                    lyEnviarPlaca.setVisibility(View.INVISIBLE);

                    lyPlacaEnviada.setVisibility(View.VISIBLE);
                    lyEncasoDe.setVisibility(View.VISIBLE);
                    lyDetenerServicio.setVisibility(View.VISIBLE);
                    lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
                    lblNoPlaca.setVisibility(View.VISIBLE);
                }

            }
        });

        btnDetenerServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaDetenerServicio();
            }
        });
    }

    //********************************** INSERT AL SERVIDOR ***********************************//
    public void insertPlacaTransporte(){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Telefono",cargarInfoTelefono)
                .add("Placa",placa)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/StatusPlacas")
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
                    final String myResponse = response.body().string();
                    TransporteSeguro.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String resp = myResponse;
                            String valor = "true";
                            if(resp.equals(valor)){
                                Toast.makeText(getApplicationContext(),"PLACA REGISTRADA",Toast.LENGTH_LONG).show();
                                Log.i("HERE", "PLACA REGISTRADA EN LA BD CON STATUS 1");
                            }else{
                                alertaReiniciarPlaca();
                            }
                            Log.i("HERE", resp);

                        }
                    });
                }
            }
        });
    }
    //**********************************  UPDATE AL SERVIDOR ***********************************//
    public void updatePlacaStatus(){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Telefono",cargarInfoTelefono)
                .add("Placa",cargarInfoPlaca)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/StatusPlacas?placaTel="+cargarInfoTelefono+"&reiniciarPlaca="+cargarInfoPlaca)
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
                    final String myResponse = response.body().string();
                    TransporteSeguro.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String resp = myResponse;
                            String valor = "true";
                            if(resp.equals(valor)){
                                Toast.makeText(getApplicationContext(),"VIAJE CONCLUIDO",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO, FAVOR DE VERIFICAR SU CONEXCIÓN A INTERNET", Toast.LENGTH_LONG).show();
                            }
                            Log.i("HERE", resp);
                        }
                    });
                }
            }
        });
    }
    //**********************************  GET AL SERVIDOR ***********************************//
    public void getDatosPlaca(){
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/StatusPlacas?placaExistenteTelefono="+cargarInfoTelefono+"&placaStatusUno="+cargarInfoPlaca)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    TransporteSeguro.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String resp = myResponse;
                            String valor = "true";
                            if(resp.equals(valor)){
                                Toast.makeText(getApplicationContext(), "ALERTA EN PROCESO", Toast.LENGTH_LONG).show();
                                startService( new Intent( TransporteSeguro.this, Service911TS.class ) );
                            }else{
                                Log.i("HERE", "SIN PLACA EN STATUS 1");
                            }
                            Log.i("HERE", resp);

                        }
                    });
                }
            }
        });
    }
    //**********************************************************************//
    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("PLACA",placa);
        editor.apply();
    }
    //******************************** METODOS DEL SERVICIO ****************************************//
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService( Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(FormSensorIngresaPlaca.this, ServiceShake.class));
        Log.i("MAINACT", "onDestroy!");
    }
    //************************************ PERMISOS GPS ***********************************************//
    public void solicitarPermisoLocalizacion(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(TransporteSeguro.this, "Permisos Activados", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, CODIGO_SOLICITUD_PERMISO);
        }
    }
    private void alertaGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema de GPS esta desactivado, ¿Desea Activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        startActivity(new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    private void alertaDetenerServicio(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿ESTÁ USTED SEGURO EN DETENER LA ALERTA?")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isMyServiceRunning( mSensorService.getClass())) {
                            //updateServicio();
                            updatePlacaStatus();
                            limpiarPlaca();
                            stopService( mServiceIntent );
                            stopService( new Intent( TransporteSeguro.this, Service911TS.class ) );
                            onDestroy();
                            System.exit( 0 );
                            Log.i("HEY", "CON SERVICIO INICIADO");
                        }else{
                            //updateServicio();
                            updatePlacaStatus();
                            limpiarPlaca();
                            Log.i("HEY", "SIN SERVICIO");
                            //Intent intent = new Intent( TransporteSeguro.this, MenuEventos.class );
                            //startActivity( intent );
                            finish();

                        }
                    }
                })
                .setNegativeButton("NO, CONTINUAR CON LA ALERTA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    private void alertaReiniciarPlaca(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("USTED YA TIENE UNA PLACA REGISTRADA. ¿DESEA CONCLUIR SU VIAJE?")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updatePlacaStatus();
                    }
                })
                .setNegativeButton("NO, CONTINUAR CON EL VIAJE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    public boolean checarStatusPermiso(int resultado){
        if(resultado == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_SOLICITUD_PERMISO :
                int resultado = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

                if(checarStatusPermiso(resultado)) {
                    if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
                        alertaGPS();
                    }
                } else {
                    Toast.makeText(activity, "No estan activos los permisos", Toast.LENGTH_SHORT).show();
                }
        }
    }
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("SI SALE DE ESTÁ PANTALLA, DA POR TERMINADA LA ALERTA")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isMyServiceRunning( mSensorService.getClass())) {
                            //updateServicio();
                            updatePlacaStatus();
                            limpiarPlaca();
                            stopService( mServiceIntent );
                            stopService( new Intent( TransporteSeguro.this, Service911TS.class ) );
                            onDestroy();
                            System.exit( 0 );
                            Log.i("HEY", "CON SERVICIO INICIADO");
                        }else{
                            //updateServicio();
                            updatePlacaStatus();
                            limpiarPlaca();
                            //Intent intent = new Intent( TransporteSeguro.this, MenuEventos.class );
                            //startActivity( intent );
                            //onBackPressed();
                            Log.i("HEY", "SIN SERVICIO");
                            finish();

                        }
                    }
                })
                .setNegativeButton("NO, CONTINUAR CON LA ALERTA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    private void cargarServicio(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoServicio = share.getString("servicio","sincrear");
        cargarInfoWtransporteSeguro = share.getInt("TRANSPORTE", 0);
        cargarInfoTelefono = share.getString("TELEFONO", "SIN INFORMACION");
        //Toast.makeText(getApplicationContext(),cargarInfoServicio,Toast.LENGTH_LONG).show();
    }
    private void guardarActividad() {
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("TRANSPORTE", wTransporteSeguro );
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }
    private void cargarPlaca() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoPlaca = share.getString("PLACA", "SIN INFORMACION");
        //Toast.makeText(getApplicationContext(),cargarInfoPlaca,Toast.LENGTH_LONG).show();
    }
    private void limpiarPlaca(){
        share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        editor = share.edit();
        editor.remove("PLACA").commit();
        editor.remove("servicio").commit();
    }
}
