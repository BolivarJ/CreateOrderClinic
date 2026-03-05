package app.domain.bank.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object que representa una cantidad de dinero con su correspondiente moneda.
 * Es inmutable y garantiza que el monto siempre sea no negativo.
 */
public class Dinero {
    private final BigDecimal monto;
    private final String moneda;

    public Dinero(BigDecimal monto, String moneda) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto debe ser mayor o igual a cero");
        }
        if (moneda == null || moneda.trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }
        this.monto = monto;
        this.moneda = moneda.toUpperCase();
    }

    public static Dinero cero(String moneda) {
        return new Dinero(BigDecimal.ZERO, moneda);
    }

    public Dinero sumar(Dinero otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("No se pueden sumar monedas diferentes");
        }
        return new Dinero(this.monto.add(otro.monto), this.moneda);
    }

    public Dinero restar(Dinero otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("No se pueden restar monedas diferentes");
        }
        BigDecimal resultado = this.monto.subtract(otro.monto);
        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El resultado sería negativo");
        }
        return new Dinero(resultado, this.moneda);
    }

    public boolean esInsuficiente(Dinero otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("Monedas diferentes");
        }
        return this.monto.compareTo(otro.monto) < 0;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getMoneda() {
        return moneda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dinero dinero = (Dinero) o;
        return Objects.equals(monto, dinero.monto) && Objects.equals(moneda, dinero.moneda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monto, moneda);
    }

    @Override
    public String toString() {
        return monto + " " + moneda;
    }
}
