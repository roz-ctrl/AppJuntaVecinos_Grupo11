package cl.duoc.grupo11.juntavecinos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.grupo11.juntavecinos.model.ResumenFinanciero
import cl.duoc.grupo11.juntavecinos.util.comoPesos

// Rendición de cuentas: en qué se gastó el dinero, con barras proporcionales animadas
@Composable
fun GastosPorCategoria(resumen: ResumenFinanciero, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Rendición de gastos por categoría", style = MaterialTheme.typography.titleMedium)
            resumen.gastosPorCategoria.entries
                .sortedByDescending { it.value }
                .forEach { (categoria, monto) ->
                    val fraccion = if (resumen.totalGastos > 0) monto.toFloat() / resumen.totalGastos else 0f
                    val progreso by animateFloatAsState(
                        targetValue = fraccion,
                        animationSpec = tween(durationMillis = 800),
                        label = "progresoGasto"
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = categoria.etiqueta, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${monto.comoPesos()} (${(fraccion * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.error,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                    }
                }
        }
    }
}
