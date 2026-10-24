package cl.duoc.grupo11.juntavecinos.model

import java.time.LocalDate
import java.time.LocalTime

enum class EstadoReserva { PENDIENTE, CONFIRMADA, RECHAZADA }

// Solicitud de arriendo de una dependencia de la sede (todos los datos son ficticios)
data class Reserva(
    val id: Int,
    val dependencia: String,
    val solicitante: String,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val monto: Int,
    val estado: EstadoReserva = EstadoReserva.PENDIENTE
)
