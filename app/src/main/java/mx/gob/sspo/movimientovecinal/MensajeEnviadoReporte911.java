package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MensajeEnviadoReporte911 extends AppCompatActivity {
    ImageView btnHome;
    TextView lblFolioCad911;
    String folio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_enviado_reporte911);

        btnHome = findViewById(R.id.imgHomeReporte911Mensaje);
        lblFolioCad911 = findViewById(R.id.lblFolioCad911);

        Intent i = getIntent();
        folio = i.getStringExtra("FolioCad");
        lblFolioCad911.setText("Folio:"+folio);


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
