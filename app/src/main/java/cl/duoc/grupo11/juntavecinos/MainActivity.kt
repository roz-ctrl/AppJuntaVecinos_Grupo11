package cl.duoc.grupo11.juntavecinos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.grupo11.juntavecinos.ui.screens.HomeScreen
import cl.duoc.grupo11.juntavecinos.ui.theme.AppJuntaVecinosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppJuntaVecinosTheme {
                HomeScreen()
            }
        }
    }
}
