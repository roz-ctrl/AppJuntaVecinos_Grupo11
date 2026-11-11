package cl.duoc.grupo11.juntavecinos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import cl.duoc.grupo11.juntavecinos.ui.screens.HomeScreen
import cl.duoc.grupo11.juntavecinos.ui.theme.AppJuntaVecinosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppJuntaVecinosTheme {
                Scaffold { innerPadding ->
                    HomeScreen(usuario = null, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
