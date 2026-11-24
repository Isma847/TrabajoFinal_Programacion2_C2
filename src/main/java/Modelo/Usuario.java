package Modelo;

import Enumerados.Rol;

public class Usuario extends Cuenta {

    private Integer edad;
    private Rol rol = Rol.USUARIO;
    private boolean puedeEliminar;

    public Usuario(String apellido, String nombre, String email, String password,
                   String username, Integer edad, Rol rol, boolean puedeEliminar) {
        super(apellido, nombre, email, password, username);
        this.edad = edad;
        this.rol = rol;
        this.puedeEliminar = puedeEliminar;
    }

    public Usuario(String apellido, String nombre, String email, String password,
                   String username, int edad) {
        this(apellido, nombre, email, password, username, edad, Rol.USUARIO, false);
    }

    public Usuario() { }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isPuedeEliminar() {
        return puedeEliminar;
    }

    public void setPuedeEliminar(boolean puedeEliminar) {
        this.puedeEliminar = puedeEliminar;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "edad=" + edad +
                ", rol=" + rol +
                ", puedeEliminar=" + puedeEliminar +
                "} " + super.toString();
    }
}
