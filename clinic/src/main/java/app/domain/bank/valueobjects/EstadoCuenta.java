package app.domain.bank.valueobjects;

/**
 * Enumeración que representa los posibles estados de una cuenta bancaria.
 */
public enum EstadoCuenta {
    ACTIVA("Activa"),
    BLOQUEADA("Bloqueada"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
