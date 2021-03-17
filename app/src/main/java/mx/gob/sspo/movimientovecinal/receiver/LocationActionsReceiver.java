package mx.gob.sspo.movimientovecinal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mx.gob.sspo.movimientovecinal.ServiceShake.LocationService;

public class LocationActionsReceiver extends BroadcastReceiver {

    public static final String ACTION_CANCEL = "com.demo.app.receiver.CANCEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_CANCEL)) {
            Intent service = new Intent(context, LocationService.class);
            context.stopService(service);
        }
    }
}
