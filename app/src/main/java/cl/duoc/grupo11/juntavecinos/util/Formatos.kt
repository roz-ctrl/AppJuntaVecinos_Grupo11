package cl.duoc.grupo11.juntavecinos.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private val localeChile: Locale = Locale.forLanguageTag("es-CL")
private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val formatoFechaLarga = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", localeChile)
private val formatoHora = DateTimeFormatter.ofPattern("HH:mm")

// Formatea montos en pesos chilenos, por ejemplo: $25.000 o -$5.000
fun Int.comoPesos(): String {
    val texto = String.format(localeChile, "%,d", abs(this))
    return if (this < 0) "-\$$texto" else "\$$texto"
}

fun LocalDate.comoTexto(): String = format(formatoFecha)

// Ejemplo: "Viernes 13 de noviembre"
fun LocalDate.comoTextoLargo(): String =
    format(formatoFechaLarga).replaceFirstChar { it.uppercase() }

fun LocalTime.comoTexto(): String = format(formatoHora)
