package cl.duoc.grupo11.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.repository.CuentaPrueba
import cl.duoc.grupo11.juntavecinos.repository.UsuarioRepository
import cl.duoc.grupo11.juntavecinos.repository.UsuarioRepositoryMemoria
import cl.duoc.grupo11.juntavecinos.util.esCorreoValido
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Errores de validación por campo (null = sin error)
data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null
)

// Estado completo del formulario de inicio de sesión
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val claveVisible: Boolean = false,
    val cargando: Boolean = false,
    val errores: LoginErrores = LoginErrores(),
    val errorGeneral: String? = null
)

class LoginViewModel(
    private val repository: UsuarioRepository = UsuarioRepositoryMemoria
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    val cuentasDePrueba: List<CuentaPrueba> = repository.cuentasDePrueba

    // Cada cambio actualiza el campo y limpia su error
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null), errorGeneral = null) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null), errorGeneral = null) }
    }

    fun alternarVisibilidadClave() {
        _estado.update { it.copy(claveVisible = !it.claveVisible) }
    }

    // Completa el formulario con una cuenta ficticia de prueba
    fun usarCuentaDePrueba(cuenta: CuentaPrueba) {
        _estado.update { LoginUiState(correo = cuenta.correo, clave = cuenta.clave) }
    }

    // Valida todos los campos y actualiza los errores; devuelve true si el formulario es válido
    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val errores = LoginErrores(
            correo = when {
                actual.correo.isBlank() -> "Ingresa tu correo"
                !esCorreoValido(actual.correo) -> "El correo no tiene un formato válido"
                else -> null
            },
            clave = when {
                actual.clave.isBlank() -> "Ingresa tu contraseña"
                actual.clave.length < 6 -> "Debe tener al menos 6 caracteres"
                else -> null
            }
        )
        _estado.update { it.copy(errores = errores) }
        return errores.correo == null && errores.clave == null
    }

    fun iniciarSesion(onExito: (Usuario) -> Unit) {
        if (!validarFormulario()) return
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, errorGeneral = null) }
            delay(800) // Simula la consulta al servidor para mostrar el indicador de carga
            val usuario = repository.autenticar(_estado.value.correo, _estado.value.clave)
            if (usuario != null) {
                _estado.value = LoginUiState()
                onExito(usuario)
            } else {
                _estado.update { it.copy(cargando = false, errorGeneral = "Correo o contraseña incorrectos") }
            }
        }
    }
}
