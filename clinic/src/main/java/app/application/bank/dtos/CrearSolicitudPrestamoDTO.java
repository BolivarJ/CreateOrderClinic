package app.application.bank.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para crear una nueva solicitud de préstamo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearSolicitudPrestamoDTO {
    private String numeroIdentificacionCliente;
    private String tipoPrestamo;
    private BigDecimal montoSolicitado;
    private BigDecimal tasaInteres;
    private Integer plazoMeses;
    private String moneda;
}
