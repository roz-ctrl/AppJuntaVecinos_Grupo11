package cl.duoc.grupo11.juntavecinos.repository

import cl.duoc.grupo11.juntavecinos.model.Directiva
import cl.duoc.grupo11.juntavecinos.model.Tesorero
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.model.Vecino

// Contrato del origen de datos de usuarios
interface UsuarioRepository {
    fun autenticar(correo: String, clave: String): Usuario?
    fun obtenerTodos(): List<Usuario>
}

// Implementación en memoria con cuentas ficticias de prueba.
// Más adelante se reemplaza por el microservicio en Spring Boot.
object UsuarioRepositoryMemoria : UsuarioRepository {

    private val usuarios: List<Usuario> = listOf(
        Vecino(1, "María Pérez", "vecino@zaror.cl", "vecino123"),
        Directiva(2, "Luis Soto", "directiva@zaror.cl", "directiva123"),
        Tesorero(3, "Ana Fuentes", "tesoreria@zaror.cl", "tesoreria123")
    )

    override fun autenticar(correo: String, clave: String): Usuario? =
        usuarios.find { it.correo.equals(correo.trim(), ignoreCase = true) && it.claveEsCorrecta(clave) }

    override fun obtenerTodos(): List<Usuario> = usuarios
}
