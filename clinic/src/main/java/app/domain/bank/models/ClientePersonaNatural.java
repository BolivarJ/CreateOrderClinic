package app.domain.bank.models;

import app.domain.bank.valueobjects.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa a un Cliente Persona Natural del banco.
 */
public class ClientePersonaNatural {
    private final Long id;
    private final Identificacion identificacion;
    private String nombreCompleto;
    private Email email;
    private Telefono telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private EstadoUsuario estadoCliente;
    private LocalDateTime fechaRegistro;

    public ClientePersonaNatural(
        Long id,
        Identificacion identificacion,
        String nombreCompleto,
        Email email,
        Telefono telefono,
        String direccion,
        LocalDate fechaNacimiento
    ) {
        this.id = Objects.requireNonNull(id, "El ID es obligatorio");
        this.identificacion = Objects.requireNonNull(identificacion, "La identificación es obligatoria");
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "El nombre es obligatorio");
        this.email = Objects.requireNonNull(email, "El email es obligatorio");
        this.telefono = Objects.requireNonNull(telefono, "El teléfono es obligatorio");
        this.direccion = Objects.requireNonNull(direccion, "La dirección es obligatoria");
        this.fechaNacimiento = Objects.requireNonNull(fechaNacimiento, "La fecha de nacimiento es obligatoria");
        
        validarEdad(fechaNacimiento);
        
        this.estadoCliente = EstadoUsuario.ACTIVO;
        this.fechaRegistro = LocalDateTime.now();
    }

    private void validarEdad(LocalDate fechaNacimiento) {
        LocalDate hoy = LocalDate.now();
        int edad = hoy.getYear() - fechaNacimiento.getYear();
        if (hoy.getMonthValue() < fechaNacimiento.getMonthValue() ||
            (hoy.getMonthValue() == fechaNacimiento.getMonthValue() && hoy.getDayOfMonth() < fechaNacimiento.getDayOfMonth())) {
            edad--;
        }
        if (edad < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor de 18 años");
        }
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
    public Identificacion getIdentificacion() { return identificacion; }
    public String getNombreCompleto() { return nombreCompleto; }
    public Email getEmail() { return email; }
    public Telefono getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public EstadoUsuario getEstadoCliente() { return estadoCliente; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientePersonaNatural that = (ClientePersonaNatural) o;
        return Objects.equals(id, that.id) && Objects.equals(identificacion, that.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, identificacion);
    }
}
