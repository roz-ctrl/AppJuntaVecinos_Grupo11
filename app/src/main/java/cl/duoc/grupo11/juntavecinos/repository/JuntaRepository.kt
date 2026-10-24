package cl.duoc.grupo11.juntavecinos.repository

import cl.duoc.grupo11.juntavecinos.model.EstadoReserva
import cl.duoc.grupo11.juntavecinos.model.Movimiento
import cl.duoc.grupo11.juntavecinos.model.Reserva
import cl.duoc.grupo11.juntavecinos.model.TipoMovimiento
import java.time.LocalDate
import java.time.LocalTime

// Origen de datos simulado en memoria. Más adelante se reemplaza por Room (SQLite)
// y luego por el microservicio propio en Spring Boot consumido con Retrofit.
// Todos los nombres y montos son ficticios.
class JuntaRepository {

    private val reservas = listOf(
        Reserva(
            id = 1,
            dependencia = "Salón principal",
            solicitante = "Vecino/a ficticio 1",
            fecha = LocalDate.of(2026, 11, 7),
            horaInicio = LocalTime.of(15, 0),
            horaFin = LocalTime.of(20, 0),
            monto = 25000,
            estado = EstadoReserva.CONFIRMADA
        ),
        Reserva(
            id = 2,
            dependencia = "Sala multiuso",
            solicitante = "Vecino/a ficticio 2",
            fecha = LocalDate.of(2026, 11, 14),
            horaInicio = LocalTime.of(10, 0),
            horaFin = LocalTime.of(13, 0),
            monto = 15000
        ),
        Reserva(
            id = 3,
            dependencia = "Salón principal",
            solicitante = "Vecino/a ficticio 3",
            fecha = LocalDate.of(2026, 11, 28),
            horaInicio = LocalTime.of(18, 0),
            horaFin = LocalTime.of(23, 0),
            monto = 25000
        )
    )

    private val movimientos = listOf(
        Movimiento(1, TipoMovimiento.INGRESO, "Arriendo", "Arriendo salón principal", 25000, LocalDate.of(2026, 10, 3)),
        Movimiento(2, TipoMovimiento.INGRESO, "Arriendo", "Arriendo sala multiuso", 15000, LocalDate.of(2026, 10, 17)),
        Movimiento(3, TipoMovimiento.GASTO, "Luz", "Consumo eléctrico de la sede", 18500, LocalDate.of(2026, 10, 10)),
        Movimiento(4, TipoMovimiento.GASTO, "Agua", "Consumo de agua de la sede", 9200, LocalDate.of(2026, 10, 12))
    )

    fun obtenerReservas(): List<Reserva> = reservas

    fun obtenerMovimientos(): List<Movimiento> = movimientos
}
