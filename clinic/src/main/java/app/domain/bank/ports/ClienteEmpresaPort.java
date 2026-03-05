package app.domain.bank.ports;

import app.domain.bank.models.ClienteEmpresa;
import app.domain.bank.valueobjects.Identificacion;
import java.util.Optional;

/**
 * Puerto de dominio que define el contrato para persistar y recuperar Clientes Empresa.
 */
public interface ClienteEmpresaPort {
    void guardar(ClienteEmpresa cliente);
    Optional<ClienteEmpresa> obtenerPorId(Long id);
    Optional<ClienteEmpresa> obtenerPorNit(Identificacion nit);
    void actualizar(ClienteEmpresa cliente);
    boolean existePorNit(Identificacion nit);
}
