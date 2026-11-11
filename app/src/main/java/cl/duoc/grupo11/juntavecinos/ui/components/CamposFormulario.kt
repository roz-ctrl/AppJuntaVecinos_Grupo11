package cl.duoc.grupo11.juntavecinos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.duoc.grupo11.juntavecinos.util.comoTexto
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// Campo de solo lectura que abre un menú desplegable con opciones (genérico: sirve para cualquier tipo)
@Composable
fun <T> SelectorDesplegable(
    etiqueta: String,
    opciones: List<T>,
    seleccionada: T?,
    textoDe: (T) -> String,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    var expandido by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = seleccionada?.let(textoDe).orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente que abre el menú al tocar el campo
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expandido = true }
        )
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoDe(opcion)) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

// Campo de fecha que abre el calendario de Material 3 (DatePicker)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    etiqueta: String,
    fecha: LocalDate?,
    onFechaSeleccionada: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    var mostrarCalendario by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = fecha?.comoTexto().orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            placeholder = { Text("dd/mm/aaaa") },
            trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Abrir calendario") },
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { mostrarCalendario = true }
        )
    }

    if (mostrarCalendario) {
        // El DatePicker trabaja en milisegundos UTC
        val estadoCalendario = rememberDatePickerState(
            initialSelectedDateMillis = fecha?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoCalendario.selectedDateMillis?.let { millis ->
                        onFechaSeleccionada(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoCalendario)
        }
    }
}
