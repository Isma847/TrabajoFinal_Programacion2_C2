package Modelo;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;

public class Pelicula extends ContenidoAudiovisual {

    private String fechaEstreno;

    public Pelicula(boolean baja, ClasificacionEdad clasificaion, int duracionMinutos, String nombre, GeneroAudiovisual genero, int idDirector, String fechaEstreno, int id, String descripcion) {
        super(baja, clasificaion, duracionMinutos, nombre, genero, idDirector, id, descripcion);
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
}