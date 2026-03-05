package app.domain.bank.valueobjects;

/**
 * Enumeración que representa los tipos de préstamo disponibles.
 */
public enum TipoPrestamo {
    CONSUMO("Consumo"),
    VEHICULO("Vehículo"),
    HIPOTECARIO("Hipotecario"),
    EMPRESARIAL("Empresarial");

    private final String descripcion;

    TipoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
