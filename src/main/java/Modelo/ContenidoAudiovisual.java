package Modelo;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;

public abstract class ContenidoAudiovisual extends ContenidoMultimedia {
    private GeneroAudiovisual genero;
    private int idDirector;

    public ContenidoAudiovisual(boolean baja, ClasificacionEdad clasificaion, int duracionMinutos, String nombre, GeneroAudiovisual genero, int idDirector, int id, String descripcion) {
        super(baja, clasificaion, duracionMinutos, nombre, id, descripcion);
        this.genero = genero;
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
        return genero;
    }

    public void setGenero(GeneroAudiovisual genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return "ContenidoAudiovisual{" +
                "director=" + idDirector +
                ", Genero=" + genero +
                "} " + super.toString();
    }
}
