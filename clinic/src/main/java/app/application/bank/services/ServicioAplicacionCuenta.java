package app.application.bank.services;

import app.domain.bank.models.CuentaBancaria;
import app.domain.bank.ports.CuentaBancariaPort;
import app.domain.bank.services.ServicioCuenta;
import app.domain.bank.valueobjects.*;
import app.application.bank.dtos.CrearCuentaBancariaDTO;
import app.application.bank.dtos.RespuestaOperacionDTO;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Servicio de Aplicación para gestionar cuentas bancarias.
 * Implementa los casos de uso relacionados con cuentas.
 */
public class ServicioAplicacionCuenta {
    private final CuentaBancariaPort cuentaPort;
    private final ServicioCuenta servicioCuentaDominio;
    private Long contadorIds = 1L;

    public ServicioAplicacionCuenta(
        CuentaBancariaPort cuentaPort,
        ServicioCuenta servicioCuentaDominio
    ) {
        this.cuentaPort = Objects.requireNonNull(cuentaPort, "CuentaPort es obligatorio");
        this.servicioCuentaDominio = Objects.requireNonNull(servicioCuentaDominio, "ServicioCuentaDominio es obligatorio");
    }

    /**
     * Caso de uso: Abrir una nueva cuenta bancaria.
     */
    public RespuestaOperacionDTO abrirCuenta(CrearCuentaBancariaDTO dto) {
        try {
            Identificacion identificacion = new Identificacion(dto.getNumeroIdentificacionTitular());
            NumeroCuenta numeroCuenta = new NumeroCuenta(dto.getNumeroCuenta());
            TipoCuenta tipoCuenta = TipoCuenta.valueOf(dto.getTipoCuenta().toUpperCase());
            Dinero saldoInicial = new Dinero(new BigDecimal(dto.getSaldoInicial()), dto.getMoneda());

            CuentaBancaria nuevaCuenta = new CuentaBancaria(
                contadorIds++,
                numeroCuenta,
                identificacion,
                tipoCuenta,
                saldoInicial,
                dto.getMoneda()
            );

            servicioCuentaDominio.abrirCuenta(nuevaCuenta);

            return new RespuestaOperacionDTO(
                true,
                "Cuenta abierta exitosamente",
                nuevaCuenta.getId().toString(),
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
                "Error al abrir la cuenta",
                null,
                "INTERNO_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Bloquear una cuenta.
     */
    public RespuestaOperacionDTO bloquearCuenta(Long idCuenta, String razon) {
        try {
            CuentaBancaria cuenta = cuentaPort.obtenerPorId(idCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

            servicioCuentaDominio.bloquearCuenta(cuenta, razon);

            return new RespuestaOperacionDTO(
                true,
                "Cuenta bloqueada exitosamente",
                idCuenta.toString(),
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
                "Error al bloquear la cuenta",
                null,
                "INTERNO_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Desbloquear una cuenta.
     */
    public RespuestaOperacionDTO desbloquearCuenta(Long idCuenta) {
        try {
            CuentaBancaria cuenta = cuentaPort.obtenerPorId(idCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

            servicioCuentaDominio.desbloquearCuenta(cuenta);

            return new RespuestaOperacionDTO(
                true,
                "Cuenta desbloqueada exitosamente",
                idCuenta.toString(),
                null
            );
        } catch (Exception e) {
            return new RespuestaOperacionDTO(
                false,
                "Error al desbloquear la cuenta",
                null,
                "INTERNO_ERROR"
            );
        }
    }

    /**
     * Caso de uso: Obtener información de una cuenta.
     */
    public CuentaBancaria obtenerInformacionCuenta(String numeroCuenta) {
        NumeroCuenta cuenta = new NumeroCuenta(numeroCuenta);
        return cuentaPort.obtenerPorNumeroCuenta(cuenta)
            .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
    }
}
