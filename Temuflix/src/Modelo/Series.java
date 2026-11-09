package Modelo;

public class Series extends ContenidoAudiovisual{

    private int CantidadEpisodios;
    private int CantidadTemporadas;

    public Series(boolean accesible, ClasificacionEdad clasificaion, int duracionHoras, int duracionMinutos, int duracionSegundos, String nombre, Director director, GeneroAudiovisual genero, int ID, int cantidadEpisodios, int cantidadTemporadas) {
        super(accesible, clasificaion, duracionHoras, duracionMinutos, duracionSegundos, nombre, director, genero, ID);
        CantidadEpisodios = cantidadEpisodios;
        CantidadTemporadas = cantidadTemporadas;
    }

    public Series(int ID) {
        super(ID);
    }

    public int getCantidadEpisodios() {
        return CantidadEpisodios;
    }

    public void setCantidadEpisodios(int cantidadEpisodios) {
        CantidadEpisodios = cantidadEpisodios;
    }

    public int getCantidadTemporadas() {
        return CantidadTemporadas;
    }

    public void setCantidadTemporadas(int cantidadTemporadas) {
        CantidadTemporadas = cantidadTemporadas;
    }

    @Override
    public String toString() {
        return "Series{" +
                "CantidadEpisodios=" + CantidadEpisodios +
                ", CantidadTemporadas=" + CantidadTemporadas +
                "} " + super.toString();
    }
}
