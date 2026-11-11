package cl.duoc.grupo11.juntavecinos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.grupo11.juntavecinos.ui.screens.HomeScreen
import cl.duoc.grupo11.juntavecinos.ui.screens.LoginScreen
import cl.duoc.grupo11.juntavecinos.ui.theme.AppJuntaVecinosTheme
import cl.duoc.grupo11.juntavecinos.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppJuntaVecinosTheme {
                val sesionViewModel: SesionViewModel = viewModel()
                val usuario by sesionViewModel.usuario.collectAsState()
                Scaffold { innerPadding ->
                    if (usuario == null) {
                        LoginScreen(
                            onLoginExitoso = sesionViewModel::iniciarSesion,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        HomeScreen(usuario = usuario, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
