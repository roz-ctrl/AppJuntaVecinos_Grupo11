package cl.duoc.grupo11.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.grupo11.juntavecinos.model.CategoriaMovimiento
import cl.duoc.grupo11.juntavecinos.model.Dependencia
import cl.duoc.grupo11.juntavecinos.model.EstadoReserva
import cl.duoc.grupo11.juntavecinos.model.Movimiento
import cl.duoc.grupo11.juntavecinos.model.Reserva
import cl.duoc.grupo11.juntavecinos.model.TipoMovimiento
import cl.duoc.grupo11.juntavecinos.model.Usuario
import cl.duoc.grupo11.juntavecinos.repository.MovimientoRepository
import cl.duoc.grupo11.juntavecinos.repository.MovimientoRepositoryMemoria
import cl.duoc.grupo11.juntavecinos.repository.ReservaRepository
import cl.duoc.grupo11.juntavecinos.repository.ReservaRepositoryMemoria
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

enum class FiltroReserva(val etiqueta: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    CONFIRMADAS("Confirmadas"),
    HISTORIAL("Rechazadas y canceladas")
}

// Errores de validación del formulario de reserva (null = sin error)
data class ReservaErrores(
    val fecha: String? = null,
    val horario: String? = null,
    val motivo: String? = null
)

// Estado del formulario de reserva (crear o editar)
data class ReservaFormState(
    val id: Int = NUEVA_RESERVA,
    val dependencia: Dependencia = Dependencia.SALON_PRINCIPAL,
    val fecha: LocalDate? = null,
    val horaInicio: LocalTime = LocalTime.of(10, 0),
    val horaFin: LocalTime = LocalTime.of(13, 0),
    val motivo: String = "",
    val errores: ReservaErrores = ReservaErrores()
) {
    val esEdicion: Boolean get() = id != NUEVA_RESERVA

    // Monto estimado según la tarifa: se recalcula al cambiar dependencia u horario
    val montoEstimado: Int
        get() = if (horaFin.isAfter(horaInicio)) {
            Duration.between(horaInicio, horaFin).toHours().toInt() * dependencia.tarifaPorHora
        } else {
            0
        }

    companion object {
        const val NUEVA_RESERVA = -1
    }
}

