package cl.duoc.grupo11.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.grupo11.juntavecinos.model.ResumenFinanciero
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.model.Vecino
import cl.duoc.grupo11.juntavecinos.repository.MovimientoRepositoryMemoria
import cl.duoc.grupo11.juntavecinos.repository.ReservaRepositoryMemoria
import cl.duoc.grupo11.juntavecinos.ui.components.TarjetaReserva
import cl.duoc.grupo11.juntavecinos.ui.components.TarjetaResumen
import cl.duoc.grupo11.juntavecinos.ui.theme.AppJuntaVecinosTheme
import cl.duoc.grupo11.juntavecinos.viewmodel.HomeUiState
import cl.duoc.grupo11.juntavecinos.viewmodel.HomeViewModel

// Pantalla de inicio: observa el estado del ViewModel
@Composable
fun HomeScreen(
    usuario: Usuario?,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onVerReservas: () -> Unit = {},
    onNuevaReserva: () -> Unit = {},
    onVerFinanzas: () -> Unit = {},
    onNuevoMovimiento: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()
    HomeContent(
        estado = estado,
        usuario = usuario,
        modifier = modifier,
        onVerReservas = onVerReservas,
        onNuevaReserva = onNuevaReserva,
        onVerFinanzas = onVerFinanzas,
        onNuevoMovimiento = onNuevoMovimiento
    )
}

// Contenido visual sin lógica: recibe el estado ya calculado
@Composable
fun HomeContent(
    estado: HomeUiState,
    usuario: Usuario?,
    modifier: Modifier = Modifier,
    onVerReservas: () -> Unit = {},
    onNuevaReserva: () -> Unit = {},
    onVerFinanzas: () -> Unit = {},
    onNuevoMovimiento: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Hola, ${usuario?.nombre ?: "vecino/a"}",
                    style = MaterialTheme.typography.headlineSmall
                )
                usuario?.let {
                    Text(
                        text = "Perfil: ${it.rol.etiqueta}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Aviso solo para la directiva: solicitudes por revisar
        if (usuario?.puedeGestionarReservas == true && estado.reservasPendientes > 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tienes ${estado.reservasPendientes} solicitud(es) por revisar",
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onVerReservas) { Text("Revisar") }
                    }
                }
            }
        }

        // Accesos rápidos según los permisos del perfil
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (usuario?.puedeSolicitarReservas == true) {
                    Button(onClick = onNuevaReserva) { Text("Solicitar reserva") }
                }
                if (usuario?.puedeRegistrarMovimientos == true) {
                    Button(onClick = onNuevoMovimiento) { Text("Registrar movimiento") }
                }
                FilledTonalButton(onClick = onVerFinanzas) { Text("Ver finanzas") }
            }
        }

        item { TarjetaResumen(resumen = estado.resumen) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Próximas reservas", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onVerReservas) { Text("Ver todas") }
            }
        }

        if (estado.proximasReservas.isEmpty()) {
            item { Text(text = "No hay reservas próximas.") }
        }
        items(estado.proximasReservas, key = { it.id }) { reserva ->
            TarjetaReserva(reserva = reserva)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppJuntaVecinosTheme {
        HomeContent(
            estado = HomeUiState(
                resumen = ResumenFinanciero.desde(MovimientoRepositoryMemoria.movimientos.value),
                proximasReservas = ReservaRepositoryMemoria.reservas.value.take(2),
                reservasPendientes = 1
            ),
            usuario = Vecino(1, "María Pérez", "vecino@zaror.cl", "vecino123")
        )
    }
}
