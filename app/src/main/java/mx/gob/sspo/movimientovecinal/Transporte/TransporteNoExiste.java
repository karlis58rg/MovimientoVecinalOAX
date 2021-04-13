package mx.gob.sspo.movimientovecinal.Transporte;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import mx.gob.sspo.movimientovecinal.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransporteNoExiste#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransporteNoExiste extends Fragment {
    public static TextView lblTituloNoExiste;
    TextView txtPlacaNoExitoso,txtNucNoExitoso;
    Button btnFinalizarViaje;
    String cargarPlaca,cargarNuc,sinInformacion = "SIN INFORMACION",cargarServicio,serbar="creado";
    SharedPreferences share;
    SharedPreferences.Editor editor;
    LinearLayout lyPlacaExitoso,lyNucNoExitoso;
    Dialog myDialog;
    TextView txtCerrarNotificacion;

    public TransporteNoExiste() {
    }

    public static TransporteNoExiste newInstance(String param1, String param2) {
        TransporteNoExiste fragment = new TransporteNoExiste();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_transporte_no_existe, container, false);
        /***********************************************************************************/
        cargarDatosTransporte();
        myDialog = new Dialog(getContext());
        txtPlacaNoExitoso = root.findViewById(R.id.txtPlacaNoExitoso);
        txtNucNoExitoso = root.findViewById(R.id.txtNucNoExitoso);
        btnFinalizarViaje = root.findViewById(R.id.btnFinalizarViajeNoExit);
        lyPlacaExitoso = root.findViewById(R.id.lyPlacaExitoso);
        lyNucNoExitoso = root.findViewById(R.id.lyNucNoExitoso);
        lblTituloNoExiste = root.findViewById(R.id.lblTitulo);

        txtPlacaNoExitoso.setText(cargarPlaca);
        txtNucNoExitoso.setText(cargarNuc);

        if(cargarPlaca.equals(sinInformacion)){
            lyPlacaExitoso.setVisibility(View.GONE);
        }
        if(cargarNuc.equals(sinInformacion)){
            lyNucNoExitoso.setVisibility(View.GONE);
        }

        btnFinalizarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cargarServicio.equals(serbar)){
                    eliminarDatosTransporte();
                    showPopUp(view);
                }else{
                    eliminarDatosTransporte();
                    getActivity().onBackPressed();
                }
            }
        });
        /************************************************************************************/
        return  root;
    }
    private void cargarDatosTransporte() {
        share = getActivity().getSharedPreferences("main", getActivity().MODE_PRIVATE);
        cargarPlaca = share.getString("PLACA", "SIN INFORMACION");
        cargarNuc = share.getString("NUC", "SIN INFORMACION");
        cargarServicio = share.getString("servicio","sincrear");
    }

    private void eliminarDatosTransporte() {
        share = getActivity().getSharedPreferences("main", getActivity().MODE_PRIVATE);
        editor = share.edit();
        editor.remove("PLACA").commit();
        editor.remove("NUC").commit();
        editor.remove("servicio").commit();
        editor.remove("DATO").commit();
    }
    public void showPopUp(View v){
        myDialog.setContentView(R.layout.costumpopup);
        txtCerrarNotificacion = myDialog.findViewById(R.id.lblCerrarMensaje);
        txtCerrarNotificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                getActivity().onBackPressed();
            }
        });
        myDialog.show();
    }
}