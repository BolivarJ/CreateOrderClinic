# 🏦 Arquitectura DDD - Sistema de Gestión Bancaria

## 📋 Descripción General

Este proyecto implementa una **aplicación bancaria robusta** utilizando **Domain-Driven Design (DDD)** con Java y Spring Boot. La arquitectura está organizada en capas claramente definidas siguiendo los principios de gestión del dominio.

## 🏗️ Estructura del Proyecto

```
clinic/
├── src/main/java/app/
│   ├── domain/bank/
│   │   ├── models/                    # 🔵 Entidades del Dominio
│   │   ├── valueobjects/              # 🟢 Objetos de Valor
│   │   ├── aggregates/                # 🟡 Agregados (si es necesario)
│   │   ├── services/                  # 🔴 Servicios de Dominio
│   │   └── ports/                     # 🟣 Interfaces (Puertos)
│   │
│   ├── application/bank/
│   │   ├── services/                  # 🟠 Servicios de Aplicación
│   │   └── dtos/                      # 📦 Data Transfer Objects
│   │
│   └── adapters/                      # 🔧 Adaptadores (Próximo paso)
│       └── bank/
│           └── persistence/           # BD: SQL & NoSQL
```

## 📦 Componentes Principales

### 1. **Capa de Dominio** (`domain/bank/`)

#### Value Objects (Objetos de Valor)
Los Value Objects son **inmutables** y representan conceptos importantes del negocio:

| Value Object | Descripción | Validaciones |
|---|---|---|
| `Dinero` | Monto con moneda | Monto ≥ 0, moneda válida |
| `Email` | Dirección de correo | Formato válido con @ y dominio |
| `Telefono` | Número telefónico | 7-15 dígitos |
| `Identificacion` | DNI/Cédula/NIT | Máx 20 caracteres |
| `NumeroCuenta` | Número de cuenta | Máx 20 caracteres, único |
| Enumeraciones | Estados y tipos | TipoCuenta, EstadoCuenta, EstadoPrestamo, EstadoTransferencia, RolBancario |

#### Entidades del Dominio
Entidades con **identidad única** que pueden cambiar de estado:

- **Usuario**: Componente central, contiene credenciales y permisos
- **ClientePersonaNatural**: Persona física cliente del banco
- **ClienteEmpresa**: Entidad jurídica cliente del banco
- **CuentaBancaria**: Agregado que gestiona saldo y estado
- **Prestamo**: Agregado que gestiona el ciclo de vida del crédito
- **Transferencia**: Agregado que gestiona movimientos de fondos

#### Servicios de Dominio
Orquestan lógica de negocio compleja:

- **ServicioTransferencia**: Ejecuta transferencias, valida fondos, marca vencidas
- **ServicioPrestamo**: Aprueba, rechaza y desembolsa préstamos
- **ServicioCuenta**: Abre, bloquea y desbloquea cuentas

#### Puertos (Interfaces)
Definen los contratos para persistencia:

```java
// Ejemplos de Puertos
public interface TransferenciaPort { ... }
public interface CuentaBancariaPort { ... }
public interface PrestamoPort { ... }
public interface BitacoraPort { ... }
```

### 2. **Capa de Aplicación** (`application/bank/`)

#### Servicios de Aplicación
Implementan los **casos de uso** del negocio:

- **ServicioAplicacionTransferencia**: Crear, aprobar, rechazar transferencias
- **ServicioAplicacionPrestamo**: Crear solicitudes, aprobar, desembolsar
- **ServicioAplicacionCuenta**: Abrir, bloquear, desbloquear cuentas

#### DTOs (Data Transfer Objects)
Objetos para transferencia de datos entre capas:

```java
// Ejemplos de DTOs
CrearTransferenciaDTO
CrearSolicitudPrestamoDTO
CrearCuentaBancariaDTO
RespuestaOperacionDTO
```

### 3. **Capa de Adaptadores** (`adapters/`) - Por implementar

- **Controladores REST**: Endpoints que exponen casos de uso
- **Persistencia JPA**: Implementación de Puertos con Spring Data JPA
- **Persistencia NoSQL**: Bitácora de operaciones en MongoDB/Firebase
- **Configuración Spring**: Inyección de dependencias

## 🎯 Casos de Uso Implementados

