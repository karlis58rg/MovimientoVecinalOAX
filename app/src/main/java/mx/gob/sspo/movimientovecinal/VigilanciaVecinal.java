package mx.gob.sspo.movimientovecinal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import mx.gob.sspo.movimientovecinal.ui.vigilancia_vecinal.Vigilancia;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class VigilanciaVecinal extends AppCompatActivity {

    ImageView home;
    ImageView btnVigilancia;
    String cargarInfoTelefono,respuestaJson,m_Item1,fecha,hora;
    int cargarInfoUserRegistradoVigilancia,guardarInfoUserRegistradoVigilancia;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int numberRandom;
    String randomCodigoVerifi,codigoVerifi;
    private static final String TAG = "VigilanciaVecinal";
    private static final int NOTIFICATION_ID = 101;
    private int cargarInfoWalertaVecinal;
    int wAlertaVecinal = 0;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vigilancia_vecinal);
        cargarUserRegistrado();
        Random();
        if(cargarInfoUserRegistradoVigilancia != 1){
            getUserExistVV();
        }
        if(cargarInfoWalertaVecinal == 1){
            Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_SHORT).show();
            getUserVigilancia();
        }else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("LO SENTIMOS, ES NECESARIO TENER UN ACCESO DIRECTO CONFIGURADO")
                    .setCancelable(false)
                    .setPositiveButton("CONFIGURAR ACCESO DIRECTO", new DialogInterface.OnClickListener() {
                        @SuppressLint("NewApi")
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AppWidgetManager mAppWidgetManager = getSystemService(AppWidgetManager.class);
                            ComponentName myProvider = new ComponentName(getApplication(), MiWidgetVV.class);
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

        home = findViewById(R.id.imgHomeVigilancia);
        btnVigilancia = findViewById(R.id.imgAlertaVecinal);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnVigilancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_SHORT).show();
                getUserVigilancia();
            }
        });
    }

    /******************GET A LA BD***********************************/
    public void getUserExistVV() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/VigilanciaVecinal?telefonoVV="+cargarInfoTelefono)
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
                    VigilanciaVecinal.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final String resp = myResponse;
                            final String valor = "true";
                            if(resp.equals(valor)){
                                guardarInfoUserRegistradoVigilancia = 1;
                                guardarUsuarioVV();
                            }else{
                                Intent i = new Intent(VigilanciaVecinal.this,MensajeSalidaVigilanciaVecinal.class);
                                startActivity(i);
                                finish();
                            }
                            Log.i("HERE", resp);
                        }
                    });
                }
            }
        });
    }

    /******************GET A LA BD***********************************/
    public void getUserVigilancia() {
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/VigilanciaVecinal?folioVigilancia="+randomCodigoVerifi+"&telefonoVigilancia="+cargarInfoTelefono+"&fechaVigilancia="+fecha+"&horaVigilancia="+hora)
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
                    VigilanciaVecinal.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(VigilanciaVecinal.this,MensajeEnviadoVigilanciaVecinal.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void guardarUsuarioVV() {
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("BANDERAUSERVIGILANCIAVECINAL", guardarInfoUserRegistradoVigilancia );
        editor.commit();
    }

    public void cargarUserRegistrado() {
        share = getApplication().getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoTelefono = share.getString("TELEFONO", "SIN INFORMACION");
        cargarInfoWalertaVecinal = share.getInt("ALERTAVECINAL", 0);
        cargarInfoUserRegistradoVigilancia = share.getInt("BANDERAUSERVIGILANCIAVECINAL", 0);
    }


    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    public void Random(){
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = "OAX2021"+codigoVerifi;
    }
}
