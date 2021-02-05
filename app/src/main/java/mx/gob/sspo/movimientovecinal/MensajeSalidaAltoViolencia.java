package mx.gob.sspo.movimientovecinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MensajeSalidaAltoViolencia extends AppCompatActivity {

    ImageView btnHome,btn911;
    private static  final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_salida_alto_violencia);

        btnHome = findViewById(R.id.imgHomeViolenciaMensajeError);
        btn911 = findViewById(R.id.img911);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn911.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();

            }
        });
    }

    //*************************** REALIZA LLAMADA AL 911 ********************************************//

    private void makePhoneCall() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);

        }else{
            String dial = "tel:911";
            Intent call = new Intent(Intent.ACTION_CALL);
            call.setData(Uri.parse(dial));
            startActivity(call);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();

            }else{
                Toast.makeText(getApplication().getApplicationContext(),"Permisos Denegados",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
