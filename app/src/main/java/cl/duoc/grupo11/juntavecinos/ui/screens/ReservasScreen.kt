package cl.duoc.grupo11.juntavecinos.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.grupo11.juntavecinos.model.EstadoReserva
import cl.duoc.grupo11.juntavecinos.model.Reserva
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.ui.components.MensajeExito
import cl.duoc.grupo11.juntavecinos.ui.components.TarjetaReserva
import cl.duoc.grupo11.juntavecinos.viewmodel.FiltroReserva
import cl.duoc.grupo11.juntavecinos.viewmodel.ReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    usuario: Usuario?,
    viewModel: ReservaViewModel,
    onNuevaReserva: () -> Unit,
    onEditarReserva: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val reservas by viewModel.reservas.collectAsState()
    val filtro by viewModel.filtro.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val mostrarMensaje by viewModel.mostrarMensaje.collectAsState()
    var reservaAEliminar by remember { mutableStateOf<Reserva?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { MensajeExito(visible = mostrarMensaje, texto = mensaje) }

            // Filtros por estado
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroReserva.entries.forEach { opcion ->
                        FilterChip(
                            selected = filtro == opcion,
                            onClick = { viewModel.cambiarFiltro(opcion) },
                            label = { Text(opcion.etiqueta) },
                            leadingIcon = if (filtro == opcion) {
                                { Icon(Icons.Filled.Check, contentDescription = null) }
                            } else {
                                null
                            }
                        )
                    }
                }
            }

            if (reservas.isEmpty()) {
                item {
                    Text(
                        text = "No hay reservas en esta categoría.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }

            items(reservas, key = { it.id }) { reserva ->
                TarjetaReserva(reserva = reserva, modifier = Modifier.animateItem()) {
                    AccionesReserva(
                        reserva = reserva,
                        usuario = usuario,
                        viewModel = viewModel,
                        onEditar = onEditarReserva,
                        onEliminar = { reservaAEliminar = it }
                    )
                }
            }
        }

        // Solo los perfiles que pueden solicitar reservas ven el botón flotante
        if (usuario?.puedeSolicitarReservas == true) {
            ExtendedFloatingActionButton(
                onClick = onNuevaReserva,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Solicitar") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }

    // Confirmación antes de eliminar
    reservaAEliminar?.let { reserva ->
        AlertDialog(
            onDismissRequest = { reservaAEliminar = null },
            title = { Text("Eliminar reserva") },
            text = { Text("¿Eliminar del historial la reserva de ${reserva.solicitante} (${reserva.dependencia.nombre})?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(reserva, usuario)
                    reservaAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { reservaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

// Botones de cada tarjeta: dependen del perfil y del estado de la reserva
@Composable
private fun RowScope.AccionesReserva(
    reserva: Reserva,
    usuario: Usuario?,
    viewModel: ReservaViewModel,
    onEditar: (Int) -> Unit,
    onEliminar: (Reserva) -> Unit
) {
    val esSolicitante = usuario?.id == reserva.solicitanteId
    val esPendiente = reserva.estado == EstadoReserva.PENDIENTE

    if (esSolicitante && esPendiente) {
        TextButton(onClick = { viewModel.cancelar(reserva, usuario) }) { Text("Cancelar") }
        TextButton(onClick = { onEditar(reserva.id) }) { Text("Editar") }
    }
    if (usuario?.puedeGestionarReservas == true && esPendiente) {
        OutlinedButton(onClick = { viewModel.rechazar(reserva, usuario) }) { Text("Rechazar") }
        Button(onClick = { viewModel.aprobar(reserva, usuario) }) { Text("Aprobar") }
    }
    if (usuario?.puedeRegistrarMovimientos == true && reserva.estado == EstadoReserva.CONFIRMADA && !reserva.pagada) {
        Button(onClick = { viewModel.registrarPago(reserva, usuario) }) { Text("Registrar pago") }
    }
    if (usuario?.puedeGestionarReservas == true && !reserva.estaVigente) {
        IconButton(onClick = { onEliminar(reserva) }) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar reserva")
        }
    }
}
