package cl.duoc.grupo11.juntavecinos.repository

import cl.duoc.grupo11.juntavecinos.model.Dependencia
import cl.duoc.grupo11.juntavecinos.model.EstadoReserva
import cl.duoc.grupo11.juntavecinos.model.Reserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

// Contrato del origen de datos de reservas
interface ReservaRepository {
    val reservas: StateFlow<List<Reserva>>
    fun buscarPorId(id: Int): Reserva?
    fun agregar(reserva: Reserva): Reserva
    fun actualizar(reserva: Reserva)
    fun eliminar(id: Int)
}

// Implementación en memoria (singleton) compartida por todas las pantallas.
// Más adelante se reemplaza por Room y luego por el microservicio en Spring Boot.
object ReservaRepositoryMemoria : ReservaRepository {

    private val _reservas = MutableStateFlow(datosIniciales())
    override val reservas: StateFlow<List<Reserva>> = _reservas.asStateFlow()

    private var siguienteId = _reservas.value.maxOf { it.id } + 1

    override fun buscarPorId(id: Int): Reserva? = _reservas.value.find { it.id == id }

    override fun agregar(reserva: Reserva): Reserva {
        val nueva = reserva.copy(id = siguienteId++)
        _reservas.update { it + nueva }
        return nueva
    }

    override fun actualizar(reserva: Reserva) {
        _reservas.update { lista -> lista.map { if (it.id == reserva.id) reserva else it } }
    }

    override fun eliminar(id: Int) {
        _reservas.update { lista -> lista.filterNot { it.id == id } }
    }

    // Reservas ficticias con fechas relativas a hoy para que la demo siempre tenga datos
    private fun datosIniciales(): List<Reserva> {
        val hoy = LocalDate.now()
        return listOf(
            Reserva(
                id = 1,
                dependencia = Dependencia.SALON_PRINCIPAL,
                solicitanteId = 1,
                solicitante = "María Pérez",
                fecha = hoy.plusDays(2),
                horaInicio = LocalTime.of(15, 0),
                horaFin = LocalTime.of(20, 0),
                motivo = "Cumpleaños familiar",
                estado = EstadoReserva.CONFIRMADA
            ),
            Reserva(
                id = 2,
                dependencia = Dependencia.SALA_MULTIUSO,
                solicitanteId = 20,
                solicitante = "Carlos Vera",
                fecha = hoy.plusDays(5),
                horaInicio = LocalTime.of(10, 0),
                horaFin = LocalTime.of(13, 0),
                motivo = "Taller de reciclaje"
            ),
            Reserva(
                id = 3,
                dependencia = Dependencia.QUINCHO,
                solicitanteId = 1,
                solicitante = "María Pérez",
                fecha = hoy.plusDays(9),
                horaInicio = LocalTime.of(13, 0),
                horaFin = LocalTime.of(18, 0),
                motivo = "Almuerzo de vecinos del pasaje"
            ),
            Reserva(
                id = 4,
                dependencia = Dependencia.SALON_PRINCIPAL,
                solicitanteId = 21,
                solicitante = "Rosa Díaz",
                fecha = hoy.minusDays(6),
                horaInicio = LocalTime.of(18, 0),
                horaFin = LocalTime.of(23, 0),
                motivo = "Celebración de aniversario",
                estado = EstadoReserva.CONFIRMADA,
                pagada = true
            )
        )
    }
}
