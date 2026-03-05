# 📁 Mapa Completo de Archivos - Sistema Bancario DDD

## 🏗️ Estructura Creada

```
clinic/
│
├── 📄 ARQUITECTURA_DDD.md           ← Descripción completa de la arquitectura
├── 📄 GUIA_USO_SERVICIOS.md         ← 9 ejemplos de código + flujos
├── 📄 PROYECTO_COMPLETADO.md        ← Resumen de lo entregado
├── 📄 MAPA_ARCHIVOS.md              ← Este archivo
│
└── src/main/java/app/
    │
    └── domain/bank/                 ← 🔵 CAPA DE DOMINIO
        │
        ├── models/                  (8 archivos)
        │   ├── Usuario.java                        [Entidad] Usuario del sistema
        │   ├── EstadoUsuario.java                  [Enum] Estados del usuario
        │   ├── ClientePersonaNatural.java          [Entidad] Persona física
        │   ├── ClienteEmpresa.java                 [Entidad] Persona jurídica
        │   ├── CuentaBancaria.java                 [Agregado] Gestión de cuentas
        │   ├── Prestamo.java                       [Agregado] Gestión de préstamos
        │   └── Transferencia.java                  [Agregado] Gestión de transferencias
        │
        ├── valueobjects/            (11 archivos)
        │   ├── Dinero.java                         [VO] Cantidad monetaria inmutable
        │   ├── Email.java                          [VO] Email validado
        │   ├── Telefono.java                       [VO] Teléfono validado
        │   ├── Identificacion.java                 [VO] DNI/Cédula/NIT
        │   ├── NumeroCuenta.java                   [VO] Número de cuenta
        │   ├── TipoCuenta.java                     [Enum] AHORROS, CORRIENTE, etc.
        │   ├── EstadoCuenta.java                   [Enum] ACTIVA, BLOQUEADA, CANCELADA
        │   ├── EstadoPrestamo.java                 [Enum] EN_ESTUDIO, APROBADO, etc.
        │   ├── EstadoTransferencia.java            [Enum] PENDIENTE, EJECUTADA, etc.
        │   ├── TipoPrestamo.java                   [Enum] CONSUMO, HIPOTECARIO, etc.
        │   └── RolBancario.java                    [Enum] Roles del sistema
        │
        ├── services/                (3 archivos)
        │   ├── ServicioTransferencia.java          [Domain Service] Lógica de transferencias
        │   ├── ServicioPrestamo.java               [Domain Service] Lógica de préstamos
        │   └── ServicioCuenta.java                 [Domain Service] Lógica de cuentas
        │
        └── ports/                   (7 archivos)
            ├── TransferenciaPort.java              [Port] Contrato de persistencia
            ├── CuentaBancariaPort.java             [Port] Contrato de persistencia
            ├── PrestamoPort.java                   [Port] Contrato de persistencia
            ├── UsuarioPort.java                    [Port] Contrato de persistencia
            ├── ClientePersonaNaturalPort.java      [Port] Contrato de persistencia
            ├── ClienteEmpresaPort.java             [Port] Contrato de persistencia
            └── BitacoraPort.java                   [Port] Contrato para Bitácora NoSQL
    
    └── application/bank/            ← 🟠 CAPA DE APLICACIÓN
        │
        ├── services/                (3 archivos)
        │   ├── ServicioAplicacionTransferencia.java   [App Service] Casos de uso: crear, aprobar, verificar
        │   ├── ServicioAplicacionPrestamo.java        [App Service] Casos de uso: crear, aprobar, desembolsar
        │   └── ServicioAplicacionCuenta.java          [App Service] Casos de uso: abrir, bloquear
        │
        └── dtos/                    (4 archivos)
            ├── CrearTransferenciaDTO.java          [DTO] Request para transferencia
            ├── CrearSolicitudPrestamoDTO.java      [DTO] Request para préstamo
            ├── CrearCuentaBancariaDTO.java         [DTO] Request para cuenta
            └── RespuestaOperacionDTO.java          [DTO] Response estándar
```

## 📊 Resumen por Capa

### **Capa de Dominio** (36 archivos Java)

#### Value Objects (11)
```
✓ Dinero.java              → Aritmética monetaria segura
✓ Email.java               → Validación de formato
✓ Telefono.java            → Validación de dígitos
✓ Identificacion.java      → Identificador único
✓ NumeroCuenta.java        → IBAN/Número de cuenta
✓ TipoCuenta.java          → Tipos de cuenta disponibles
✓ EstadoCuenta.java        → Estados de cuenta
✓ EstadoPrestamo.java      → Ciclo de vida de préstamo
✓ EstadoTransferencia.java → Ciclo de vida de transferencia
✓ TipoPrestamo.java        → Tipos de crédito
✓ RolBancario.java         → Roles del sistema
```

