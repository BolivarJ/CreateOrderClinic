package app.domain.bank.valueobjects;

/**
 * Enumeración que representa los posibles estados de un préstamo.
 */
public enum EstadoPrestamo {
    EN_ESTUDIO("En estudio"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    DESEMBOLSADO("Desembolsado"),
    EN_MORA("En mora"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