// ViewModel compartido entre la lista de reservas y su formulario
class ReservaViewModel(
    private val repository: ReservaRepository = ReservaRepositoryMemoria,
    private val movimientoRepository: MovimientoRepository = MovimientoRepositoryMemoria
) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroReserva.TODAS)
    val filtro: StateFlow<FiltroReserva> = _filtro.asStateFlow()

    // Lista filtrada y ordenada por fecha; se actualiza sola cuando cambia el repositorio
    val reservas: StateFlow<List<Reserva>> = combine(repository.reservas, _filtro) { lista, filtro ->
        lista.filter { reserva ->
            when (filtro) {
                FiltroReserva.TODAS -> true
                FiltroReserva.PENDIENTES -> reserva.estado == EstadoReserva.PENDIENTE
                FiltroReserva.CONFIRMADAS -> reserva.estado == EstadoReserva.CONFIRMADA
                FiltroReserva.HISTORIAL -> !reserva.estaVigente
            }
        }.sortedWith(compareBy({ it.fecha }, { it.horaInicio }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _formulario = MutableStateFlow(ReservaFormState())
    val formulario: StateFlow<ReservaFormState> = _formulario.asStateFlow()

    // Mensaje de confirmación que se muestra con animación (AnimatedVisibility)
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()
    private val _mostrarMensaje = MutableStateFlow(false)
    val mostrarMensaje: StateFlow<Boolean> = _mostrarMensaje.asStateFlow()
    private var trabajoMensaje: Job? = null

    fun cambiarFiltro(filtro: FiltroReserva) {
        _filtro.value = filtro
    }

    // ---------- Formulario ----------

    // Carga la reserva a editar o deja el formulario vacío para una nueva
    fun prepararFormulario(reservaId: Int) {
        val existente = repository.buscarPorId(reservaId)
        _formulario.value = existente?.let {
            ReservaFormState(
                id = it.id,
                dependencia = it.dependencia,
                fecha = it.fecha,
                horaInicio = it.horaInicio,
                horaFin = it.horaFin,
                motivo = it.motivo
            )
        } ?: ReservaFormState()
    }

    fun onDependenciaChange(valor: Dependencia) {
        _formulario.update { it.copy(dependencia = valor, errores = it.errores.copy(horario = null)) }
    }

    fun onFechaChange(valor: LocalDate) {
        _formulario.update { it.copy(fecha = valor, errores = it.errores.copy(fecha = null, horario = null)) }
    }

    fun onHoraInicioChange(valor: LocalTime) {
        _formulario.update { it.copy(horaInicio = valor, errores = it.errores.copy(horario = null)) }
    }

    fun onHoraFinChange(valor: LocalTime) {
        _formulario.update { it.copy(horaFin = valor, errores = it.errores.copy(horario = null)) }
    }

    fun onMotivoChange(valor: String) {
        _formulario.update { it.copy(motivo = valor, errores = it.errores.copy(motivo = null)) }
    }

    // Valida todos los campos y las reglas de negocio; devuelve true si se puede guardar
    fun validarFormulario(): Boolean {
        val form = _formulario.value
        val errores = ReservaErrores(
            fecha = when {
                form.fecha == null -> "Selecciona una fecha"
                form.fecha.isBefore(LocalDate.now()) -> "La fecha no puede ser anterior a hoy"
                else -> null
            },
            horario = when {
                !form.horaFin.isAfter(form.horaInicio) -> "La hora de término debe ser posterior a la de inicio"
                hayCruceDeHorario(form) -> "Ya existe una reserva en ese horario para ${form.dependencia.nombre}"
                else -> null
            },
            motivo = when {
                form.motivo.isBlank() -> "Indica el motivo del arriendo"
                form.motivo.trim().length < 5 -> "El motivo debe tener al menos 5 caracteres"
                else -> null
            }
        )
        _formulario.update { it.copy(errores = errores) }
        return errores.fecha == null && errores.horario == null && errores.motivo == null
    }

    // Regla de negocio: no puede haber dos reservas vigentes que se crucen
    private fun hayCruceDeHorario(form: ReservaFormState): Boolean {
        val fecha = form.fecha ?: return false
        val candidata = Reserva(
            id = form.id,
            dependencia = form.dependencia,
            solicitanteId = 0,
            solicitante = "",
            fecha = fecha,
            horaInicio = form.horaInicio,
            horaFin = form.horaFin,
            motivo = form.motivo
        )
        return repository.reservas.value.any { candidata.seCruzaCon(it) }
    }

    // Guarda la reserva (nueva o editada); devuelve true si se guardó
    fun guardar(usuario: Usuario?): Boolean {
        if (usuario == null || !usuario.puedeSolicitarReservas || !validarFormulario()) return false
        val form = _formulario.value
        val fecha = form.fecha ?: return false

        if (form.esEdicion) {
            val original = repository.buscarPorId(form.id) ?: return false
            repository.actualizar(
                original.copy(
                    dependencia = form.dependencia,
                    fecha = fecha,
                    horaInicio = form.horaInicio,
                    horaFin = form.horaFin,
                    motivo = form.motivo.trim(),
                    estado = EstadoReserva.PENDIENTE
                )
            )
            mostrarMensaje("Reserva actualizada")
        } else {
            repository.agregar(
                Reserva(
                    id = 0,
                    dependencia = form.dependencia,
                    solicitanteId = usuario.id,
                    solicitante = usuario.nombre,
                    fecha = fecha,
                    horaInicio = form.horaInicio,
                    horaFin = form.horaFin,
                    motivo = form.motivo.trim()
                )
            )
            mostrarMensaje("Solicitud enviada: queda pendiente de aprobación")
        }
        _formulario.value = ReservaFormState()
        return true
    }

    // ---------- Acciones según el perfil ----------

    // Directiva: aprueba una solicitud pendiente
    fun aprobar(reserva: Reserva, usuario: Usuario?) {
        if (usuario?.puedeGestionarReservas != true) return
        repository.actualizar(reserva.copy(estado = EstadoReserva.CONFIRMADA))
        mostrarMensaje("Reserva confirmada")
    }

    // Directiva: rechaza una solicitud pendiente
    fun rechazar(reserva: Reserva, usuario: Usuario?) {
        if (usuario?.puedeGestionarReservas != true) return
        repository.actualizar(reserva.copy(estado = EstadoReserva.RECHAZADA))
        mostrarMensaje("Reserva rechazada")
    }

    // Solicitante: cancela su propia solicitud
    fun cancelar(reserva: Reserva, usuario: Usuario?) {
        if (usuario?.id != reserva.solicitanteId) return
        repository.actualizar(reserva.copy(estado = EstadoReserva.CANCELADA))
        mostrarMensaje("Reserva cancelada")
    }

    // Directiva: elimina del historial una reserva rechazada o cancelada
    fun eliminar(reserva: Reserva, usuario: Usuario?) {
        if (usuario?.puedeGestionarReservas != true || reserva.estaVigente) return
        repository.eliminar(reserva.id)
        mostrarMensaje("Reserva eliminada del historial")
    }

    // Tesorería: registra el pago del arriendo como un ingreso en finanzas
    fun registrarPago(reserva: Reserva, usuario: Usuario?) {
        if (usuario?.puedeRegistrarMovimientos != true) return
        if (reserva.pagada || reserva.estado != EstadoReserva.CONFIRMADA) return
        movimientoRepository.agregar(
            Movimiento(
                id = 0,
                tipo = TipoMovimiento.INGRESO,
                categoria = CategoriaMovimiento.ARRIENDO,
                descripcion = "Arriendo ${reserva.dependencia.nombre} (${reserva.solicitante})",
                monto = reserva.monto,
                fecha = LocalDate.now(),
                registradoPor = usuario.nombre,
                reservaId = reserva.id
            )
        )
        repository.actualizar(reserva.copy(pagada = true))
        mostrarMensaje("Pago registrado en finanzas")
    }

    // Muestra el mensaje y lo oculta automáticamente después de unos segundos
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
