package mx.gob.sspo.movimientovecinal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import mx.gob.sspo.movimientovecinal.ui.slideshow.SlideshowFragment;

/**
 * Implementation of App Widget functionality.
 */
public class MiWidgetVV extends AppWidgetProvider {
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int cargarInfoAlertaVecinalWidget,wAlertaVecinal, cargarInfoWalertavecinal;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        System.out.println("SE CREA EL HILO POR PRIMERA VEZ");
        share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoAlertaVecinalWidget= share.getInt("ALERTAVECINAL", 0);

        if(cargarInfoAlertaVecinalWidget == 1) {
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                System.out.println(cargarInfoAlertaVecinalWidget);
                Intent intent = new Intent(context, VigilanciaVecinal.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget_v_v);
                views.setOnClickPendingIntent(R.id.imagen_widgetVV, pendingIntent);
                views.setImageViewResource(R.id.imagen_widgetVV, R.drawable.ic_vigilancia_vecinal);
                System.out.println(appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }else if(cargarInfoAlertaVecinalWidget != 1) {
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                wAlertaVecinal = 1;
                share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
                editor = share.edit();
                editor.putInt("ALERTAVECINAL", wAlertaVecinal);
                editor.commit();
                System.out.println(cargarInfoAlertaVecinalWidget);
                Intent intent = new Intent(context, VigilanciaVecinal.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget_v_v);
                views.setOnClickPendingIntent(R.id.imagen_widgetVV, pendingIntent);
                views.setImageViewResource(R.id.imagen_widgetVV, R.drawable.ic_vigilancia_vecinal);
                System.out.println(appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.out.println("SE EJECUTA AL HACER CLICK ALERTA VECINAL");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        System.out.println("SE QUITA LA INSTANCIA DEL WIDGET VIOLENCIA");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoWalertavecinal = share.getInt("ALERTAVECINAL", 0);
        if (cargarInfoWalertavecinal == 1) {
            share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
            editor = share.edit();
            editor.remove("ALERTAVECINAL").commit();
            System.out.println("SE ELIMINA WIDGET ALERTAVECINAL");
        }
        super.onDeleted(context, appWidgetIds);
    }
}