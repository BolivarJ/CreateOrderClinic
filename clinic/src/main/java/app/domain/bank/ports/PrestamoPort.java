package app.domain.bank.ports;

import app.domain.bank.models.Prestamo;
import app.domain.bank.valueobjects.Identificacion;
import app.domain.bank.valueobjects.EstadoPrestamo;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de dominio que define el contrato para persistar y recuperar Préstamos.
 */
public interface PrestamoPort {
    void guardar(Prestamo prestamo);
    Optional<Prestamo> obtenerPorId(Long id);
    List<Prestamo> obtenerPorCliente(Identificacion idCliente);
    List<Prestamo> obtenerPorEstado(EstadoPrestamo estado);
    void actualizar(Prestamo prestamo);
    List<Prestamo> obtenerEnEstudioPorCliente(Identificacion idCliente);
}
