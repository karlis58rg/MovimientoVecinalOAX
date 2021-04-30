package mx.gob.sspo.movimientovecinal.Transporte;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import mx.gob.sspo.movimientovecinal.R;
import mx.gob.sspo.movimientovecinal.TransporteSeguro;
import mx.gob.sspo.movimientovecinal.TransporteSeguroRespuesta;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransporteExistente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransporteExistente extends Fragment {
    public static TextView lblTituloListos;
    TextView txtPlacaExitoso,txtNucExitoso,txtSitio,txtMarca,txtTipo;
    Button btnFinalizarViaje;
    String cargarPlaca,cargarNuc,cargarSitio,cargarMarca,cargarTipo,cargarServicio,serbar="creado";
    SharedPreferences share;
    SharedPreferences.Editor editor;
    Dialog myDialog;
    TextView txtCerrarNotificacion;
    public TransporteExistente() {
    }

    public static TransporteExistente newInstance(String param1, String param2) {
        TransporteExistente fragment = new TransporteExistente();
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
        View root = inflater.inflate(R.layout.fragment_transporte_existente, container, false);
        /***********************************************************************************/
        cargarDatosTransporte();
        myDialog = new Dialog(getContext());
        txtPlacaExitoso = root.findViewById(R.id.txtPlacaExitoso);
        txtNucExitoso = root.findViewById(R.id.txtNucExitoso);
        txtSitio = root.findViewById(R.id.txtSitio);
        txtMarca = root.findViewById(R.id.txtMarca);
        txtTipo = root.findViewById(R.id.txtTipo);
        btnFinalizarViaje = root.findViewById(R.id.btnFinalizarViaje);
        lblTituloListos = root.findViewById(R.id.lblTituloListosExiste);

        txtPlacaExitoso.setText(cargarPlaca);
        txtNucExitoso.setText(cargarNuc);
        txtSitio.setText(cargarSitio);
        txtMarca.setText(cargarMarca);
        txtTipo.setText(cargarTipo);

        btnFinalizarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cargarServicio.equals(serbar)){
                    eliminarDatosTransporte();
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
        cargarPlaca = share.getString("placaJson", "SIN INFORMACION");
        cargarNuc = share.getString("nucJson", "SIN INFORMACION");
        cargarSitio = share.getString("sitioJson", "SIN INFORMACION");
        cargarMarca = share.getString("marcaJson", "SIN INFORMACION");
        cargarTipo = share.getString("tipoJson", "SIN INFORMACION");
        cargarServicio = share.getString("servicio","sincrear");
    }

    private void eliminarDatosTransporte() {
        share = getActivity().getSharedPreferences("main", getActivity().MODE_PRIVATE);
        editor = share.edit();
        editor.remove("placaJson").commit();
        editor.remove("nucJson").commit();
        editor.remove("sitioJson").commit();
        editor.remove("marcaJson").commit();
        editor.remove("tipoJson").commit();
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