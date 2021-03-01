package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class Splash extends AppCompatActivity {
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    String cargarInfoTelefono,cargarInfoNombre,cargarInfoTelefonoConfianza;
    String telefono = "SIN INFORMACION";
    String nombre = "SIN INFORMACION";
    String telConfianza = "SIN INFORMACION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cargarDatos();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               /* if (cargarInfoTelefono.isEmpty() != false){
                    if(cargarInfoNombre != nombre && cargarInfoTelefonoConfianza != telConfianza){
                        Intent i = new Intent(Splash.this,FormRegistroUsuario.class);
                        startActivity(i);
                        finish();
                    }
                    Intent i = new Intent(Splash.this,FormAddTelefono.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(Splash.this,MenuEventos.class);
                    startActivity(i);
                    finish();
                }*/
                if (cargarInfoTelefono.equals(telefono)){
                    Intent i = new Intent(Splash.this,FormAddTelefono.class);
                    startActivity(i);
                    finish();
                }else if(cargarInfoNombre.equals(nombre) || cargarInfoTelefonoConfianza.equals(telConfianza)){
                    Intent iRU = new Intent(Splash.this,FormRegistroUsuario.class);
                    startActivity(iRU);
                    finish();
                }else{

                    Intent iME = new Intent(Splash.this,MenuEventos.class);
                    startActivity(iME);
                    finish();
                }
                Log.i("HEY", cargarInfoTelefono+cargarInfoNombre+cargarInfoTelefonoConfianza);
            }
        },3000);
    }

    private void cargarDatos(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoTelefono = shared.getString("TELEFONO","SIN INFORMACION");
        cargarInfoNombre = shared.getString("NOMBRE","SIN INFORMACION");
        cargarInfoTelefonoConfianza = shared.getString("TELCONFIANZA","SIN INFORMACION");
        //Toast.makeText(getApplicationContext(),cargarInfoTelefono,Toast.LENGTH_LONG).show();
    }
}
