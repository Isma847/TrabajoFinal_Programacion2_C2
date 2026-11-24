package Modelo;


/**
 * Persona que dirige películas y series. No tiene baja lógica: se puede eliminar de verdad.
 */
public class Director extends Persona {
    private String descripcion;

    public Director(String apellido, String nombre, String descripcion) {
        super(apellido, nombre);
        this.descripcion = descripcion;

    }
    public String getDescripcion() {
        return descripcion;
    }

}
