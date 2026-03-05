# 📖 Guía de Uso - Servicios Bancarios DDD

## 🎓 Ejemplos de Uso de los Servicios

### 📌 Ejemplo 1: Crear y Ejecutar una Transferencia Inmediata

#### Escenario
Un cliente desea transferir $500 entre sus propias cuentas (no requiere aprobación).

#### Código

```java
// 1. Inyectar dependencias
@SpringBootApplication
public class BancoApplication {
    @Bean
    public ServicioAplicacionTransferencia servicioAplicacionTransferencia(
        TransferenciaPort transferenciaPort,
        CuentaBancariaPort cuentaPort,
        ServicioTransferencia servicioTransferenciaDominio
    ) {
        return new ServicioAplicacionTransferencia(
            transferenciaPort,
            cuentaPort,
            servicioTransferenciaDominio
        );
    }
}

// 2. Usar el servicio
@Service
public class TransferenciaBancaria {
    private final ServicioAplicacionTransferencia servicioTransferencia;

    public void realizarTransferencia() {
        CrearTransferenciaDTO request = new CrearTransferenciaDTO(
            "CUENTA_ORIGEN_001",      // Cuenta origen
            "CUENTA_DESTINO_002",     // Cuenta destino
            "500.00",                 // Monto
            "USD",                    // Moneda
            123L                      // ID del usuario que crea
        );

        RespuestaOperacionDTO respuesta = servicioTransferencia
            .crearTransferencia(request);

        if (respuesta.isExitoso()) {
            System.out.println("✓ Transferencia exitosa: " + respuesta.getMensaje());
            System.out.println("  ID: " + respuesta.getIdOperacion());
        } else {
            System.out.println("✗ Error: " + respuesta.getMensaje());
            System.out.println("  Código: " + respuesta.getCodigoError());
        }
    }
}

// 3. Resultado esperado
// ✓ Transferencia exitosa: Transferencia ejecutada exitosamente
//   ID: 1
```

---

### 📌 Ejemplo 2: Crear Transferencia de Alto Monto (Requiere Aprobación)

#### Escenario
Una empresa desea transferir $15,000 (excede umbral de $10,000). Requiere aprobación del Supervisor.

#### Código

```java
@Service
public class TransferenciaEmpresa {
    private final ServicioAplicacionTransferencia servicioTransferencia;

    public void transferenciaPorAprobar() {
        // 1. EMPLEADO DE EMPRESA crea la transferencia
        CrearTransferenciaDTO request = new CrearTransferenciaDTO(
            "CUENTA_EMPRESA_001",
            "CUENTA_BENEFICIARIO_999",
            "15000.00",  // Monto > $10,000
            "USD",
            456L  // ID del empleado que crea
        );

        RespuestaOperacionDTO respuesta1 = servicioTransferencia
            .crearTransferencia(request);

        System.out.println(respuesta1.getMensaje());
        // ✓ Transferencia creada y en espera de aprobación
        Long idTransferencia = Long.parseLong(respuesta1.getIdOperacion());

        // 2. SUPERVISOR DE EMPRESA aprueba
        RespuestaOperacionDTO respuesta2 = servicioTransferencia
            .aprobarTransferencia(idTransferencia, 789L);  // ID del supervisor

        if (respuesta2.isExitoso()) {
            System.out.println(respuesta2.getMensaje());
            // ✓ Transferencia aprobada y ejecutada
        }
    }
}
```

---

### 📌 Ejemplo 3: Rechazar una Transferencia en Espera

#### Escenario
El Supervisor rechaza una transferencia que fue creada pero requiere aprobación.

#### Código

```java
@Service
public class RechazoTransferencia {
    private final ServicioAplicacionTransferencia servicioTransferencia;

    public void rechazarTransferencia() {
        Long idTransferencia = 2L;
        Long idSupervisor = 789L;

        RespuestaOperacionDTO respuesta = servicioTransferencia
            .rechazarTransferencia(idTransferencia, idSupervisor);

        if (respuesta.isExitoso()) {
            System.out.println("✓ Transferencia rechazada");
            // Los fondos NO se mueven
            // La Bitácora registra el rechazo
        }
    }
}
```

---

