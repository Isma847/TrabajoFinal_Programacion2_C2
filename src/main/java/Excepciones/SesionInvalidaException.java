package Excepciones;

/// Se utiliza para cuando se quiere ingresar a funciones de cuenta prohibidas siendo usuario
/// Como por ejemplo la gestión de contenidos multimedia.

public class SesionInvalidaException extends Exception {

    public SesionInvalidaException(String mensaje) {
        super(mensaje);
    }
}