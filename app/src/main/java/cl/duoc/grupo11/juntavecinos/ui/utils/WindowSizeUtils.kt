package cl.duoc.grupo11.juntavecinos.ui.utils

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

// Calcula el tamaño de la ventana (compacta, mediana o expandida) para adaptar la interfaz
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun obtenerWindowSizeClass(): WindowSizeClass {
    val activity = requireNotNull(LocalActivity.current) { "Se necesita una Activity" }
    return calculateWindowSizeClass(activity)
}
