package cl.duoc.grupo11.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.grupo11.juntavecinos.model.CategoriaMovimiento
import cl.duoc.grupo11.juntavecinos.model.Movimiento
import cl.duoc.grupo11.juntavecinos.model.ResumenFinanciero
import cl.duoc.grupo11.juntavecinos.model.TipoMovimiento
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.repository.MovimientoRepository
import cl.duoc.grupo11.juntavecinos.repository.MovimientoRepositoryMemoria
import cl.duoc.grupo11.juntavecinos.util.textoAMonto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class FiltroMovimiento(val etiqueta: String) {
    TODOS("Todos"),
    INGRESOS("Ingresos"),
    GASTOS("Gastos")
}

// Errores de validación del formulario de movimiento (null = sin error)
data class MovimientoErrores(
    val categoria: String? = null,
    val descripcion: String? = null,
    val monto: String? = null,
    val fecha: String? = null,
    val comprobante: String? = null
) {
    val hayErrores: Boolean
        get() = listOfNotNull(categoria, descripcion, monto, fecha, comprobante).isNotEmpty()
}

// Estado del formulario para registrar un ingreso o gasto
data class MovimientoFormState(
    val tipo: TipoMovimiento = TipoMovimiento.GASTO,
    val categoria: CategoriaMovimiento? = null,
    val descripcion: String = "",
    val monto: String = "",
    val fecha: LocalDate? = LocalDate.now(),
    val comprobanteUri: String? = null,
    val errores: MovimientoErrores = MovimientoErrores()
) {
    // Solo se muestran las categorías que corresponden al tipo elegido
    val categoriasDisponibles: List<CategoriaMovimiento>
        get() = CategoriaMovimiento.delTipo(tipo)
}

// ViewModel compartido entre la pantalla de finanzas y el formulario de movimientos
class FinanzasViewModel(
    private val repository: MovimientoRepository = MovimientoRepositoryMemoria
) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroMovimiento.TODOS)
    val filtro: StateFlow<FiltroMovimiento> = _filtro.asStateFlow()

    // Movimientos filtrados, del más reciente al más antiguo
    val movimientos: StateFlow<List<Movimiento>> = combine(repository.movimientos, _filtro) { lista, filtro ->
        lista.filter { movimiento ->
            when (filtro) {
                FiltroMovimiento.TODOS -> true
                FiltroMovimiento.INGRESOS -> movimiento.tipo == TipoMovimiento.INGRESO
                FiltroMovimiento.GASTOS -> movimiento.tipo == TipoMovimiento.GASTO
            }
        }.sortedWith(compareByDescending<Movimiento> { it.fecha }.thenByDescending { it.id })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Resumen calculado con todos los movimientos (transparencia para los vecinos)
    val resumen: StateFlow<ResumenFinanciero> = repository.movimientos
        .map { ResumenFinanciero.desde(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResumenFinanciero())

    private val _formulario = MutableStateFlow(MovimientoFormState())
    val formulario: StateFlow<MovimientoFormState> = _formulario.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()
    private val _mostrarMensaje = MutableStateFlow(false)
    val mostrarMensaje: StateFlow<Boolean> = _mostrarMensaje.asStateFlow()
    private var trabajoMensaje: Job? = null

    fun cambiarFiltro(filtro: FiltroMovimiento) {
        _filtro.value = filtro
    }

    // ---------- Formulario ----------

    fun limpiarFormulario() {
        _formulario.value = MovimientoFormState()
    }

    // Al cambiar el tipo se borra la categoría si ya no corresponde
    fun onTipoChange(valor: TipoMovimiento) {
        _formulario.update {
            it.copy(
                tipo = valor,
                categoria = it.categoria?.takeIf { categoria -> categoria.tipo == valor },
                errores = it.errores.copy(categoria = null, comprobante = null)
            )
        }
    }

    fun onCategoriaChange(valor: CategoriaMovimiento) {
        _formulario.update { it.copy(categoria = valor, errores = it.errores.copy(categoria = null)) }
    }

    fun onDescripcionChange(valor: String) {
        _formulario.update { it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null)) }
    }

    // Solo se aceptan dígitos (pesos chilenos, sin decimales)
    fun onMontoChange(valor: String) {
        _formulario.update {
            it.copy(monto = valor.filter(Char::isDigit).take(9), errores = it.errores.copy(monto = null))
        }
    }

    fun onFechaChange(valor: LocalDate) {
        _formulario.update { it.copy(fecha = valor, errores = it.errores.copy(fecha = null)) }
    }

    fun onComprobanteChange(uri: String?) {
        _formulario.update { it.copy(comprobanteUri = uri, errores = it.errores.copy(comprobante = null)) }
    }

    // Valida los campos y las reglas de negocio; devuelve true si se puede guardar
    fun validarFormulario(): Boolean {
        val form = _formulario.value
        val errores = MovimientoErrores(
            categoria = if (form.categoria == null) "Selecciona una categoría" else null,
            descripcion = when {
                form.descripcion.isBlank() -> "Describe el movimiento"
                form.descripcion.trim().length < 4 -> "La descripción es muy corta"
                else -> null
            },
            monto = if (textoAMonto(form.monto) == null) "Ingresa un monto mayor a 0" else null,
            fecha = when {
                form.fecha == null -> "Selecciona la fecha"
                form.fecha.isAfter(LocalDate.now()) -> "La fecha no puede ser futura"
                else -> null
            },
            // Regla de negocio: todo gasto debe tener su comprobante como respaldo
            comprobante = if (form.tipo == TipoMovimiento.GASTO && form.comprobanteUri == null) {
                "Adjunta la foto de la boleta o comprobante del gasto"
            } else {
                null
            }
        )
        _formulario.update { it.copy(errores = errores) }
        return !errores.hayErrores
    }

    // Solo tesorería puede registrar movimientos; devuelve true si se guardó
    fun guardar(usuario: Usuario?): Boolean {
        if (usuario?.puedeRegistrarMovimientos != true || !validarFormulario()) return false
        val form = _formulario.value
        val categoria = form.categoria ?: return false
        val monto = textoAMonto(form.monto) ?: return false
        val fecha = form.fecha ?: return false

        repository.agregar(
            Movimiento(
                id = 0,
                tipo = form.tipo,
                categoria = categoria,
                descripcion = form.descripcion.trim(),
                monto = monto,
                fecha = fecha,
                registradoPor = usuario.nombre,
                comprobanteUri = form.comprobanteUri
            )
        )
        mostrarMensaje("${form.tipo.etiqueta} registrado correctamente")
        limpiarFormulario()
        return true
    }

    fun eliminar(movimiento: Movimiento, usuario: Usuario?) {
        if (usuario?.puedeRegistrarMovimientos != true) return
        repository.eliminar(movimiento.id)
        mostrarMensaje("Movimiento eliminado")
    }

    private fun mostrarMensaje(texto: String) {
        trabajoMensaje?.cancel()
        trabajoMensaje = viewModelScope.launch {
            _mensaje.value = texto
            _mostrarMensaje.value = true
            delay(2_500)
            _mostrarMensaje.value = false
        }
    }
}
