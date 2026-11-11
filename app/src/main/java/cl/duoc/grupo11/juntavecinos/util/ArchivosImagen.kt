package cl.duoc.grupo11.juntavecinos.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Carpeta interna de la app donde se guardan las fotos (declarada en res/xml/file_paths.xml)
private fun carpetaImagenes(context: Context): File =
    File(context.filesDir, "imagenes").apply { mkdirs() }

private fun uriDe(context: Context, archivo: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

// Crea un archivo vacío y devuelve su Uri para que la app de cámara guarde ahí la foto
fun crearUriParaFoto(context: Context, prefijo: String): Uri {
    val archivo = File.createTempFile("${prefijo}_", ".jpg", carpetaImagenes(context))
    return uriDe(context, archivo)
}

// Copia una imagen de la galería al almacenamiento interno de la app,
// así sigue disponible aunque se pierda el permiso temporal de la galería
fun copiarImagenAlmacenamientoInterno(context: Context, origen: Uri, prefijo: String): Uri? =
    runCatching {
        val destino = File.createTempFile("${prefijo}_", ".jpg", carpetaImagenes(context))
        context.contentResolver.openInputStream(origen)?.use { entrada ->
            destino.outputStream().use { salida -> entrada.copyTo(salida) }
        } ?: return null
        uriDe(context, destino)
    }.getOrNull()
