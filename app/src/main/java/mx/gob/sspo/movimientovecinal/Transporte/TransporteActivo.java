package mx.gob.sspo.movimientovecinal.Transporte;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.gob.sspo.movimientovecinal.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransporteActivo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransporteActivo extends Fragment {

    public TransporteActivo() {
    }

    public static TransporteActivo newInstance(String param1, String param2) {
        TransporteActivo fragment = new TransporteActivo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transporte_activo, container, false);
    }
}