package app.domain.bank.services;

import app.domain.bank.models.CuentaBancaria;
import app.domain.bank.ports.CuentaBancariaPort;
import app.domain.bank.ports.BitacoraPort;
import app.domain.bank.valueobjects.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de Dominio que gestiona operaciones sobre cuentas bancarias.
 */
public class ServicioCuenta {
    private final CuentaBancariaPort cuentaPort;
    private final BitacoraPort bitacoraPort;

    public ServicioCuenta(CuentaBancariaPort cuentaPort, BitacoraPort bitacoraPort) {
        this.cuentaPort = Objects.requireNonNull(cuentaPort, "CuentaPort es obligatorio");
        this.bitacoraPort = Objects.requireNonNull(bitacoraPort, "BitacoraPort es obligatorio");
    }

    /**
     * Abre una nueva cuenta bancaria para un cliente.
     */
    public void abrirCuenta(CuentaBancaria cuenta) {
        // Validar que la cuenta no exista ya
        if (cuentaPort.existePorNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese número");
        }

        // Guardar la cuenta
        cuentaPort.guardar(cuenta);

        // Registrar en bitácora
        registrarAperturaCuenta(cuenta);
    }

    /**
     * Bloquea una cuenta existente.
     */
    public void bloquearCuenta(CuentaBancaria cuenta, String razon) {
        if (!cuenta.estaActiva()) {
            throw new IllegalStateException("La cuenta no puede bloquearse, no está activa");
        }

        cuenta.cambiarEstado(EstadoCuenta.BLOQUEADA);
        cuentaPort.actualizar(cuenta);

        // Registrar en bitácora
        registrarCambioEstadoCuenta(cuenta, EstadoCuenta.ACTIVA, razon);
    }

    /**
     * Desbloquea una cuenta bloqueada.
     */
    public void desbloquearCuenta(CuentaBancaria cuenta) {
        if (cuenta.getEstado() != EstadoCuenta.BLOQUEADA) {
            throw new IllegalStateException("La cuenta no está bloqueada");
        }

        cuenta.cambiarEstado(EstadoCuenta.ACTIVA);
        cuentaPort.actualizar(cuenta);

        // Registrar en bitácora
        registrarCambioEstadoCuenta(cuenta, EstadoCuenta.BLOQUEADA, "Desbloqueo de cuenta");
    }

    private void registrarAperturaCuenta(CuentaBancaria cuenta) {
        Map<String, Object> datosDetalle = new LinkedHashMap<>();
        datosDetalle.put("numeroCuenta", cuenta.getNumeroCuenta().getNumero());
        datosDetalle.put("tipoCuenta", cuenta.getTipoCuenta().getDescripcion());
        datosDetalle.put("saldoInicial", cuenta.getSaldoActual().getMonto().toString());
        datosDetalle.put("moneda", cuenta.getSaldoActual().getMoneda());
        datosDetalle.put("estado", EstadoCuenta.ACTIVA.getDescripcion());
        datosDetalle.put("fechaApertura", LocalDateTime.now().toString());

        bitacoraPort.registrar(
            UUID.randomUUID().toString(),
            "APERTURA_CUENTA",
            0L, // Será asignado por el servicio de aplicación
            "EMPLEADO_VENTANILLA",
            cuenta.getId().toString(),
            datosDetalle
        );
    }

    private void registrarCambioEstadoCuenta(CuentaBancaria cuenta, EstadoCuenta estadoAnterior, String razon) {
        Map<String, Object> datosDetalle = new LinkedHashMap<>();
        datosDetalle.put("numeroCuenta", cuenta.getNumeroCuenta().getNumero());
        datosDetalle.put("estadoAnterior", estadoAnterior.getDescripcion());
        datosDetalle.put("estadoNuevo", cuenta.getEstado().getDescripcion());
        datosDetalle.put("razon", razon);
        datosDetalle.put("fecha", LocalDateTime.now().toString());

        bitacoraPort.registrar(
            UUID.randomUUID().toString(),
            "CAMBIO_ESTADO_CUENTA",
            0L,
            "EMPLEADO_VENTANILLA",
            cuenta.getId().toString(),
            datosDetalle
        );
    }
}
