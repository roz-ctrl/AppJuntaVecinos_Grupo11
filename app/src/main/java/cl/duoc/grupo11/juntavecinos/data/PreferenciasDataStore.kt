package cl.duoc.grupo11.juntavecinos.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore con nombre 'preferencias_usuario' (reemplazo moderno de SharedPreferences)
private val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

// Guarda preferencias simples del usuario de forma persistente
class PreferenciasDataStore(private val context: Context) {

    private val claveModoOscuro = booleanPreferencesKey("modo_oscuro")

    // Flujo reactivo: emite un valor nuevo cada vez que cambia la preferencia
    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { preferencias ->
        preferencias[claveModoOscuro] ?: false
    }

    suspend fun guardarModoOscuro(valor: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[claveModoOscuro] = valor
        }
    }
}
