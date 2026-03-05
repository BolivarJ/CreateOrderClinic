package app.domain.bank.valueobjects;

import java.util.Objects;

/**
 * Value Object que representa un número de cuenta bancaria único.
 */
public class NumeroCuenta {
    private final String numero;

    public NumeroCuenta(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío");
        }
        if (numero.trim().length() > 20) {
            throw new IllegalArgumentException("El número de cuenta no puede exceder 20 caracteres");
        }
        this.numero = numero.trim().toUpperCase();
    }

    public String getNumero() {
        return numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumeroCuenta that = (NumeroCuenta) o;
        return Objects.equals(numero, that.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return numero;
    }
}
