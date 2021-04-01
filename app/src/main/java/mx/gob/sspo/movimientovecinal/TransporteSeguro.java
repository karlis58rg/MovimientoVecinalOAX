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
import android.app.Dialog;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    String cargarInfoServicio,cargarInfoTelefono,cargarInfoPlaca,cargarInfoNuc;
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
    FloatingActionButton btnStart;
    String cargarInfoPlacaGuardada = "SIN INFORMACION",cargarInfoNucGuardada = "SIN INFORMACION";
    Dialog myDialog;
    TextView txtCerrarNotificacion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_seguro);
        myDialog = new Dialog(this);
        cargarPlacaNucWidget();

        home = findViewById(R.id.imgHomeTransporte);
        /***************FASE 1********************/
        lyTransporte = findViewById(R.id.lyTransporte);
        lyIntroduce = findViewById(R.id.lyIntroduce);
        lyPlaca = findViewById(R.id.lyPlaca);
        txtPlaca = findViewById(R.id.txtPlaca);
        lyEnviarPlaca = findViewById(R.id.lyEnviarPlaca);
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

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPlaca.length() > 10){
                    nuc = txtPlaca.getText().toString();
                    guardar();
                    getNuc();
                }else if(txtPlaca.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"EL CAMPO **PLACA** ES OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else{
                    placa = txtPlaca.getText().toString();
                    lblNoPlaca.setText(placa);
                    guardar();
                    getPlaca();
                }
            }
        });

        btnDetenerServicioMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp(view);
            }
        });

        imgPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPlaca.setEnabled(true);
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
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "LA OPCIÓN FUE CANCELADA", Toast.LENGTH_LONG).show();
            lyQr1.setVisibility(View.INVISIBLE);
            lyQr2.setVisibility(View.INVISIBLE);
            lyQr3.setVisibility(View.INVISIBLE);
        }

    }

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
                                        guardar();
                                        guardarBandera();
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
                                        guardar();
                                        guardarBandera();
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
                                        guardar();
                                        guardarBandera();
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
                                        guardar();
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

    //**********************************************************************//
    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("PLACA",placa);
        editor.putString("NUC",nuc);
        editor.apply();
    }
    private void guardarBandera(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("BANDERAPLACANUC",banderaPlacaNuc);
        editor.apply();
    }
    private void cargarPlacaNucWidget() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoWtransporteSeguro = share.getInt("TRANSPORTE", 0);
        cargarInfoPlaca = share.getString("PLACA", "SIN INFORMACION");
        cargarInfoNuc = share.getString("NUC", "SIN INFORMACION");
        cargarInfoBanderaPlacaNuc = share.getInt("BANDERAPLACANUC", 0);
    }
    private void eliminarDatosGuardados(){
        share = getSharedPreferences("main", Context.MODE_PRIVATE);
        editor = share.edit();
        editor.remove("PLACA").commit();
        editor.remove("NUC").commit();
        editor.remove("BANDERAPLACANUC").commit();
    }

    public void showPopUp(View v){
        myDialog.setContentView(R.layout.costumpopup);
        txtCerrarNotificacion = myDialog.findViewById(R.id.lblCerrarMensaje);
        txtCerrarNotificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }
}
