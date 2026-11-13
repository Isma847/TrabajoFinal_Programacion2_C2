package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario extends Cuenta{

    private int edad;
    private List<ContenidoMultimedia> Historial;
    private List<ContenidoMultimedia> favoritos;

    public Usuario(String apellido, String nombre, boolean activo, String email, String password, String username, int edad, List<ContenidoMultimedia> favoritos, List<ContenidoMultimedia> historial) {
        super(apellido, nombre, activo, email, password, username);
        this.edad = edad;
        this.favoritos = new ArrayList<>();
        this.Historial = new ArrayList<>();
    }

    public Usuario() {
        this.favoritos = new ArrayList<>();
        this.Historial = new ArrayList<>();
    }

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
                ", Historial=" + Historial +
                ", favoritos=" + favoritos +
                "} " + super.toString();
    }
}
