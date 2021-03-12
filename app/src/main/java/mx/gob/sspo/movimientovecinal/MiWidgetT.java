package mx.gob.sspo.movimientovecinal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import mx.gob.sspo.movimientovecinal.ServiceShake.Service911TS;
import mx.gob.sspo.movimientovecinal.ui.transporte.Transporte;

/**
 * Implementation of App Widget functionality.
 */
public class MiWidgetT extends AppWidgetProvider {
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int cargarInfoTransporteWidget,wTransporteSeguroView;
    String cargarInfoPlacaTransporte;

    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private Service911TS mSensorService;
    Context ctx;
    AppWidgetManager manager;
    View view;

    public Context getCtx() {
        return ctx;
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        //Construct the RemoteViews object
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget_t);
        //views.setTextViewText(R.id.imagen_widgetT, widgetText);

        //Instruct the widget manager to update the widget
        //appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
            System.out.println("SE CREA EL HILO POR PRIMERA VEZ TRANSPORTE");
            share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
            cargarInfoTransporteWidget = share.getInt("TRANSPORTE", 0);
            cargarInfoPlacaTransporte = share.getString("PLACA", "SIN INFORMACION");

        if(cargarInfoTransporteWidget == 1) {
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                System.out.println(cargarInfoTransporteWidget);
                Intent intent = new Intent(context, TransporteSeguro.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget_t);
                views.setOnClickPendingIntent(R.id.imagen_widgetT, pendingIntent);
                //if (Transporte.num_imag_transporte == 0) {
                views.setImageViewResource(R.id.imagen_widgetT, R.drawable.ic_destino);
                //}
                System.out.println(appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }else if(cargarInfoTransporteWidget != 1) {
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                int appWidgetId = appWidgetIds[i];
                wTransporteSeguroView = 1;
                share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
                editor = share.edit();
                editor.putInt("TRANSPORTE", wTransporteSeguroView);
                editor.commit();
                System.out.println(cargarInfoTransporteWidget);
                Intent intent = new Intent(context, TransporteSeguro.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget_t);
                views.setOnClickPendingIntent(R.id.imagen_widgetT, pendingIntent);
                //if (Transporte.num_imag_transporte == 0) {
                views.setImageViewResource(R.id.imagen_widgetT, R.drawable.ic_destino);
                //}
                System.out.println(appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }


            //updateAppWidget(context, appWidgetManager, appWidgetId);

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.out.println("SE EJECUTA AL HACER CLICK TRANSPORTE");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        System.out.println("SE QUITA LA INSTANCIA DEL WIDGET TRANSPORTE");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoTransporteWidget = share.getInt("TRANSPORTE", 0);
        if(cargarInfoTransporteWidget == 1){
            share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
            editor = share.edit();
            editor.remove("TRANSPORTE").commit();
            System.out.println("SE ELIMINA WIDGET TRANSPORTE");
        }
        super.onDeleted(context, appWidgetIds);
    }

}
