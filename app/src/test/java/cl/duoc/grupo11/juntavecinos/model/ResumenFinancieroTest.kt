package cl.duoc.grupo11.juntavecinos.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ResumenFinancieroTest {

    private val hoy = LocalDate.of(2026, 11, 11)

    private val movimientos = listOf(
        Movimiento(1, TipoMovimiento.INGRESO, CategoriaMovimiento.ARRIENDO, "Arriendo", 25_000, hoy, "Tesorería"),
        Movimiento(2, TipoMovimiento.INGRESO, CategoriaMovimiento.CUOTA_SOCIAL, "Cuotas", 45_000, hoy, "Tesorería"),
        Movimiento(3, TipoMovimiento.GASTO, CategoriaMovimiento.LUZ, "Luz", 18_500, hoy, "Tesorería"),
        Movimiento(4, TipoMovimiento.GASTO, CategoriaMovimiento.LUZ, "Luz atrasada", 1_500, hoy, "Tesorería"),
        Movimiento(5, TipoMovimiento.GASTO, CategoriaMovimiento.AGUA, "Agua", 9_200, hoy, "Tesorería")
    )

    @Test
    fun `calcula ingresos, gastos y saldo`() {
        val resumen = ResumenFinanciero.desde(movimientos)
        assertEquals(70_000, resumen.totalIngresos)
        assertEquals(29_200, resumen.totalGastos)
        assertEquals(40_800, resumen.saldo)
    }

    @Test
    fun `agrupa los gastos por categoría`() {
        val resumen = ResumenFinanciero.desde(movimientos)
        assertEquals(20_000, resumen.gastosPorCategoria[CategoriaMovimiento.LUZ])
        assertEquals(9_200, resumen.gastosPorCategoria[CategoriaMovimiento.AGUA])
        assertEquals(2, resumen.gastosPorCategoria.size)
    }

    @Test
    fun `sin movimientos el saldo es cero`() {
        val resumen = ResumenFinanciero.desde(emptyList())
        assertEquals(0, resumen.saldo)
    }

    @Test
    fun `el monto con signo es negativo para los gastos`() {
        assertEquals(25_000, movimientos[0].montoConSigno)
        assertEquals(-18_500, movimientos[2].montoConSigno)
    }
}
