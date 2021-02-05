package mx.gob.sspo.movimientovecinal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import mx.gob.sspo.movimientovecinal.ui.slideshow.SlideshowFragment;
import mx.gob.sspo.movimientovecinal.ui.transporte.Transporte;

/**
 * Implementation of App Widget functionality.
 */
public class MiWidget extends AppWidgetProvider {
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int cargarInfoViolenciaWidget,cargarInfoTransporteWidget,wTransporte,wViolencia,cargarInfoWtransporte,cargarInfoWviolencia,valorWidget;
    String cargarInfoPlacaTransporte;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        System.out.println("SE CREA EL HILO POR PRIMERA VEZ");
        share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoTransporteWidget = share.getInt("TRANSPORTE", 0);
        cargarInfoViolenciaWidget= share.getInt("VIOLENCIA", 0);
        cargarInfoPlacaTransporte = share.getString("PLACA", "SIN INFORMACION");

        if(cargarInfoTransporteWidget == 1) {
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                System.out.println(cargarInfoTransporteWidget);
                Intent intent = new Intent(context, TransporteSeguro.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                //PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget);
                views.setOnClickPendingIntent(R.id.imagen_widget, pendingIntent);
                if (Transporte.num_imag_transporte == 0) {
                   views.setImageViewResource(R.id.imagen_widget, R.drawable.ic_destino);
                }
                System.out.println(appWidgetId);
                wTransporte = 1;
                share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
                editor = share.edit();
                editor.putInt("WTRANSPORTE", wTransporte ).commit();
                editor.remove("TRANSPORTE").commit();
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
        if(cargarInfoViolenciaWidget == 2) {
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                System.out.println(cargarInfoViolenciaWidget);
                Intent intent = new Intent(context, AltoALaViolencia.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                //PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget);
                views.setOnClickPendingIntent(R.id.imagen_widget, pendingIntent);
                if (SlideshowFragment.num_imag_violencia == 0) {
                    views.setImageViewResource(R.id.imagen_widget, R.drawable.ic_altoviolencia);
                }
                System.out.println(appWidgetId);
                wViolencia = 1;
                share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
                editor = share.edit();
                editor.putInt("WVIOLENCIA", wViolencia ).commit();
                editor.remove("VIOLENCIA").commit();
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.out.println("SE EJECUTA AL HACER CLICK");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        System.out.println("SE QUITA LA INSTANCIA DEL WIDGET");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoWtransporte = share.getInt("WTRANSPORTE", 0);
        cargarInfoWviolencia = share.getInt("WVIOLENCIA", 0);
        if(cargarInfoWtransporte == 1){
            share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
            editor = share.edit();
            editor.remove("WTRANSPORTE").commit();
            System.out.println("SE ELIMINA WIDGET TRANSPORTE");
        }else if (cargarInfoWviolencia == 1) {
            share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
            editor = share.edit();
            editor.remove("WVIOLENCIA").commit();
            System.out.println("SE ELIMINA WIDGET VIOLENCIA");
        }
        super.onDeleted(context, appWidgetIds);
    }
}

