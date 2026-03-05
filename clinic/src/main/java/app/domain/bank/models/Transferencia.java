package app.domain.bank.models;

import app.domain.bank.valueobjects.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una Transferencia bancaria.
 * Agregado raíz que gestiona el estado y la aprobación de transferencias.
 */
public class Transferencia {
    private final Long id;
    private final NumeroCuenta cuentaOrigen;
    private final NumeroCuenta cuentaDestino;
    private Dinero monto;
    private EstadoTransferencia estado;
    private Long idUsuarioCreador;
    private Long idUsuarioAprobador;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAprobacion;
    private LocalDateTime fechaEjecucion;
    private boolean requiereAprobacion;
    private LocalDateTime fechaVencimiento;

    public Transferencia(
        Long id,
        NumeroCuenta cuentaOrigen,
        NumeroCuenta cuentaDestino,
        Dinero monto,
        Long idUsuarioCreador,
        boolean requiereAprobacion
    ) {
        this.id = Objects.requireNonNull(id, "El ID es obligatorio");
        this.cuentaOrigen = Objects.requireNonNull(cuentaOrigen, "La cuenta origen es obligatoria");
        this.cuentaDestino = Objects.requireNonNull(cuentaDestino, "La cuenta destino es obligatoria");
        this.monto = Objects.requireNonNull(monto, "El monto es obligatorio");
        this.idUsuarioCreador = Objects.requireNonNull(idUsuarioCreador, "El usuario creador es obligatorio");
        
        if (this.cuentaOrigen.equals(this.cuentaDestino)) {
            throw new IllegalArgumentException("Las cuentas origen y destino no pueden ser iguales");
        }
        
        this.requiereAprobacion = requiereAprobacion;
        this.estado = requiereAprobacion ? EstadoTransferencia.EN_ESPERA_APROBACION : EstadoTransferencia.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        
        // Establecer fecha de vencimiento si requiere aprobación (1 hora)
        if (requiereAprobacion) {
            this.fechaVencimiento = this.fechaCreacion.plusHours(1);
        }
    }

    /**
     * Aprueba la transferencia.
     */
    public void aprobar(Long idUsuarioAprobador) {
        if (estado != EstadoTransferencia.EN_ESPERA_APROBACION) {
            throw new IllegalStateException("Solo se pueden aprobar transferencias en espera de aprobación");
        }
        
        if (LocalDateTime.now().isAfter(fechaVencimiento)) {
            this.estado = EstadoTransferencia.VENCIDA;
            throw new IllegalStateException("La transferencia ha vencido");
        }
        
        this.idUsuarioAprobador = Objects.requireNonNull(idUsuarioAprobador, "El usuario aprobador es obligatorio");
        this.estado = EstadoTransferencia.APROBADA;
        this.fechaAprobacion = LocalDateTime.now();
    }

    /**
     * Rechaza la transferencia.
     */
    public void rechazar(Long idUsuarioAprobador) {
        if (estado != EstadoTransferencia.EN_ESPERA_APROBACION) {
            throw new IllegalStateException("Solo se pueden rechazar transferencias en espera de aprobación");
        }
        
        this.idUsuarioAprobador = Objects.requireNonNull(idUsuarioAprobador, "El usuario aprobador es obligatorio");
        this.estado = EstadoTransferencia.RECHAZADA;
        this.fechaAprobacion = LocalDateTime.now();
    }

    /**
     * Ejecuta la transferencia.
     * Valida que esté aprobada o que no requiera aprobación.
     */
    public void ejecutar() {
        if (requiereAprobacion && estado != EstadoTransferencia.APROBADA) {
            throw new IllegalStateException("La transferencia debe estar aprobada para ejecutarse");
        }
        if (!requiereAprobacion && estado != EstadoTransferencia.PENDIENTE) {
            throw new IllegalStateException("La transferencia ya fue ejecutada o está en estado inválido");
        }
        
        this.estado = EstadoTransferencia.EJECUTADA;
        this.fechaEjecucion = LocalDateTime.now();
    }

    /**
     * Marca la transferencia como vencida.
     */
    public void marcarComoVencida() {
        if (estado != EstadoTransferencia.EN_ESPERA_APROBACION) {
            throw new IllegalStateException("Solo las transferencias en espera de aprobación pueden vencer");
        }
        this.estado = EstadoTransferencia.VENCIDA;
    }

    public boolean estaVencida() {
        return estado == EstadoTransferencia.EN_ESPERA_APROBACION && 
               LocalDateTime.now().isAfter(fechaVencimiento);
    }

    public boolean puedeEjecutarse() {
        return (requiereAprobacion && estado == EstadoTransferencia.APROBADA) ||
               (!requiereAprobacion && estado == EstadoTransferencia.PENDIENTE);
    }

    // Getters
    public Long getId() { return id; }
    public NumeroCuenta getCuentaOrigen() { return cuentaOrigen; }
    public NumeroCuenta getCuentaDestino() { return cuentaDestino; }
    public Dinero getMonto() { return monto; }
    public EstadoTransferencia getEstado() { return estado; }
    public Long getIdUsuarioCreador() { return idUsuarioCreador; }
    public Long getIdUsuarioAprobador() { return idUsuarioAprobador; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public LocalDateTime getFechaEjecucion() { return fechaEjecucion; }
    public boolean isRequiereAprobacion() { return requiereAprobacion; }
    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transferencia that = (Transferencia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
