package cl.duoc.grupo11.juntavecinos.ui.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

// Muestra la foto en forma circular; si no hay foto, muestra un ícono de perfil por defecto
@Composable
fun ImagenInteligente(
    uri: Uri?,
    modifier: Modifier = Modifier,
    tamano: Dp = 120.dp
) {
    if (uri != null) {
        AsyncImage(
            model = uri,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(tamano)
                .clip(CircleShape)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    } else {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Sin foto de perfil",
            modifier = modifier.size(tamano),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
