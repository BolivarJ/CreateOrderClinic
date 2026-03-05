package app.domain.bank.valueobjects;

/**
 * Enumeración que representa los posibles estados de una transferencia.
 */
public enum EstadoTransferencia {
    PENDIENTE("Pendiente"),
    EN_ESPERA_APROBACION("En espera de aprobación"),
    APROBADA("Aprobada"),
    EJECUTADA("Ejecutada"),
    RECHAZADA("Rechazada"),
    VENCIDA("Vencida");

    private final String descripcion;

    EstadoTransferencia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
