package mx.gob.sspo.movimientovecinal;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import mx.gob.sspo.movimientovecinal.ServiceShake.LocationService;
import mx.gob.sspo.movimientovecinal.Transporte.TransporteExistente;
import mx.gob.sspo.movimientovecinal.Transporte.TransporteNoExiste;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransporteSeguro extends AppCompatActivity  {
    SharedPreferences share;
    SharedPreferences.Editor editor;
    ImageView imgBorrarNuc,imgBorrarPlaca,imgQR;
    EditText txtPlaca,txtNuc;
    Button btnEnviarInformacion;
    String resultadoQr,placa,forma,nuc,serie,respuestaJson,mensaje1,mensaje2,datoVehiculo,cargarDato,datoEncontrado = "ENCONTRADO",datoNoEncontrado="NOENCONTRADO",cargarServicio,servar = "sincrear";
    String Tag = "QR";
    int countResultado;
    String mensajeObjJson;
    String informacionJson,nucJson,sitioJson;
    String marcaJson,tipoJson;
    String placaJson;
    int home;
    int cargarWidget = 0;
    AlertDialog alert = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_seguro);
        cargarDatosTransporte();

        txtPlaca = findViewById(R.id.txtPlaca);
        txtNuc = findViewById(R.id.txtNuc);
        imgBorrarPlaca = findViewById(R.id.imgBorrarPlaca);
        imgBorrarNuc = findViewById(R.id.imgBorrarNuc);
        imgQR = findViewById(R.id.imgQR);
        btnEnviarInformacion = findViewById(R.id.btnEnviarInformacion);

        Intent iHome = getIntent();
        home = iHome.getIntExtra("home",0);

        if(cargarWidget == 1 && cargarServicio.equals(servar)){
            if(cargarDato.equals(datoEncontrado)){
                startLocationService();
            }else if(cargarDato.equals(datoNoEncontrado)){
                startLocationService();
            }
        }

        if(cargarDato.equals(datoEncontrado)){
            Intent i = new Intent(TransporteSeguro.this,TransporteSeguroRespuesta.class);
            startActivity(i);
            finish();
        }
       if(cargarDato.equals(datoNoEncontrado)){
            Intent i = new Intent(TransporteSeguro.this,TransporteSeguroRespuesta.class);
            startActivity(i);
            finish();
        }

        if(cargarWidget == 1){
        }else {
            txtPlaca.setEnabled(false);
            txtNuc.setEnabled(false);
            imgQR.setEnabled(false);
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
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            finish();
                        }
                    });
            alert = builder.create();
            alert.show();
        }

        imgQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNuc.setEnabled(false);
                txtPlaca.setEnabled(false);
                new IntentIntegrator(TransporteSeguro.this).initiateScan();
            }
        });

        txtPlaca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(txtPlaca.getText().length() == 0){
                    txtNuc.setEnabled(true);
                    imgQR.setEnabled(true);

                }else{
                    txtNuc.setEnabled(false);
                    imgQR.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtNuc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(txtNuc.getText().length() == 0){
                    txtPlaca.setEnabled(true);
                    imgQR.setEnabled(true);
                }else{
                    txtPlaca.setEnabled(false);
                    imgQR.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        btnEnviarInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtPlaca.getText().toString().isEmpty()&&txtNuc.getText().toString().isEmpty()){
                    Toast.makeText(TransporteSeguro.this, "INGRESA LA PLACA O NUC O QR DE LA UNIDAD", Toast.LENGTH_LONG).show();
                }else if(txtPlaca.isEnabled()){
                    if(txtPlaca.getText().toString().length() < 6){
                        Toast.makeText(TransporteSeguro.this, "MINIMO 7 CARACTERES", Toast.LENGTH_LONG).show();
                    }else{
                        getPlaca();
                    }
                }else if(txtNuc.isEnabled()) {
                    if (txtNuc.getText().toString().length() < 17) {
                        Toast.makeText(TransporteSeguro.this, "MINIMO 17 CARACTERES\n 00-000/AA-BBB-000", Toast.LENGTH_LONG).show();
                    } else {
                        getNuc();
                    }
                }
            }
        });

        imgBorrarPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPlaca.setText("");
                txtNuc.setEnabled(true);
                imgQR.setEnabled(true);
            }
        });
        imgBorrarNuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNuc.setText("");
                txtPlaca.setEnabled(true);
                imgQR.setEnabled(true);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                    getNuc();
                }else{
                    Toast.makeText(TransporteSeguro.this, "LO SENTIMOS, EL QR NO PERTENECE A SEMOVI OAXACA", Toast.LENGTH_LONG).show();
                    txtPlaca.setEnabled(true);
                    txtNuc.setEnabled(true);
                }
            }
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(TransporteSeguro.this, "LA OPCIÓN FUE CANCELADA", Toast.LENGTH_LONG).show();
            txtPlaca.setEnabled(true);
            imgQR.setEnabled(true);
        }
    }
    /*******************************************************************************************************/
    public void getPlaca() {
        Toast.makeText(TransporteSeguro.this,"UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS",Toast.LENGTH_SHORT).show();
        placa = txtPlaca.getText().toString();
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
                                    Toast.makeText(TransporteSeguro.this,"NO SE CUENTA CON INFORMACIÓN",Toast.LENGTH_SHORT).show();
                                }else{
                                    JSONObject jObj = null;
                                    String resObj = myResponse;
                                    resObj = resObj.replace("["," ");
                                    resObj = resObj.replace("]"," ");
                                    jObj = new JSONObject(""+resObj+"");
                                    mensaje1 = jObj.getString("mensaje");
                                    mensaje2 = "ENCONTRADO";
                                    if(mensaje1.equals(mensaje2)){
                                        datoVehiculo = mensaje2;
                                        guardar();
                                        Log.i("JSON", String.valueOf(jObj));

                                        JSONObject obj = new JSONObject(myResponse);
                                        mensajeObjJson = obj.getString("mensaje");
                                        informacionJson = obj.getString("informacion");
                                        JSONObject informacion = new JSONObject(informacionJson);
                                        nucJson = informacion.getString("nuc");
                                        sitioJson = informacion.getString("sitio");
                                        JSONArray vehiculos_activos = informacion.getJSONArray("vehiculos_activos");
                                        for(int i = 0; i < vehiculos_activos .length(); i++)
                                        {
                                            JSONObject object = vehiculos_activos.getJSONObject(i);
                                            marcaJson = object.getString("marca");
                                            tipoJson = object.getString("tipo");
                                        }
                                        JSONArray conductores = informacion.getJSONArray("conductores");
                                        for(int i = 0; i < conductores .length(); i++)
                                        {
                                            JSONObject object = conductores.getJSONObject(i);
                                            placaJson = object.getString("placa");
                                        }
                                        guardarDatosVehiculo();
                                        Intent i = new Intent(TransporteSeguro.this,TransporteSeguroRespuesta.class);
                                        startActivity(i);
                                        finish();
                                    }else
                                    {
                                        datoVehiculo = "NOENCONTRADO";
                                        guardar();
                                        Intent i = new Intent(TransporteSeguro.this,TransporteSeguroRespuesta.class);
                                        startActivity(i);
                                        finish();
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
    /******************GET A LA BD***********************************/
    public void getNuc() {
        if(imgQR.isEnabled()){
        }else{
            nuc = txtNuc.getText().toString();
        }
        Toast.makeText(TransporteSeguro.this,"UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TransporteSeguro.this,"ERROR AL OBTENER LA INFORMACIÓN, POR FAVOR VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(TransporteSeguro.this,"NO SE CUENTA CON INFORMACIÓN",Toast.LENGTH_SHORT).show();
                                }else{
                                    JSONObject jObj = null;
                                    String resObj = myResponse;
                                    resObj = resObj.replace("["," ");
                                    resObj = resObj.replace("]"," ");
                                    jObj = new JSONObject(""+resObj+"");
                                    mensaje1 = jObj.getString("mensaje");
                                    mensaje2 = "ENCONTRADO";
                                    if(mensaje1.equals(mensaje2)){
                                        datoVehiculo = mensaje2;
                                        guardar();
                                        Log.i("JSON", String.valueOf(jObj));

                                        JSONObject obj = new JSONObject(myResponse);
                                        mensajeObjJson = obj.getString("mensaje");
                                        informacionJson = obj.getString("informacion");
                                        JSONObject informacion = new JSONObject(informacionJson);
                                        nucJson = informacion.getString("nuc");
                                        sitioJson = informacion.getString("sitio");
                                        JSONArray vehiculos_activos = informacion.getJSONArray("vehiculos_activos");
                                        for(int i = 0; i < vehiculos_activos .length(); i++)
                                        {
                                            JSONObject object = vehiculos_activos.getJSONObject(i);
                                            marcaJson = object.getString("marca");
                                            tipoJson = object.getString("tipo");
                                        }
                                        JSONArray conductores = informacion.getJSONArray("conductores");
                                        for(int i = 0; i < conductores .length(); i++)
                                        {
                                            JSONObject object = conductores.getJSONObject(i);
                                            placaJson = object.getString("placa");
                                        }
                                        guardarDatosVehiculo();
                                        Intent i = new Intent(TransporteSeguro.this,TransporteSeguroRespuesta.class);
                                        startActivity(i);
                                        finish();
                                    }else
                                    {
                                        datoVehiculo = "NOENCONTRADO";
                                        guardar();
                                        Intent i = new Intent(TransporteSeguro.this,TransporteSeguroRespuesta.class);
                                        startActivity(i);
                                        finish();
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
        editor.putString("DATO",datoVehiculo);
        editor.apply();
    }

    private void guardarDatosVehiculo(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("placaJson",placaJson);
        editor.putString("nucJson",nucJson);
        editor.putString("sitioJson",sitioJson);
        editor.putString("marcaJson",marcaJson);
        editor.putString("tipoJson",tipoJson);
        editor.apply();
    }

    private void cargarDatosTransporte() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        cargarDato = share.getString("DATO", "SIN INFORMACION");
        cargarWidget = share.getInt("TRANSPORTE",0);
        cargarServicio = share.getString("servicio","sincrear");
        Log.i("SERVICIO",cargarServicio);
    }
    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        this.startService(intent);
    }

}
