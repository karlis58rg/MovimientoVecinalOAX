package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MensajeEnviadoAltoViolencia extends AppCompatActivity {
    ImageView btnHome;
    TextView lblFolioCadAV;
    String folio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_enviado_alto_violencia);

        btnHome = findViewById(R.id.imgHomeViolenciaMensaje);
        lblFolioCadAV = findViewById(R.id.lblFolioCadAV);

        Intent i = getIntent();
        folio = i.getStringExtra("FolioCad");
        lblFolioCadAV.setText("Folio:"+folio);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
