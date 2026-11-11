package cl.duoc.grupo11.juntavecinos.model

// Clase base de los perfiles de usuario.
// Cada subclase define sus propios permisos (herencia y polimorfismo).
sealed class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    private val clave: String,
    val rol: Rol
) {
    open val puedeSolicitarReservas: Boolean = false
    open val puedeGestionarReservas: Boolean = false
    open val puedeRegistrarMovimientos: Boolean = false

    // Encapsulamiento: la clave no se expone, solo se puede comparar
    fun claveEsCorrecta(intento: String): Boolean = clave == intento

    // Lista de permisos que se muestra en la pantalla de perfil
    fun permisos(): List<String> = buildList {
        if (puedeSolicitarReservas) add("Solicitar arriendos de la sede")
        if (puedeGestionarReservas) add("Aprobar o rechazar reservas")
        if (puedeRegistrarMovimientos) add("Registrar ingresos y gastos")
        add("Consultar la información financiera")
    }
}

// Vecino/a: solicita arriendos y consulta la información financiera
class Vecino(id: Int, nombre: String, correo: String, clave: String) :
    Usuario(id, nombre, correo, clave, Rol.VECINO) {
    override val puedeSolicitarReservas = true
}

// Directiva / Secretaría: verifica disponibilidad y aprueba o rechaza reservas
class Directiva(id: Int, nombre: String, correo: String, clave: String) :
    Usuario(id, nombre, correo, clave, Rol.DIRECTIVA) {
    override val puedeSolicitarReservas = true
    override val puedeGestionarReservas = true
}

// Tesorería: registra pagos, ingresos y gastos
class Tesorero(id: Int, nombre: String, correo: String, clave: String) :
    Usuario(id, nombre, correo, clave, Rol.TESORERIA) {
    override val puedeRegistrarMovimientos = true
}
