package cl.duoc.grupo11.juntavecinos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.grupo11.juntavecinos.model.ResumenFinanciero
import cl.duoc.grupo11.juntavecinos.util.comoPesos

// Tarjeta con ingresos, gastos y saldo (transparencia financiera)
@Composable
fun TarjetaResumen(resumen: ResumenFinanciero, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "Resumen financiero", style = MaterialTheme.typography.titleMedium)
            FilaMonto("Ingresos", resumen.totalIngresos, MaterialTheme.colorScheme.primary)
            FilaMonto("Gastos", resumen.totalGastos, MaterialTheme.colorScheme.error)
            HorizontalDivider()
            FilaMonto(
                etiqueta = "Saldo disponible",
                monto = resumen.saldo,
                color = if (resumen.saldo >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                destacado = true
            )
        }
    }
}

@Composable
private fun FilaMonto(etiqueta: String, monto: Int, color: Color, destacado: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = monto.comoPesos(),
            color = color,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Medium
        )
    }
}
