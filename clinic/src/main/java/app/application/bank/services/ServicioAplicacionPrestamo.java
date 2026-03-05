package app.application.bank.services;

import app.domain.bank.models.CuentaBancaria;
import app.domain.bank.models.Prestamo;
import app.domain.bank.ports.CuentaBancariaPort;
import app.domain.bank.ports.PrestamoPort;
import app.domain.bank.services.ServicioPrestamo;
import app.domain.bank.valueobjects.*;
import app.application.bank.dtos.CrearSolicitudPrestamoDTO;
import app.application.bank.dtos.RespuestaOperacionDTO;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Servicio de Aplicación para gestionar préstamos.
 * Implementa los casos de uso relacionados con préstamos.
 */
public class ServicioAplicacionPrestamo {
    private final PrestamoPort prestamoPort;
    private final CuentaBancariaPort cuentaPort;
    private final ServicioPrestamo servicioPrestamoDominio;
    private Long contadorIds = 1L;

    public ServicioAplicacionPrestamo(
        PrestamoPort prestamoPort,
        CuentaBancariaPort cuentaPort,
        ServicioPrestamo servicioPrestamoDominio
    ) {
        this.prestamoPort = Objects.requireNonNull(prestamoPort, "PrestamoPort es obligatorio");
        this.cuentaPort = Objects.requireNonNull(cuentaPort, "CuentaPort es obligatorio");
        this.servicioPrestamoDominio = Objects.requireNonNull(servicioPrestamoDominio, "ServicioPrestamoDominio es obligatorio");
    }

    /**
     * Caso de uso: Crear solicitud de préstamo.
     */
    public RespuestaOperacionDTO crearSolicitudPrestamo(CrearSolicitudPrestamoDTO dto) {
        try {
            Identificacion identificacion = new Identificacion(dto.getNumeroIdentificacionCliente());
            TipoPrestamo tipoPrestamo = TipoPrestamo.valueOf(dto.getTipoPrestamo().toUpperCase());
            Dinero montoSolicitado = new Dinero(dto.getMontoSolicitado(), dto.getMoneda());

            Prestamo nuevoPrestamo = new Prestamo(
                contadorIds++,
                identificacion,
                tipoPrestamo,
                montoSolicitado,
                dto.getTasaInteres(),
                dto.getPlazoMeses(),
                dto.getMoneda()
            );

            prestamoPort.guardar(nuevoPrestamo);

            return new RespuestaOperacionDTO(
                true,
                "Solicitud de préstamo creada exitosamente",
                nuevoPrestamo.getId().toString(),
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
                "Error al crear la solicitud de préstamo",
                null,
                "INTERNO_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Aprobar un préstamo (solo Analista Interno).
     */
    public RespuestaOperacionDTO aprobarPrestamo(Long idPrestamo, BigDecimal montoAprobado, String moneda) {
        try {
            Prestamo prestamo = prestamoPort.obtenerPorId(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

            Dinero montoAprobar = new Dinero(montoAprobado, moneda);
            servicioPrestamoDominio.aprobarPrestamo(prestamo, montoAprobar);

            return new RespuestaOperacionDTO(
                true,
                "Préstamo aprobado exitosamente",
                idPrestamo.toString(),
                null
            );
        } catch (IllegalArgumentException e) {
            return new RespuestaOperacionDTO(
                false,
                "Error: " + e.getMessage(),
                null,
                "VALIDACION_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Rechazar un préstamo (solo Analista Interno).
     */
    public RespuestaOperacionDTO rechazarPrestamo(Long idPrestamo) {
        try {
            Prestamo prestamo = prestamoPort.obtenerPorId(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

            servicioPrestamoDominio.rechazarPrestamo(prestamo);

            return new RespuestaOperacionDTO(
                true,
                "Préstamo rechazado",
                idPrestamo.toString(),
                null
            );
        } catch (Exception e) {
            return new RespuestaOperacionDTO(
                false,
                "Error al rechazar el préstamo",
                null,
                "INTERNO_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Desembolsar un préstamo aprobado.
     */
    public RespuestaOperacionDTO desembolsarPrestamo(Long idPrestamo, String numeroCuentaDestino) {
        try {
            Prestamo prestamo = prestamoPort.obtenerPorId(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

            NumeroCuenta cuenta = new NumeroCuenta(numeroCuentaDestino);
            servicioPrestamoDominio.desembolsarPrestamo(prestamo, cuenta);

            return new RespuestaOperacionDTO(
                true,
                "Préstamo desembolsado exitosamente",
                idPrestamo.toString(),
                null
            );
        } catch (IllegalArgumentException e) {
            return new RespuestaOperacionDTO(
                false,
                "Error de validación: " + e.getMessage(),
                null,
                "VALIDACION_ERROR"
            );
        } catch (IllegalStateException e) {
            return new RespuestaOperacionDTO(
                false,
                "Error de estado: " + e.getMessage(),
                null,
                "ESTADO_ERROR"
            );
        }
    }
}
