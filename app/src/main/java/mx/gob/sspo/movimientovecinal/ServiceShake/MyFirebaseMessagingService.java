package mx.gob.sspo.movimientovecinal.ServiceShake;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import mx.gob.sspo.movimientovecinal.AlertaAmber;
import mx.gob.sspo.movimientovecinal.R;
import mx.gob.sspo.movimientovecinal.Shake.App;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent(requireContext(), AlertaAmber.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {0,100,1000,300,200,100,500,200,100};
        Notification notification = new NotificationCompat
                .Builder(requireContext(), getString(R.string.default_notification_channel_id))
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                //.setSound("")sonido
                .setVibrate(pattern)//patron de viracion permitido
                .setSmallIcon(R.drawable.ic_logo_app)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_logo_app,"Aceptar",pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);
        //NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(requireContext());
        //notificationManagerCompat.notify(1,notification);
    }

    private Context requireContext() {
        return this;
    }

}
