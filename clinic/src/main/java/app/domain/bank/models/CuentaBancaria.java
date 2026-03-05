package app.domain.bank.models;

import app.domain.bank.valueobjects.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una Cuenta Bancaria.
 * Agregado raíz que gestiona el saldo y el estado de la cuenta.
 */
public class CuentaBancaria {
    private final Long id;
    private final NumeroCuenta numeroCuenta;
    private final Identificacion idTitular;
    private TipoCuenta tipoCuenta;
    private Dinero saldoActual;
    private EstadoCuenta estado;
    private LocalDate fechaApertura;
    private LocalDateTime fechaActualizacion;

    public CuentaBancaria(
        Long id,
        NumeroCuenta numeroCuenta,
        Identificacion idTitular,
        TipoCuenta tipoCuenta,
        Dinero saldoInicial,
        String moneda
    ) {
        this.id = Objects.requireNonNull(id, "El ID es obligatorio");
        this.numeroCuenta = Objects.requireNonNull(numeroCuenta, "El número de cuenta es obligatorio");
        this.idTitular = Objects.requireNonNull(idTitular, "El identificador del titular es obligatorio");
        this.tipoCuenta = Objects.requireNonNull(tipoCuenta, "El tipo de cuenta es obligatorio");
        this.saldoActual = Objects.requireNonNull(saldoInicial, "El saldo inicial es obligatorio");
        
        this.estado = EstadoCuenta.ACTIVA;
        this.fechaApertura = LocalDate.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Deposita dinero en la cuenta.
     * Valida que la cuenta esté activa antes de realizar la operación.
     */
    public void depositar(Dinero monto) {
        if (!estado.equals(EstadoCuenta.ACTIVA)) {
            throw new IllegalStateException("No se puede depositar en una cuenta que no está activa");
        }
        Objects.requireNonNull(monto, "El monto es obligatorio");
        this.saldoActual = this.saldoActual.sumar(monto);
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Retira dinero de la cuenta.
     * Valida que exista saldo suficiente y que la cuenta esté activa.
     */
    public void retirar(Dinero monto) {
        if (!estado.equals(EstadoCuenta.ACTIVA)) {
            throw new IllegalStateException("No se puede retirar de una cuenta que no está activa");
        }
        Objects.requireNonNull(monto, "El monto es obligatorio");
        if (this.saldoActual.esInsuficiente(monto)) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el retiro");
        }
        this.saldoActual = this.saldoActual.restar(monto);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void cambiarEstado(EstadoCuenta nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado, "El estado no puede ser nulo");
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean estaActiva() {
        return estado == EstadoCuenta.ACTIVA;
    }

    public boolean puedeRealizarOperaciones() {
        return estado == EstadoCuenta.ACTIVA;
    }

    // Getters
    public Long getId() { return id; }
    public NumeroCuenta getNumeroCuenta() { return numeroCuenta; }
    public Identificacion getIdTitular() { return idTitular; }
    public TipoCuenta getTipoCuenta() { return tipoCuenta; }
    public Dinero getSaldoActual() { return saldoActual; }
    public EstadoCuenta getEstado() { return estado; }
    public LocalDate getFechaApertura() { return fechaApertura; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuentaBancaria that = (CuentaBancaria) o;
        return Objects.equals(id, that.id) && Objects.equals(numeroCuenta, that.numeroCuenta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroCuenta);
    }
}
