package cl.duoc.grupo11.juntavecinos.model

import java.time.LocalDate

enum class TipoMovimiento { INGRESO, GASTO }

// Ingreso o gasto registrado por tesorería. El registro es solo informativo:
// la app no procesa pagos reales. El comprobante se adjuntará con la cámara.
data class Movimiento(
    val id: Int,
    val tipo: TipoMovimiento,
    val categoria: String,
    val descripcion: String,
    val monto: Int,
    val fecha: LocalDate,
    val comprobanteUri: String? = null
)
