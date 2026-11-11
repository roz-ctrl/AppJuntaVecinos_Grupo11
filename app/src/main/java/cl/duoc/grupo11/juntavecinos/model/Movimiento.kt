package cl.duoc.grupo11.juntavecinos.model

import java.time.LocalDate

enum class TipoMovimiento(val etiqueta: String) {
    INGRESO("Ingreso"),
    GASTO("Gasto")
}

// Cada categoría pertenece a un tipo de movimiento
enum class CategoriaMovimiento(val etiqueta: String, val tipo: TipoMovimiento) {
    ARRIENDO("Arriendo de sede", TipoMovimiento.INGRESO),
    CUOTA_SOCIAL("Cuota social", TipoMovimiento.INGRESO),
    DONACION("Donación", TipoMovimiento.INGRESO),
    LUZ("Luz", TipoMovimiento.GASTO),
    AGUA("Agua", TipoMovimiento.GASTO),
    GAS("Gas", TipoMovimiento.GASTO),
    INTERNET("Internet", TipoMovimiento.GASTO),
    MANTENCION("Mantención", TipoMovimiento.GASTO),
    OTRO_GASTO("Otro gasto", TipoMovimiento.GASTO);

    companion object {
        fun delTipo(tipo: TipoMovimiento): List<CategoriaMovimiento> =
            entries.filter { it.tipo == tipo }
    }
}

// Ingreso o gasto registrado por tesorería. El registro es solo informativo:
// la app no procesa pagos reales ni se conecta con bancos.
data class Movimiento(
    val id: Int,
    val tipo: TipoMovimiento,
    val categoria: CategoriaMovimiento,
    val descripcion: String,
    val monto: Int,
    val fecha: LocalDate,
    val registradoPor: String,
    val comprobanteUri: String? = null,
    val reservaId: Int? = null
) {
    // Monto con signo: positivo para ingresos y negativo para gastos
    val montoConSigno: Int
        get() = if (tipo == TipoMovimiento.INGRESO) monto else -monto
}
