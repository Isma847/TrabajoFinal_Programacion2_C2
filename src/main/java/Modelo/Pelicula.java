package Modelo;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;

import java.util.Objects;

public class Pelicula extends ContenidoAudiovisual implements Comparable<Pelicula>{

    private String fechaEstreno;

    public Pelicula(boolean baja, ClasificacionEdad clasificaion, int duracionMinutos, String nombre, GeneroAudiovisual genero, int idDirector, String fechaEstreno, int id) {
        super(baja, clasificaion, duracionMinutos, nombre, genero, idDirector, id);
        this.fechaEstreno = fechaEstreno;
    }

    public Pelicula() { }

    public String getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(String fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "fechaEstreno='" + fechaEstreno + '\'' +
                "} " + super.toString();
    }

    @Override
    public int compareTo(Pelicula o) {
        return Integer.compare(this.getId(), o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Pelicula pelicula = (Pelicula) o;
        return Objects.equals(getId(), pelicula.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
