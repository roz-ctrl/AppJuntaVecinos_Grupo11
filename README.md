# AppJuntaVecinos_Grupo11

Aplicación móvil Android de **gestión de arriendos y transparencia financiera** para la Junta de Vecinos Población Zaror (Cerrillos).

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

- [x] Inicio de sesión con perfiles (vecino/a, directiva y tesorería) y validaciones
- [x] Solicitud, edición y cancelación de reservas con validación de disponibilidad
- [x] Aprobación o rechazo de reservas por la directiva
- [x] Registro del pago de un arriendo como ingreso
- [x] Registro y eliminación de ingresos y gastos por tesorería
- [x] Foto del comprobante con la cámara o la galería del dispositivo
- [x] Resumen financiero y rendición de gastos por categoría, visibles para los vecinos
- [x] Notificaciones de confirmación de reservas y pagos
- [x] Foto de perfil, modo oscuro guardado con DataStore y diseño adaptable (teléfono y tablet)
- [ ] Persistencia local con Room (SQLite)
- [ ] Microservicio propio en Spring Boot consumido con Retrofit

## Funcionalidades por pantalla

| Pantalla | Qué permite |
|---|---|
| Login | Formulario con validación de correo y contraseña, indicador de carga y cuentas de prueba |
| Inicio | Saludo según el perfil, resumen financiero, próximas reservas y accesos rápidos |
| Reservas | Lista con filtros por estado y acciones según el perfil (aprobar, rechazar, cancelar, editar, registrar pago) |
| Solicitud de reserva | Dependencia, fecha (calendario), horario y motivo, con monto estimado en tiempo real |
| Finanzas | Resumen, rendición por categoría, lista de movimientos con filtros y comprobantes |
| Nuevo movimiento | Tipo, categoría, descripción, monto, fecha y foto del comprobante |
| Mi perfil | Foto de perfil, datos, permisos del perfil, modo oscuro y cierre de sesión |

## Cuentas de prueba (ficticias)

| Perfil | Correo | Contraseña |
|---|---|---|
| Vecino/a | vecino@zaror.cl | vecino123 |
| Directiva | directiva@zaror.cl | directiva123 |
| Tesorería | tesoreria@zaror.cl | tesoreria123 |

## Reglas de negocio

1. No pueden existir dos reservas de la misma dependencia que se crucen en fecha y horario.
2. Solo la directiva puede aprobar o rechazar una reserva.
3. Solo tesorería puede registrar movimientos de ingreso o gasto.
4. Todo monto debe ser mayor a cero y la fecha de una reserva no puede ser anterior a hoy.
5. Todo gasto debe tener la foto de su boleta o comprobante como respaldo.
6. Solo se puede registrar el pago de una reserva confirmada, y una sola vez.
7. Los vecinos pueden consultar la información financiera, pero no modificarla.
8. El registro de pagos es informativo: la app no procesa transacciones reales ni se conecta con bancos o pasarelas de pago.
9. Todos los datos usados en el desarrollo (nombres, montos, horarios) son ficticios.

## Tecnologías

Kotlin · Android Studio · Jetpack Compose · Material Design 3 · MVVM · StateFlow · Navigation Compose · Window Size Classes · DataStore · Coil · JUnit

## Arquitectura (MVVM) y POO

- **Model:** clases de datos con sus reglas de negocio. `Usuario` es una clase sellada con las subclases `Vecino`, `Directiva` y `Tesorero`, que sobrescriben sus permisos (herencia y polimorfismo); la clave es privada (encapsulamiento). `Reserva` calcula su monto y valida si se cruza con otra (`seCruzaCon`).
- **Repository:** interfaces (`ReservaRepository`, `MovimientoRepository`, `UsuarioRepository`) con implementaciones en memoria. Así se pueden reemplazar por Room o por la API sin cambiar los ViewModels.
- **ViewModel:** manejan el estado con `StateFlow`, validan los formularios (estado + errores por campo) y aplican los permisos de cada perfil.
- **View:** pantallas Composable que solo observan el estado (`collectAsState`) y envían eventos al ViewModel.

## Estructura del proyecto

```
app/src/main/java/cl/duoc/grupo11/juntavecinos/
├── MainActivity.kt
├── model/          → Clases de modelo: Usuario, Reserva, Movimiento, Dependencia, ResumenFinanciero
├── repository/     → Interfaces del origen de datos y su implementación en memoria
├── data/           → Preferencias guardadas con DataStore
├── viewmodel/      → Lógica de presentación y estado (StateFlow)
├── navigation/     → Rutas (sealed class Screen) y NavHost
├── util/           → Formatos, validaciones, notificaciones y archivos de imagen
└── ui/
    ├── screens/    → Pantallas Composable
    ├── components/ → Componentes reutilizables (tarjetas, selectores, ImagenInteligente)
    ├── utils/      → Cálculo del tamaño de ventana (diseño adaptable)
    └── theme/      → Colores, tipografía y tema Material 3

app/src/test/       → Pruebas unitarias (JUnit) de modelos, validaciones y ViewModel
```

## Forma de trabajo

- Rama principal: `main`. Cada funcionalidad se desarrolla en su propia rama `feature/...` y se integra a `main` con un merge.
- Las tareas se planifican en Trello con las listas *Por hacer*, *En curso* y *Finalizado*.
