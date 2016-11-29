package cl.aterbonus.multas.utilidades;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import cl.aterbonus.multas.R;

/**
 * Created by aterbonus on 28-11-16.
 */

public class Helper {

    public static void toast(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }

    public static void notificacion(Context context, String title, String contentText) {
        notificacion(context, title, contentText, new Intent(context, context.getClass()));

    }

    private static void notificacion(Context context, String title, String contentText, Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(contentText);

        PendingIntent contIntent = PendingIntent.getActivity(
                context, 0, intent, 0);

        mBuilder.setContentIntent(contIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify("LA TERRIBLE NOTIFICACION".hashCode(), mBuilder.build());
    }

    public static void notificacionRoll(Context context, String title, String contentText) {
        notificacion(context, title, contentText, new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ")));
    }
}