#### Entidades (8)
```
✓ Usuario.java             → Usuario del sistema con credenciales
✓ EstadoUsuario.java       → ACTIVO, INACTIVO, BLOQUEADO
✓ ClientePersonaNatural.java → Persona física > 18 años
✓ ClienteEmpresa.java      → Entidad jurídica con representante
✓ CuentaBancaria.java      → Agregado de cuenta bancaria
✓ Prestamo.java            → Agregado de préstamo/crédito
✓ Transferencia.java       → Agregado de transferencia
```

#### Servicios de Dominio (3)
```
✓ ServicioTransferencia.java
  - Ejecutar transferencia entre cuentas
  - Validar fondos suficientes
  - Marcar vencidas automáticamente
  - Registrar en Bitácora

✓ ServicioPrestamo.java
  - Aprobar/Rechazar préstamos
  - Desembolsar a cuenta
  - Actualizar saldo
  - Registrar en Bitácora

✓ ServicioCuenta.java
  - Abrir nueva cuenta
  - Bloquear/Desbloquear
  - Validar estado
  - Registrar en Bitácora
```

#### Puertos (Interfaces) (7)
```
✓ TransferenciaPort.java        → guardar(), obtenerPorId(), actualizar(), etc.
✓ CuentaBancariaPort.java       → guardar(), obtenerPorNumeroCuenta(), actualizar(), etc.
✓ PrestamoPort.java             → guardar(), obtenerPorId(), obtenerPorEstado(), etc.
✓ UsuarioPort.java              → guardar(), obtenerPorIdentificacion(), obtenerPorNombreUsuario(), etc.
✓ ClientePersonaNaturalPort.java → guardar(), obtenerPorId(), obtenerPorIdentificacion(), etc.
✓ ClienteEmpresaPort.java       → guardar(), obtenerPorNit(), actualizar(), etc.
✓ BitacoraPort.java             → registrar(), obtenerPorId(), obtenerPorProducto(), etc.
```

### **Capa de Aplicación** (7 archivos Java)

#### Servicios de Aplicación (3)
```
✓ ServicioAplicacionTransferencia.java
  - crearTransferencia(CrearTransferenciaDTO): RespuestaOperacionDTO
  - aprobarTransferencia(Long, Long): RespuestaOperacionDTO
  - rechazarTransferencia(Long, Long): RespuestaOperacionDTO
  - verificarYMarcarTransferenciasVencidas(): RespuestaOperacionDTO

✓ ServicioAplicacionPrestamo.java
  - crearSolicitudPrestamo(CrearSolicitudPrestamoDTO): RespuestaOperacionDTO
  - aprobarPrestamo(Long, BigDecimal, String): RespuestaOperacionDTO
  - rechazarPrestamo(Long): RespuestaOperacionDTO
  - desembolsarPrestamo(Long, String): RespuestaOperacionDTO

✓ ServicioAplicacionCuenta.java
  - abrirCuenta(CrearCuentaBancariaDTO): RespuestaOperacionDTO
  - bloquearCuenta(Long, String): RespuestaOperacionDTO
  - desbloquearCuenta(Long): RespuestaOperacionDTO
  - obtenerInformacionCuenta(String): CuentaBancaria
```

#### DTOs (4)
```
✓ CrearTransferenciaDTO.java
  - cuentaOrigen: String
  - cuentaDestino: String
  - monto: String
  - moneda: String
  - idUsuarioCreador: Long

✓ CrearSolicitudPrestamoDTO.java
  - numeroIdentificacionCliente: String
  - tipoPrestamo: String
  - montoSolicitado: BigDecimal
  - tasaInteres: BigDecimal
  - plazoMeses: Integer
  - moneda: String

✓ CrearCuentaBancariaDTO.java
  - numeroIdentificacionTitular: String
  - numeroCuenta: String
  - tipoCuenta: String
  - saldoInicial: String
  - moneda: String

✓ RespuestaOperacionDTO.java
  - exitoso: boolean
  - mensaje: String
  - idOperacion: String
  - codigoError: String
```

### **Documentación** (4 archivos Markdown)

```
✓ ARQUITECTURA_DDD.md           (500+ líneas)
  - Descripción de arquitectura
  - Componentes principales
  - Casos de uso implementados
  - Validaciones de negocio
  - Diagramas ASCII
  - Referencia de clases
  - Próximos pasos

✓ GUIA_USO_SERVICIOS.md         (600+ líneas)
  - 9 ejemplos de código
  - Flujos completos paso a paso
  - Matriz de permisos por rol
  - Todas las validaciones
  - Escenarios del mundo real

✓ PROYECTO_COMPLETADO.md        (300+ líneas)
  - Resumen de entregables
  - Checklist de implementación
  - Estadísticas del proyecto
  - Próximos pasos
  - Matriz de responsabilidades

✓ MAPA_ARCHIVOS.md              ← Este archivo
  - Estructura de directorios
  - Descripción de cada archivo
  - Referencia rápida
```

## 🔍 Búsqueda Rápida

### Necesito... encontrar el archivo de...

