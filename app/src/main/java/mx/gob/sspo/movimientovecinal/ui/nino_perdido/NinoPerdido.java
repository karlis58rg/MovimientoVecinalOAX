package mx.gob.sspo.movimientovecinal.ui.nino_perdido;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import mx.gob.sspo.movimientovecinal.R;

public class NinoPerdido extends Fragment {

    private NinoPerdidoViewModel mViewModel;
    static WebView wvNino;

    public static NinoPerdido newInstance() {
        return new NinoPerdido();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nino_perdido_fragment, container, false);
        //************************************************************************//
        wvNino = root.findViewById(R.id.webNinoPerdido);

        wvNino.setWebViewClient(new WebViewClient());
        wvNino.loadUrl("https://oaxacaseguro.sspo.gob.mx/Login/busquedaMenorAusente.aspx");

        WebSettings webSettings = wvNino.getSettings();
        webSettings.setJavaScriptEnabled(true);


        wvNino.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && wvNino.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }

                return false;
            }
        });


        //************************************************************************//
        return root;
    }

    private void webViewGoBack(){
        wvNino.goBack();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NinoPerdidoViewModel.class);
        // TODO: Use the ViewModel
    }

}
