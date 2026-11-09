package Modelo;

public class Peliculas extends ContenidoAudiovisual {

    private String FechaEstreno;

    public Peliculas(boolean accesible, ClasificacionEdad clasificaion, int duracionHoras, int duracionMinutos, int duracionSegundos, String nombre, Director director, GeneroAudiovisual genero, int ID, String fechaEstreno) {
        super(accesible, clasificaion, duracionHoras, duracionMinutos, duracionSegundos, nombre, director, genero, ID);
        FechaEstreno = fechaEstreno;
    }

    public Peliculas(int ID) {
        super(ID);
    }

    public String getFechaEstreno() {
        return FechaEstreno;
    }

    public void setFechaEstreno(String fechaEstreno) {
        FechaEstreno = fechaEstreno;
    }

    @Override
    public String toString() {
        return "Peliculas{" +
                "FechaEstreno='" + FechaEstreno + '\'' +
                "} " + super.toString();
    }
}
