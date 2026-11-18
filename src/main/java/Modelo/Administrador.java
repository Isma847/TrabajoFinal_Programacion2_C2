package Modelo;

public class Administrador extends Cuenta {

    private boolean puedeEliminar;

    public Administrador(String apellido, String nombre, String email, String password, String username, boolean puedeEliminar) {
        super(apellido, nombre, email, password, username);
        this.puedeEliminar = puedeEliminar;
    }

    public Administrador() { }

    public boolean isPuedeEliminar() {
        return puedeEliminar;
    }

    public void setPuedeEliminar(boolean puedeEliminar) {
        this.puedeEliminar = puedeEliminar;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "puedeEliminar=" + puedeEliminar +
                "} " + super.toString();
    }
}