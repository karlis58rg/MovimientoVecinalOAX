package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MensajeEnviadoVigilanciaVecinal extends AppCompatActivity {

    ImageView btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_enviado_vigilancia_vecinal);

        btnHome = findViewById(R.id.imgHomeVigilanciaMensaje);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
