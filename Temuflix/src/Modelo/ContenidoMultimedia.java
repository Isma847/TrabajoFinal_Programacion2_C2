package Modelo;

public class ContenidoMultimedia {


    private String nombre;
    private int DuracionHoras;
    private int DuracionMinutos;
    private int DuracionSegundos;
    private ClasificacionEdad Clasificaion;
    private boolean Accesible;

    public ContenidoMultimedia(boolean accesible, ClasificacionEdad clasificaion, int duracionHoras, int duracionMinutos, int duracionSegundos, String nombre) {
        Accesible = accesible;
        Clasificaion = clasificaion;
        DuracionHoras = duracionHoras;
        DuracionMinutos = duracionMinutos;
        DuracionSegundos = duracionSegundos;
        this.nombre = nombre;
    }

    public ContenidoMultimedia() {
        Accesible = false;
    }

    public boolean isAccesible() {
        return Accesible;
    }

    public void setAccesible(boolean accesible) {
        Accesible = accesible;
    }

    public ClasificacionEdad getClasificaion() {
        return Clasificaion;
    }

    public void setClasificaion(ClasificacionEdad clasificaion) {
        Clasificaion = clasificaion;
    }

    public int getDuracionHoras() {
        return DuracionHoras;
    }

    public void setDuracionHoras(int duracionHoras) {
        DuracionHoras = duracionHoras;
    }

    public int getDuracionMinutos() {
        return DuracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        DuracionMinutos = duracionMinutos;
    }

    public int getDuracionSegundos() {
        return DuracionSegundos;
    }

    public void setDuracionSegundos(int duracionSegundos) {
        DuracionSegundos = duracionSegundos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "ContenidoMultimedia{" +
                "Clasificaion=" + Clasificaion +
                ", DuracionHoras=" + DuracionHoras +
                ", DuracionMinutos=" + DuracionMinutos +
                ", DuracionSegundos=" + DuracionSegundos +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
