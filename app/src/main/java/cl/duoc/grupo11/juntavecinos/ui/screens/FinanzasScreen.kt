package cl.duoc.grupo11.juntavecinos.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cl.duoc.grupo11.juntavecinos.model.Movimiento
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.ui.components.GastosPorCategoria
import cl.duoc.grupo11.juntavecinos.ui.components.MensajeExito
import cl.duoc.grupo11.juntavecinos.ui.components.TarjetaMovimiento
import cl.duoc.grupo11.juntavecinos.ui.components.TarjetaResumen
import cl.duoc.grupo11.juntavecinos.viewmodel.FiltroMovimiento
import cl.duoc.grupo11.juntavecinos.viewmodel.FinanzasViewModel
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanzasScreen(
    usuario: Usuario?,
    viewModel: FinanzasViewModel,
    onNuevoMovimiento: () -> Unit,
    modifier: Modifier = Modifier
) {
    val movimientos by viewModel.movimientos.collectAsState()
    val resumen by viewModel.resumen.collectAsState()
    val filtro by viewModel.filtro.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val mostrarMensaje by viewModel.mostrarMensaje.collectAsState()
    val esTesoreria = usuario?.puedeRegistrarMovimientos == true

    var movimientoAEliminar by remember { mutableStateOf<Movimiento?>(null) }
    var comprobanteAVer by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { MensajeExito(visible = mostrarMensaje, texto = mensaje) }

            item {
                Column {
                    Text(text = "Transparencia financiera", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (esTesoreria) {
                            "Registra los ingresos y gastos de la sede con su comprobante."
                        } else {
                            "Información disponible para todos los vecinos. Los montos son ficticios."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item { TarjetaResumen(resumen = resumen) }

            if (resumen.gastosPorCategoria.isNotEmpty()) {
                item { GastosPorCategoria(resumen = resumen) }
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroMovimiento.entries.forEach { opcion ->
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

            if (movimientos.isEmpty()) {
                item { Text(text = "No hay movimientos registrados.") }
            }

            items(movimientos, key = { it.id }) { movimiento ->
                TarjetaMovimiento(
                    movimiento = movimiento,
                    puedeEliminar = esTesoreria,
                    onEliminar = { movimientoAEliminar = movimiento },
                    onVerComprobante = { comprobanteAVer = movimiento.comprobanteUri },
                    modifier = Modifier.animateItem()
                )
            }
        }

        if (esTesoreria) {
            ExtendedFloatingActionButton(
                onClick = onNuevoMovimiento,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Registrar") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }

    movimientoAEliminar?.let { movimiento ->
        AlertDialog(
            onDismissRequest = { movimientoAEliminar = null },
            title = { Text("Eliminar movimiento") },
            text = { Text("¿Eliminar \"${movimiento.descripcion}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(movimiento, usuario)
                    movimientoAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { movimientoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    // Vista ampliada del comprobante
    comprobanteAVer?.let { uri ->
        AlertDialog(
            onDismissRequest = { comprobanteAVer = null },
            title = { Text("Comprobante") },
            text = {
                AsyncImage(
                    model = uri.toUri(),
                    contentDescription = "Foto del comprobante",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            },
            confirmButton = {
                TextButton(onClick = { comprobanteAVer = null }) { Text("Cerrar") }
            }
        )
    }
}
