package Modelo;

import java.util.HashMap;
import java.util.Map;

public class Director extends Persona {

    private Map<Integer, ContenidoAudiovisual> trabajos;

    public Director(String apellido, String nombre, Map<Integer, ContenidoAudiovisual> trabajos) {
        super(apellido, nombre);
        this.trabajos = new HashMap<Integer, ContenidoAudiovisual>();
    }

}
