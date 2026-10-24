# AppJuntaVecinos_Grupo11

Aplicación móvil Android de **gestión de arriendos y transparencia financiera** para la Junta de Vecinos Población Zaror (Cerrillos).

Proyecto semestral de **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC), sección 008D.

## Integrantes – Grupo 11

- Vicente Ignacio Caba Molina
- José Tomas Rojas Quiroga
- Pedro Hernán Ponce Romero

## Contexto

La Junta de Vecinos arrienda su sede comunitaria (2 a 3 arriendos al mes) y administra los recursos asociados, incluidos los consumos básicos. Hoy el control de reservas es manual, con riesgo de duplicidad, y la información financiera está dispersa en documentos y planillas. Eso hace difícil rendir cuentas a tiempo y limita el acceso de los vecinos a la información.

**Objetivo:** ordenar la administración de arriendos, mantener al día los ingresos y gastos y dar a los vecinos acceso permanente a la información financiera.

## Perfiles de usuario

| Perfil | Funciones |
|---|---|
| Vecino/a | Solicita el arriendo de dependencias y consulta la información financiera |
| Directiva / Secretaría | Verifica la disponibilidad de fechas y horarios, y aprueba o rechaza reservas |
| Tesorería | Registra pagos, ingresos y gastos (arriendos y consumos básicos) y prepara rendiciones |

## Alcance del MVP

- [ ] Solicitud de reserva con validación de disponibilidad
- [ ] Aprobación o rechazo de reservas por la directiva
- [ ] Registro de ingresos y gastos por tesorería
- [ ] Foto del comprobante con la cámara del dispositivo
- [ ] Resumen financiero visible para los vecinos (ingresos, gastos, saldo)
- [ ] Notificaciones de confirmación de reserva, pagos y recordatorios
- [ ] Persistencia local con Room (SQLite)
- [ ] Microservicio propio en Spring Boot consumido con Retrofit

## Reglas de negocio

1. No pueden existir dos reservas de la misma dependencia que se crucen en fecha y horario.
2. Solo la directiva puede aprobar o rechazar una reserva.
3. Solo tesorería puede registrar movimientos de ingreso o gasto.
4. Todo monto debe ser mayor a cero.
5. Los vecinos pueden consultar la información financiera, pero no modificarla.
6. El registro de pagos es informativo: la app no procesa transacciones reales ni se conecta con bancos o pasarelas de pago.
7. Todos los datos usados en el desarrollo (nombres, montos, horarios) son ficticios.

## Tecnologías

Kotlin · Android Studio · Jetpack Compose · Material Design 3 · MVVM · StateFlow · Navigation Compose · Room/SQLite · Retrofit · Spring Boot

## Estructura del proyecto

```
app/src/main/java/cl/duoc/grupo11/juntavecinos/
├── MainActivity.kt
├── model/         → Clases de datos (Reserva, Movimiento, Rol)
├── repository/    → Origen de datos (por ahora simulado en memoria)
├── viewmodel/     → Lógica de presentación y estado (StateFlow)
└── ui/
    ├── screens/   → Pantallas Composable
    └── theme/     → Colores, tipografía y tema Material 3
```

## Cómo ejecutar

1. Clonar el repositorio y abrir la carpeta en Android Studio.
2. Esperar a que termine la sincronización de Gradle.
3. Seleccionar un emulador o un dispositivo físico (Android 8.0 o superior) y presionar **Run 'app'**.

## Forma de trabajo

- Rama principal: `main`. Cada funcionalidad se desarrolla en su propia rama `feature/...` (por ejemplo, `feature/home-screen`).
- Las tareas se planifican en Trello con las listas *Por hacer*, *En curso* y *Finalizado*.
