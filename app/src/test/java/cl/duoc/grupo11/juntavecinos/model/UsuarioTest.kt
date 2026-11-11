package cl.duoc.grupo11.juntavecinos.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UsuarioTest {

    private val vecino: Usuario = Vecino(1, "Vecino", "vecino@zaror.cl", "vecino123")
    private val directiva: Usuario = Directiva(2, "Directiva", "directiva@zaror.cl", "directiva123")
    private val tesorero: Usuario = Tesorero(3, "Tesorería", "tesoreria@zaror.cl", "tesoreria123")

    @Test
    fun `cada perfil tiene sus propios permisos (polimorfismo)`() {
        assertTrue(vecino.puedeSolicitarReservas)
        assertFalse(vecino.puedeGestionarReservas)
        assertFalse(vecino.puedeRegistrarMovimientos)

        assertTrue(directiva.puedeGestionarReservas)
        assertFalse(directiva.puedeRegistrarMovimientos)

        assertTrue(tesorero.puedeRegistrarMovimientos)
        assertFalse(tesorero.puedeSolicitarReservas)
    }

    @Test
    fun `cada subclase tiene el rol que le corresponde`() {
        assertEquals(Rol.VECINO, vecino.rol)
        assertEquals(Rol.DIRECTIVA, directiva.rol)
        assertEquals(Rol.TESORERIA, tesorero.rol)
    }

    @Test
    fun `la clave solo se puede comparar`() {
        assertTrue(vecino.claveEsCorrecta("vecino123"))
        assertFalse(vecino.claveEsCorrecta("otra"))
    }

    @Test
    fun `todos los perfiles pueden consultar la información financiera`() {
        listOf(vecino, directiva, tesorero).forEach { usuario ->
            assertTrue(usuario.permisos().contains("Consultar la información financiera"))
        }
    }
}
