package Modelo;

public class Director extends Persona {
    private String descripcion;

    public Director(String apellido, String nombre, String descripcion) {
        super(apellido, nombre);
        this.descripcion = descripcion;
    }

}
