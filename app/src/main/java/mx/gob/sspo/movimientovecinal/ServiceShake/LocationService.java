package mx.gob.sspo.movimientovecinal.ServiceShake;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import mx.gob.sspo.movimientovecinal.R;
import mx.gob.sspo.movimientovecinal.receiver.LocationActionsReceiver;

public class LocationService extends Service {

    private static String TAG = "LocationService";
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Intent receiverIntent = new Intent(this,
                LocationActionsReceiver.class)
                .setAction(LocationActionsReceiver.ACTION_CANCEL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, receiverIntent, 0);

        String GENERAL_CHANNEL_ID = getString(R.string.default_notification_channel_id);
        Notification notification = new NotificationCompat.Builder(this, GENERAL_CHANNEL_ID)
                .setContentTitle("Localizando")
                .setContentText("Enviando ubicación...")
                .setSmallIcon(R.drawable.ic_logo_app)
                .addAction(R.drawable.ic_baseline_cancel_24, "Detener", pendingIntent)
                .build();

        startForeground(1, notification);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "Location : " +
                        locationResult.getLastLocation().getLatitude() +
                        " , " + locationResult.getLastLocation().getLongitude());
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
