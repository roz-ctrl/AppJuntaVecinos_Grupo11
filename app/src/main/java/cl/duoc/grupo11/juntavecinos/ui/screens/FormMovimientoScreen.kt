package cl.duoc.grupo11.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cl.duoc.grupo11.juntavecinos.model.TipoMovimiento
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.ui.components.BotonesImagen
import cl.duoc.grupo11.juntavecinos.ui.components.CampoFecha
import cl.duoc.grupo11.juntavecinos.ui.components.SelectorDesplegable
import cl.duoc.grupo11.juntavecinos.viewmodel.FinanzasViewModel
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormMovimientoScreen(
    usuario: Usuario?,
    viewModel: FinanzasViewModel,
    onGuardado: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Se limpia el formulario solo al entrar (no al rotar la pantalla)
    var preparado by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!preparado) {
            viewModel.limpiarFormulario()
            preparado = true
        }
    }
    val form by viewModel.formulario.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .widthIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Tipo de movimiento", style = MaterialTheme.typography.titleSmall)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            TipoMovimiento.entries.forEachIndexed { indice, tipo ->
                SegmentedButton(
                    selected = form.tipo == tipo,
                    onClick = { viewModel.onTipoChange(tipo) },
                    shape = SegmentedButtonDefaults.itemShape(index = indice, count = TipoMovimiento.entries.size)
                ) {
                    Text(tipo.etiqueta)
                }
            }
        }

        SelectorDesplegable(
            etiqueta = "Categoría",
            opciones = form.categoriasDisponibles,
            seleccionada = form.categoria,
            textoDe = { it.etiqueta },
            onSeleccion = viewModel::onCategoriaChange,
            error = form.errores.categoria,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.descripcion,
            onValueChange = viewModel::onDescripcionChange,
            label = { Text("Descripción") },
            isError = form.errores.descripcion != null,
            supportingText = { form.errores.descripcion?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.monto,
            onValueChange = viewModel::onMontoChange,
            label = { Text("Monto") },
            prefix = { Text("$ ") },
            isError = form.errores.monto != null,
            supportingText = { form.errores.monto?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        CampoFecha(
            etiqueta = "Fecha",
            fecha = form.fecha,
            onFechaSeleccionada = viewModel::onFechaChange,
            error = form.errores.fecha,
            modifier = Modifier.fillMaxWidth()
        )

        // Comprobante con recurso nativo: cámara o galería
        Text(text = "Comprobante (boleta o recibo)", style = MaterialTheme.typography.titleSmall)
        Text(
            text = if (form.tipo == TipoMovimiento.GASTO) "Obligatorio para los gastos." else "Opcional para los ingresos.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BotonesImagen(
            prefijoArchivo = "comprobante",
            onImagenSeleccionada = { uri -> viewModel.onComprobanteChange(uri.toString()) }
        )
        form.comprobanteUri?.let { uri ->
            AsyncImage(
                model = uri.toUri(),
                contentDescription = "Vista previa del comprobante",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            TextButton(onClick = { viewModel.onComprobanteChange(null) }) { Text("Quitar foto") }
        }
        form.errores.comprobante?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = { if (viewModel.guardar(usuario)) onGuardado() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar movimiento")
        }
    }
}
