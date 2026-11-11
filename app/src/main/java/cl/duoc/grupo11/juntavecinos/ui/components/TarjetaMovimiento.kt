package cl.duoc.grupo11.juntavecinos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.grupo11.juntavecinos.model.Movimiento
import cl.duoc.grupo11.juntavecinos.model.TipoMovimiento
import cl.duoc.grupo11.juntavecinos.util.comoPesos
import cl.duoc.grupo11.juntavecinos.util.comoTexto

// Tarjeta de un ingreso o gasto. El botón de eliminar solo aparece para tesorería.
@Composable
fun TarjetaMovimiento(
    movimiento: Movimiento,
    puedeEliminar: Boolean,
    onEliminar: () -> Unit,
    onVerComprobante: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    val color = if (esIngreso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = color.copy(alpha = 0.15f)) {
                Icon(
                    imageVector = if (esIngreso) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = movimiento.tipo.etiqueta,
                    tint = color,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = movimiento.categoria.etiqueta, style = MaterialTheme.typography.titleSmall)
                Text(text = movimiento.descripcion, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${movimiento.fecha.comoTexto()} · ${movimiento.registradoPor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (movimiento.comprobanteUri != null) {
                    TextButton(onClick = onVerComprobante, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Filled.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver comprobante")
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (esIngreso) "+" else "-") + movimiento.monto.comoPesos(),
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
                if (puedeEliminar) {
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar movimiento")
                    }
                }
            }
        }
    }
}
