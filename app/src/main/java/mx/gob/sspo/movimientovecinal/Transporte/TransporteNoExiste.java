package mx.gob.sspo.movimientovecinal.Transporte;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import mx.gob.sspo.movimientovecinal.R;
import mx.gob.sspo.movimientovecinal.ServiceShake.LocationService;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransporteNoExiste#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransporteNoExiste extends Fragment {
    public static TextView lblTituloListos;
    TextView txtPlacaNoExitoso,txtNucNoExitoso;
    Button btnSegumientoNoExitoso,btnAlertamientoNoExitoso,btnFinalizarViaje;
    String cargarPlaca,cargarNuc,sinInformacion = "SIN INFORMACION",cargarServicio = "creado",serbar="creado",varAlertamiento = "sinAlerta";
    SharedPreferences share;
    SharedPreferences.Editor editor;
    LinearLayout lyPlacaNoExitoso,lyNucNoExitoso;
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
        btnSegumientoNoExitoso = root.findViewById(R.id.btnSegumientoNoExitoso);
        btnAlertamientoNoExitoso = root.findViewById(R.id.btnAlertamientoNoExitoso);
        btnFinalizarViaje = root.findViewById(R.id.btnFinalizarViajeNoExit);
        lyPlacaNoExitoso = root.findViewById(R.id.lyPlacaNoExitoso);
        lyNucNoExitoso = root.findViewById(R.id.lyNucNoExitoso);

        txtPlacaNoExitoso.setText(cargarPlaca);
        txtNucNoExitoso.setText(cargarNuc);

        if(cargarPlaca.equals(sinInformacion)){
            lyPlacaNoExitoso.setVisibility(View.GONE);
        }
        if(cargarNuc.equals(sinInformacion)){
            lyNucNoExitoso.setVisibility(View.GONE);
        }

        btnSegumientoNoExitoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "SE ESTÁ ENVIANDO SU UBICACIÓN", Toast.LENGTH_LONG).show();
                startLocationService();
            }
        });

        btnAlertamientoNoExitoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarDatosTransporte();
                Toast.makeText(getActivity(), "SU REPORTE AL C4 HA SIDO ENVIADO", Toast.LENGTH_LONG).show();
                System.out.println("CARGANDO SERVICIOOOOOOO" + cargarServicio);
                if(cargarServicio.equals(serbar)){
                    varAlertamiento = "alertando";
                    guardarAlerta();
                    System.out.println(varAlertamiento);
                }else{
                    varAlertamiento = "alertando";
                    guardarAlerta();
                    startLocationService();
                }
            }
        });

        btnFinalizarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "VIAJE CONCLUIDO", Toast.LENGTH_LONG).show();
                eliminarDatosTransporte();
                getActivity().onBackPressed(); //REGRESAS AL MENU PRINCIPAL
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
        editor.remove("alertamientoApp").commit();
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

    private void startLocationService() {
        Intent intent = new Intent(getContext(), LocationService.class);
        getActivity().startService(intent);
    }
    private void guardarAlerta() {
        share = getActivity().getSharedPreferences("main", MODE_PRIVATE);
        editor = share.edit();
        editor.putString("alertamientoApp", varAlertamiento);
        editor.apply();
    }
}