package mx.gob.sspo.movimientovecinal.ui.slideshow;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.biubiubiu.justifytext.library.JustifyTextView;
import mx.gob.sspo.movimientovecinal.AltoALaViolencia;
import mx.gob.sspo.movimientovecinal.MensajeSalidaAltoViolencia;
import mx.gob.sspo.movimientovecinal.MiWidget;
import mx.gob.sspo.movimientovecinal.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    Button btnCrear;
    TextView lblTitulo,lblCargando;
    ImageView imgCM;
    JustifyTextView lblCuerpo;
    SharedPreferences share;
    String cargarInfoTelefono,respuestaJson,m_Item1;
    int cargarInfoWViolencia;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        /*************************************************************/
        //*****************************************************************//
        cargarServicio();
        lblTitulo = root.findViewById(R.id.lblPrincipal);
        imgCM = root.findViewById(R.id.imgCM);
        lblCuerpo = root.findViewById(R.id.lblSegundo);
        lblCargando = root.findViewById(R.id.lblTerceroCodigoMujer);
        btnCrear = root.findViewById(R.id.boton_crear_widget);

        lblTitulo.setVisibility(View.GONE);
        imgCM.setVisibility(View.GONE);
        lblCuerpo.setVisibility(View.GONE);
        btnCrear.setVisibility(View.GONE);
        getUserViolencia();

        if(cargarInfoWViolencia == 1){
            btnCrear.setText("WIDGET CREADO");
            btnCrear.setEnabled(false);
        }

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                cargarServicio();
                if(cargarInfoWViolencia == 1){
                    btnCrear.setText("WIDGET CREADO");
                    btnCrear.setEnabled(false);
                    Toast.makeText(getContext(), "LO SENTIMOS, USTED YA CUENTA CON UN ACCESO DIRECTO EN EL MENÚ DE SU DISPOSITIVO", Toast.LENGTH_LONG).show();
                }else{
                    AppWidgetManager mAppWidgetManager = v.getContext().getSystemService(AppWidgetManager.class);
                    ComponentName myProvider = new ComponentName(v.getContext(), MiWidget.class);
                    if(mAppWidgetManager.isRequestPinAppWidgetSupported()){
                        mAppWidgetManager.requestPinAppWidget(myProvider,null,null);
                    }
                }
            }
        });

        return root;
    }
    /******************GET A LA BD***********************************/
    public void getUserViolencia() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/UsuarioRegistrado?telefono="+cargarInfoTelefono)
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
                    SlideshowFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jObj = null;
                                String resObj = myResponse;
                                System.out.println(resObj);
                                jObj = new JSONObject("" + resObj + "");
                                respuestaJson = jObj.getString("m_Item1");
                                m_Item1 = "SIN INFORMACION";
                                if (respuestaJson.equals(m_Item1)) {
                                    Intent i = new Intent(getContext(), MensajeSalidaAltoViolencia.class);
                                    startActivity(i);
                                    getActivity().finish();
                                } else {
                                    lblTitulo.setVisibility(View.VISIBLE);
                                    imgCM.setVisibility(View.VISIBLE);
                                    lblCuerpo.setVisibility(View.VISIBLE);
                                    btnCrear.setVisibility(View.VISIBLE);
                                    lblCargando.setVisibility(View.GONE);
                                    Log.i("HERE", "" + jObj);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
    private void cargarServicio(){
        share = getActivity().getSharedPreferences("main",getContext().MODE_PRIVATE);
        cargarInfoWViolencia = share.getInt("VIOLENCIA", 0);
        cargarInfoTelefono = share.getString("TELEFONO", "SIN INFORMACION");
    }
}