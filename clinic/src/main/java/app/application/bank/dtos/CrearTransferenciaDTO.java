package app.application.bank.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para crear una transferencia.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearTransferenciaDTO {
    private String cuentaOrigen;
    private String cuentaDestino;
    private String monto;
    private String moneda;
    private Long idUsuarioCreador;
}
