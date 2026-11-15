package Modelo;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;

public class Serie extends ContenidoAudiovisual {

    private int cantidadCapitulos;
    private int cantidadTemporadas;

    public Serie(boolean baja, ClasificacionEdad clasificaion, int duracionMinutos, String nombre, GeneroAudiovisual genero, int idDirector, int id, int cantidadCapitulos, int cantidadTemporadas) {
        super(baja, clasificaion, duracionMinutos, nombre, genero, idDirector, id);
        this.cantidadCapitulos = cantidadCapitulos;
        this.cantidadTemporadas = cantidadTemporadas;
    }

    public Serie() {}

    public int getCantidadCapitulos() {
        return cantidadCapitulos;
    }

    public void setCantidadCapitulos(int cantidadCapitulos) {
        this.cantidadCapitulos = cantidadCapitulos;
    }

    public int getCantidadTemporadas() {
        return cantidadTemporadas;
    }

    public void setCantidadTemporadas(int cantidadTemporadas) {
        this.cantidadTemporadas = cantidadTemporadas;
    }

    @Override
    public String toString() {
        return "Series{" +
                "CantidadCapitulos=" + cantidadCapitulos +
                ", CantidadTemporadas=" + cantidadTemporadas +
                "} " + super.toString();
    }
}
