package Modelo;

public class Administrador extends Cuenta {

    private int nivelAcceso;

    public Administrador(String apellido, String nombre, boolean baja, String email, String password, String username, int nivelAcceso) {
        super(apellido, nombre, baja, email, password, username);
        this.nivelAcceso = nivelAcceso;
    }

    public Administrador() { }

    public int getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(int nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "nivelAcceso=" + this.nivelAcceso +
                "} " + super.toString();
    }
}