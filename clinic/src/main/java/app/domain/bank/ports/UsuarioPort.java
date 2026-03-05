package app.domain.bank.ports;

import app.domain.bank.models.Usuario;
import app.domain.bank.valueobjects.Identificacion;
import java.util.Optional;

/**
 * Puerto de dominio que define el contrato para persistar y recuperar Usuarios.
 * La implementación será proporcionada por la capa de adaptadores.
 */
public interface UsuarioPort {
    void guardar(Usuario usuario);
    Optional<Usuario> obtenerPorId(Long id);
    Optional<Usuario> obtenerPorIdentificacion(Identificacion identificacion);
    Optional<Usuario> obtenerPorNombreUsuario(String nombreUsuario);
    void actualizar(Usuario usuario);
    boolean existePorIdentificacion(Identificacion identificacion);
}
