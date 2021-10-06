package mx.gob.sspo.movimientovecinal;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReporteEmergencias extends Fragment implements OnMapReadyCallback {
    /**************** MAPA*****************/
    private OnFragmentInteractionListener mListener;
    GoogleMap map;
    Boolean actul_posicion = true;
    Marker marker = null;
    Double lat_origen, lon_origen;
    TextView tv_add;
    LatLng aux;
    Location aux_loc;
    String direccion1, municipio, estado;
    ScrollView scroll;
    AlertDialog alert = null;

    /************** EVENTOS*****************/
    EditText txtDescEmergencia;
    ImageView home, photo, video, audio, imgImagen, recordAu, playAu, detenerAudio,cancelarAudio,imgSAlirImage,imgSalirVideos;
    VideoView videoViewImage;
    Chronometer tiempo;
    Button btnSendEmergencia;
    Switch switchDisc;
    int bandera = 0;
    int status = 0;
    boolean statusMultimediaImagen,statusMultimediaVideo,statusMultimediaAudio;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALERY_CAPTURE = 2;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    String audioEncodeString, cadena, cadenaVideo, cadenaAudio, mediaPath;
    String fecha, hora, rutaMultimedia, descEmergencia;
    String cargarInfoTelefono, cargarInfoNombre, cargarInfoApaterno, cargarInfoAmaterno;
    int numberRandom;
    String randomCodigoVerifi, codigoVerifi;
    String opcDiscapacidad = "NO";
    String TAG = "REPORTE EMERGENCIAS";
    long detenerse;
    Boolean correr = false;
    int runAudio = 0;
    private String outputFile = null;
    private MediaRecorder miGrabacion;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    Dialog myDialog;
    TextView txtCamara,txtGaleria,txtCerrarNotificacion;


    public ReporteEmergencias() {
        // Required empty public constructor
    }

    public static ReporteEmergencias newInstance(String param1, String param2) {
        ReporteEmergencias fragment = new ReporteEmergencias();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_reporte_emergencias, container, false);
        //*****************************************************************//
        cargarDatos();
        Random();
        myDialog = new Dialog(getContext());
        scroll = view.findViewById(R.id.scrollMap);

        home = view.findViewById(R.id.imgHome);
        photo = view.findViewById(R.id.imgImagen);
        video = view.findViewById(R.id.imgVideo);
        audio = view.findViewById(R.id.imgAudio);

        imgImagen = view.findViewById(R.id.viewImage);
        videoViewImage = view.findViewById(R.id.viewVideo);

        recordAu = view.findViewById(R.id.imgGrabar);
        playAu = view.findViewById(R.id.imgReproducir);
        detenerAudio = view.findViewById(R.id.imgDetener);
        cancelarAudio = view.findViewById(R.id.imgCancelar);
        imgSAlirImage = view.findViewById(R.id.imgSalirImage);
        imgSalirVideos = view.findViewById(R.id.imgSalirVideo);

        tiempo = view.findViewById(R.id.timer);

        btnSendEmergencia = view.findViewById(R.id.btnEnviarEmergencia);

        txtDescEmergencia = view.findViewById(R.id.txtEmergencia);
        /////posicion de cursor///////////
        txtDescEmergencia.setFocusableInTouchMode(true);
        txtDescEmergencia.requestFocus();

        imgImagen.setVisibility(view.GONE);
        videoViewImage.setVisibility(view.GONE);
        tiempo.setVisibility(View.GONE);
        recordAu.setVisibility(view.GONE);
        detenerAudio.setVisibility(view.GONE);
        playAu.setVisibility(view.GONE);
        cancelarAudio.setVisibility(view.GONE);
        imgSAlirImage.setVisibility(view.GONE);
        imgSalirVideos.setVisibility(view.GONE);


        switchDisc = view.findViewById(R.id.switchDiscapacidad);


        switchDisc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheked) {
                if (isCheked == true) {
                    opcDiscapacidad = "SI";
                } else {
                    opcDiscapacidad = "NO";
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(v);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (bandera == 1 || bandera == 3) {
                    imgImagen.clearAnimation();
                    resetChronometro();
                }*/
                bandera = 2;
                System.out.println(bandera);
                photo.setVisibility(view.GONE);
                audio.setVisibility(view.GONE);
                videoViewImage.setVisibility(view.VISIBLE);
                imgSalirVideos.setVisibility(view.VISIBLE);
                imgImagen.setVisibility(view.GONE);
                playAu.setVisibility(View.GONE);
                recordAu.setVisibility(View.GONE);
                detenerAudio.setVisibility(View.GONE);
                tiempo.setVisibility(View.GONE);
                llamarItemVideo();
                Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE VIDEO", Toast.LENGTH_SHORT).show();

            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (bandera == 1 || bandera == 2) {
                    imgImagen.clearAnimation();
                    videoViewImage.clearAnimation();
                }*/
                bandera = 3;
                System.out.println(bandera);
                photo.setVisibility(view.GONE);
                video.setVisibility(view.GONE);
                cancelarAudio.setVisibility(View.VISIBLE);
                recordAu.setVisibility(view.VISIBLE);
                detenerAudio.setVisibility(View.VISIBLE);
                detenerAudio.setEnabled(false);
                playAu.setVisibility(View.VISIBLE);
                playAu.setEnabled(false);
                tiempo.setVisibility(View.VISIBLE);
                videoViewImage.setVisibility(view.GONE);
                imgImagen.setVisibility(view.GONE);
                Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE AUDIO", Toast.LENGTH_SHORT).show();
            }
        });

        cancelarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertaEliminarAudio();
            }
        });

        imgSAlirImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertaEliminarImagen();
            }
        });

        imgSalirVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertaEliminarVideo();
            }
        });

        recordAu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabar();
            }
        });

        detenerAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detener();
            }
        });

        playAu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bandera = 4;
                reproducir();
            }
        });

        btnSendEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_SHORT).show();
                    if (txtDescEmergencia.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "EL CAMPO **DESCRIPCIÓN DE EMERGENCIA** ES OBLIGATORIO", Toast.LENGTH_SHORT).show();
                    } else if (tv_add.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "LO SENTIMOS, SU UBICACIÓN ES NECESARIA PARA EL FUNCIONAMIENTO DE ESTE APARTADO", Toast.LENGTH_SHORT).show();
                    } else if (bandera == 1) {
                        System.out.println("btnEnviar 1 "+bandera);
                        if(cadena.isEmpty()){
                            bandera = 0;
                            Toast.makeText(getContext(), "SU ARCHIVO MULTIMEDIA NO PUDO SER ENVIADO EXITOSAMENTE, SIN EMBARGO SU EMERGENCIA FUE RECIBIDA", Toast.LENGTH_SHORT).show();
                            statusMultimediaImagen = false;
                            insertBdEventoIOS();
                        }else{
                            Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_SHORT).show();
                            insertImagen();
                            insertBdEventoIOS();
                        }
                    } else if (bandera == 2) {
                        System.out.println("btnEnviar 2"+bandera);
                        if(cadenaVideo.isEmpty()){
                            bandera = 0;
                            Toast.makeText(getContext(), "SU ARCHIVO MULTIMEDIA NO PUDO SER ENVIADO EXITOSAMENTE, SIN EMBARGO SU EMERGENCIA FUE RECIBIDA", Toast.LENGTH_SHORT).show();
                            statusMultimediaVideo = false;
                            insertBdEventoIOS();
                        }else {
                            Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_SHORT).show();
                            insertVideo();
                            insertBdEventoIOS();
                        }
                    } else if (bandera == 3) {
                        System.out.println("btnEnviar 3"+bandera);
                        if(cadenaAudio.isEmpty()){
                            bandera = 0;
                            Toast.makeText(getContext(), "SU ARCHIVO MULTIMEDIA NO PUDO SER ENVIADO EXITOSAMENTE, SIN EMBARGO SU EMERGENCIA FUE RECIBIDA", Toast.LENGTH_SHORT).show();
                            statusMultimediaAudio = false;
                            recordAu.setVisibility(view.GONE);
                            detenerAudio.setVisibility(view.GONE);
                            playAu.setVisibility(view.GONE);
                            tiempo.setVisibility(View.GONE);
                            resetChronometro();
                            insertBdEventoIOS();
                        }else{
                            Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_SHORT).show();
                            recordAu.setVisibility(view.GONE);
                            detenerAudio.setVisibility(view.GONE);
                            playAu.setVisibility(view.GONE);
                            tiempo.setVisibility(View.GONE);
                            resetChronometro();
                            insertAudio();
                            insertBdEventoIOS();
                        }
                    } else {
                        Toast.makeText(getActivity(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_SHORT).show();
                        insertBdEventoIOS();
                    }

                }catch (Exception e){
                    Intent i = new Intent(getActivity(), MensajeError.class);
                    startActivity(i);
                    getActivity().onBackPressed();
                }
            }
        });

        //*************************** SE MUESTRA EL MAPA ****************************************//
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final String[] permisos = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        map = googleMap;
        tv_add = (TextView) getActivity().findViewById(R.id.tv_miadres);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        //activar el boton " ubicación" de mapa y regrese la dirección actual/////
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                actul_posicion=true;
                return false;
            }
        });
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                aux_loc = location;

                if(actul_posicion) {
                    iniciar(location);
                }
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                scroll.requestDisallowInterceptTouchEvent(true);
                Toast.makeText(getActivity(), "Se moverá el marcador", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng neww = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                mi_ubi(neww);
            }
        });
    }

    public String mi_ubi(LatLng au){
        String address = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(au.latitude, au.longitude, 1);
            lat_origen = au.latitude;
            lon_origen = au.longitude;
            address = addresses.get(0).getAddressLine(0);
            address = "Dirección: \n" + address;
            tv_add.setText(address);
            Address DirCalle = addresses.get(0);
            direccion1 = DirCalle.getAddressLine(0);
            municipio = DirCalle.getLocality();
            estado = DirCalle.getAdminArea();
            if(municipio != null){
                municipio = DirCalle.getLocality();
            }else{
                municipio = "SIN INFORMACIÓN";
            }

        } catch (IOException e){

            e.printStackTrace();
        }

        return address;
    }

    public void iniciar(Location location){
        tv_add.setText("");

        if(marker != null){
            marker.remove();
        }

        lat_origen = location.getLatitude();
        lon_origen = location.getLongitude();
        actul_posicion = false;

        LatLng mi_posicion = new LatLng(lat_origen, lon_origen);

        marker = map.addMarker(new MarkerOptions().position(mi_posicion).title("Mi posición!").draggable(true));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat_origen, lon_origen)).zoom(14).bearing(15).build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        aux = new LatLng(lat_origen, lon_origen);

        mi_ubi(aux);
    }

    //********************************** IMAGEN ***********************************//
    //****************************** ABRE LA CAMARA ***********************************//
    private  void llamarItem(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }
    private  void llamarItemGaleria(){
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takePictureIntent.setType("image/");
        startActivityForResult(takePictureIntent.createChooser(takePictureIntent,"SELECCIONA LA APLICACIÓN"),REQUEST_GALERY_CAPTURE);
    }
    //********************************** VIDEO ***********************************//
    //****************************** PARA UTILIZACIÓN DEL VIDEO ***********************************//

    private  void llamarItemVideo(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,20);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent,REQUEST_VIDEO_CAPTURE);
        }
    }

    //********************************** ACCIÓN PARA AUDIO, VIDEO E IMAGEN ***********************************//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(bandera == 1){
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgImagen.setImageBitmap(imageBitmap);
                imagen();
            }else if(requestCode == REQUEST_GALERY_CAPTURE && resultCode == Activity.RESULT_OK){
                Uri path = data.getData();
                imgImagen.setImageURI(path);
                imgImagen.buildDrawingCache();
                imagen();
            }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED){
                imgImagen.setImageBitmap(null);
                imgImagen.destroyDrawingCache();
                imgImagen.setVisibility(View.GONE);
                audio.setVisibility(View.VISIBLE);
                video.setVisibility(View.VISIBLE);
                imgSAlirImage.setVisibility(View.GONE);
            }
            else if(requestCode == REQUEST_GALERY_CAPTURE && resultCode == Activity.RESULT_CANCELED){
                imgImagen.setImageBitmap(null);
                imgImagen.destroyDrawingCache();
                imgImagen.setVisibility(View.GONE);
                audio.setVisibility(View.VISIBLE);
                video.setVisibility(View.VISIBLE);
                imgSAlirImage.setVisibility(View.GONE);
            }
        }else if(bandera == 2){
            super.onActivityResult(requestCode,resultCode,data);
            try
            {
                if (requestCode == 0 && resultCode == Activity.RESULT_OK && null != data)
                {
                    Toast.makeText(getActivity(), "EL CODIGO NO ES DE VIDEO", Toast.LENGTH_LONG).show();

                }else if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_CANCELED){
                    videoViewImage.setVisibility(View.GONE);
                    videoViewImage.clearAnimation();
                    photo.setVisibility(View.VISIBLE);
                    audio.setVisibility(View.VISIBLE);
                    imgSalirVideos.setVisibility(View.GONE);
                }
                else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK )
                {
                    Uri videoUri = data.getData();
                    videoViewImage.setVideoURI(videoUri);
                    videoViewImage.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
                    int DataNew = videoViewImage.getBufferPercentage();
                    Log.i("HERE", "PRIMER PARTE DONDE TRAE LA URI");
                    System.out.println("video"+videoUri);

                    // SELECCIÓN DEL VIDEO ********************

                    Uri selectedVideo = data.getData();
                    String[] filePathColum = {MediaStore.Video.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedVideo,filePathColum,null,null,null);
                    //Cursor cursor = getActivity().getContentResolver().query(videoUri,filePathColum,null,null,null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    Log.i("HERE", "GRABANDO");

                    //RUTA FISICA DEL VIDEO *******************

                    int columIndex = cursor.getColumnIndex(filePathColum[0]);
                    mediaPath = cursor.getString(columIndex);
                    //txtResVideo.setText(mediaPath);
                    cursor.close();
                    Log.i("HERE", "MEDIA PATH");
                    System.out.println("media path"+ mediaPath);


                    // VISTA PREVIA DEL VIDEO DESDE UNA RUTA FISICA ************************

                    videoViewImage.setVideoURI(selectedVideo);
                    MediaController mediaController = new MediaController(this.getActivity());
                    videoViewImage.setMediaController(mediaController);
                    mediaController.setAnchorView(videoViewImage);
                    Log.i("HERE", "VISTA PREVIA");
                    InputStream inputStream = null;
                    String encodedString = null;

                    try
                    {
                        inputStream = new FileInputStream(mediaPath);
                        System.out.println("IMPUT STREAM"+inputStream.toString());

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    byte[] bytes;
                    byte[] buffer = new byte[209715200];
                    int bytesRead;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    try
                    {
                        while ((bytesRead = inputStream.read(buffer)) != -1)
                        {
                            output.write(buffer,0,bytesRead);
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    bytes = output.toByteArray();
                    encodedString = Base64.encodeToString(bytes,Base64.NO_WRAP);
                    cadenaVideo = encodedString;
                    System.out.println(cadenaVideo);
                    Log.i("CADENA VIDEO",cadenaVideo);
                    //rest.setText(encodedString);

                }else
                {
                    Toast.makeText(getActivity(),"VIDEO EN CONSTRUCCIÓN",Toast.LENGTH_SHORT);
                }

            }catch (Exception e)
            {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    //********************************** SE CONVIERTE A BASE64 ***********************************//
    private void imagen(){
        imgImagen.buildDrawingCache();
        Bitmap bitmap = imgImagen.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40,baos);
        byte[] imgBytes = baos.toByteArray();
        String imgString = android.util.Base64.encodeToString(imgBytes, android.util.Base64.NO_WRAP);
        cadena = imgString;
        System.out.println(cadena);
        Log.i("BASE64",cadena);

        imgBytes = android.util.Base64.decode(imgString, android.util.Base64.NO_WRAP);
        Bitmap decoded= BitmapFactory.decodeByteArray(imgBytes,0,imgBytes.length);
        imgImagen.setImageBitmap(decoded);
        System.out.print("IMAGEN" + imgImagen);

    }
    private void videoBase64(){
        //CONVERTIR  VIDEO A BASE64 **************************
        InputStream inputStream = null;
        String encodedString = null;
        try
        {
            inputStream = new FileInputStream(mediaPath);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                output.write(buffer,0,bytesRead);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        encodedString = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
        cadenaVideo = encodedString;
        Log.i("CADENA VIDEO",cadenaVideo);
        //rest.setText(encodedString);
    }

    //********************************** INSERTA REGISTRO A LA BD ***********************************//
    //*********************** METODO QUE INSERTA A LA BASE DE DATOS DESPUES DE INSERTAR AL CAD ***********************//
    public void insertBdEventoIOS() {
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);

        descEmergencia = txtDescEmergencia.getText().toString().toUpperCase();


        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("Folio", randomCodigoVerifi)
                .add("Telefono", cargarInfoTelefono)
                .add("NombreUsuario", cargarInfoNombre)
                .add("APaternoUsuario", cargarInfoApaterno)
                .add("AMaternoUsuario", cargarInfoAmaterno)
                .add("Municipio", municipio)
                .add("Estado", estado)
                .add("Latitud", lat_origen.toString())
                .add("Longitud", lon_origen.toString())
                .add("DescripcionEmergencia", descEmergencia)
                .add("Discapacidad", opcDiscapacidad)
                .add("Fecha", fecha)
                .add("Hora", hora)
                .add("Archivo", rutaMultimedia)
                .build();

        Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/Eventos911/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getContext(), "ERROR AL ENVIAR SU REGISTRO, FAVOR DE VERIFICAR SU CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
                Looper.loop();
                // Toast.makeText(getActivity(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    final String resp = myResponse;
                    ReporteEmergencias.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                String respCad = resp;
                                final String valor = "\"false\"";
                                if(respCad.equals(valor)){
                                    Intent i = new Intent(getActivity(), MensajeError.class);
                                    startActivity(i);
                                    getActivity().onBackPressed();
                                }else{
                                    String respuestaFolio = respCad;
                                    respuestaFolio = respuestaFolio.replace('"',' ');
                                    Intent i = new Intent(getActivity(), MensajeEnviadoReporte911.class);
                                    i.putExtra("FolioCad", respuestaFolio);
                                    startActivity(i);
                                    getActivity().onBackPressed();
                                }
                                Log.i(TAG, resp);
                        }
                    });
                }
            }
        });
    }
    //********************************** INSERTA IMAGEN AL SERVIDOR ***********************************//
    public void insertImagen() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Description",randomCodigoVerifi +".jpg" )
                .add("ImageData", cadena)
                .build();
        Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/MultimediaImageUser/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getContext(), "ERROR AL ENVIAR SU REGISTRO, FAVOR DE VERIFICAR SU CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    final String resp = myResponse;
                    ReporteEmergencias.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String respImagen = resp;
                            final String valor = "true";
                            if(respImagen.equals(valor)){
                                statusMultimediaImagen = true;
                                rutaMultimedia = "https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/Images/" + randomCodigoVerifi + ".jpg";
                                System.out.println("IMG"+statusMultimediaImagen);
                                System.out.println("EL DATO DE LA IMAGEN SE ENVIO CORRECTAMENTE");
                                Toast.makeText(getContext(), "EL ARCHIVO MULTIMEDIA SE ENVIO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "ERROR AL ENVIAR SU ARCHIVO, FAVOR DE LLAMAR AL 911", Toast.LENGTH_SHORT).show();
                                statusMultimediaImagen = false;
                                rutaMultimedia = "https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/Images/sinarchivo.jpg";
                                System.out.println("IMG"+statusMultimediaImagen);
                            }
                            Log.i("HERE", resp);
                        }
                    });
                }
            }
        });
    }

    //********************************** INSERTA VIDEO AL SERVIDOR ***********************************//
    public void insertVideo() {
        cadenaVideo = cadenaVideo.replaceAll("\n","");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("DescriptionVideo", randomCodigoVerifi+".mp4" )
                .add("VideoData", cadenaVideo)
                .build();

        Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/MultimediaVideoUser/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getContext(), "ERROR AL ENVIAR SU ARCHIVO MULTIMEDIA, FAVOR DE MARCAR AL 911", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    final String resp = myResponse;
                    ReporteEmergencias.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String respImagen = resp;
                            final String valor = "true";
                            if(respImagen.equals(valor)){
                                statusMultimediaVideo = true;
                                rutaMultimedia = "https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/Video/" + randomCodigoVerifi + ".mp4";
                                System.out.println("Video"+rutaMultimedia+statusMultimediaVideo);
                                System.out.println("EL DATO DEL VIDEO SE ENVIO CORRECTAMENTE");
                                Toast.makeText(getContext(), "EL ARCHIVO MULTIMEDIA SE ENVIO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "ERROR AL ENVIAR SU ARCHIVO, FAVOR DE LLAMAR AL 911", Toast.LENGTH_SHORT).show();
                                statusMultimediaVideo = false;
                                rutaMultimedia = "https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/Video/sinarchivo.jpg";
                                System.out.println("Video"+rutaMultimedia+statusMultimediaVideo);
                            }
                            Log.i("HERE", resp);
                        }
                    });
                }
            }
        });
    }

    //********************************** INSERTA AUDIO AL SERVIDOR ***********************************//
    public void insertAudio() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("DescriptionAudio", randomCodigoVerifi+".mp4" )
                .add("AudioData", cadenaAudio)
                .build();

        Request request = new Request.Builder()
                .url("https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/api/MultimediaAudioUser/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getContext(), "ERROR AL ENVIAR SU REGISTRO", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    final String resp = myResponse;
                    ReporteEmergencias.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String respImagen = resp;
                            final String valor = "true";
                            if(respImagen.equals(valor)){
                                statusMultimediaAudio = true;
                                rutaMultimedia = "https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/Audio/" + randomCodigoVerifi + ".mp4";
                                System.out.println("Audio"+statusMultimediaAudio);
                                System.out.println("EL DATO DEL AUDIO SE ENVIO CORRECTAMENTE");
                                Toast.makeText(getContext(), "EL ARCHIVO MULTIMEDIA SE ENVIO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "ERROR AL ENVIAR SU ARCHIVO, FAVOR DE LLAMAR AL 911", Toast.LENGTH_SHORT).show();
                                statusMultimediaAudio = false;
                                rutaMultimedia = "https://oaxacaseguro.sspo.gob.mx/AppMovimientoVecinal/Audio/sinarchivo.jpg";
                                System.out.println("Audio"+statusMultimediaAudio);
                            }
                            Log.i("HERE", resp);
                        }
                    });
                }
            }
        });
    }

    //********************* INICIA EL CONTADOR **********************************************************************//
    private void resetChronometro() {
        tiempo.setBase(SystemClock.elapsedRealtime());
        detenerse = 0;
    }
    private void stopChronometro() {
        if(correr){
            tiempo.stop();
            detenerse = SystemClock.elapsedRealtime() -tiempo.getBase();
            correr = false;
        }
    }
    private void startChronometro() {
        runAudio = 1;
        if(!correr){
            tiempo.setBase(SystemClock.elapsedRealtime() -detenerse);
            tiempo.start();
            correr = true;
        }
    }
    ////*********************************************GRABAR AUDIO*********************************************//////
    public void grabar() {
        if(status == 1){
            resetChronometro();
            detenerAudio.setEnabled(false);
        }else if(status == 2){
            resetChronometro();
            detenerAudio.setEnabled(false);
        }
        Toast.makeText(getActivity(), "GRABANDO", Toast.LENGTH_SHORT).show();
        startChronometro();
        recordAu.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_grabar_dos));
        detenerAudio.setEnabled(true);
        recordAu.setEnabled(false);
        outputFile = getContext().getExternalFilesDir(null).getAbsolutePath() + "/Grabacion.3gp";
        miGrabacion = new MediaRecorder();
        miGrabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
        miGrabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        miGrabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        miGrabacion.setOutputFile(outputFile);
        Log.i("RUTA_AUDIO",outputFile);
        //audioBase64();
        try {
            miGrabacion.prepare();
            miGrabacion.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void detener() {
        Toast.makeText(getActivity(), "DETENIENDO GRABACION", Toast.LENGTH_SHORT).show();
        status = 1;
        runAudio = 0;
        stopChronometro();
        recordAu.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_grabar));
        recordAu.setEnabled(true);
        if (miGrabacion != null) {
            miGrabacion.stop();
            miGrabacion.release();
            miGrabacion = null;
            //Toast.makeText(getActivity().getApplicationContext(), "El audio fue grabado con éxito", Toast.LENGTH_LONG).show();
        }
        playAu.setEnabled(true);
        reproducir();
        //audioBase64();
    }
    public void reproducir() {
        if(correr == true){
            Toast.makeText(getActivity(), "LO SENTIMOS, DEBE DETENER LA GRABACIÓN ", Toast.LENGTH_SHORT).show();
        }
        status = 2;
        MediaPlayer m = new MediaPlayer();
        try {
            m.setDataSource(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            m.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        m.start();
        Toast.makeText(getActivity().getApplicationContext(), "REPRODUCIENDO AUDIO", Toast.LENGTH_LONG).show();
        audioBase64();
    }
    public void audioBase64(){
        //CONVERTIR AUDIO A BASE64
        InputStream inputStream = null;
        audioEncodeString = null;
        try
        {
            inputStream = new FileInputStream(outputFile);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        byte[] bytes;
        byte[] bufferAudio = new byte[8192];
        int bytesAudioRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            while ((bytesAudioRead = inputStream.read(bufferAudio)) != -1)
            {
                output.write(bufferAudio,0,bytesAudioRead);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        bytes = output.toByteArray();
        audioEncodeString = Base64.encodeToString(bytes,Base64.NO_WRAP);
        cadenaAudio = audioEncodeString;
        Log.i("CADENA AUDIO",cadenaAudio);
    }

    //*********************************************************************//
    public void cargarDatos() {
        share = getActivity().getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoTelefono = share.getString("TELEFONO", "");
        cargarInfoNombre = share.getString("NOMBRE", "SIN INFORMACION");
        cargarInfoApaterno = share.getString("APATERNO", "SIN INFORMACION");
        cargarInfoAmaterno = share.getString("AMATERNO", "SIN INFORMACION");
    }
    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    public void Random(){
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = "OAX2021"+codigoVerifi;
    }

    public void alertaEliminarImagen(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("EL ARCHIVO SE ELIMINARA.\n ¿DESEA CONTINUAR?")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bandera = 0;
                        imgImagen.setImageBitmap(null);
                        imgImagen.destroyDrawingCache();
                        audio.setVisibility(View.VISIBLE);
                        video.setVisibility(View.VISIBLE);
                        imgImagen.setVisibility(View.GONE);
                        imgSAlirImage.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    public void alertaEliminarVideo(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("EL ARCHIVO SE ELIMINARA.\n ¿DESEA CONTINUAR?")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bandera = 0;
                        Log.i("SaliendoVideo", String.valueOf(bandera));
                        videoViewImage.setBackgroundResource(0);
                        videoViewImage.setVisibility(View.GONE);
                        audio.setVisibility(View.VISIBLE);
                        photo.setVisibility(View.VISIBLE);
                        imgSalirVideos.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    public void alertaEliminarAudio(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("EL ARCHIVO SE ELIMINARA.\n ¿DESEA CONTINUAR?")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(correr == true){
                            tiempo.stop();
                            detenerse = SystemClock.elapsedRealtime() -tiempo.getBase();
                            correr = false;
                            stopChronometro();
                        }
                        bandera = 0;
                        resetChronometro();
                        recordAu.setVisibility(View.GONE);
                        detenerAudio.setVisibility(View.GONE);
                        tiempo.setVisibility(View.GONE);
                        playAu.setVisibility(View.GONE);
                        audioEncodeString = null;
                        photo.setVisibility(View.VISIBLE);
                        video.setVisibility(View.VISIBLE);
                        cancelarAudio.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    public void showPopUp(View v){
        myDialog.setContentView(R.layout.costumpopupimage);
        txtCamara = myDialog.findViewById(R.id.lblCamara);
        txtGaleria = myDialog.findViewById(R.id.lblGaleria);
        txtCerrarNotificacion = myDialog.findViewById(R.id.lblCancelarImagen);

        txtCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bandera = 1;
                System.out.println("btnCamara 1 "+bandera);
                video.setVisibility(View.GONE);
                audio.setVisibility(View.GONE);
                imgImagen.setVisibility(View.VISIBLE);
                imgSAlirImage.setVisibility(View.VISIBLE);
                videoViewImage.setVisibility(View.GONE);
                playAu.setVisibility(View.GONE);
                recordAu.setVisibility(View.GONE);
                detenerAudio.setVisibility(View.GONE);
                tiempo.setVisibility(View.GONE);
                llamarItem();
                Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE TOMA DE FOTO", Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        });
        txtGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    bandera = 1;
                    System.out.println("btnGaleria 1 "+bandera);
                    video.setVisibility(View.GONE);
                    audio.setVisibility(View.GONE);
                    imgImagen.setVisibility(View.VISIBLE);
                    imgSAlirImage.setVisibility(View.VISIBLE);
                    videoViewImage.setVisibility(View.GONE);
                    playAu.setVisibility(View.GONE);
                    recordAu.setVisibility(View.GONE);
                    detenerAudio.setVisibility(View.GONE);
                    tiempo.setVisibility(View.GONE);
                    llamarItemGaleria();
                    Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE IMAGEN DESDE GALERIA", Toast.LENGTH_SHORT).show();
                    myDialog.dismiss();
                }
            }
        });

        txtCerrarNotificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }
}
