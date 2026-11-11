package cl.duoc.grupo11.juntavecinos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.duoc.grupo11.juntavecinos.model.EstadoReserva

// Etiqueta de estado con cambio de color animado (animateColorAsState)
@Composable
fun EstadoReservaChip(estado: EstadoReserva, modifier: Modifier = Modifier) {
    val colorObjetivo = when (estado) {
        EstadoReserva.PENDIENTE -> Color(0xFFF9A825)
        EstadoReserva.CONFIRMADA -> Color(0xFF2E7D32)
        EstadoReserva.RECHAZADA -> Color(0xFFC62828)
        EstadoReserva.CANCELADA -> Color(0xFF757575)
    }
    val color by animateColorAsState(
        targetValue = colorObjetivo,
        animationSpec = tween(durationMillis = 500),
        label = "colorEstadoReserva"
    )
    Surface(
        modifier = modifier,
        color = color,
        contentColor = Color.White,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = estado.etiqueta,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
