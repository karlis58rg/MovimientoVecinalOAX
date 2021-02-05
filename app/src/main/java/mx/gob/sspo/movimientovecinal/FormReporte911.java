package mx.gob.sspo.movimientovecinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class FormReporte911 extends AppCompatActivity implements ReporteEmergencias.OnFragmentInteractionListener {
    private static final int CODIGO_SOLICITUD_PERMISO = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private LocationManager locationManager;
    private Context context;
    private Activity activity;
    AlertDialog alert = null;
    int acceso = 0;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_reporte911);
        ///validacion de permisos

        if (ContextCompat.checkSelfPermission(FormReporte911.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormReporte911.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormReporte911.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }

        context = getApplicationContext();
        activity = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(acceso == 0){
            solicitarPermiso();
        } else {
            iniciar_fragment();

        }
    }

    public void iniciar_fragment(){
        Fragment fragmento = new ReporteEmergencias();
        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, fragmento).commit();
    }

    private void alertaGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema de GPS esta desactivado, ¿Desea Activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }


    public boolean checarStatusPermiso(int resultado){
        if(resultado == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public void solicitarPermiso(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(FormReporte911.this, "Permisos Activados", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            }, CODIGO_SOLICITUD_PERMISO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_SOLICITUD_PERMISO :
                int resultado = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

                if(checarStatusPermiso(resultado)) {
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        alertaGPS();
                    }
                    iniciar_fragment();
                } else {

                    Toast.makeText(activity, "No estan activos los permisos", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alert != null){
            alert.dismiss();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
