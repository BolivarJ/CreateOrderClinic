package app.domain.bank.models;

import app.domain.bank.valueobjects.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un Préstamo / Crédito en el banco.
 * Agregado raíz que gestiona el ciclo de vida del préstamo.
 */
public class Prestamo {
    private final Long id;
    private final Identificacion idClienteSolicitante;
    private TipoPrestamo tipoPrestamo;
    private Dinero montoSolicitado;
    private Dinero montoAprobado;
    private BigDecimal tasaInteres;
    private Integer plazoMeses;
    private EstadoPrestamo estado;
    private LocalDate fechaAprobacion;
    private LocalDate fechaDesembolso;
    private NumeroCuenta cuentaDestinoDesembolso;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Prestamo(
        Long id,
        Identificacion idClienteSolicitante,
        TipoPrestamo tipoPrestamo,
        Dinero montoSolicitado,
        BigDecimal tasaInteres,
        Integer plazoMeses,
        String moneda
    ) {
        this.id = Objects.requireNonNull(id, "El ID es obligatorio");
        this.idClienteSolicitante = Objects.requireNonNull(idClienteSolicitante, "El cliente es obligatorio");
        this.tipoPrestamo = Objects.requireNonNull(tipoPrestamo, "El tipo de préstamo es obligatorio");
        this.montoSolicitado = Objects.requireNonNull(montoSolicitado, "El monto solicitado es obligatorio");
        
        if (tasaInteres == null || tasaInteres.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La tasa de interés debe ser mayor a cero");
        }
        if (plazoMeses == null || plazoMeses <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor a cero");
        }
        
        this.tasaInteres = tasaInteres;
        this.plazoMeses = plazoMeses;
        this.montoAprobado = Dinero.cero(moneda);
        this.estado = EstadoPrestamo.EN_ESTUDIO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Aprueba el préstamo con el monto a desembolsar.
     * Solo puede hacerlo un Analista Interno.
     */
    public void aprobar(Dinero montoAprobado) {
        if (estado != EstadoPrestamo.EN_ESTUDIO) {
            throw new IllegalStateException("Solo se pueden aprobar préstamos en estado 'En estudio'");
        }
        if (montoAprobado == null || montoAprobado.getMonto().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto aprobado debe ser mayor a cero");
        }
        
        this.montoAprobado = montoAprobado;
        this.estado = EstadoPrestamo.APROBADO;
        this.fechaAprobacion = LocalDate.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Rechaza el préstamo.
     */
    public void rechazar() {
        if (estado != EstadoPrestamo.EN_ESTUDIO) {
            throw new IllegalStateException("Solo se pueden rechazar préstamos en estado 'En estudio'");
        }
        this.estado = EstadoPrestamo.RECHAZADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Realiza el desembolso del préstamo aprobado.
     */
    public void desembolsar(NumeroCuenta cuentaDestino) {
        if (estado != EstadoPrestamo.APROBADO) {
            throw new IllegalStateException("Solo se pueden desembolsar préstamos aprobados");
        }
        if (cuentaDestino == null) {
            throw new IllegalArgumentException("La cuenta de destino es obligatoria");
        }
        
        this.cuentaDestinoDesembolso = cuentaDestino;
        this.estado = EstadoPrestamo.DESEMBOLSADO;
        this.fechaDesembolso = LocalDate.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean puedeDesembolsarse() {
        return estado == EstadoPrestamo.APROBADO && cuentaDestinoDesembolso != null;
    }

    // Getters
    public Long getId() { return id; }
    public Identificacion getIdClienteSolicitante() { return idClienteSolicitante; }
    public TipoPrestamo getTipoPrestamo() { return tipoPrestamo; }
    public Dinero getMontoSolicitado() { return montoSolicitado; }
    public Dinero getMontoAprobado() { return montoAprobado; }
    public BigDecimal getTasaInteres() { return tasaInteres; }
    public Integer getPlazoMeses() { return plazoMeses; }
    public EstadoPrestamo getEstado() { return estado; }
    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
    public LocalDate getFechaDesembolso() { return fechaDesembolso; }
    public NumeroCuenta getCuentaDestinoDesembolso() { return cuentaDestinoDesembolso; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prestamo prestamo = (Prestamo) o;
        return Objects.equals(id, prestamo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
