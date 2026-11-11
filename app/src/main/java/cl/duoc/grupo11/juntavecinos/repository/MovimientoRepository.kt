package cl.duoc.grupo11.juntavecinos.repository

import cl.duoc.grupo11.juntavecinos.model.CategoriaMovimiento
import cl.duoc.grupo11.juntavecinos.model.Movimiento
import cl.duoc.grupo11.juntavecinos.model.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

// Contrato del origen de datos de ingresos y gastos
interface MovimientoRepository {
    val movimientos: StateFlow<List<Movimiento>>
    fun agregar(movimiento: Movimiento): Movimiento
    fun eliminar(id: Int)
}

// Implementación en memoria (singleton) compartida por todas las pantallas
object MovimientoRepositoryMemoria : MovimientoRepository {

    private val _movimientos = MutableStateFlow(datosIniciales())
    override val movimientos: StateFlow<List<Movimiento>> = _movimientos.asStateFlow()

    private var siguienteId = _movimientos.value.maxOf { it.id } + 1

    override fun agregar(movimiento: Movimiento): Movimiento {
        val nuevo = movimiento.copy(id = siguienteId++)
        _movimientos.update { it + nuevo }
        return nuevo
    }

    override fun eliminar(id: Int) {
        _movimientos.update { lista -> lista.filterNot { it.id == id } }
    }

    // Montos ficticios: no corresponden a información real de la Junta de Vecinos
    private fun datosIniciales(): List<Movimiento> {
        val hoy = LocalDate.now()
        val tesoreria = "Ana Fuentes"
        return listOf(
            Movimiento(1, TipoMovimiento.INGRESO, CategoriaMovimiento.ARRIENDO, "Arriendo salón principal (Rosa Díaz)", 25000, hoy.minusDays(6), tesoreria, reservaId = 4),
            Movimiento(2, TipoMovimiento.INGRESO, CategoriaMovimiento.CUOTA_SOCIAL, "Cuotas sociales del mes", 45000, hoy.minusDays(10), tesoreria),
            Movimiento(3, TipoMovimiento.GASTO, CategoriaMovimiento.LUZ, "Boleta de luz de la sede", 18500, hoy.minusDays(8), tesoreria),
            Movimiento(4, TipoMovimiento.GASTO, CategoriaMovimiento.AGUA, "Boleta de agua de la sede", 9200, hoy.minusDays(7), tesoreria),
            Movimiento(5, TipoMovimiento.GASTO, CategoriaMovimiento.INTERNET, "Plan de internet de la sede", 15990, hoy.minusDays(4), tesoreria),
            Movimiento(6, TipoMovimiento.GASTO, CategoriaMovimiento.MANTENCION, "Ampolletas y artículos de aseo", 12000, hoy.minusDays(2), tesoreria)
        )
    }
}