| Necesito... | Archivo |
|---|---|
| Validar un email | `Email.java` |
| Sumar dinero | `Dinero.java` |
| Crear una transferencia | `ServicioAplicacionTransferencia.java` |
| Ejecutar transferencia | `ServicioTransferencia.java` |
| Aprobar préstamo | `ServicioAplicacionPrestamo.java` |
| Estadios de transferencia | `EstadoTransferencia.java` |
| Respuesta estándar | `RespuestaOperacionDTO.java` |
| Información de usuario | `Usuario.java` |
| Abrir cuenta | `ServicioAplicacionCuenta.java` |
| Registrar operación | `BitacoraPort.java` |
| Entender arquitectura | `ARQUITECTURA_DDD.md` |
| Ver ejemplos de código | `GUIA_USO_SERVICIOS.md` |

## 📐 Relaciones entre Componentes

```
┌─────────────────────────────────────────────────────────┐
│                 SERVICIO DE APLICACIÓN                   │
│  (ServicioAplicacionTransferencia, etc.)                │
└─────────────┬───────────────────────────────────────────┘
              │
              ├─→ DTO (CrearTransferenciaDTO)
              ├─→ RespuestaOperacionDTO
              └─→ SERVICIO DE DOMINIO
                   │
┌──────────────────▼──────────────────────────────────────┐
│            SERVICIO DE DOMINIO                           │
│  (ServicioTransferencia, ServicioPrestamo, etc.)        │
└──────────────────┬──────────────────────────────────────┘
              │
              ├─→ ENTIDADES (Transferencia, CuentaBancaria)
              │    ├─→ VALUE OBJECTS (Dinero, NumeroCuenta)
              │    └─→ ENUMERACIONES (EstadoTransferencia)
              │
              ├─→ PUERTOS (TransferenciaPort, CuentaBancariaPort)
              │
              └─→ Bitácora (BitacoraPort)
```

## ✅ Verificación de Implementación

### Value Objects (Inmutables)
- ✅ Dinero: Operaciones seguras (+, -, comparación)
- ✅ Email: Validación de formato
- ✅ Telefono: Validación de dígitos
- ✅ Identificacion: Formato válido
- ✅ NumeroCuenta: Número único
- ✅ Enumeraciones: Estados y tipos definidos

### Entidades (Con Identidad)
- ✅ Usuario: ID único, datos de autenticación
- ✅ ClientePersonaNatural: Mayor de 18 años
- ✅ ClienteEmpresa: Con representante legal
- ✅ CuentaBancaria: Agregado con comportamiento
- ✅ Prestamo: Agregado con ciclo de vida
- ✅ Transferencia: Agregado con vencimiento

### Servicios de Dominio
- ✅ Orquestan múltiples agregados
- ✅ Contienen lógica compleja
- ✅ Registran en Bitácora
- ✅ Validan reglas de negocio

### Servicios de Aplicación
- ✅ Implementan casos de uso
- ✅ Usan DTOs para entrada/salida
- ✅ Delegan a servicios de dominio
- ✅ Retornan respuestas estructuradas

### Puertos (Interfaces)
- ✅ Abstractos de persistencia
- ✅ Sin implementación
- ✅ Inversión de dependencias
- ✅ Fáciles de mockear

## 🚀 Cómo Usar Esta Estructura

### 1. Entender la Arquitectura
```
Leer: ARQUITECTURA_DDD.md
Tiempo: 15-20 minutos
Comprenda: Capas, patrones, flujos
```

### 2. Ver Ejemplos de Código
```
Leer: GUIA_USO_SERVICIOS.md
Tiempo: 20-30 minutos
Ejecute: Los ejemplos mentalmente o en IDE
```

### 3. Implementar Adaptadores
```
Crear: Repositories, Controllers, Config
Tiempo: 2-3 días
Implementar: JPA, REST, MongoDB
```

### 4. Agregar Seguridad
```
Agregar: Spring Security, JWT, Encriptación
Tiempo: 1-2 días
Proteger: Endpoints, validar roles
```

### 5. Escribir Tests
```
Crear: Unit tests, Integration tests
Tiempo: 2-3 días
Cubrir: >80% del código
```

---

## 📞 Referencia Rápida

| Concepto | Ubicación | Responsabilidad |
|----------|-----------|---|
| Dinero seguro | `Dinero.java` | Validar monto, sumar, restar |
| Email válido | `Email.java` | Validar formato @ dominio |
| Transferencia | `Transferencia.java` | Aprobar, rechazar, ejecutar, vencer |
| Cuenta | `CuentaBancaria.java` | Depositar, retirar, cambiar estado |
| Préstamo | `Prestamo.java` | Aprobar, rechazar, desembolsar |
| Crear transferencia | `ServicioAplicacionTransferencia.java` | Caso de uso: crear y ejecutar |
| Ejecutar transferencia | `ServicioTransferencia.java` | Lógica: validar fondos, actualizar saldos |
| Auditoría completa | `BitacoraPort.java` | Registrar todas las operaciones |

---

**Todos los archivos están listos para la siguiente etapa de implementación.**

¡Listo para construir los adaptadores y controladores REST! 🚀
