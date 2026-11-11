package cl.duoc.grupo11.juntavecinos.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.grupo11.juntavecinos.util.copiarImagenAlmacenamientoInterno
import cl.duoc.grupo11.juntavecinos.util.crearUriParaFoto

// Botones para obtener una imagen con los recursos nativos del dispositivo:
// cámara (TakePicture) y galería (GetContent), usando ActivityResult API
@Composable
fun BotonesImagen(
    prefijoArchivo: String,
    onImagenSeleccionada: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Uri donde la cámara guardará la foto (rememberSaveable: sobrevive si Android recrea la pantalla)
    var uriFotoPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }

    val lanzadorCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        // Si el usuario cancela, exito es false y no se hace nada (la app no se cae)
        if (exito) uriFotoPendiente?.let(onImagenSeleccionada)
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { copiarImagenAlmacenamientoInterno(context, it, prefijoArchivo) }
            ?.let(onImagenSeleccionada)
    }

    fun abrirCamara() {
        val uri = crearUriParaFoto(context, prefijoArchivo)
        uriFotoPendiente = uri
        lanzadorCamara.launch(uri)
    }

    // La cámara es un permiso peligroso: se solicita en tiempo de ejecución
    val lanzadorPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            Toast.makeText(context, "Se necesita el permiso de cámara para tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = {
            val tienePermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            if (tienePermiso) abrirCamara() else lanzadorPermiso.launch(Manifest.permission.CAMERA)
        }) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cámara")
        }
        OutlinedButton(onClick = { lanzadorGaleria.launch("image/*") }) {
            Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Galería")
        }
    }
}
