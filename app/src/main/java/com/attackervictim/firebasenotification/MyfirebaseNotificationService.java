package com.attackervictim.firebasenotification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Objects;

public class MyfirebaseNotificationService extends FirebaseMessagingService{
    public static final String TAG ="FIREBASE";
    private static final String CHANNEL_ID = "CHANNEL_SAMPLE";
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message Notification Title: " + Objects.requireNonNull(remoteMessage.getNotification()).getTitle());
        Log.d(TAG, "Message Notification Body: " + Objects.requireNonNull(remoteMessage.getNotification()).getBody());
        createNotificationChannel();
        lanzarNotificacion(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }
    public void lanzarNotificacion(String title,String body){

        Intent i=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,NOTIFICATION_ID,i, PendingIntent.FLAG_IMMUTABLE);

        Uri sonido= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificacion=new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body) // Aquí usamos el parámetro body
                .setSound(sonido)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager notificationManager=(NotificationManager)  getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,notificacion.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sample Channel";
            String description = "This is a sample notification channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Guardar token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
       /* // Aquí asumimos que tienes una forma de obtener el usuario actual.
        // Esto dependerá de cómo esté configurada tu aplicación.
        UsuarioDto currentUser = getCurrentUser();

        if (currentUser != null) {
            // Establece el nuevo token
            currentUser.setFCMToken(token);

            // Actualiza el usuario en la base de datos
            UsuarioService usuarioService = new UsuarioService();
            usuarioService.updateUsuario(currentUser);
        }*/
    }




}
