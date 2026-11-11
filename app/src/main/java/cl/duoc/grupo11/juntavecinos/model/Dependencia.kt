package cl.duoc.grupo11.juntavecinos.model

// Espacios de la sede que se pueden arrendar, con una tarifa ficticia por hora
enum class Dependencia(val nombre: String, val tarifaPorHora: Int) {
    SALON_PRINCIPAL("Salón principal", 5000),
    SALA_MULTIUSO("Sala multiuso", 3000),
    QUINCHO("Quincho", 4000)
}
