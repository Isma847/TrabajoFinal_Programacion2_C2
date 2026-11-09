package Modelo;

import java.util.Objects;

public class ContenidoAudiovisual extends ContenidoMultimedia {

    private static int contador = 0;

    private int ID;
    private GeneroAudiovisual Genero;
    private Director director;

    public ContenidoAudiovisual(boolean accesible, ClasificacionEdad clasificaion, int duracionHoras, int duracionMinutos, int duracionSegundos, String nombre, Director director, GeneroAudiovisual genero, int ID) {
        super(accesible, clasificaion, duracionHoras, duracionMinutos, duracionSegundos, nombre);
        this.director = director;
        Genero = genero;
        contador++;
        this.ID = contador;
    }

    public ContenidoAudiovisual(int ID) {
        contador++;
        this.ID = contador;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public GeneroAudiovisual getGenero() {
        return Genero;
    }

    public void setGenero(GeneroAudiovisual genero) {
        Genero = genero;
    }

    public int getID() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ContenidoAudiovisual that)) return false;
        return ID == that.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    @Override
    public String toString() {
        return "ContenidoAudiovisual{" +
                "director=" + director +
                ", ID=" + ID +
                ", Genero=" + Genero +
                "} " + super.toString();
    }
}
