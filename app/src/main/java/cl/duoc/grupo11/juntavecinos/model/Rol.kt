package cl.duoc.grupo11.juntavecinos.model

// Perfiles de usuario definidos en el caso de la Junta de Vecinos
enum class Rol {
    VECINO,     // Solicita arriendos y consulta la información financiera
    DIRECTIVA,  // Verifica disponibilidad y hace seguimiento de gastos
    TESORERIA   // Registra pagos, ingresos y gastos, y prepara rendiciones
}