### Transferencias
```
1. Crear Transferencia
   ├─ Validar cuentas activas
   ├─ Determinar si requiere aprobación (umbral: $10,000)
   ├─ Ejecutar o esperar aprobación
   └─ Registrar en Bitácora

2. Aprobar Transferencia (Supervisor)
   ├─ Validar estado "En espera de aprobación"
   ├─ Ejecutar transferencia
   ├─ Actualizar saldos
   └─ Registrar en Bitácora

3. Verificar Vencimiento
   ├─ Buscar transferencias en espera > 1 hora
   ├─ Marcar como vencidas
   └─ Registrar en Bitácora
```

### Préstamos
```
1. Crear Solicitud
   ├─ Validar cliente
   ├─ Guardar en estado "En estudio"
   └─ Registrar en Bitácora

2. Aprobar/Rechazar (Analista Interno)
   ├─ Validar estado actual
   ├─ Cambiar a "Aprobado" o "Rechazado"
   └─ Registrar en Bitácora

3. Desembolsar
   ├─ Validar préstamo aprobado
   ├─ Validar cuenta destino
   ├─ Actualizar saldo de la cuenta
   └─ Registrar en Bitácora
```

### Cuentas
```
1. Abrir Cuenta
   ├─ Validar cliente activo
   ├─ Crear cuenta con saldo inicial
   └─ Registrar en Bitácora

2. Bloquear/Desbloquear
   ├─ Cambiar estado
   └─ Registrar en Bitácora
```

## 🔐 Validaciones de Negocio

### Cliente
- Edad mínima: 18 años
- Identificación única
- Email con formato válido
- Teléfono: 7-15 dígitos

### Cuenta
- No se puede abrir para usuario Inactivo/Bloqueado
- Número de cuenta único
- Operaciones solo en cuentas Activas
- Saldo no puede ser negativo

### Transferencia
- Fondos suficientes en cuenta origen
- Monedas iguales en origen/destino
- Vencimiento automático después de 1 hora
- Aprobación obligatoria si monto > $10,000

### Préstamo
- Monto solicitado > 0
- Tasa de interés > 0
- Plazo en meses > 0
- Transiciones de estado válidas
- Desembolso solo a cuenta del cliente

## 📊 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                 CAPA DE PRESENTACIÓN                     │
│        (Controladores REST - Por implementar)            │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│              CAPA DE APLICACIÓN (Use Cases)              │
│   ┌────────────────────────────────────────────────┐   │
│   │  ServicioAplicacionTransferencia               │   │
│   │  ServicioAplicacionPrestamo                    │   │
│   │  ServicioAplicacionCuenta                      │   │
│   │  Manejo de DTOs y Respuestas                   │   │
│   └────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│             CAPA DE DOMINIO (Lógica de Negocio)         │
│   ┌────────────────────────────────────────────────┐   │
│   │  Entidades: Usuario, Cliente, Cuenta,          │   │
│   │             Préstamo, Transferencia            │   │
│   │  Value Objects: Dinero, Email, Telefono, etc   │   │
│   │  Servicios: ServicioTransferencia,             │   │
│   │             ServicioPrestamo, ServicioCuenta   │   │
│   │  Puertos: TransferenciaPort, CuentaPort, etc   │   │
│   └────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│         CAPA DE ADAPTADORES (Implementaciones)          │
│   ┌────────────────────────────────────────────────┐   │
│   │  JPA Repositories (SQL - MySQL)                │   │
│   │  MongoDB Repositories (NoSQL - Bitácora)       │   │
│   │  Configuración de Inyección de Dependencias    │   │
│   └────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│         BASES DE DATOS (Por implementar)                │
│   ┌────────────────────────────────────────────────┐   │
│   │  MySQL: Usuarios, Cuentas, Préstamos,          │   │
│   │         Transferencias                         │   │
│   │  MongoDB: Bitácora de Operaciones              │   │
│   └────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────┘
```

## 🔄 Flujo de Transferencia Completo

```
1. Cliente crea Transferencia
   ↓
2. ServicioAplicacionTransferencia.crearTransferencia()
   ├─ Valida cuentas existan y estén activas
   ├─ Crea objeto Transferencia
   └─ Determina si requiere aprobación
   ↓
3. Si NO requiere aprobación:
   ├─ ServicioTransferencia.ejecutarTransferencia()
   ├─ Valida fondos suficientes
   ├─ Ejecuta: CuentaOrigen.retirar() + CuentaDestino.depositar()
   ├─ Actualiza persistencia
   └─ Registra en Bitácora ✓
   
4. Si REQUIERE aprobación:
   ├─ Estado: EN_ESPERA_APROBACION
   ├─ Espera a Supervisor de Empresa
   └─ (Ver paso 5)
   ↓
