package app.domain.bank.ports;

import app.domain.bank.models.CuentaBancaria;
import app.domain.bank.valueobjects.NumeroCuenta;
import app.domain.bank.valueobjects.Identificacion;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de dominio que define el contrato para persistar y recuperar Cuentas Bancarias.
 */
public interface CuentaBancariaPort {
    void guardar(CuentaBancaria cuenta);
    Optional<CuentaBancaria> obtenerPorId(Long id);
    Optional<CuentaBancaria> obtenerPorNumeroCuenta(NumeroCuenta numeroCuenta);
    List<CuentaBancaria> obtenerPorTitular(Identificacion idTitular);
    void actualizar(CuentaBancaria cuenta);
    boolean existePorNumeroCuenta(NumeroCuenta numeroCuenta);
    List<CuentaBancaria> obtenerCuentasActivasPorTitular(Identificacion idTitular);
}
