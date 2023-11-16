package com.example.proyecto;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Notificar extends IntentService {

    private static String Titulo = "";

    private static String Contenido = "";
    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;

    public Notificar() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this){
            try {
                wait(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel();
            }
            Titulo = intent.getStringExtra("titulo");
            Contenido = intent.getStringExtra("contenido");
            // Llama al método para mostrar la notificación
            showNotification();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Crea el canal de notificación para dispositivos con Android 8.0 (Oreo) o superior
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Mi Canal de Notificaciones",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        // Configura opciones adicionales del canal (puedes personalizar según tus necesidades)
        channel.setDescription("Descripción del canal");
        channel.enableLights(true);
        channel.enableVibration(true);

        // Registra el canal con el NotificationManager
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void showNotification() {
        // Crea una notificación utilizando NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                // Icono pequeño en la barra de notificaciones
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(Titulo)
                .setContentText(Contenido)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Obtén el NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Muestra la notificación
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}