### 📌 Ejemplo 4: Crear una Solicitud de Préstamo

#### Escenario
Un cliente solicita un préstamo de consumo por $5,000 con 12 meses de plazo.

#### Código

```java
@Service
public class SolicitudPrestamo {
    private final ServicioAplicacionPrestamo servicioPrestamo;

    public void solicitarPrestamo() {
        CrearSolicitudPrestamoDTO request = new CrearSolicitudPrestamoDTO(
            "123-456-789",           // Cédula del cliente
            "CONSUMO",               // Tipo de préstamo
            new BigDecimal("5000"),  // Monto solicitado
            new BigDecimal("15.5"),  // Tasa de interés anual 15.5%
            12,                      // Plazo en meses
            "USD"                    // Moneda
        );

        RespuestaOperacionDTO respuesta = servicioPrestamo
            .crearSolicitudPrestamo(request);

        if (respuesta.isExitoso()) {
            System.out.println("✓ Solicitud creada exitosamente");
            System.out.println("  Estado: En estudio");
            System.out.println("  ID: " + respuesta.getIdOperacion());
            // Espera a que el Analista Interno revise
        }
    }
}
```

---

### 📌 Ejemplo 5: Aprobar un Préstamo (Analista Interno)

#### Escenario
El Analista Interno aprueba un préstamo con monto parcial.

#### Código

```java
@Service
public class AprobacionPrestamo {
    private final ServicioAplicacionPrestamo servicioPrestamo;

    public void aprobarPrestamo() {
        Long idPrestamo = 1L;
        BigDecimal montoAprobado = new BigDecimal("4500");  // Menos que lo solicitado
        String moneda = "USD";

        RespuestaOperacionDTO respuesta = servicioPrestamo
            .aprobarPrestamo(idPrestamo, montoAprobado, moneda);

        if (respuesta.isExitoso()) {
            System.out.println("✓ Préstamo aprobado");
            System.out.println("  Monto aprobado: $4,500.00");
            System.out.println("  Estado: Aprobado");
            System.out.println("  Próximo paso: Desembolso");
        }
    }
}
```

---

### 📌 Ejemplo 6: Desembolsar un Préstamo Aprobado

#### Escenario
El Back-Office desembolsa el préstamo aprobado a la cuenta del cliente.

#### Código

```java
@Service
public class DesembolsoPrestamo {
    private final ServicioAplicacionPrestamo servicioPrestamo;

    public void desembolsar() {
        Long idPrestamo = 1L;
        String numeroCuentaDestino = "CUENTA_CLIENTE_001";

        RespuestaOperacionDTO respuesta = servicioPrestamo
            .desembolsarPrestamo(idPrestamo, numeroCuentaDestino);

        if (respuesta.isExitoso()) {
            System.out.println("✓ Préstamo desembolsado");
            System.out.println("  Monto: $4,500.00");
            System.out.println("  Cuenta destino: " + numeroCuentaDestino);
            System.out.println("  Estado: Desembolsado");
            System.out.println("  Los fondos ya están en la cuenta del cliente");
        }
    }
}
```

---

### 📌 Ejemplo 7: Abrir una Nueva Cuenta Bancaria

#### Escenario
Un empleado de ventanilla abre una cuenta de ahorros para un cliente.

#### Código

```java
@Service
public class AperturaCuenta {
    private final ServicioAplicacionCuenta servicioCuenta;

    public void abrirCuenta() {
        CrearCuentaBancariaDTO request = new CrearCuentaBancariaDTO(
            "987-654-321",        // Cédula del titular
            "ACC-2024-00001",     // Número de cuenta único
            "AHORROS",            // Tipo de cuenta
            "0.00",               // Saldo inicial
            "USD"                 // Moneda
        );

        RespuestaOperacionDTO respuesta = servicioCuenta
            .abrirCuenta(request);

        if (respuesta.isExitoso()) {
            System.out.println("✓ Cuenta abierta exitosamente");
            System.out.println("  Número de cuenta: ACC-2024-00001");
            System.out.println("  Tipo: Ahorros");
            System.out.println("  Estado: Activa");
            System.out.println("  ID: " + respuesta.getIdOperacion());
        }
    }
}
```

---

