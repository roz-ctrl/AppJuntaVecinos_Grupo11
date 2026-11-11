package cl.duoc.grupo11.juntavecinos.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

enum class EstadoReserva(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada")
}

// Solicitud de arriendo de una dependencia de la sede (todos los datos son ficticios)
data class Reserva(
    val id: Int,
    val dependencia: Dependencia,
    val solicitanteId: Int,
    val solicitante: String,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val motivo: String,
    val estado: EstadoReserva = EstadoReserva.PENDIENTE,
    val pagada: Boolean = false
) {
    // Cantidad de horas arrendadas
    val horas: Int get() = Duration.between(horaInicio, horaFin).toHours().toInt()

    // El monto se calcula según la tarifa por hora de la dependencia
    val monto: Int get() = horas * dependencia.tarifaPorHora

    // Solo las reservas pendientes o confirmadas ocupan la sede
    val estaVigente: Boolean
        get() = estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA

    // Regla de negocio: dos reservas vigentes no pueden cruzarse
    // en la misma dependencia, el mismo día y el mismo horario
    fun seCruzaCon(otra: Reserva): Boolean =
        id != otra.id &&
            estaVigente && otra.estaVigente &&
            dependencia == otra.dependencia &&
            fecha == otra.fecha &&
            horaInicio < otra.horaFin && otra.horaInicio < horaFin
}
