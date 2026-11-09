package Modelo;

public abstract class Persona {

    private String Nombre;
    private String Apellido;

    public Persona(String apellido, String nombre) {
        Apellido = apellido;
        Nombre = nombre;
    }

    public Persona() {
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "Apellido='" + Apellido + '\'' +
                ", Nombre='" + Nombre + '\'' +
                '}';
    }
}
