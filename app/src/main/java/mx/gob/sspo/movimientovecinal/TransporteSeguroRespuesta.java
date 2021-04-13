package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;

import mx.gob.sspo.movimientovecinal.Transporte.TransporteExistente;
import mx.gob.sspo.movimientovecinal.Transporte.TransporteNoExiste;

public class TransporteSeguroRespuesta extends AppCompatActivity {
    FragmentTransaction transaction;
    Fragment fragmentExiste,fragmentNoExiste;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    String datoVehiculo = "ENCONTRADO";
    String cargarInfoPlaca,cargarInfoNuc,cargarInfoBanderaPlacaNuc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_seguro_respuesta);
        fragmentExiste = new TransporteExistente();
        fragmentNoExiste = new TransporteNoExiste();
        cargarDatosTransporte();
        transaction = getSupportFragmentManager().beginTransaction();
        if(cargarInfoBanderaPlacaNuc.equals(datoVehiculo)){
            transaction.replace(R.id.contenedorTransporteSeguro,fragmentExiste).commit();
        }else{
            transaction.replace(R.id.contenedorTransporteSeguro,fragmentNoExiste).commit();
        }
    }

    private void cargarDatosTransporte() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoPlaca = share.getString("PLACA", "SIN INFORMACION");
        cargarInfoNuc = share.getString("NUC", "SIN INFORMACION");
        cargarInfoBanderaPlacaNuc = share.getString("DATO", "NOENCONTRADO");
    }
}