### 📌 Ejemplo 8: Bloquear una Cuenta (Fraude/Seguridad)

#### Escenario
Se detecta actividad sospechosa. El sistema bloquea la cuenta preventivamente.

#### Código

```java
@Service
public class BloqueoSeguridad {
    private final ServicioAplicacionCuenta servicioCuenta;

    public void bloquearPorSeguridad() {
        Long idCuenta = 5L;
        String razon = "Actividad sospechosa detectada. Validación pendiente.";

        RespuestaOperacionDTO respuesta = servicioCuenta
            .bloquearCuenta(idCuenta, razon);

        if (respuesta.isExitoso()) {
            System.out.println("✓ Cuenta bloqueada");
            System.out.println("  Razón: " + razon);
            System.out.println("  Estado: Bloqueada");
            System.out.println("  No se pueden realizar operaciones");
        }
    }
}
```

---

### 📌 Ejemplo 9: Verificar Transferencias Vencidas (Proceso Automático)

#### Escenario
Un job automático verifica y marca transferencias que han excedido 1 hora sin aprobación.

#### Código

```java
@Component
public class VerificadorTransferenciasVencidas {
    private final ServicioAplicacionTransferencia servicioTransferencia;

    // Ejecutar cada 5 minutos
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void verificar() {
        System.out.println("[" + LocalDateTime.now() + "] Verificando transferencias vencidas...");

        RespuestaOperacionDTO respuesta = servicioTransferencia
            .verificarYMarcarTransferenciasVencidas();

        System.out.println(respuesta.getMensaje());
        // [2024-03-02 10:30:00] Verificación de transferencias vencidas completada
    }
}
```

---

## 🔄 Flujos Completos Paso a Paso

### Flujo: Transferencia de Empresa (Requiere Aprobación)

```
┌─────────────────────────────────────────────────────────────┐
│ 1. EMPLEADO DE EMPRESA                                      │
│    Crea transferencia de $15,000                             │
│    → crearTransferencia()                                    │
│    → Estado: EN_ESPERA_APROBACION (monto > $10,000)        │
│    → Se registra en Bitácora: "TRANSFERENCIA_CREADA"       │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. SUPERVISOR DE EMPRESA                                    │
│    Revisa transferencia                                      │
│                                                              │
│    OPCIÓN A: Aprueba                                        │
│    → aprobarTransferencia()                                 │
│    → Valida fondos suficientes                              │
│    → Ejecuta: CuentaOrigen.retirar($15,000)                │
│    → Ejecuta: CuentaDestino.depositar($15,000)             │
│    → Estado: EJECUTADA                                      │
│    → Se registra en Bitácora: "TRANSFERENCIA_APROBADA"     │
│    → Se registra en Bitácora: "TRANSFERENCIA_EJECUTADA"    │
│                                                              │
│    OPCIÓN B: Rechaza                                        │
│    → rechazarTransferencia()                                │
│    → Estado: RECHAZADA                                      │
│    → Sin movimiento de fondos                               │
│    → Se registra en Bitácora: "TRANSFERENCIA_RECHAZADA"    │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. VERIFICADOR AUTOMÁTICO (Cada 5 minutos)                 │
│    Si transferencia en EN_ESPERA_APROBACION > 1 hora:      │
│    → marcarComoVencida()                                    │
│    → Estado: VENCIDA                                        │
│    → Sin movimiento de fondos                               │
│    → Se registra en Bitácora: "TRANSFERENCIA_VENCIDA"      │
└─────────────────────────────────────────────────────────────┘
```

