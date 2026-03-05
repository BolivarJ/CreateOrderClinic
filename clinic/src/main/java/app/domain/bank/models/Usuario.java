package app.domain.bank.models;

import app.domain.bank.valueobjects.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un Usuario del sistema bancario.
 * Esta es una entidad con identidad única en el dominio.
 */
public class Usuario {
    private final Long id;
    private String nombreCompleto;
    private Identificacion identificacion;
    private Email email;
    private Telefono telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private RolBancario rol;
    private EstadoUsuario estado;
    private String nombreUsuario;
    private String contrasenia;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Usuario(
        Long id,
        String nombreCompleto,
        Identificacion identificacion,
        Email email,
        Telefono telefono,
        String direccion,
        LocalDate fechaNacimiento,
        RolBancario rol,
        String nombreUsuario,
        String contrasenia
    ) {
        this.id = Objects.requireNonNull(id, "El ID es obligatorio");
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "El nombre es obligatorio");
        this.identificacion = Objects.requireNonNull(identificacion, "La identificación es obligatoria");
        this.email = Objects.requireNonNull(email, "El email es obligatorio");
        this.telefono = Objects.requireNonNull(telefono, "El teléfono es obligatorio");
        this.direccion = Objects.requireNonNull(direccion, "La dirección es obligatoria");
        this.fechaNacimiento = Objects.requireNonNull(fechaNacimiento, "La fecha de nacimiento es obligatoria");
        this.rol = Objects.requireNonNull(rol, "El rol es obligatorio");
        this.nombreUsuario = Objects.requireNonNull(nombreUsuario, "El nombre de usuario es obligatorio");
        this.contrasenia = Objects.requireNonNull(contrasenia, "La contraseña es obligatoria");
        
        validarEdad(fechaNacimiento);
        
        this.estado = EstadoUsuario.ACTIVO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    private void validarEdad(LocalDate fechaNacimiento) {
        LocalDate hoy = LocalDate.now();
        int edad = hoy.getYear() - fechaNacimiento.getYear();
        if (hoy.getMonthValue() < fechaNacimiento.getMonthValue() ||
            (hoy.getMonthValue() == fechaNacimiento.getMonthValue() && hoy.getDayOfMonth() < fechaNacimiento.getDayOfMonth())) {
            edad--;
        }
        if (edad < 18) {
            throw new IllegalArgumentException("El usuario debe ser mayor de 18 años");
        }
    }

    public void cambiarEstado(EstadoUsuario nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado, "El estado no puede ser nulo");
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void actualizarContacto(Email email, Telefono telefono) {
        this.email = Objects.requireNonNull(email, "El email es obligatorio");
        this.telefono = Objects.requireNonNull(telefono, "El teléfono es obligatorio");
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void actualizarDireccion(String nuevaDireccion) {
        this.direccion = Objects.requireNonNull(nuevaDireccion, "La dirección es obligatoria");
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean estaActivo() {
        return estado == EstadoUsuario.ACTIVO;
    }

    public boolean estaBloqueado() {
        return estado == EstadoUsuario.BLOQUEADO;
    }

    // Getters
    public Long getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public Identificacion getIdentificacion() { return identificacion; }
    public Email getEmail() { return email; }
    public Telefono getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public RolBancario getRol() { return rol; }
    public EstadoUsuario getEstado() { return estado; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getContrasenia() { return contrasenia; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(identificacion, usuario.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, identificacion);
    }
}
