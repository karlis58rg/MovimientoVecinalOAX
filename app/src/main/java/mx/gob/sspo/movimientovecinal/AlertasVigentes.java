package mx.gob.sspo.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import mx.gob.sspo.movimientovecinal.ui.nino_perdido.NinoPerdido;

public class AlertasVigentes extends AppCompatActivity {
    ImageView imgHomeAlertaAmberVigentes;
    WebView webAlertaVigente;

    public static AlertasVigentes newInstance() {
        return new AlertasVigentes();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas_vigentes);

        imgHomeAlertaAmberVigentes = findViewById(R.id.imgHomeAlertaAmberVigentes);
        webAlertaVigente = findViewById(R.id.webAlertaVigente);

        imgHomeAlertaAmberVigentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        webAlertaVigente.setWebViewClient(new WebViewClient());
        webAlertaVigente.loadUrl("https://oaxacaseguro.sspo.gob.mx/Login/busquedaMenorAusente.aspx");

        WebSettings webSettings = webAlertaVigente.getSettings();
        webSettings.setJavaScriptEnabled(true);


        webAlertaVigente.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webAlertaVigente.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }

                return false;
            }
        });

    }

    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };

    private void webViewGoBack(){
        webAlertaVigente.goBack();
    }
}