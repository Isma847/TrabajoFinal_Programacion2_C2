package Modelo;

import java.util.Objects;

public class Cuenta extends Persona{

    private String Username;
    private String Password;
    private String Email;
    private boolean Activo;

    public Cuenta(String apellido, String nombre, boolean activo, String email, String password, String username) {
        super(apellido, nombre);
        Activo = activo;
        Email = email;
        Password = password;
        Username = username;
    }

    public Cuenta() {
    }

    public boolean isActivo() {
        return Activo;
    }

    public void setActivo(boolean activo) {
        Activo = activo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cuenta cuenta)) return false;
        return Objects.equals(Email, cuenta.Email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Email);
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "Activo=" + Activo +
                ", Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                ", Username='" + Username + '\'' +
                "} " + super.toString();
    }
}
