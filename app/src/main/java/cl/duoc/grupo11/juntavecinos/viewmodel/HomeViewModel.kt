package cl.duoc.grupo11.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.grupo11.juntavecinos.model.EstadoReserva
import cl.duoc.grupo11.juntavecinos.model.Reserva
import cl.duoc.grupo11.juntavecinos.model.ResumenFinanciero
import cl.duoc.grupo11.juntavecinos.repository.MovimientoRepository
import cl.duoc.grupo11.juntavecinos.repository.MovimientoRepositoryMemoria
import cl.duoc.grupo11.juntavecinos.repository.ReservaRepository
import cl.duoc.grupo11.juntavecinos.repository.ReservaRepositoryMemoria
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

// Estado que observa la pantalla de inicio
data class HomeUiState(
    val resumen: ResumenFinanciero = ResumenFinanciero(),
    val proximasReservas: List<Reserva> = emptyList(),
    val reservasPendientes: Int = 0
)

class HomeViewModel(
    reservaRepository: ReservaRepository = ReservaRepositoryMemoria,
    movimientoRepository: MovimientoRepository = MovimientoRepositoryMemoria
) : ViewModel() {

    // Combina reservas y movimientos en un solo estado para la UI
    val estado: StateFlow<HomeUiState> = combine(
        reservaRepository.reservas,
        movimientoRepository.movimientos
    ) { reservas, movimientos ->
        val hoy = LocalDate.now()
        HomeUiState(
            resumen = ResumenFinanciero.desde(movimientos),
            proximasReservas = reservas
                .filter { it.estaVigente && !it.fecha.isBefore(hoy) }
                .sortedWith(compareBy({ it.fecha }, { it.horaInicio }))
                .take(3),
            reservasPendientes = reservas.count { it.estado == EstadoReserva.PENDIENTE }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
