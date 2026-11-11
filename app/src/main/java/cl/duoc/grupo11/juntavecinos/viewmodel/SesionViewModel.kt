package cl.duoc.grupo11.juntavecinos.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import cl.duoc.grupo11.juntavecinos.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ViewModel compartido por toda la app: guarda el usuario que inició sesión
// y su foto de perfil (cámara o galería)
class SesionViewModel : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _fotoPerfil = MutableStateFlow<Uri?>(null)
    val fotoPerfil: StateFlow<Uri?> = _fotoPerfil.asStateFlow()

    fun iniciarSesion(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun cerrarSesion() {
        _usuario.value = null
        _fotoPerfil.value = null
    }

    fun actualizarFotoPerfil(uri: Uri?) {
        _fotoPerfil.value = uri
    }
}
