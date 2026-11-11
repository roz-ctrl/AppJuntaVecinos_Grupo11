package cl.duoc.grupo11.juntavecinos.model

// Perfiles de usuario definidos en el caso de la Junta de Vecinos
enum class Rol(val etiqueta: String) {
    VECINO("Vecino/a"),
    DIRECTIVA("Directiva / Secretaría"),
    TESORERIA("Tesorería")
}
