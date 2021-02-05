package mx.gob.sspo.movimientovecinal.ui.funciones;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

import mx.gob.sspo.movimientovecinal.R;

public class Funciones extends Fragment {

    private FuncionesViewModel mViewModel;
    ImageSlider imageSlider;

    public static Funciones newInstance() {
        return new Funciones();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.funciones_fragment, container, false);
        //******************************* EVENTOS DE LOS BOTONES *****************************************//
        imageSlider = root.findViewById(R.id.slider);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.gu1,"", ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.gu2,"", ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.gu3,"", ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.gu4,"", ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.gu5,"", ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.gu6,"", ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.gu7,"", ScaleTypes.CENTER_INSIDE));
        imageSlider.setImageList(slideModels,ScaleTypes.CENTER_INSIDE);
        //*******************************************************************//
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FuncionesViewModel.class);
        // TODO: Use the ViewModel
    }

}
