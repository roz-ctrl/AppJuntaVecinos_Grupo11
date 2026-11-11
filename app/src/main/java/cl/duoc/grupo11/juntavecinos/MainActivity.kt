package cl.duoc.grupo11.juntavecinos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.grupo11.juntavecinos.navigation.AppNavigation
import cl.duoc.grupo11.juntavecinos.ui.theme.AppJuntaVecinosTheme
import cl.duoc.grupo11.juntavecinos.ui.utils.obtenerWindowSizeClass
import cl.duoc.grupo11.juntavecinos.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppJuntaVecinosTheme {
                // ViewModel de sesión compartido por todas las pantallas
                val sesionViewModel: SesionViewModel = viewModel()
                val windowSizeClass = obtenerWindowSizeClass()
                AppNavigation(
                    sesionViewModel = sesionViewModel,
                    anchoVentana = windowSizeClass.widthSizeClass
                )
            }
        }
    }
}
