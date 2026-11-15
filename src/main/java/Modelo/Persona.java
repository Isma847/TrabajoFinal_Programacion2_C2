package Modelo;

public abstract class Persona {

    private String nombre;
    private String apellido;
    private int id;

    public Persona(String apellido, String nombre) {
        this.apellido = apellido;
        this.nombre = nombre;
    }

    public Persona() {
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "apellido='" + apellido + '\'' +
                ", Nombre='" + nombre + '\'' +
                '}';
    }
}
