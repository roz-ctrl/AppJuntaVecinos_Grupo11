package cl.duoc.grupo11.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.grupo11.juntavecinos.model.Reserva
import cl.duoc.grupo11.juntavecinos.model.TipoMovimiento
import cl.duoc.grupo11.juntavecinos.repository.JuntaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Estado que observa la pantalla de inicio
data class HomeUiState(
    val totalIngresos: Int = 0,
    val totalGastos: Int = 0,
    val proximasReservas: List<Reserva> = emptyList()
) {
    val saldo: Int get() = totalIngresos - totalGastos
}

class HomeViewModel(
    private val repository: JuntaRepository = JuntaRepository()
) : ViewModel() {

    // Estado interno mutable
    private val _estado = MutableStateFlow(HomeUiState())

    // Estado expuesto a la UI (solo lectura)
    val estado: StateFlow<HomeUiState> = _estado.asStateFlow()

    init {
        cargarResumen()
    }

    // Calcula el resumen financiero y ordena las reservas por fecha
    private fun cargarResumen() {
        val movimientos = repository.obtenerMovimientos()
        _estado.value = HomeUiState(
            totalIngresos = movimientos.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto },
            totalGastos = movimientos.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto },
            proximasReservas = repository.obtenerReservas().sortedBy { it.fecha }
        )
    }
}
