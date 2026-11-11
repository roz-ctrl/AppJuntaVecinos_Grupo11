package cl.duoc.grupo11.juntavecinos.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun `acepta correos con formato válido`() {
        assertTrue(esCorreoValido("vecino@zaror.cl"))
        assertTrue(esCorreoValido("  directiva.junta@zaror.cl "))
    }

    @Test
    fun `rechaza correos sin arroba o sin dominio`() {
        assertFalse(esCorreoValido("vecino"))
        assertFalse(esCorreoValido("vecino@"))
        assertFalse(esCorreoValido("vecino@zaror"))
        assertFalse(esCorreoValido(""))
    }

    @Test
    fun `convierte solo montos enteros mayores a cero`() {
        assertEquals(21_990, textoAMonto("21990"))
        assertNull(textoAMonto("0"))
        assertNull(textoAMonto("-500"))
        assertNull(textoAMonto("abc"))
        assertNull(textoAMonto(""))
    }

    @Test
    fun `formatea montos en pesos chilenos`() {
        assertEquals("\$25.000", 25_000.comoPesos())
        assertEquals("-\$690", (-690).comoPesos())
    }
}
