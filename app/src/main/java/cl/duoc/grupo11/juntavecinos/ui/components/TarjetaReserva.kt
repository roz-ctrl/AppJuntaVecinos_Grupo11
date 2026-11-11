package cl.duoc.grupo11.juntavecinos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.grupo11.juntavecinos.model.Reserva
import cl.duoc.grupo11.juntavecinos.util.comoPesos
import cl.duoc.grupo11.juntavecinos.util.comoTexto
import cl.duoc.grupo11.juntavecinos.util.comoTextoLargo

// Tarjeta reutilizable de una reserva. Las acciones (botones) dependen del perfil
// y las define cada pantalla.
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TarjetaReserva(
    reserva: Reserva,
    modifier: Modifier = Modifier,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = reserva.dependencia.nombre, style = MaterialTheme.typography.titleMedium)
                EstadoReservaChip(reserva.estado)
            }
            Text(
                text = "${reserva.fecha.comoTextoLargo()} · ${reserva.horaInicio.comoTexto()} a ${reserva.horaFin.comoTexto()}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = "Solicitante: ${reserva.solicitante}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Motivo: ${reserva.motivo}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "${reserva.horas} h · ${reserva.monto.comoPesos()}" + if (reserva.pagada) " · Pagada" else "",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            // FlowRow: si los botones no caben en una línea, pasan a la siguiente
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                acciones()
            }
        }
    }
}
