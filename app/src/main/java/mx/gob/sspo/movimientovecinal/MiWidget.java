package mx.gob.sspo.movimientovecinal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import mx.gob.sspo.movimientovecinal.ui.slideshow.SlideshowFragment;
import mx.gob.sspo.movimientovecinal.ui.transporte.Transporte;

/**
 * Implementation of App Widget functionality.
 */
public class MiWidget extends AppWidgetProvider {
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int cargarInfoViolenciaWidget,wViolencia,cargarInfoWviolencia;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        System.out.println("SE CREA EL HILO POR PRIMERA VEZ");
        share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoViolenciaWidget= share.getInt("VIOLENCIA", 0);

        if(cargarInfoViolenciaWidget == 1) {
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                System.out.println(cargarInfoViolenciaWidget);
                Intent intent = new Intent(context, AltoALaViolencia.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget);
                views.setOnClickPendingIntent(R.id.imagen_widget, pendingIntent);
                views.setImageViewResource(R.id.imagen_widget, R.drawable.ic_altoviolencia);
                System.out.println(appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }else if(cargarInfoViolenciaWidget != 1) {
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                wViolencia = 1;
                share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
                editor = share.edit();
                editor.putInt("VIOLENCIA", wViolencia);
                editor.commit();
                System.out.println(cargarInfoViolenciaWidget);
                Intent intent = new Intent(context, AltoALaViolencia.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget);
                views.setOnClickPendingIntent(R.id.imagen_widget, pendingIntent);
                views.setImageViewResource(R.id.imagen_widget, R.drawable.ic_altoviolencia);
                System.out.println(appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, views);


            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.out.println("SE EJECUTA AL HACER CLICK VIOLENCIA");
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
        cargarInfoWviolencia = share.getInt("VIOLENCIA", 0);
        if (cargarInfoWviolencia == 1) {
            share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
            editor = share.edit();
            editor.remove("VIOLENCIA").commit();
            System.out.println("SE ELIMINA WIDGET VIOLENCIA");
        }
        super.onDeleted(context, appWidgetIds);
    }
}

