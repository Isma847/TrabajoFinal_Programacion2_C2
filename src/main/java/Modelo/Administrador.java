package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Administrador extends Cuenta {

    private List<ContenidoMultimedia> Agregados;

    public Administrador(String apellido, String nombre, boolean activo, String email, String password, String username, int edad, List<ContenidoMultimedia> favoritos, List<ContenidoMultimedia> historial) {
        super(apellido, nombre, activo, email, password, username);
        this.Agregados = new ArrayList<>();
    }

    public Administrador() {
        this.Agregados = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "Agregados=" + Agregados +
                "} " + super.toString();
    }
}
