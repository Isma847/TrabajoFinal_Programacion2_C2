package Modelo;

import Enumerados.ClasificacionEdad;

public class ContenidoMultimedia {

    private int id;
    private String nombre;
    private int duracionMinutos;
    private ClasificacionEdad clasificaion;
    private boolean baja = false;

    public ContenidoMultimedia(boolean baja, ClasificacionEdad clasificaion, int duracionMinutos, String nombre, int id) {
        this.baja = baja;
        this.clasificaion = clasificaion;
        this.duracionMinutos = duracionMinutos;
        this.nombre = nombre;
        this.id = id;
    }

    public ContenidoMultimedia() {}

    public boolean getBaja() {
        return baja;
    }

    public void setBaja(boolean accesible) {
        baja = accesible;
    }

    public ClasificacionEdad getClasificaion() {
        return clasificaion;
    }

    public void setClasificaion(ClasificacionEdad clasificaion) {
        clasificaion = clasificaion;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        duracionMinutos = duracionMinutos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ContenidoMultimedia{" +
                "Clasificaion=" + clasificaion +
                ", DuracionMinutos=" + duracionMinutos +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
