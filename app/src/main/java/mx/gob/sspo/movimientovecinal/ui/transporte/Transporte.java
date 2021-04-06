package mx.gob.sspo.movimientovecinal.ui.transporte;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

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

import mx.gob.sspo.movimientovecinal.MiWidget;
import mx.gob.sspo.movimientovecinal.MiWidgetT;
import mx.gob.sspo.movimientovecinal.R;

public class Transporte extends Fragment {

    private TransporteViewModel mViewModel;
    Button btnCrear;
    public static int num_imag_transporte = 0;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int widgetTransporte = 0,cargarInfoWtransporteSeguro;

    public static Transporte newInstance() {
        return new Transporte();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.transporte_fragment, container, false);
        /*************************************************************/
        //*****************************************************************//
        cargarServicio();
        btnCrear = root.findViewById(R.id.boton_crear_widget_transporte);
        if(cargarInfoWtransporteSeguro == 1){
            btnCrear.setText("WIDGET CREADO");
            btnCrear.setEnabled(false);
        }
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                cargarServicio();
                if(cargarInfoWtransporteSeguro == 1){
                    btnCrear.setText("WIDGET CREADO");
                    btnCrear.setEnabled(false);
                    Toast.makeText(getContext(), "LO SENTIMOS, USTED YA CUENTA CON UN ACCESO DIRECTO EN EL MENÚ DE SU DISPOSITIVO", Toast.LENGTH_LONG).show();
                }else{
                    AppWidgetManager mAppWidgetManager = v.getContext().getSystemService(AppWidgetManager.class);
                    ComponentName myProvider = new ComponentName(v.getContext(), MiWidgetT.class);
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
        mViewModel = ViewModelProviders.of(this).get(TransporteViewModel.class);
        // TODO: Use the ViewModel
    }

    private void cargarServicio(){
        share = getActivity().getSharedPreferences("main",getContext().MODE_PRIVATE);
        cargarInfoWtransporteSeguro = share.getInt("TRANSPORTE", 0);
        //Toast.makeText(getApplicationContext(),cargarInfoServicio,Toast.LENGTH_LONG).show();
    }

}