### Flujo: Solicitud y Desembolso de Préstamo

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CLIENTE / EMPLEADO COMERCIAL                             │
│    Solicita préstamo de consumo: $5,000                      │
│    → crearSolicitudPrestamo()                               │
│    → Estado: EN_ESTUDIO                                     │
│    → Se registra en Bitácora: "SOLICITUD_PRESTAMO"         │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. ANALISTA INTERNO DEL BANCO                               │
│    Revisa solicitud                                          │
│                                                              │
│    OPCIÓN A: Aprueba                                        │
│    → aprobarPrestamo($4,500)                                │
│    → Monto aprobado: $4,500 (parcial)                       │
│    → Estado: APROBADO                                       │
│    → Se registra en Bitácora: "APROBACION_PRESTAMO"        │
│                                                              │
│    OPCIÓN B: Rechaza                                        │
│    → rechazarPrestamo()                                     │
│    → Estado: RECHAZADO                                      │
│    → Se registra en Bitácora: "RECHAZO_PRESTAMO"           │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. BACK-OFFICE (Si fue aprobado)                            │
│    Realiza desembolso a la cuenta del cliente               │
│    → desembolsarPrestamo(CUENTA_001)                        │
│    → CuentaDestino.depositar($4,500)                        │
│    → Estado: DESEMBOLSADO                                   │
│    → Saldo actualizado en cuenta del cliente                │
│    → Se registra en Bitácora: "DESEMBOLSO_PRESTAMO"        │
│    → DISPONIBLE PARA EL CLIENTE                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 Matriz de Permisos por Rol

| Operación | Persona Natural | Empresa | Ventanilla | Comercial | Emp. Empresa | Supervisor | Analista |
|-----------|---|---|---|---|---|---|---|
| Ver propias cuentas | ✓ | ✓ | - | - | ✓ | ✓ | ✓ |
| Crear transferencia | ✓ | - | - | - | ✓ | - | - |
| Aprobar transferencia | - | - | - | - | - | ✓ | - |
| Abrir cuenta | - | - | ✓ | ✓ | - | - | - |
| Solicitar préstamo | ✓ | ✓ | - | ✓ | - | - | - |
| Aprobar préstamo | - | - | - | - | - | - | ✓ |
| Desembolsar préstamo | - | - | - | - | - | - | ✓ |
| Ver Bitácora completa | - | - | - | - | - | - | ✓ |

---

## ⚡ Validaciones Ejecutadas Automáticamente

### 1. Validaciones de Value Objects

```
❌ Dinero:
   - Monto < 0: "El monto debe ser mayor o igual a cero"
   - Moneda vacía: "La moneda es obligatoria"
   - Resta resulta negativa: "El resultado sería negativo"

❌ Email:
   - Sin @ o dominio: "El email no tiene un formato válido"
   - Vacío: "El email no puede estar vacío"

❌ Telefono:
   - < 7 dígitos: "El teléfono debe tener entre 7 y 15 dígitos"
   - > 15 dígitos: "El teléfono debe tener entre 7 y 15 dígitos"

❌ Identificacion:
   - > 20 caracteres: "excede 20 caracteres"

❌ NumeroCuenta:
   - > 20 caracteres: "excede 20 caracteres"
```

### 2. Validaciones de Entidades

```
❌ Usuario:
   - Edad < 18 años: "debe ser mayor de 18 años"
   - Identificación duplicada: "Único en todo el aplicativo"
   - Email inválido: Delega a Email VO

❌ CuentaBancaria:
   - Cliente inactivo: "No se puede abrir cuenta para cliente Inactivo"
   - Número duplicado: "Ya existe una cuenta con ese número"
   - Operación en cuenta bloqueada: "No está activa"
   - Retiro sin fondos: "Saldo insuficiente"

❌ Transferencia:
   - Cuentas origen = destino: "No pueden ser iguales"
   - Fondos insuficientes: "Saldo insuficiente"
   - Cuenta origen bloqueada: "No puede realizar operaciones"
   - Vencimiento > 1 hora: Marca como VENCIDA

❌ Prestamo:
   - Tasa interés ≤ 0: "debe ser mayor a cero"
   - Plazo ≤ 0: "debe ser mayor a cero"
   - Desembolso sin cuenta: "cuenta destino es obligatoria"
   - Transición de estado inválida: "estado inválido"
```

---

## 🎯 Resumen

Con esta arquitectura DDD:

✅ **Lógica de negocio centralizada** en el dominio  
✅ **Validaciones automáticas** en Value Objects y Entidades  
✅ **Casos de uso claros** en Servicios de Aplicación  
✅ **Tolerancia a cambios** mediante Puertos  
✅ **Trazabilidad completa** en Bitácora de Operaciones  
✅ **Fácil testing** con inversión de dependencias  
✅ **Escalabilidad** separando capas de forma clara  

