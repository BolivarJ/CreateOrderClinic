package app.domain.bank.services;

import app.domain.bank.models.CuentaBancaria;
import app.domain.bank.models.Prestamo;
import app.domain.bank.ports.CuentaBancariaPort;
import app.domain.bank.ports.PrestamoPort;
import app.domain.bank.ports.BitacoraPort;
import app.domain.bank.valueobjects.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de Dominio que gestiona los préstamos y sus operaciones.
 * Orquesta la lógica de desembolso y su impacto en las cuentas.
 */
public class ServicioPrestamo {
    private final CuentaBancariaPort cuentaPort;
    private final PrestamoPort prestamoPort;
    private final BitacoraPort bitacoraPort;

    public ServicioPrestamo(
        CuentaBancariaPort cuentaPort,
        PrestamoPort prestamoPort,
        BitacoraPort bitacoraPort
    ) {
        this.cuentaPort = Objects.requireNonNull(cuentaPort, "CuentaPort es obligatorio");
        this.prestamoPort = Objects.requireNonNull(prestamoPort, "PrestamoPort es obligatorio");
        this.bitacoraPort = Objects.requireNonNull(bitacoraPort, "BitacoraPort es obligatorio");
    }

    /**
     * Desembolsa un préstamo aprobado a la cuenta destino especificada.
     * Valida que la cuenta exista y esté activa, luego aumenta el saldo.
     */
    public void desembolsarPrestamo(Prestamo prestamo, NumeroCuenta cuentaDestino) {
        // Validar que el préstamo esté aprobado
        if (prestamo.getEstado() != EstadoPrestamo.APROBADO) {
            throw new IllegalStateException("Solo se pueden desembolsar préstamos aprobados");
        }

        // Validar que el monto aprobado sea válido
        if (prestamo.getMontoAprobado().getMonto().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto aprobado debe ser mayor a cero");
        }

        // Obtener y validar la cuenta destino
        CuentaBancaria cuenta = cuentaPort.obtenerPorNumeroCuenta(cuentaDestino)
            .orElseThrow(() -> new IllegalArgumentException("La cuenta destino no existe"));

        if (!cuenta.estaActiva()) {
            throw new IllegalStateException("La cuenta destino no está activa");
        }

        // Validar que la cuenta pertenece al titular del préstamo
        if (!cuenta.getIdTitular().equals(prestamo.getIdClienteSolicitante())) {
            throw new IllegalArgumentException("La cuenta no pertenece al cliente del préstamo");
        }

        // Registrar saldo antes del desembolso
        Dinero saldoAntes = cuenta.getSaldoActual();

        // Realizar desembolso
        cuenta.depositar(prestamo.getMontoAprobado());
        Dinero saldoDespues = cuenta.getSaldoActual();

        // Actualizar en persistencia
        cuenta.depositar(prestamo.getMontoAprobado());
        cuentaPort.actualizar(cuenta);

        // Marcar préstamo como desembolsado
        prestamo.desembolsar(cuentaDestino);
        prestamoPort.actualizar(prestamo);

        // Registrar en bitácora
        registrarDesembolsoPrestamo(prestamo, saldoAntes, saldoDespues, cuenta);
    }

    /**
     * Registra el desembolso de un préstamo en la bitácora.
     */
    private void registrarDesembolsoPrestamo(
        Prestamo prestamo,
        Dinero saldoAntes,
        Dinero saldoDespues,
        CuentaBancaria cuenta
    ) {
        Map<String, Object> datosDetalle = new LinkedHashMap<>();
        datosDetalle.put("idPrestamo", prestamo.getId());
        datosDetalle.put("tipoPrestamo", prestamo.getTipoPrestamo().getDescripcion());
        datosDetalle.put("montoAprobado", prestamo.getMontoAprobado().getMonto().toString());
        datosDetalle.put("moneda", prestamo.getMontoAprobado().getMoneda());
        datosDetalle.put("tasaInteres", prestamo.getTasaInteres().toString());
        datosDetalle.put("plazoMeses", prestamo.getPlazoMeses());
        datosDetalle.put("cuentaDestino", prestamo.getCuentaDestinoDesembolso().getNumero());
        datosDetalle.put("saldoAntesDespues", saldoAntes.getMonto().toString());
        datosDetalle.put("saldoDepuesDespues", saldoDespues.getMonto().toString());
        datosDetalle.put("estado", EstadoPrestamo.DESEMBOLSADO.getDescripcion());
        datosDetalle.put("fechaDesembolso", LocalDateTime.now().toString());

        bitacoraPort.registrar(
            UUID.randomUUID().toString(),
            "DESEMBOLSO_PRESTAMO",
            0L, // ID del analista será agregado por el servicio de aplicación
            "ANALISTA_INTERNO",
            prestamo.getId().toString(),
            datosDetalle
        );
    }

    /**
     * Aprueba un préstamo (por parte del Analista Interno).
     */
    public void aprobarPrestamo(Prestamo prestamo, Dinero montoAprobado) {
        prestamo.aprobar(montoAprobado);
        prestamoPort.actualizar(prestamo);

        // Registrar aprobación en bitácora
        registrarAprobacionPrestamo(prestamo);
    }

    /**
     * Rechaza un préstamo (por parte del Analista Interno).
     */
    public void rechazarPrestamo(Prestamo prestamo) {
        prestamo.rechazar();
        prestamoPort.actualizar(prestamo);

        // Registrar rechazo en bitácora
        registrarRechazo(prestamo);
    }

    private void registrarAprobacionPrestamo(Prestamo prestamo) {
        Map<String, Object> datosDetalle = new LinkedHashMap<>();
        datosDetalle.put("idPrestamo", prestamo.getId());
        datosDetalle.put("tipoPrestamo", prestamo.getTipoPrestamo().getDescripcion());
        datosDetalle.put("montoSolicitado", prestamo.getMontoSolicitado().getMonto().toString());
        datosDetalle.put("montoAprobado", prestamo.getMontoAprobado().getMonto().toString());
        datosDetalle.put("tasaInteres", prestamo.getTasaInteres().toString());
        datosDetalle.put("estado", EstadoPrestamo.APROBADO.getDescripcion());
        datosDetalle.put("fechaAprobacion", LocalDateTime.now().toString());

        bitacoraPort.registrar(
            UUID.randomUUID().toString(),
            "APROBACION_PRESTAMO",
            0L,
            "ANALISTA_INTERNO",
            prestamo.getId().toString(),
            datosDetalle
        );
    }

    private void registrarRechazo(Prestamo prestamo) {
        Map<String, Object> datosDetalle = new LinkedHashMap<>();
        datosDetalle.put("idPrestamo", prestamo.getId());
        datosDetalle.put("tipoPrestamo", prestamo.getTipoPrestamo().getDescripcion());
        datosDetalle.put("estado", EstadoPrestamo.RECHAZADO.getDescripcion());
        datosDetalle.put("fechaRechazo", LocalDateTime.now().toString());

        bitacoraPort.registrar(
            UUID.randomUUID().toString(),
            "RECHAZO_PRESTAMO",
            0L,
            "ANALISTA_INTERNO",
            prestamo.getId().toString(),
            datosDetalle
        );
    }
}
