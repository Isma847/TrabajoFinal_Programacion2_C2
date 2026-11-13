package Modelo;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;

import java.util.Objects;

public class ContenidoAudiovisual extends ContenidoMultimedia {
    private GeneroAudiovisual Genero;
    private int idDirector;

    public ContenidoAudiovisual(boolean baja, ClasificacionEdad clasificaion, int duracionMinutos, String nombre, GeneroAudiovisual genero, int idDirector, int id) {
        super(baja, clasificaion, duracionMinutos, nombre, id);
        Genero = genero;
        this.idDirector = idDirector;
    }

    public ContenidoAudiovisual() {}

    public int getIdDirector() {
        return idDirector;
    }

    public void setIdDirector(int idDirector) {
        this.idDirector = idDirector;
    }

    public GeneroAudiovisual getGenero() {
        return Genero;
    }

    public void setGenero(GeneroAudiovisual genero) {
        Genero = genero;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ContenidoAudiovisual that)) return false;
        return this.getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ContenidoAudiovisual{" +
                "director=" + idDirector +
                ", Genero=" + Genero +
                "} " + super.toString();
    }
}
