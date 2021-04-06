package mx.gob.sspo.movimientovecinal.ui.vigilancia_vecinal;

import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.biubiubiu.justifytext.library.JustifyTextView;
import mx.gob.sspo.movimientovecinal.MensajeEnviadoVigilanciaVecinal;
import mx.gob.sspo.movimientovecinal.MensajeSalidaVigilanciaVecinal;
import mx.gob.sspo.movimientovecinal.MenuEventos;
import mx.gob.sspo.movimientovecinal.MiWidgetT;
import mx.gob.sspo.movimientovecinal.MiWidgetVV;
import mx.gob.sspo.movimientovecinal.R;
import mx.gob.sspo.movimientovecinal.VigilanciaVecinal;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Vigilancia extends Fragment {

    private VigilanciaViewModel mViewModel;
    Button btnCrear;
    TextView lblTitulo,lblCargando;
    ImageView imgVV;
    JustifyTextView lblCuerpo;
    SharedPreferences share;
    int cargarInfoWVigilanciaV;
    String cargarInfoTelefono;


    public static Vigilancia newInstance() {
        return new Vigilancia();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.vigilancia_fragment, container, false);
        /*************************************************************/
        //*****************************************************************//
        cargarServicio();
        lblTitulo = root.findViewById(R.id.lblVigilanciaVecinal);
        imgVV = root.findViewById(R.id.imgVV);
        lblCuerpo = root.findViewById(R.id.lblSegundoVigilancia);
        lblCargando = root.findViewById(R.id.lblTerceroVigilancia);
        btnCrear = root.findViewById(R.id.boton_crear_widget_vigilancia);

        lblTitulo.setVisibility(View.GONE);
        imgVV.setVisibility(View.GONE);
        btnCrear.setVisibility(View.GONE);
        lblCuerpo.setVisibility(View.GONE);
        getUserVigilanciaExist();

        if(cargarInfoWVigilanciaV == 1){
            btnCrear.setText("WIDGET CREADO");
            btnCrear.setEnabled(false);
        }

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                cargarServicio();
                if(cargarInfoWVigilanciaV == 1){
                    btnCrear.setText("WIDGET CREADO");
                    btnCrear.setEnabled(false);
                    Toast.makeText(getContext(), "LO SENTIMOS, USTED YA CUENTA CON UN ACCESO DIRECTO EN EL MENÚ DE SU DISPOSITIVO", Toast.LENGTH_LONG).show();
                }else{
                    AppWidgetManager mAppWidgetManager = v.getContext().getSystemService(AppWidgetManager.class);
                    ComponentName myProvider = new ComponentName(v.getContext(), MiWidgetVV.class);
                    if(mAppWidgetManager.isRequestPinAppWidgetSupported()){
                        mAppWidgetManager.requestPinAppWidget(myProvider,null,null);
                    }
                }
            }
        });
        return root;
    }

    private void getUserVigilanciaExist() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/VigilanciaVecinal?telefonoVV="+cargarInfoTelefono)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getContext(),"ERROR AL OBTENER LA INFORMACIÓN, POR FAVOR VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Vigilancia.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final String resp = myResponse;
                            final String valor = "true";
                            if(resp.equals(valor)){
                                lblTitulo.setVisibility(View.VISIBLE);
                                imgVV.setVisibility(View.VISIBLE);
                                btnCrear.setVisibility(View.VISIBLE);
                                lblCuerpo.setVisibility(View.VISIBLE);
                                lblCargando.setVisibility(View.GONE);
                            }else{
                                Intent i = new Intent(getContext(),MensajeSalidaVigilanciaVecinal.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                            Log.i("HERE", resp);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VigilanciaViewModel.class);
        // TODO: Use the ViewModel
    }

    private void cargarServicio(){
        share = getActivity().getSharedPreferences("main",getContext().MODE_PRIVATE);
        cargarInfoWVigilanciaV = share.getInt("ALERTAVECINAL", 0);
        cargarInfoTelefono = share.getString("TELEFONO", "SIN INFORMACION");
    }

}