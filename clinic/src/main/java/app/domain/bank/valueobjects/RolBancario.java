package app.domain.bank.valueobjects;

/**
 * Enumeración que representa los roles disponibles en el sistema bancario.
 */
public enum RolBancario {
    CLIENTE_PERSONA_NATURAL("Cliente Persona Natural"),
    CLIENTE_EMPRESA("Cliente Empresa"),
    EMPLEADO_VENTANILLA("Empleado de Ventanilla"),
    EMPLEADO_COMERCIAL("Empleado Comercial"),
    EMPLEADO_EMPRESA("Empleado de Empresa"),
    SUPERVISOR_EMPRESA("Supervisor de Empresa"),
    ANALISTA_INTERNO("Analista Interno del Banco");

    private final String descripcion;

    RolBancario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
