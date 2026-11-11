package cl.duoc.grupo11.juntavecinos.model

// Totales que se muestran a los vecinos (transparencia financiera)
data class ResumenFinanciero(
    val totalIngresos: Int = 0,
    val totalGastos: Int = 0,
    val gastosPorCategoria: Map<CategoriaMovimiento, Int> = emptyMap()
) {
    val saldo: Int get() = totalIngresos - totalGastos

    companion object {
        // Calcula el resumen a partir de la lista de movimientos
        fun desde(movimientos: List<Movimiento>): ResumenFinanciero {
            val gastos = movimientos.filter { it.tipo == TipoMovimiento.GASTO }
            return ResumenFinanciero(
                totalIngresos = movimientos
                    .filter { it.tipo == TipoMovimiento.INGRESO }
                    .sumOf { it.monto },
                totalGastos = gastos.sumOf { it.monto },
                gastosPorCategoria = gastos
                    .groupBy { it.categoria }
                    .mapValues { (_, lista) -> lista.sumOf { it.monto } }
            )
        }
    }
}
