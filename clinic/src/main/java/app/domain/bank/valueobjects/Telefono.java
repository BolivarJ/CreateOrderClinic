package app.domain.bank.valueobjects;

import java.util.Objects;

/**
 * Value Object que representa un número de teléfono válido.
 * Rango válido: 7 a 15 dígitos.
 */
public class Telefono {
    private static final int LONGITUD_MINIMA = 7;
    private static final int LONGITUD_MAXIMA = 15;
    
    private final String numero;

    public Telefono(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío");
        }
        
        String soloDigitos = numero.replaceAll("[^0-9]", "");
        
        if (soloDigitos.length() < LONGITUD_MINIMA || soloDigitos.length() > LONGITUD_MAXIMA) {
            throw new IllegalArgumentException(
                String.format("El teléfono debe tener entre %d y %d dígitos", 
                    LONGITUD_MINIMA, LONGITUD_MAXIMA)
            );
        }
        
        this.numero = soloDigitos;
    }

    public String getNumero() {
        return numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Telefono telefono = (Telefono) o;
        return Objects.equals(numero, telefono.numero);
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
