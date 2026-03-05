package app.application.bank.services;

import app.domain.bank.models.CuentaBancaria;
import app.domain.bank.models.Transferencia;
import app.domain.bank.ports.CuentaBancariaPort;
import app.domain.bank.ports.TransferenciaPort;
import app.domain.bank.services.ServicioTransferencia;
import app.domain.bank.valueobjects.*;
import app.application.bank.dtos.CrearTransferenciaDTO;
import app.application.bank.dtos.RespuestaOperacionDTO;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Servicio de Aplicación para gestionar transferencias.
 * Implementa los casos de uso relacionados con transferencias.
 */
public class ServicioAplicacionTransferencia {
    private final TransferenciaPort transferenciaPort;
    private final CuentaBancariaPort cuentaPort;
    private final ServicioTransferencia servicioTransferenciaDominio;
    private Long contadorIds = 1L;
    private static final BigDecimal UMBRAL_TRANSFERENCIA_EMPRESA = new BigDecimal("10000");

    public ServicioAplicacionTransferencia(
        TransferenciaPort transferenciaPort,
        CuentaBancariaPort cuentaPort,
        ServicioTransferencia servicioTransferenciaDominio
    ) {
        this.transferenciaPort = Objects.requireNonNull(transferenciaPort, "TransferenciaPort es obligatorio");
        this.cuentaPort = Objects.requireNonNull(cuentaPort, "CuentaPort es obligatorio");
        this.servicioTransferenciaDominio = Objects.requireNonNull(servicioTransferenciaDominio, "ServicioTransferenciaDominio es obligatorio");
    }

    /**
     * Caso de uso: Crear una transferencia.
     * Determina automáticamente si requiere aprobación basado en el monto.
     */
    public RespuestaOperacionDTO crearTransferencia(CrearTransferenciaDTO dto) {
        try {
            NumeroCuenta cuentaOrigen = new NumeroCuenta(dto.getCuentaOrigen());
            NumeroCuenta cuentaDestino = new NumeroCuenta(dto.getCuentaDestino());
            Dinero monto = new Dinero(new BigDecimal(dto.getMonto()), dto.getMoneda());

            // Validar que exista la cuenta origen
            CuentaBancaria origen = cuentaPort.obtenerPorNumeroCuenta(cuentaOrigen)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no existe"));

            if (!origen.puedeRealizarOperaciones()) {
                return new RespuestaOperacionDTO(
                    false,
                    "La cuenta origen no puede realizar operaciones",
                    null,
                    "CUENTA_INACTIVA"
                );
            }

            // Determinar si requiere aprobación (umbral para empresas)
            boolean requiereAprobacion = monto.getMonto().compareTo(UMBRAL_TRANSFERENCIA_EMPRESA) > 0;

            Transferencia nuevaTransferencia = new Transferencia(
                contadorIds++,
                cuentaOrigen,
                cuentaDestino,
                monto,
                dto.getIdUsuarioCreador(),
                requiereAprobacion
            );

            transferenciaPort.guardar(nuevaTransferencia);

            String mensaje = requiereAprobacion 
                ? "Transferencia creada y en espera de aprobación"
                : "Transferencia ejecutada exitosamente";

            if (!requiereAprobacion) {
                servicioTransferenciaDominio.ejecutarTransferencia(nuevaTransferencia);
            }

            return new RespuestaOperacionDTO(
                true,
                mensaje,
                nuevaTransferencia.getId().toString(),
                null
            );
        } catch (IllegalArgumentException e) {
            return new RespuestaOperacionDTO(
                false,
                "Error al validar datos: " + e.getMessage(),
                null,
                "VALIDACION_ERROR"
            );
        } catch (Exception e) {
            return new RespuestaOperacionDTO(
                false,
                "Error al crear la transferencia",
                null,
                "INTERNO_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Aprobar una transferencia (Supervisor de Empresa).
     */
    public RespuestaOperacionDTO aprobarTransferencia(Long idTransferencia, Long idAprobador) {
        try {
            Transferencia transferencia = transferenciaPort.obtenerPorId(idTransferencia)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada"));

            transferencia.aprobar(idAprobador);
            servicioTransferenciaDominio.ejecutarTransferencia(transferencia);

            return new RespuestaOperacionDTO(
                true,
                "Transferencia aprobada y ejecutada",
                idTransferencia.toString(),
                null
            );
        } catch (IllegalStateException e) {
            return new RespuestaOperacionDTO(
                false,
                "Error de estado: " + e.getMessage(),
                null,
                "ESTADO_ERROR"
            );
        } catch (Exception e) {
            return new RespuestaOperacionDTO(
                false,
                "Error al aprobar la transferencia",
                null,
                "INTERNO_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Rechazar una transferencia (Supervisor de Empresa).
     */
    public RespuestaOperacionDTO rechazarTransferencia(Long idTransferencia, Long idAprobador) {
        try {
            Transferencia transferencia = transferenciaPort.obtenerPorId(idTransferencia)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada"));

            transferencia.rechazar(idAprobador);
            transferenciaPort.actualizar(transferencia);

            return new RespuestaOperacionDTO(
                true,
                "Transferencia rechazada",
                idTransferencia.toString(),
                null
            );
        } catch (Exception e) {
            return new RespuestaOperacionDTO(
                false,
                "Error al rechazar la transferencia",
                null,
                "INTERNO_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Verificar transferencias vencidas (Proceso automático).
     */
    public RespuestaOperacionDTO verificarYMarcarTransferenciasVencidas() {
        try {
            servicioTransferenciaDominio.verificarTransferenciasVencidas();
            return new RespuestaOperacionDTO(
                true,
                "Verificación de transferencias vencidas completada",
                null,
                null
            );
        } catch (Exception e) {
            return new RespuestaOperacionDTO(
                false,
                "Error al verificar transferencias vencidas",
                null,
                "INTERNO_ERROR"
            );
        }
    }
}
