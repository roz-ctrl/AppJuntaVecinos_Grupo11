package cl.duoc.grupo11.juntavecinos.navigation

// Rutas tipadas de la app (sealed class): evita errores al escribir las rutas a mano
sealed class Screen(val route: String, val titulo: String) {

    data object Login : Screen("login", "Iniciar sesión")
    data object Inicio : Screen("inicio", "Inicio")
    data object Reservas : Screen("reservas", "Reservas")
    data object Finanzas : Screen("finanzas", "Finanzas")
    data object Perfil : Screen("perfil", "Mi perfil")
    data object FormMovimiento : Screen("form_movimiento", "Nuevo movimiento")

    // Ruta con argumento: id de la reserva a editar (-1 = nueva reserva)
    data object FormReserva : Screen("form_reserva/{reservaId}", "Solicitud de reserva") {
        const val ARG_ID = "reservaId"
        const val NUEVA = -1
        fun crearRuta(reservaId: Int = NUEVA): String = "form_reserva/$reservaId"
    }

    companion object {
        // Destinos principales que aparecen en la barra de navegación
        val principales: List<Screen>
            get() = listOf(Inicio, Reservas, Finanzas, Perfil)

        fun desdeRuta(ruta: String?): Screen? =
            listOf(Login, Inicio, Reservas, Finanzas, Perfil, FormMovimiento, FormReserva)
                .find { it.route == ruta }
    }
}
