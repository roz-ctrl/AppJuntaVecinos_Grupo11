package cl.duoc.grupo11.juntavecinos.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.ui.components.BotonesImagen
import cl.duoc.grupo11.juntavecinos.ui.components.ImagenInteligente

@Composable
fun PerfilScreen(
    usuario: Usuario?,
    fotoPerfil: Uri?,
    onFotoSeleccionada: (Uri) -> Unit,
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmarSalida by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Foto de perfil con recursos nativos (cámara o galería)
        ImagenInteligente(uri = fotoPerfil)
        BotonesImagen(prefijoArchivo = "perfil", onImagenSeleccionada = onFotoSeleccionada)

        Text(text = usuario?.nombre.orEmpty(), style = MaterialTheme.typography.headlineSmall)
        Text(text = usuario?.correo.orEmpty(), style = MaterialTheme.typography.bodyMedium)
        Text(
            text = usuario?.rol?.etiqueta.orEmpty(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Los permisos dependen de la subclase de Usuario (polimorfismo)
        Card(modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Permisos de tu perfil", style = MaterialTheme.typography.titleMedium)
                usuario?.permisos()?.forEach { permiso ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = permiso)
                    }
                }
            }
        }

        // Preferencia guardada con DataStore: se mantiene al cerrar la app
        Card(modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.DarkMode, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Modo oscuro", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "Se guarda en el dispositivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = modoOscuro, onCheckedChange = onModoOscuroChange)
            }
        }

        OutlinedButton(onClick = { confirmarSalida = true }) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    onCerrarSesion()
                }) { Text("Salir") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }
}
