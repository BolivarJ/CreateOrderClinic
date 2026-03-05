package app.domain.bank.ports;

import app.domain.bank.models.ClientePersonaNatural;
import app.domain.bank.valueobjects.Identificacion;
import java.util.Optional;

/**
 * Puerto de dominio que define el contrato para persistar y recuperar Clientes Persona Natural.
 */
public interface ClientePersonaNaturalPort {
    void guardar(ClientePersonaNatural cliente);
    Optional<ClientePersonaNatural> obtenerPorId(Long id);
    Optional<ClientePersonaNatural> obtenerPorIdentificacion(Identificacion identificacion);
    void actualizar(ClientePersonaNatural cliente);
    boolean existePorIdentificacion(Identificacion identificacion);
}
