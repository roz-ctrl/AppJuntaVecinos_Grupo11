package cl.duoc.grupo11.juntavecinos.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun `formulario vacío muestra errores en ambos campos`() {
        val viewModel = LoginViewModel()
        assertFalse(viewModel.validarFormulario())
        assertEquals("Ingresa tu correo", viewModel.estado.value.errores.correo)
        assertEquals("Ingresa tu contraseña", viewModel.estado.value.errores.clave)
    }

    @Test
    fun `correo inválido y clave corta no pasan la validación`() {
        val viewModel = LoginViewModel()
        viewModel.onCorreoChange("vecino")
        viewModel.onClaveChange("123")
        assertFalse(viewModel.validarFormulario())
        assertEquals("El correo no tiene un formato válido", viewModel.estado.value.errores.correo)
        assertEquals("Debe tener al menos 6 caracteres", viewModel.estado.value.errores.clave)
    }

    @Test
    fun `al escribir se limpia el error del campo`() {
        val viewModel = LoginViewModel()
        viewModel.validarFormulario()
        viewModel.onCorreoChange("vecino@zaror.cl")
        assertNull(viewModel.estado.value.errores.correo)
    }

    @Test
    fun `una cuenta de prueba completa un formulario válido`() {
        val viewModel = LoginViewModel()
        viewModel.usarCuentaDePrueba(viewModel.cuentasDePrueba.first())
        assertTrue(viewModel.validarFormulario())
    }
}
