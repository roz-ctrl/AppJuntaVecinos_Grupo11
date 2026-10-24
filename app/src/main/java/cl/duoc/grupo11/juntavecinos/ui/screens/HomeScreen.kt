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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.grupo11.juntavecinos.model.Reserva
import cl.duoc.grupo11.juntavecinos.repository.JuntaRepository
import cl.duoc.grupo11.juntavecinos.ui.theme.AppJuntaVecinosTheme
import cl.duoc.grupo11.juntavecinos.viewmodel.HomeUiState
import cl.duoc.grupo11.juntavecinos.viewmodel.HomeViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

// Formatea montos en pesos chilenos, por ejemplo: $25.000
private fun formatearPesos(monto: Int): String =
    "$" + String.format(Locale("es", "CL"), "%,d", monto)

// Pantalla de inicio: observa el estado del ViewModel
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val estado by viewModel.estado.collectAsState()
    HomeContent(estado = estado)
}

// Contenido visual sin lógica: recibe el estado ya calculado
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(estado: HomeUiState) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Junta de Vecinos Zaror") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { ResumenFinanciero(estado) }
            item {
                Text(
                    text = "Próximas reservas",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(estado.proximasReservas) { reserva ->
                ReservaItem(reserva)
            }
        }
    }
}

// Tarjeta con ingresos, gastos y saldo (transparencia financiera)
@Composable
private fun ResumenFinanciero(estado: HomeUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Resumen del mes", style = MaterialTheme.typography.titleMedium)
            FilaMonto(etiqueta = "Ingresos", monto = estado.totalIngresos)
            FilaMonto(etiqueta = "Gastos", monto = estado.totalGastos)
            FilaMonto(etiqueta = "Saldo", monto = estado.saldo, destacado = true)
        }
    }
}

@Composable
private fun FilaMonto(etiqueta: String, monto: Int, destacado: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = etiqueta)
        Text(
            text = formatearPesos(monto),
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
            color = if (destacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ReservaItem(reserva: Reserva) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = reserva.dependencia, style = MaterialTheme.typography.titleSmall)
            Text(text = "${reserva.fecha.format(formatoFecha)} · ${reserva.horaInicio} a ${reserva.horaFin}")
            Text(
                text = "Estado: ${reserva.estado.name.lowercase()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val repository = JuntaRepository()
    AppJuntaVecinosTheme {
        HomeContent(
            estado = HomeUiState(
                totalIngresos = 40000,
                totalGastos = 27700,
                proximasReservas = repository.obtenerReservas()
            )
        )
    }
}
