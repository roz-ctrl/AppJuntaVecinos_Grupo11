package cl.duoc.grupo11.juntavecinos.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class ReservaTest {

    private val fecha = LocalDate.of(2026, 11, 20)

    private fun reserva(
        id: Int,
        inicio: Int,
        fin: Int,
        dependencia: Dependencia = Dependencia.SALON_PRINCIPAL,
        estado: EstadoReserva = EstadoReserva.PENDIENTE
    ) = Reserva(
        id = id,
        dependencia = dependencia,
        solicitanteId = 1,
        solicitante = "Vecino de prueba",
        fecha = fecha,
        horaInicio = LocalTime.of(inicio, 0),
        horaFin = LocalTime.of(fin, 0),
        motivo = "Prueba",
        estado = estado
    )

    @Test
    fun `el monto se calcula con las horas y la tarifa de la dependencia`() {
        val salon = reserva(id = 1, inicio = 15, fin = 20)
        assertEquals(5, salon.horas)
        assertEquals(25_000, salon.monto)

        val quincho = reserva(id = 2, inicio = 10, fin = 13, dependencia = Dependencia.QUINCHO)
        assertEquals(12_000, quincho.monto)
    }

    @Test
    fun `dos reservas que se solapan en la misma dependencia se cruzan`() {
        val existente = reserva(id = 1, inicio = 15, fin = 20)
        val nueva = reserva(id = 2, inicio = 14, fin = 16)
        assertTrue(nueva.seCruzaCon(existente))
    }

    @Test
    fun `reservas seguidas sin solaparse no se cruzan`() {
        val existente = reserva(id = 1, inicio = 15, fin = 20)
        val nueva = reserva(id = 2, inicio = 20, fin = 22)
        assertFalse(nueva.seCruzaCon(existente))
    }

    @Test
    fun `el mismo horario en otra dependencia no se cruza`() {
        val existente = reserva(id = 1, inicio = 15, fin = 20)
        val nueva = reserva(id = 2, inicio = 15, fin = 20, dependencia = Dependencia.QUINCHO)
        assertFalse(nueva.seCruzaCon(existente))
    }

    @Test
    fun `una reserva rechazada o cancelada no bloquea el horario`() {
        val rechazada = reserva(id = 1, inicio = 15, fin = 20, estado = EstadoReserva.RECHAZADA)
        val cancelada = reserva(id = 2, inicio = 15, fin = 20, estado = EstadoReserva.CANCELADA)
        val nueva = reserva(id = 3, inicio = 16, fin = 18)
        assertFalse(nueva.seCruzaCon(rechazada))
        assertFalse(nueva.seCruzaCon(cancelada))
    }

    @Test
    fun `una reserva no se cruza consigo misma al editarla`() {
        val original = reserva(id = 1, inicio = 15, fin = 20)
        val editada = original.copy(horaFin = LocalTime.of(21, 0))
        assertFalse(editada.seCruzaCon(original))
    }
}
