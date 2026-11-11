package cl.duoc.grupo11.juntavecinos.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.grupo11.juntavecinos.ui.screens.EnConstruccion
import cl.duoc.grupo11.juntavecinos.ui.screens.HomeScreen
import cl.duoc.grupo11.juntavecinos.ui.screens.LoginScreen
import cl.duoc.grupo11.juntavecinos.ui.screens.PerfilScreen
import cl.duoc.grupo11.juntavecinos.viewmodel.SesionViewModel

// Ícono de cada destino principal
private fun Screen.icono(): ImageVector = when (this) {
    Screen.Reservas -> Icons.Filled.Event
    Screen.Finanzas -> Icons.Filled.AccountBalanceWallet
    Screen.Perfil -> Icons.Filled.Person
    else -> Icons.Filled.Home
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    sesionViewModel: SesionViewModel,
    anchoVentana: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val pantallaActual = Screen.desdeRuta(rutaActual)
    val esPrincipal = pantallaActual in Screen.principales

    // Diseño adaptable: barra inferior en teléfonos y riel lateral en pantallas medianas o grandes
    val usarRielLateral = anchoVentana != WindowWidthSizeClass.Compact

    // Navega entre secciones principales sin acumular pantallas repetidas en la pila
    fun navegarPrincipal(destino: Screen) {
        navController.navigate(destino.route) {
            popUpTo(Screen.Inicio.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun irAlLogin() {
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    // Si no hay sesión activa, se vuelve al login
    LaunchedEffect(usuario, rutaActual) {
        if (usuario == null && rutaActual != null && rutaActual != Screen.Login.route) {
            irAlLogin()
        }
    }

    Scaffold(
        topBar = {
            if (pantallaActual != null && pantallaActual != Screen.Login) {
                TopAppBar(
                    title = { Text(pantallaActual.titulo) },
                    navigationIcon = {
                        if (!esPrincipal) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (esPrincipal && !usarRielLateral) {
                NavigationBar {
                    Screen.principales.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.route,
                            onClick = { navegarPrincipal(destino) },
                            icon = { Icon(destino.icono(), contentDescription = null) },
                            label = { Text(destino.titulo) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (esPrincipal && usarRielLateral) {
                NavigationRail {
                    Screen.principales.forEach { destino ->
                        NavigationRailItem(
                            selected = rutaActual == destino.route,
                            onClick = { navegarPrincipal(destino) },
                            icon = { Icon(destino.icono(), contentDescription = null) },
                            label = { Text(destino.titulo) }
                        )
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = Screen.Login.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(onLoginExitoso = { usuarioIngresado ->
                        sesionViewModel.iniciarSesion(usuarioIngresado)
                        navController.navigate(Screen.Inicio.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    })
                }
                composable(Screen.Inicio.route) {
                    HomeScreen(
                        usuario = usuario,
                        onVerReservas = { navegarPrincipal(Screen.Reservas) },
                        onNuevaReserva = { navController.navigate(Screen.FormReserva.crearRuta()) },
                        onVerFinanzas = { navegarPrincipal(Screen.Finanzas) },
                        onNuevoMovimiento = { navController.navigate(Screen.FormMovimiento.route) }
                    )
                }
                composable(Screen.Reservas.route) {
                    EnConstruccion(titulo = Screen.Reservas.titulo)
                }
                composable(Screen.Finanzas.route) {
                    EnConstruccion(titulo = Screen.Finanzas.titulo)
                }
                composable(Screen.Perfil.route) {
                    PerfilScreen(
                        usuario = usuario,
                        onCerrarSesion = {
                            sesionViewModel.cerrarSesion()
                            irAlLogin()
                        }
                    )
                }
                composable(
                    route = Screen.FormReserva.route,
                    arguments = listOf(navArgument(Screen.FormReserva.ARG_ID) { type = NavType.IntType })
                ) {
                    EnConstruccion(titulo = Screen.FormReserva.titulo)
                }
                composable(Screen.FormMovimiento.route) {
                    EnConstruccion(titulo = Screen.FormMovimiento.titulo)
                }
            }
        }
    }
}
