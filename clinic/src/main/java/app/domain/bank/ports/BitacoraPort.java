package app.domain.bank.ports;

import java.util.Map;

/**
 * Puerto de dominio para la Bitácora de Operaciones.
 * Define el contrato para registrar operaciones en una base de datos NoSQL.
 */
public interface BitacoraPort {
    void registrar(String idBitacora, String tipoOperacion, Long idUsuario, 
                   String rolUsuario, String idProductoAfectado, 
                   Map<String, Object> datosDetalle);
    
    Map<String, Object> obtenerPorId(String idBitacora);
    
    java.util.List<Map<String, Object>> obtenerPorProducto(String idProductoAfectado);
    
    java.util.List<Map<String, Object>> obtenerPorTipoOperacion(String tipoOperacion);
}
