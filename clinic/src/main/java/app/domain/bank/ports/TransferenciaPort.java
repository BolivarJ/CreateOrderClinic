package app.domain.bank.ports;

import app.domain.bank.models.Transferencia;
import app.domain.bank.valueobjects.EstadoTransferencia;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de dominio que define el contrato para persistar y recuperar Transferencias.
 */
public interface TransferenciaPort {
    void guardar(Transferencia transferencia);
    Optional<Transferencia> obtenerPorId(Long id);
    List<Transferencia> obtenerPorEstado(EstadoTransferencia estado);
    void actualizar(Transferencia transferencia);
    List<Transferencia> obtenerEnEsperaDeAprobacion();
    List<Transferencia> obtenerPorCreador(Long idUsuarioCreador);
}
