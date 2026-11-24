package Excepciones;

/// Se utiliza para las verificaciones de datos propias del sistema como por ejemplo los limites de caracteres.

public class DatoInvalidoException extends RuntimeException {
    public DatoInvalidoException(String message) {
        super(message);
    }
}
