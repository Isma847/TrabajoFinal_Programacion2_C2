package Modelo;

import Interfaces.TieneBaja;

import java.util.Objects;

public class Cuenta extends Persona implements TieneBaja {

    private String username;
    private String password;
    private String email;
    private boolean baja;

    public Cuenta(String apellido, String nombre, boolean baja, String email, String password, String username) {
        super(apellido, nombre);
        this.baja = baja;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public Cuenta() {
    }

    @Override
    public boolean getBaja() {
        return baja;
    }

    @Override
    public void setBaja(boolean baja) {
        this.baja = baja;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cuenta cuenta)) return false;
        return Objects.equals(this.email, cuenta.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.email);
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "baja=" + baja +
                ", Email='" + email + '\'' +
                ", Username='" + username + '\'' +
                "} " + super.toString();
    }
}