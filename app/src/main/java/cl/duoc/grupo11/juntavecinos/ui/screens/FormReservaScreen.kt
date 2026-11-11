package cl.duoc.grupo11.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.grupo11.juntavecinos.model.Dependencia
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.ui.components.CampoFecha
import cl.duoc.grupo11.juntavecinos.ui.components.SelectorDesplegable
import cl.duoc.grupo11.juntavecinos.util.comoPesos
import cl.duoc.grupo11.juntavecinos.util.comoTexto
import cl.duoc.grupo11.juntavecinos.util.mostrarNotificacion
import cl.duoc.grupo11.juntavecinos.viewmodel.ReservaViewModel
import java.time.LocalTime

// Horarios disponibles para arrendar la sede
private val horasInicio = (8..22).map { LocalTime.of(it, 0) }
private val horasFin = (9..23).map { LocalTime.of(it, 0) }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormReservaScreen(
    reservaId: Int,
    usuario: Usuario?,
    viewModel: ReservaViewModel,
    onGuardado: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Al abrir la pantalla se carga la reserva a editar (o un formulario vacío).
    // rememberSaveable evita que se borre lo escrito si se rota la pantalla.
    var preparado by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(reservaId) {
        if (!preparado) {
            viewModel.prepararFormulario(reservaId)
            preparado = true
        }
    }
    val form by viewModel.formulario.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .widthIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (form.esEdicion) "Editar solicitud" else "Nueva solicitud de arriendo",
            style = MaterialTheme.typography.titleLarge
        )

        Text(text = "Dependencia", style = MaterialTheme.typography.titleSmall)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Dependencia.entries.forEach { dependencia ->
                FilterChip(
                    selected = form.dependencia == dependencia,
                    onClick = { viewModel.onDependenciaChange(dependencia) },
                    label = { Text("${dependencia.nombre} (${dependencia.tarifaPorHora.comoPesos()}/h)") }
                )
            }
        }

        CampoFecha(
            etiqueta = "Fecha del arriendo",
            fecha = form.fecha,
            onFechaSeleccionada = viewModel::onFechaChange,
            error = form.errores.fecha,
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SelectorDesplegable(
                etiqueta = "Desde",
                opciones = horasInicio,
                seleccionada = form.horaInicio,
                textoDe = { it.comoTexto() },
                onSeleccion = viewModel::onHoraInicioChange,
                modifier = Modifier.weight(1f)
            )
            SelectorDesplegable(
                etiqueta = "Hasta",
                opciones = horasFin,
                seleccionada = form.horaFin,
                textoDe = { it.comoTexto() },
                onSeleccion = viewModel::onHoraFinChange,
                modifier = Modifier.weight(1f)
            )
        }
        form.errores.horario?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = form.motivo,
            onValueChange = viewModel::onMotivoChange,
            label = { Text("Motivo del arriendo") },
            isError = form.errores.motivo != null,
            supportingText = { form.errores.motivo?.let { Text(it) } },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        // El monto se recalcula en tiempo real al cambiar dependencia u horario
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Monto estimado", style = MaterialTheme.typography.labelLarge)
                Text(text = form.montoEstimado.comoPesos(), style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "El pago se registra en tesorería. La app no procesa pagos reales.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Button(
            onClick = {
                val eraEdicion = form.esEdicion
                if (viewModel.guardar(usuario)) {
                    mostrarNotificacion(
                        context,
                        if (eraEdicion) "Solicitud actualizada" else "Solicitud enviada",
                        "Tu solicitud quedó pendiente de aprobación de la directiva"
                    )
                    onGuardado()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (form.esEdicion) "Guardar cambios" else "Enviar solicitud")
        }
    }
}
