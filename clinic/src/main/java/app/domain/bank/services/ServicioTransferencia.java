package app.domain.bank.services;

import app.domain.bank.models.CuentaBancaria;
import app.domain.bank.models.Transferencia;
import app.domain.bank.ports.CuentaBancariaPort;
import app.domain.bank.ports.TransferenciaPort;
import app.domain.bank.ports.BitacoraPort;
import app.domain.bank.valueobjects.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de Dominio que gestiona las transferencias y su impacto financiero.
 * Orquesta la lógica de negocio entre múltiples agregados.
 */
public class ServicioTransferencia {
    private final CuentaBancariaPort cuentaPort;
    private final TransferenciaPort transferenciaPort;
    private final BitacoraPort bitacoraPort;

    public ServicioTransferencia(
        CuentaBancariaPort cuentaPort,
        TransferenciaPort transferenciaPort,
        BitacoraPort bitacoraPort
    ) {
        this.cuentaPort = Objects.requireNonNull(cuentaPort, "CuentaPort es obligatorio");
        this.transferenciaPort = Objects.requireNonNull(transferenciaPort, "TransferenciaPort es obligatorio");
        this.bitacoraPort = Objects.requireNonNull(bitacoraPort, "BitacoraPort es obligatorio");
    }

    /**
     * Ejecuta una transferencia que ha sido aprobada o no requiere aprobación.
     * Valida fondos suficientes y actualiza el estado de ambas cuentas.
     */
    public void ejecutarTransferencia(Transferencia transferencia) {
        // Validar que la transferencia pueda ejecutarse
        if (!transferencia.puedeEjecutarse()) {
            throw new IllegalStateException("La transferencia no puede ser ejecutada en su estado actual");
        }

        // Obtener cuentas
        CuentaBancaria cuentaOrigen = cuentaPort.obtenerPorNumeroCuenta(transferencia.getCuentaOrigen())
            .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada"));

        CuentaBancaria cuentaDestino = cuentaPort.obtenerPorNumeroCuenta(transferencia.getCuentaDestino())
            .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada"));

        // Validar que las cuentas estén activas
        if (!cuentaOrigen.puedeRealizarOperaciones()) {
            throw new IllegalStateException("La cuenta origen no está disponible para operaciones");
        }

        // Validar fondos suficientes
        if (cuentaOrigen.getSaldoActual().esInsuficiente(transferencia.getMonto())) {
            throw new IllegalArgumentException("Fondos insuficientes en la cuenta origen");
        }

        // Registrar saldos antes del movimiento
        Dinero saldoOrigenAntes = cuentaOrigen.getSaldoActual();
        Dinero saldoDestinoAntes = cuentaDestino.getSaldoActual();

        // Ejecutar movimientos
        cuentaOrigen.retirar(transferencia.getMonto());
        cuentaDestino.depositar(transferencia.getMonto());

        // Persistir cambios
        cuentaPort.actualizar(cuentaOrigen);
        cuentaPort.actualizar(cuentaDestino);

        // Marcar transferencia como ejecutada
        transferencia.ejecutar();
        transferenciaPort.actualizar(transferencia);

        // Registrar en bitácora
        registrarTransferenciaEjecutada(transferencia, saldoOrigenAntes, saldoDestinoAntes,
                cuentaOrigen.getSaldoActual(), cuentaDestino.getSaldoActual());
    }

    /**
     * Registra una transferencia ejecutada en la bitácora.
     */
    private void registrarTransferenciaEjecutada(
        Transferencia transferencia,
        Dinero saldoOrigenAntes,
        Dinero saldoDestinoAntes,
        Dinero saldoOrigenDespues,
        Dinero saldoDestinoDespues
    ) {
        Map<String, Object> datosDetalle = new LinkedHashMap<>();
        datosDetalle.put("cuentaOrigen", transferencia.getCuentaOrigen().getNumero());
        datosDetalle.put("cuentaDestino", transferencia.getCuentaDestino().getNumero());
        datosDetalle.put("montoTransferido", transferencia.getMonto().getMonto().toString());
        datosDetalle.put("moneda", transferencia.getMonto().getMoneda());
        datosDetalle.put("saldoAntes_Origen", saldoOrigenAntes.getMonto().toString());
        datosDetalle.put("saldoDepues_Origen", saldoOrigenDespues.getMonto().toString());
        datosDetalle.put("saldoAntes_Destino", saldoDestinoAntes.getMonto().toString());
        datosDetalle.put("saldoDepues_Destino", saldoDestinoDespues.getMonto().toString());
        datosDetalle.put("estado", EstadoTransferencia.EJECUTADA.getDescripcion());
        datosDetalle.put("fechaEjecucion", LocalDateTime.now().toString());

        bitacoraPort.registrar(
            UUID.randomUUID().toString(),
            "TRANSFERENCIA_EJECUTADA",
            transferencia.getIdUsuarioCreador(),
            "USUARIO", // Rol del usuario creador
            transferencia.getId().toString(),
            datosDetalle
        );
    }

    /**
     * Verifica y marca como vencidas las transferencias que han excedido el tiempo de aprobación.
     */
    public void verificarTransferenciasVencidas() {
        List<Transferencia> enEspera = transferenciaPort.obtenerEnEsperaDeAprobacion();

        for (Transferencia transferencia : enEspera) {
            if (transferencia.estaVencida()) {
                transferencia.marcarComoVencida();
                transferenciaPort.actualizar(transferencia);

                // Registrar vencimiento en bitácora
                Map<String, Object> datosDetalle = new LinkedHashMap<>();
                datosDetalle.put("razonVencimiento", "Falta de aprobación a tiempo");
                datosDetalle.put("tiempoEsperaMinutos", 60);
                datosDetalle.put("fechaVencimiento", LocalDateTime.now().toString());

                bitacoraPort.registrar(
                    UUID.randomUUID().toString(),
                    "TRANSFERENCIA_VENCIDA",
                    transferencia.getIdUsuarioCreador(),
                    "USUARIO",
                    transferencia.getId().toString(),
                    datosDetalle
                );
            }
        }
    }
}
