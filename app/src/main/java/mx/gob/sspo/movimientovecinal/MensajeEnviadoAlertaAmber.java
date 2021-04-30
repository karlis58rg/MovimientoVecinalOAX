package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MensajeEnviadoAlertaAmber extends AppCompatActivity {
    ImageView btnHome;
    TextView lblFolioCadAA;
    String folio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_enviado_alerta_amber);

        btnHome = findViewById(R.id.imgHomeAlertaAmberMensaje);
        lblFolioCadAA = findViewById(R.id.lblFolioCadAA);

        Intent i = getIntent();
        folio = i.getStringExtra("FolioCad");
        lblFolioCadAA.setText("Folio:"+folio);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
