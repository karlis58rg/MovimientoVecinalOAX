package mx.gob.sspo.movimientovecinal.ServiceShake;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import mx.gob.sspo.movimientovecinal.AlertaAmber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    public static class ClickActionHelper {
        public static void startActivity(String AlertaAmber, Bundle extras, Context context){
            Class cls = null;
            try {
                cls = Class.forName(AlertaAmber);
            }catch(ClassNotFoundException e){
                //means you made a wrong input in firebase console
            }
            Intent i = new Intent(context, cls);
            i.putExtras(extras);
            context.startActivity(i);
        }
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("click_action")) {
            ClickActionHelper.startActivity(data.get("click_action"), null, this);
        }
    }

}
