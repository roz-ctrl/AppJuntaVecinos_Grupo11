package cl.duoc.grupo11.juntavecinos.util

// Validaciones reutilizables en los formularios (sin dependencias de Android para poder probarlas)
private val patronCorreo = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")

fun esCorreoValido(correo: String): Boolean = patronCorreo.matches(correo.trim())

// Convierte un texto a monto entero positivo; devuelve null si no es válido
fun textoAMonto(texto: String): Int? = texto.trim().toIntOrNull()?.takeIf { it > 0 }
