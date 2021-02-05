package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    String cargarInfoTelefono,cargarInfoNombre;
    String nombre = "SIN INFORMACION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cargarDatos();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cargarInfoTelefono.isEmpty() != false){
                    if(cargarInfoNombre != nombre){
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
                }
            }
        },3000);
    }

    private void cargarDatos(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoTelefono = shared.getString("TELEFONO","");
        cargarInfoNombre = shared.getString("NOMBRE","SIN INFORMACION");
        //Toast.makeText(getApplicationContext(),cargarInfoTelefono,Toast.LENGTH_LONG).show();
    }
}
