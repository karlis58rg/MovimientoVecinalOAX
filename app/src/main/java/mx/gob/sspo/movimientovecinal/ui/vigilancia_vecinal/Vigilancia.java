package mx.gob.sspo.movimientovecinal.ui.vigilancia_vecinal;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import mx.gob.sspo.movimientovecinal.MiWidgetT;
import mx.gob.sspo.movimientovecinal.MiWidgetVV;
import mx.gob.sspo.movimientovecinal.R;

public class Vigilancia extends Fragment {

    private VigilanciaViewModel mViewModel;
    Button btnCrear;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int widgetVigilanciaV = 0,cargarInfoWVigilanciaV;

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
        btnCrear = root.findViewById(R.id.boton_crear_widget_vigilancia);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(cargarInfoWVigilanciaV == 1){
                    Toast.makeText(getContext(), "LO SENTIMOS, USTED YA CUENTA CON UN ACCESO DIRECTO EN EL MENÚ DE SU DISPOSITIVO", Toast.LENGTH_LONG).show();
                }else{
                    widgetVigilanciaV= 1;
                    guardarActividad();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VigilanciaViewModel.class);
        // TODO: Use the ViewModel
    }

    private void guardarActividad() {
        share = getActivity().getSharedPreferences("main",getContext().MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("ALERTAVECINAL", widgetVigilanciaV );
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    private void cargarServicio(){
        share = getActivity().getSharedPreferences("main",getContext().MODE_PRIVATE);
        cargarInfoWVigilanciaV = share.getInt("ALERTAVECINAL", 0);
        //Toast.makeText(getApplicationContext(),cargarInfoServicio,Toast.LENGTH_LONG).show();
    }

}