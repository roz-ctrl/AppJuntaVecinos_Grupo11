package cl.duoc.grupo11.juntavecinos.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.duoc.grupo11.juntavecinos.R

private const val CANAL_AVISOS = "avisos_junta"

// El canal se crea una vez al iniciar la app (requisito desde Android 8)
fun crearCanalNotificaciones(context: Context) {
    val canal = NotificationChannel(
        CANAL_AVISOS,
        "Reservas y pagos",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Avisos de confirmación de reservas, pagos y recordatorios"
    }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
}

// Desde Android 13 hay que pedir permiso para mostrar notificaciones
fun tienePermisoNotificaciones(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

// Muestra una notificación local; si no hay permiso, simplemente no se muestra
@SuppressLint("MissingPermission")
fun mostrarNotificacion(context: Context, titulo: String, texto: String) {
    if (!tienePermisoNotificaciones(context)) return
    val notificacion = NotificationCompat.Builder(context, CANAL_AVISOS)
        .setSmallIcon(R.drawable.ic_notificacion)
        .setContentTitle(titulo)
        .setContentText(texto)
        .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notificacion)
}