5. Supervisor aprueba/rechaza
   ├─ Si APRUEBA:
   │  ├─ ServicioTransferencia.ejecutarTransferencia()
   │  ├─ Procede como en paso 3
   │  └─ Registra aprobación en Bitácora ✓
   └─ Si RECHAZA:
      ├─ Estado: RECHAZADA
      └─ Registra rechazo en Bitácora ✓
   ↓
6. Verificación automática de vencimiento (Si aplica)
   ├─ Busca transferencias en espera > 1 hora
   ├─ Las marca como VENCIDA
   └─ Registra vencimiento en Bitácora ✓
```

## 📚 Patrones Utilizados

| Patrón | Ubicación | Propósito |
|---|---|---|
| **Aggregate** | Transferencia, CuentaBancaria, Prestamo | Garantizar consistencia de datos |
| **Value Object** | Dinero, Email, etc | Encapsular lógica de validación |
| **Entity** | Usuario, Cliente | Identidad única en el dominio |
| **Domain Service** | ServicioTransferencia, etc | Lógica que cruza múltiples agregados |
| **Application Service** | ServicioAplicacionTransferencia, etc | Casos de uso del negocio |
| **Port & Adapter** | *Port.java | Inyección de dependencias |
| **DTO** | *DTO.java | Transferencia de datos sin lógica |

## 🚀 Próximos Pasos

1. **Implementar Adaptadores**
   - [ ] JpaRepository para SQL
   - [ ] MongoRepository para NoSQL
   - [ ] Inyección de dependencias con Spring

2. **Crear Controladores REST**
   - [ ] TransferenciaController
   - [ ] PrestamoController
   - [ ] CuentaController
   - [ ] UsuarioController

3. **Seguridad**
   - [ ] Spring Security para autenticación
   - [ ] Validación de roles y permisos
   - [ ] Encriptación de contraseñas

4. **Tests**
   - [ ] Tests unitarios para Value Objects
   - [ ] Tests de integración para Servicios de Dominio
   - [ ] Tests E2E para Servicios de Aplicación

5. **Documentación OpenAPI/Swagger**
   - [ ] Documentar endpoints REST
   - [ ] Ejemplos de requests/responses

## ✅ Checklist DDD

- ✅ **Entidades** con identidad única
- ✅ **Value Objects** inmutables
- ✅ **Agregados** raíz bien definidos
- ✅ **Servicios de Dominio** para lógica compleja
- ✅ **Puertos** para abstraer persistencia
- ✅ **Servicios de Aplicación** para casos de uso
- ✅ **DTOs** para transferencia de datos
- ✅ Validaciones de **reglas de negocio**
- ⏳ Adaptadores (Próximo)
- ⏳ Controladores REST (Próximo)

## 📋 Referencia de Clases Principales

### Value Objects
- `Dinero.java` - Cantidad monetaria inmutable
- `Email.java` - Dirección de correo validada
- `Telefono.java` - Número de teléfono validado
- `Identificacion.java` - DNI/Cédula/NIT
- `NumeroCuenta.java` - Número de cuenta

### Entidades
- `Usuario.java` - Usuario del sistema
- `ClientePersonaNatural.java` - Cliente persona física
- `ClienteEmpresa.java` - Cliente empresa
- `CuentaBancaria.java` - Cuenta bancaria
- `Prestamo.java` - Producto de crédito
- `Transferencia.java` - Movimiento de fondos

### Servicios de Dominio
- `ServicioTransferencia.java` - Lógica de transferencias
- `ServicioPrestamo.java` - Lógica de préstamos
- `ServicioCuenta.java` - Lógica de cuentas

### Servicios de Aplicación
- `ServicioAplicacionTransferencia.java` - Casos de uso de transferencias
- `ServicioAplicacionPrestamo.java` - Casos de uso de préstamos
- `ServicioAplicacionCuenta.java` - Casos de uso de cuentas

### Puertos
- `TransferenciaPort.java` - Persistencia de transferencias
- `CuentaBancariaPort.java` - Persistencia de cuentas
- `PrestamoPort.java` - Persistencia de préstamos
- `BitacoraPort.java` - Persistencia de auditoría

---

**Arquitecto del Proyecto**: Domain-Driven Design (DDD)  
**Lenguaje**: Java 17  
**Framework**: Spring Boot 4.0.2  
**Bases de Datos**: MySQL (SQL), MongoDB (NoSQL)
