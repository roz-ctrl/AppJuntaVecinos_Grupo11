package cl.duoc.grupo11.juntavecinos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.grupo11.juntavecinos.data.PreferenciasDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// AndroidViewModel porque DataStore necesita el contexto de la aplicación
class PreferenciasViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencias = PreferenciasDataStore(application)

    // null mientras se cargan las preferencias (se muestra un indicador de carga)
    private val _modoOscuro = MutableStateFlow<Boolean?>(null)
    val modoOscuro: StateFlow<Boolean?> = _modoOscuro.asStateFlow()

    init {
        viewModelScope.launch {
            delay(600) // Pequeña espera para que se aprecie la pantalla de carga
            preferencias.modoOscuro.collect { valor -> _modoOscuro.value = valor }
        }
    }

    fun cambiarModoOscuro(activado: Boolean) {
        viewModelScope.launch { preferencias.guardarModoOscuro(activado) }
    }
}
