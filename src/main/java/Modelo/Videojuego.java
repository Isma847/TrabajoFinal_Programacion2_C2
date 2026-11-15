package Modelo;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroVideojuegos;

public class Videojuego extends ContenidoMultimedia {
    private int dificultad;
    private GeneroVideojuegos genero;

    public Videojuego(boolean baja, ClasificacionEdad clasificaion, int duracionMinutos, String nombre, int id, int dificultad, GeneroVideojuegos genero) {
        super(baja, clasificaion, duracionMinutos, nombre, id);
        this.dificultad = dificultad;
        this.genero = genero;
    }

    public Videojuego() { }

    public int getDificultad() {
        return dificultad;
    }

    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
    }

    public GeneroVideojuegos getGenero() {
        return genero;
    }

    public void setGenero(GeneroVideojuegos genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return "Videojuegos{" +
                "Dificultad=" + dificultad +
                ", Genero=" + genero +
                "} " + super.toString();
    }
}
