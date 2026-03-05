package app.application.bank.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para crear una nueva cuenta bancaria.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearCuentaBancariaDTO {
    private String numeroIdentificacionTitular;
    private String numeroCuenta;
    private String tipoCuenta;
    private String saldoInicial;
    private String moneda;
}
