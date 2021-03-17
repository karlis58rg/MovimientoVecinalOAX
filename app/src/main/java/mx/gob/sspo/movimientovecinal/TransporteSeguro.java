package mx.gob.sspo.movimientovecinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.gob.sspo.movimientovecinal.ServiceShake.LocationService;
import mx.gob.sspo.movimientovecinal.ServiceShake.Service911TS;
import mx.gob.sspo.movimientovecinal.ui.transporte.Transporte;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransporteSeguro extends AppCompatActivity {
    LinearLayout lyTransporte,lyIntroduce,lyPlaca,lyEnviarPlaca,lyPlacaEnviada,lyEncasoDe,lyDetenerServicio,lyDetenerServicioEjecución,lyEmergenciaEnviada,lyQr1,lyQr2,lyQr3,lyPlacaQR ;
    EditText txtPlaca,txtForma,txtNuc,txtSerie;
    TextView lblNoPlaca,coordenadas,txtAlerta,txtVehiculo;
    Button btnIniciar,btnDetenerServicioMS;
    String resultadoQr,placa,forma,nuc,serie;
    ImageView home,imgPlaca,imgQr,imgEnviado;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    Activity activity;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;
    private LocationManager locationManager;
    private Context context;
    int acceso = 0;
    AlertDialog alert = null;
    String cargarInfoServicio,cargarInfoPlaca,cargarInfoTelefono,cargarInfoPlacaGuardada = "SIN INFORMACION";
    String cargarInfoNuc,cargarInfoNucGuardada = "SIN INFORMACION";
    String cargarInfoServicioShake = "creado";
    String mensaje1,mensaje2;
    String direc;
    String respuestaJson;
    Double lat,lon;
    int cargarInfoWtransporteSeguro = 0;
    int wTransporteSeguro = 0;
    int countResultado,banderaPlacaNuc,banderaNucPlaca;
    String Tag = "TransporteSeguro";
    int cargarInfoBanderaPlacaNuc;

    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private Service911TS mSensorService;
    Context ctx;
    AppWidgetManager manager;
    View view;
    /******************************************/
    //ENVIO DE COORDENADAS//
    FloatingActionButton btnStart;
    private static final int LOCATION_REQUEST_CODE = 1;

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
            //getDatosPlaca();
        }
        //Toast.makeText(getApplicationContext(),cargarInfoServicio,Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),cargarInfoPlaca,Toast.LENGTH_LONG).show();
        Log.i("HEY", cargarInfoServicio);
        Log.i("HEY", cargarInfoPlaca);

        home = findViewById(R.id.imgHomeTransporte);

        /***************FASE 1********************/
        lyTransporte = findViewById(R.id.lyTransporte);
        lyIntroduce = findViewById(R.id.lyIntroduce);
        lyPlaca = findViewById(R.id.lyPlaca);
        lyEnviarPlaca = findViewById(R.id.lyEnviarPlaca);
        txtPlaca = findViewById(R.id.txtPlaca);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnDetenerServicioMS = findViewById(R.id.btnDetenerServicioMS);
        coordenadas = (TextView)findViewById(R.id.lblCoordenadasSensorPlaca);

        /*************FASE 2********************/
        lyPlacaQR = findViewById(R.id.lyPlacaQr);
        lyPlacaEnviada = findViewById(R.id.lyPlacaEnviada);
        lyEncasoDe = findViewById(R.id.lyEncasoDe);
        lyDetenerServicio = findViewById(R.id.lyDetenerServicio);
        lyDetenerServicioEjecución = findViewById(R.id.lyDetenerServicioEjecución);
        lblNoPlaca = findViewById(R.id.lblNoPlaca);
        lyEmergenciaEnviada = findViewById(R.id.lyEmergenciaEnviada);

        /*************FASE 3********************/
        lyQr1 = findViewById(R.id.lyQR);
        lyQr2 = findViewById(R.id.lyQR2);
        lyQr3 = findViewById(R.id.lyQR3);
        imgPlaca = findViewById(R.id.imgPlacaMS);
        imgQr = findViewById(R.id.imgQrMS);
        txtForma = findViewById(R.id.txtForma);
        txtNuc = findViewById(R.id.txtNuc);
        txtSerie = findViewById(R.id.txtNoSerie);

        /*************FASE 4********************/
        imgEnviado = findViewById(R.id.imgEnviado);
        txtAlerta = findViewById(R.id.txtAlerta);
        txtVehiculo = findViewById(R.id.txtVehiculo);

        /**************LOCALIZACION *************************/
        btnStart = findViewById(R.id.btn_add);

        lyPlacaEnviada.setVisibility(View.INVISIBLE);
        lyEncasoDe.setVisibility(View.INVISIBLE);
        lyDetenerServicio.setVisibility(View.INVISIBLE);
        lyDetenerServicioEjecución.setVisibility(View.INVISIBLE);
        lblNoPlaca.setVisibility(View.INVISIBLE);
        lyEmergenciaEnviada.setVisibility(View.INVISIBLE);
        txtPlaca.setEnabled(false);
        lyQr1.setVisibility(View.INVISIBLE);
        lyQr2.setVisibility(View.INVISIBLE);
        lyQr3.setVisibility(View.INVISIBLE);
        lyPlaca.setVisibility(View.INVISIBLE);
        lyEnviarPlaca.setVisibility(View.INVISIBLE);

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
                finish();
                System.exit(0);
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
            lyQr1.setVisibility(View.INVISIBLE);
            lyQr2.setVisibility(View.INVISIBLE);
            lyQr3.setVisibility(View.INVISIBLE);
            lyPlacaQR.setVisibility(View.INVISIBLE);
            if(cargarInfoBanderaPlacaNuc == 1){
                lblNoPlaca.setText(cargarInfoPlaca);
                txtAlerta.setText("¡AVISO!");
                txtVehiculo.setText("TU TRANSPORTE ES SEGURO\n CUENTA CON REGISTRO EN SEMOVI OAXACA.");
            }else{
                lblNoPlaca.setText(cargarInfoPlaca);
                imgEnviado.setBackgroundResource(R.drawable.ic_semovi_advertencia);
                txtAlerta.setText("¡PRECAUCIÓN!");
                txtVehiculo.setText("TU TRANSPORTE NO CUENTA CON REGISTRO EN SEMOVI OAXACA.");
            }

            lyPlacaEnviada.setVisibility(View.VISIBLE);
            lyEncasoDe.setVisibility(View.VISIBLE);
            lyDetenerServicio.setVisibility(View.VISIBLE);
            lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
            lblNoPlaca.setVisibility(View.VISIBLE);
        }

        if(cargarInfoNuc.equals(cargarInfoNucGuardada)){
        }else{
            lblNoPlaca.setText(cargarInfoPlaca);
            lyTransporte.setVisibility(View.INVISIBLE);
            lyIntroduce.setVisibility(View.INVISIBLE);
            lyPlaca.setVisibility(View.INVISIBLE);
            lyEnviarPlaca.setVisibility(View.INVISIBLE);
            lyQr1.setVisibility(View.INVISIBLE);
            lyQr2.setVisibility(View.INVISIBLE);
            lyQr3.setVisibility(View.INVISIBLE);
            lyPlacaQR.setVisibility(View.INVISIBLE);
            if(cargarInfoBanderaPlacaNuc == 1){
                lblNoPlaca.setText(cargarInfoNuc);
                txtAlerta.setText("¡AVISO!");
                txtVehiculo.setText("TU TRANSPORTE ES SEGURO\n CUENTA CON REGISTRO EN SEMOVI OAXACA.");
            }else{
                lblNoPlaca.setText(cargarInfoNuc);
                imgEnviado.setBackgroundResource(R.drawable.ic_semovi_advertencia);
                txtAlerta.setText("¡PRECAUCIÓN!");
                txtVehiculo.setText("TU TRANSPORTE NO CUENTA CON REGISTRO EN SEMOVI OAXACA.");
            }

            lyPlacaEnviada.setVisibility(View.VISIBLE);
            lyEncasoDe.setVisibility(View.VISIBLE);
            lyDetenerServicio.setVisibility(View.VISIBLE);
            lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
            lblNoPlaca.setVisibility(View.VISIBLE);
        }

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPlaca.length() > 10){
                    nuc = txtPlaca.getText().toString();
                    getNuc();
                }else if(txtPlaca.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"EL CAMPO **PLACA** ES OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else{
                    placa = txtPlaca.getText().toString();
                    lblNoPlaca.setText(placa);
                    guardar();
                    getPlaca();
                    //insertPlacaTransporte();
                   /* lyTransporte.setVisibility(View.INVISIBLE);
                    lyIntroduce.setVisibility(View.INVISIBLE);
                    lyPlaca.setVisibility(View.INVISIBLE);
                    lyEnviarPlaca.setVisibility(View.INVISIBLE);
                    lyPlacaQR.setVisibility(View.INVISIBLE);

                    lyPlacaEnviada.setVisibility(View.VISIBLE);
                    lyEncasoDe.setVisibility(View.VISIBLE);
                    lyDetenerServicio.setVisibility(View.VISIBLE);
                    lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
                    lblNoPlaca.setVisibility(View.VISIBLE);*/
                }

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationService();
            }
        });

        btnDetenerServicioMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertaDetenerServicio();

            }
        });

        imgPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyPlaca.setVisibility(View.VISIBLE);
                lyEnviarPlaca.setVisibility(View.VISIBLE);
                lyQr1.setVisibility(View.INVISIBLE);
                lyQr2.setVisibility(View.INVISIBLE);
                lyQr3.setVisibility(View.INVISIBLE);
            }
        });
        imgQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(TransporteSeguro.this).initiateScan();
                lyQr1.setVisibility(View.VISIBLE);
                lyQr2.setVisibility(View.VISIBLE);
                lyQr3.setVisibility(View.VISIBLE);
                lyPlaca.setVisibility(View.INVISIBLE);
                lyEnviarPlaca.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null)
            if(result.getContents() != null){
                resultadoQr = result.getContents();
                resultadoQr = resultadoQr.replace('|',',');
                Log.i(Tag, resultadoQr);
                String[] textElements = resultadoQr.split(",");
                List<String> qrlList = Arrays.asList(textElements);
                countResultado = qrlList.size();
                if(countResultado == 3){
                    forma = textElements[0];
                    nuc = textElements[1];
                    serie = textElements[2];
                    Log.i(Tag, forma+nuc+serie);
                    txtForma.setText(forma);
                    txtNuc.setText(nuc);
                    txtSerie.setText(serie);
                    getNuc();
                }else{
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, EL QR NO PERTENECE A SEMOVI OAXACA", Toast.LENGTH_LONG).show();
                }
            }
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
        cargarServicio();
        cargarPlaca();
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
                                limpiarPlaca();
                                Toast.makeText(getApplicationContext(),"VIAJE CONCLUIDO",Toast.LENGTH_LONG).show();
                                int p = android.os.Process.myPid();
                                android.os.Process.killProcess(p);
                                finishAffinity();
                                System.exit( 0 );
                                }else{
                                limpiarPlaca();
                                int p = android.os.Process.myPid();
                                android.os.Process.killProcess(p);
                                finishAffinity();
                                System.exit( 0 );
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

    /********************************************************************************************************************/
    /******************GET A LA BD***********************************/
    public void getNuc() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("nucSemovi", nuc)
                .build();
        Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/SemoviWS")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"ERROR AL OBTENER LA INFORMACIÓN, POR FAVOR VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Log.i("JSOn", myResponse);
                    TransporteSeguro.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                respuestaJson = "null";
                                if(myResponse.equals(respuestaJson)){
                                    Toast.makeText(getApplicationContext(),"NO SE CUENTA CON INFORMACIÓN",Toast.LENGTH_SHORT).show();
                                }else{
                                    JSONObject jObj = null;
                                    String resObj = myResponse;
                                    resObj = resObj.replace("["," ");
                                    resObj = resObj.replace("]"," ");
                                        jObj = new JSONObject(""+resObj+"");
                                        mensaje1 = jObj.getString("mensaje");
                                        mensaje2 = "ENCONTRADO";
                                        if(mensaje1.equals(mensaje2)){
                                            banderaPlacaNuc = 1;
                                            guardarBandera();
                                            guardarNuc();
                                            //Toast.makeText(getApplicationContext(),"TU TRANSPORTE ES SEGURO, CUENTA CON REGISTRO EN SEMOVI OAXACA.",Toast.LENGTH_SHORT).show();
                                            lblNoPlaca.setText(nuc);
                                            txtAlerta.setText("¡AVISO!");
                                            txtVehiculo.setText("TU TRANSPORTE ES SEGURO\n CUENTA CON REGISTRO EN SEMOVI OAXACA.");
                                            lyTransporte.setVisibility(View.INVISIBLE);
                                            lyIntroduce.setVisibility(View.INVISIBLE);
                                            lyPlaca.setVisibility(View.INVISIBLE);
                                            lyEnviarPlaca.setVisibility(View.INVISIBLE);
                                            lyQr1.setVisibility(View.INVISIBLE);
                                            lyQr2.setVisibility(View.INVISIBLE);
                                            lyQr3.setVisibility(View.INVISIBLE);
                                            lyPlacaQR.setVisibility(View.INVISIBLE);

                                            lyPlacaEnviada.setVisibility(View.VISIBLE);
                                            lyEncasoDe.setVisibility(View.VISIBLE);
                                            lyDetenerServicio.setVisibility(View.VISIBLE);
                                            lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
                                            lblNoPlaca.setVisibility(View.VISIBLE);
                                        }else
                                        {
                                            banderaPlacaNuc = 2;
                                            guardarBandera();
                                            guardarNuc();
                                            lblNoPlaca.setText(nuc);
                                            imgEnviado.setBackgroundResource(R.drawable.ic_semovi_advertencia);
                                            txtAlerta.setText("¡PRECAUCIÓN!");
                                            txtVehiculo.setText("TU TRANSPORTE NO CUENTA CON REGISTRO EN SEMOVI OAXACA.");
                                            lyTransporte.setVisibility(View.INVISIBLE);
                                            lyIntroduce.setVisibility(View.INVISIBLE);
                                            lyPlaca.setVisibility(View.INVISIBLE);
                                            lyEnviarPlaca.setVisibility(View.INVISIBLE);
                                            lyQr1.setVisibility(View.INVISIBLE);
                                            lyQr2.setVisibility(View.INVISIBLE);
                                            lyQr3.setVisibility(View.INVISIBLE);
                                            lyPlacaQR.setVisibility(View.INVISIBLE);

                                            lyPlacaEnviada.setVisibility(View.VISIBLE);
                                            lyEncasoDe.setVisibility(View.VISIBLE);
                                            lyDetenerServicio.setVisibility(View.VISIBLE);
                                            lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
                                            lblNoPlaca.setVisibility(View.VISIBLE);
                                            //Toast.makeText(getApplicationContext(),"¡PRECAUCIÓN! TU TRANSPORTE NO CUENTA CON REGISTRO EN SEMOVI OAXACA.",Toast.LENGTH_SHORT).show();
                                        }

                                    Log.i("HERE", ""+jObj);
                                }

                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

        });
    }

    public void getPlaca() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("placaSemovi", placa)
                .build();
        Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/SemoviPlacaWS")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"ERROR AL OBTENER LA INFORMACIÓN, POR FAVOR VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Log.i("JSOn", myResponse);
                    TransporteSeguro.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                respuestaJson = "null";
                                if(myResponse.equals(respuestaJson)){
                                    Toast.makeText(getApplicationContext(),"NO SE CUENTA CON INFORMACIÓN",Toast.LENGTH_SHORT).show();
                                }else{
                                    JSONObject jObj = null;
                                    String resObj = myResponse;
                                    resObj = resObj.replace("["," ");
                                    resObj = resObj.replace("]"," ");
                                    jObj = new JSONObject(""+resObj+"");
                                    mensaje1 = jObj.getString("mensaje");
                                    mensaje2 = "ENCONTRADO";
                                    if(mensaje1.equals(mensaje2)){
                                        banderaPlacaNuc = 1;
                                        guardarBandera();
                                        //Toast.makeText(getApplicationContext(),"TU TRANSPORTE ES SEGURO, CUENTA CON REGISTRO EN SEMOVI OAXACA.",Toast.LENGTH_SHORT).show();
                                        lblNoPlaca.setText(placa);
                                        txtAlerta.setText("¡AVISO!");
                                        txtVehiculo.setText("TU TRANSPORTE ES SEGURO\n CUENTA CON REGISTRO EN SEMOVI OAXACA.");
                                        lyTransporte.setVisibility(View.INVISIBLE);
                                        lyIntroduce.setVisibility(View.INVISIBLE);
                                        lyPlaca.setVisibility(View.INVISIBLE);
                                        lyEnviarPlaca.setVisibility(View.INVISIBLE);
                                        lyQr1.setVisibility(View.INVISIBLE);
                                        lyQr2.setVisibility(View.INVISIBLE);
                                        lyQr3.setVisibility(View.INVISIBLE);
                                        lyPlacaQR.setVisibility(View.INVISIBLE);

                                        lyPlacaEnviada.setVisibility(View.VISIBLE);
                                        lyEncasoDe.setVisibility(View.VISIBLE);
                                        lyDetenerServicio.setVisibility(View.VISIBLE);
                                        lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
                                        lblNoPlaca.setVisibility(View.VISIBLE);
                                    }else
                                    {
                                        banderaPlacaNuc = 2;
                                        guardarBandera();
                                        lblNoPlaca.setText(placa);
                                        imgEnviado.setBackgroundResource(R.drawable.ic_semovi_advertencia);
                                        txtAlerta.setText("¡PRECAUCIÓN!");
                                        txtVehiculo.setText("TU TRANSPORTE NO CUENTA CON REGISTRO EN SEMOVI OAXACA.");
                                        lyTransporte.setVisibility(View.INVISIBLE);
                                        lyIntroduce.setVisibility(View.INVISIBLE);
                                        lyPlaca.setVisibility(View.INVISIBLE);
                                        lyEnviarPlaca.setVisibility(View.INVISIBLE);
                                        lyQr1.setVisibility(View.INVISIBLE);
                                        lyQr2.setVisibility(View.INVISIBLE);
                                        lyQr3.setVisibility(View.INVISIBLE);
                                        lyPlacaQR.setVisibility(View.INVISIBLE);

                                        lyPlacaEnviada.setVisibility(View.VISIBLE);
                                        lyEncasoDe.setVisibility(View.VISIBLE);
                                        lyDetenerServicio.setVisibility(View.VISIBLE);
                                        lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
                                        lblNoPlaca.setVisibility(View.VISIBLE);
                                        //Toast.makeText(getApplicationContext(),"¡PRECAUCIÓN! TU TRANSPORTE NO CUENTA CON REGISTRO EN SEMOVI OAXACA.",Toast.LENGTH_SHORT).show();
                                    }

                                    Log.i("HERE", ""+jObj);
                                }

                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

        });
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
                            /******QUITAR ESTA PARTE, YA SE LIMPIA Y CIERRA EN EL UpdatePlacaStatus************/

                            limpiarPlaca();
                            Toast.makeText(getApplicationContext(),"VIAJE CONCLUIDO",Toast.LENGTH_LONG).show();
                            int p = android.os.Process.myPid();
                            android.os.Process.killProcess(p);
                            finishAffinity();
                            System.exit( 0 );

                            /**********DESCOMENTAR ESTA PARTE DE ABAJO Y QUITAR LA DE ARRIBA QUE NO ESTA COMENTADA ****************/

                            //updatePlacaStatus();
                            //stopService( mServiceIntent );
                            //stopService( new Intent( TransporteSeguro.this, Service911TS.class ) );
                            //onDestroy();
                            Log.i("HEY", "CON SERVICIO INICIADO");
                        }else{
                            /******QUITAR ESTA PARTE, YA SE LIMPIA Y CIERRA EN EL UpdatePlacaStatus************/

                            limpiarPlaca();
                            Toast.makeText(getApplicationContext(),"VIAJE CONCLUIDO",Toast.LENGTH_LONG).show();
                            int p = android.os.Process.myPid();
                            android.os.Process.killProcess(p);
                            finishAffinity();
                            System.exit( 0 );

                            /**********DESCOMENTAR ESTA PARTE DE ABAJO Y QUITAR LA DE ARRIBA QUE NO ESTA COMENTADA ****************/
                            //updatePlacaStatus();
                            Log.i("HEY", "SIN SERVICIO");
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
                        //updatePlacaStatus();
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
        if (requestCode == LOCATION_REQUEST_CODE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //startLocationService();
        }



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
                            //stopService( mServiceIntent );
                            //stopService( new Intent( TransporteSeguro.this, Service911TS.class ) );
                            //onDestroy();
                            /******QUITAR ESTA PARTE, YA SE LIMPIA Y CIERRA EN EL UpdatePlacaStatus************/

                            limpiarPlaca();
                            Toast.makeText(getApplicationContext(),"VIAJE CONCLUIDO",Toast.LENGTH_LONG).show();
                            int p = android.os.Process.myPid();
                            android.os.Process.killProcess(p);
                            finishAffinity();
                            System.exit( 0 );

                            /**********DESCOMENTAR ESTA PARTE DE ABAJO Y QUITAR LA DE ARRIBA QUE NO ESTA COMENTADA ****************/
                            Log.i("HEY", "CON SERVICIO INICIADO");
                        }else{
                            /******QUITAR ESTA PARTE, YA SE LIMPIA Y CIERRA EN EL UpdatePlacaStatus************/

                            limpiarPlaca();
                            Toast.makeText(getApplicationContext(),"VIAJE CONCLUIDO",Toast.LENGTH_LONG).show();
                            int p = android.os.Process.myPid();
                            android.os.Process.killProcess(p);
                            finishAffinity();
                            System.exit( 0 );

                            /**********DESCOMENTAR ESTA PARTE DE ABAJO Y QUITAR LA DE ARRIBA QUE NO ESTA COMENTADA ****************/
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
    private void cargarPlaca() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoPlaca = share.getString("PLACA", "SIN INFORMACION");
        cargarInfoNuc = share.getString("NUC", "SIN INFORMACION");
        cargarInfoBanderaPlacaNuc = share.getInt("BANDERAPLACANUC", 0);
        //Toast.makeText(getApplicationContext(),cargarInfoPlaca,Toast.LENGTH_LONG).show();
    }
    private void guardarActividad() {
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("TRANSPORTE", wTransporteSeguro );
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }
    //**********************************************************************//
    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("PLACA",placa);
        editor.apply();
    }
    private void guardarNuc(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("NUC",nuc);
        editor.apply();
    }
    //**********************************************************************//
    private void guardarBandera(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("BANDERAPLACANUC",banderaPlacaNuc);
        editor.apply();
    }
    private void limpiarPlaca(){
        share = getSharedPreferences("main", Context.MODE_PRIVATE);
        editor = share.edit();
        editor.remove("PLACA").commit();
        editor.remove("NUC").commit();
        editor.remove("BANDERAPLACANUC").commit();
        editor.remove("servicio").commit();
    }
    //*************************************************************************************************//


    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        this.startService(intent);
    }
}
