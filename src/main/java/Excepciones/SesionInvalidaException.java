package Excepciones;

public class SesionInvalidaException extends Exception {

    public SesionInvalidaException(String mensaje) {
        super(mensaje);
    }
}