package app.domain.bank.models;

import app.domain.bank.valueobjects.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa a un Cliente Empresa del banco.
 */
public class ClienteEmpresa {
    private final Long id;
    private final Identificacion nit;
    private String razonSocial;
    private Email email;
    private Telefono telefono;
    private String direccion;
    private Long idRepresentanteLegal;
    private EstadoUsuario estadoCliente;
    private LocalDateTime fechaRegistro;

    public ClienteEmpresa(
        Long id,
        Identificacion nit,
        String razonSocial,
        Email email,
        Telefono telefono,
        String direccion,
        Long idRepresentanteLegal
    ) {
        this.id = Objects.requireNonNull(id, "El ID es obligatorio");
        this.nit = Objects.requireNonNull(nit, "El NIT es obligatorio");
        this.razonSocial = Objects.requireNonNull(razonSocial, "La razón social es obligatoria");
        this.email = Objects.requireNonNull(email, "El email es obligatorio");
        this.telefono = Objects.requireNonNull(telefono, "El teléfono es obligatorio");
        this.direccion = Objects.requireNonNull(direccion, "La dirección es obligatoria");
        this.idRepresentanteLegal = Objects.requireNonNull(idRepresentanteLegal, "El representante legal es obligatorio");
        
        this.estadoCliente = EstadoUsuario.ACTIVO;
        this.fechaRegistro = LocalDateTime.now();
    }

    public void cambiarEstado(EstadoUsuario nuevoEstado) {
        this.estadoCliente = Objects.requireNonNull(nuevoEstado, "El estado no puede ser nulo");
    }

    public void actualizarInformacion(Email email, Telefono telefono, String direccion) {
        this.email = Objects.requireNonNull(email, "El email es obligatorio");
        this.telefono = Objects.requireNonNull(telefono, "El teléfono es obligatorio");
        this.direccion = Objects.requireNonNull(direccion, "La dirección es obligatoria");
    }

    public boolean puedeAbrirCuenta() {
        return estadoCliente == EstadoUsuario.ACTIVO;
    }

    // Getters
    public Long getId() { return id; }
    public Identificacion getNit() { return nit; }
    public String getRazonSocial() { return razonSocial; }
    public Email getEmail() { return email; }
    public Telefono getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public Long getIdRepresentanteLegal() { return idRepresentanteLegal; }
    public EstadoUsuario getEstadoCliente() { return estadoCliente; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteEmpresa that = (ClienteEmpresa) o;
        return Objects.equals(id, that.id) && Objects.equals(nit, that.nit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nit);
    }
}
