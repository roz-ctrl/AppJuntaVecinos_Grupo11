package cl.duoc.grupo11.juntavecinos.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = Color(0xFF00390A),
    primaryContainer = VerdeContenedorOscuro,
    onPrimaryContainer = VerdeContenedor,
    secondary = AzulClaro,
    secondaryContainer = AzulContenedorOscuro,
    onSecondaryContainer = AzulContenedor,
    tertiary = AmbarClaro,
    tertiaryContainer = AmbarContenedorOscuro,
    onTertiaryContainer = AmbarContenedor,
    background = FondoOscuro,
    surface = FondoOscuro,
    surfaceContainerLowest = Color(0xFF0C0F0B),
    surfaceContainerLow = SuperficieOscuro1,
    surfaceContainer = SuperficieOscuro2,
    surfaceContainerHigh = SuperficieOscuro3,
    surfaceContainerHighest = SuperficieOscuro4
)

private val LightColorScheme = lightColorScheme(
    primary = Verde,
    onPrimary = Color.White,
    primaryContainer = VerdeContenedor,
    onPrimaryContainer = VerdeOscuroTexto,
    secondary = Azul,
    secondaryContainer = AzulContenedor,
    onSecondaryContainer = AzulOscuroTexto,
    tertiary = Ambar,
    tertiaryContainer = AmbarContenedor,
    onTertiaryContainer = AmbarOscuroTexto,
    background = FondoClaro,
    surface = FondoClaro,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = SuperficieClaro1,
    surfaceContainer = SuperficieClaro2,
    surfaceContainerHigh = SuperficieClaro3,
    surfaceContainerHighest = SuperficieClaro4
)

@Composable
fun AppJuntaVecinosTheme(
    darkTheme: Boolean = false,
    // Color dinámico desactivado para mantener los colores propios de la app
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
