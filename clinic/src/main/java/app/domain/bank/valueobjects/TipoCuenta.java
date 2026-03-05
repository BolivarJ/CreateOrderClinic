package app.domain.bank.valueobjects;

/**
 * Enumeración que representa los tipos de cuenta disponibles en el banco.
 */
public enum TipoCuenta {
    AHORROS("Ahorros"),
    CORRIENTE("Corriente"),
    PERSONAL("Personal"),
    EMPRESARIAL("Empresarial");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
