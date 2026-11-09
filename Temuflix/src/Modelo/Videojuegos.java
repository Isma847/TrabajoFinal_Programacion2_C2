package Modelo;

import java.util.Objects;

public class Videojuegos extends ContenidoMultimedia {

    private static int contador = 0;

    private int ID;
    private int Dificultad;
    private GeneroVideojuegos Genero;

    public Videojuegos(boolean accesible, ClasificacionEdad clasificaion, int duracionHoras, int duracionMinutos, int duracionSegundos, String nombre, int dificultad, GeneroVideojuegos genero, int ID) {
        super(accesible, clasificaion, duracionHoras, duracionMinutos, duracionSegundos, nombre);
        Dificultad = dificultad;
        Genero = genero;
        contador++;
        this.ID = contador;
    }


    public Videojuegos(int ID) {
        contador++;
        this.ID = contador;
    }

    public int getDificultad() {
        return Dificultad;
    }

    public void setDificultad(int dificultad) {
        Dificultad = dificultad;
    }

    public GeneroVideojuegos getGenero() {
        return Genero;
    }

    public void setGenero(GeneroVideojuegos genero) {
        Genero = genero;
    }

    public int getID() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Videojuegos that)) return false;
        return ID == that.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    @Override
    public String toString() {
        return "Videojuegos{" +
                "Dificultad=" + Dificultad +
                ", ID=" + ID +
                ", Genero=" + Genero +
                "} " + super.toString();
    }
}
