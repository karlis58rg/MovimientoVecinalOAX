package mx.gob.sspo.movimientovecinal.ui.slideshow;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import mx.gob.sspo.movimientovecinal.MiWidget;
import mx.gob.sspo.movimientovecinal.R;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    Button btnCrear;
    public static int num_imag_violencia = 0;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int widgetViolencia = 0,cargarInfoWViolencia;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        /*************************************************************/
        //*****************************************************************//
        cargarServicio();
        btnCrear = root.findViewById(R.id.boton_crear_widget);

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(cargarInfoWViolencia == 1){
                    Toast.makeText(getContext(), "LO SENTIMOS, USTED YA CUENTA CON UN ACCESO DIRECTO EN EL MENÚ DE SU DISPOSITIVO", Toast.LENGTH_LONG).show();
                }else{
                    widgetViolencia = 1;
                    guardarActividad();
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

    private void guardarActividad() {
        share = getActivity().getSharedPreferences("main",getContext().MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("VIOLENCIA", widgetViolencia );
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }
    private void cargarServicio(){
        share = getActivity().getSharedPreferences("main",getContext().MODE_PRIVATE);
        cargarInfoWViolencia = share.getInt("VIOLENCIA", 0);
        //Toast.makeText(getApplicationContext(),cargarInfoServicio,Toast.LENGTH_LONG).show();
    }
}