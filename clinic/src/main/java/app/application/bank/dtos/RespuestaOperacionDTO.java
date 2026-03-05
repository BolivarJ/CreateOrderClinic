package app.application.bank.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para respuestas de operaciones bancarias.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaOperacionDTO {
    private boolean exitoso;
    private String mensaje;
    private String idOperacion;
    private String codigoError;
}
