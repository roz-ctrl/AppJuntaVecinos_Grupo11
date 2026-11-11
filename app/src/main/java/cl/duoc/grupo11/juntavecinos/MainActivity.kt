package cl.duoc.grupo11.juntavecinos

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.grupo11.juntavecinos.navigation.AppNavigation
import cl.duoc.grupo11.juntavecinos.ui.theme.AppJuntaVecinosTheme
import cl.duoc.grupo11.juntavecinos.ui.utils.obtenerWindowSizeClass
import cl.duoc.grupo11.juntavecinos.viewmodel.PreferenciasViewModel
import cl.duoc.grupo11.juntavecinos.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferenciasViewModel: PreferenciasViewModel = viewModel()
            val modoOscuro by preferenciasViewModel.modoOscuro.collectAsState()

            // Los íconos de la barra de estado cambian de color según el tema elegido en la app
            DisposableEffect(modoOscuro) {
                val estilo = if (modoOscuro == true) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = estilo, navigationBarStyle = estilo)
                onDispose {}
            }

            AppJuntaVecinosTheme(darkTheme = modoOscuro == true) {
                if (modoOscuro == null) {
                    // Indicador de carga mientras se leen las preferencias guardadas
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // ViewModel de sesión compartido por todas las pantallas
                    val sesionViewModel: SesionViewModel = viewModel()
                    val windowSizeClass = obtenerWindowSizeClass()
                    AppNavigation(
                        sesionViewModel = sesionViewModel,
                        anchoVentana = windowSizeClass.widthSizeClass,
                        modoOscuro = modoOscuro == true,
                        onModoOscuroChange = preferenciasViewModel::cambiarModoOscuro
                    )
                }
            }
        }
    }
}
