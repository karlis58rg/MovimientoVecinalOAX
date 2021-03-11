package mx.gob.sspo.movimientovecinal.Shake;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import mx.gob.sspo.movimientovecinal.R;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String GENERAL_CHANEL_ID = getString(R.string.default_notification_channel_id);
            NotificationChannel notificationChannel = new NotificationChannel(GENERAL_CHANEL_ID, "General notifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
