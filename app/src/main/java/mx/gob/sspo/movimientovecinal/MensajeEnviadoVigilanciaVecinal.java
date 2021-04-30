package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MensajeEnviadoVigilanciaVecinal extends AppCompatActivity {

    ImageView btnHome;
    TextView lblFolioCad;
    String folio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_enviado_vigilancia_vecinal);

        btnHome = findViewById(R.id.imgHomeVigilanciaMensaje);
        lblFolioCad = findViewById(R.id.lblFolioCad);

        Intent i = getIntent();
        folio = i.getStringExtra("FolioCad");
        lblFolioCad.setText("Folio:"+folio);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
