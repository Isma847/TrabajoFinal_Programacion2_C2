package Modelo;

public class Usuario extends Cuenta{

    private int edad;

    public Usuario(String apellido, String nombre, String email, String password, String username, int edad) {
        super(apellido, nombre, email, password, username);
        this.edad = edad;
    }

    public Usuario() { }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "edad=" + edad +
                "} " + super.toString();
    }
}