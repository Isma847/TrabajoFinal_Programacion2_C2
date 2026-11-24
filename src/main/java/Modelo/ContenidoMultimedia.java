package Modelo;

import Enumerados.ClasificacionEdad;
import Interfaces.TieneBaja;

import java.util.Objects;

/**
 * Clase base abstracta de todo el contenido del catálogo (película, serie y videojuego): guarda
 * los datos comunes (id propio, nombre, duración, clasificación, descripción) y la baja lógica.
 */
public abstract class ContenidoMultimedia implements Comparable<ContenidoMultimedia>, TieneBaja {
    private int id;
    private String nombre;
    private int duracionMinutos;
    private ClasificacionEdad clasificacionEdad;
    private String descripcion;
    private boolean baja = false;

    public ContenidoMultimedia(boolean baja, ClasificacionEdad clasificacionEdad, int duracionMinutos, String nombre, int id, String descripcion) {
        this.baja = baja;
        this.clasificacionEdad = clasificacionEdad;
        this.duracionMinutos = duracionMinutos;
        this.nombre = nombre;
        this.id = id;
        this.descripcion = descripcion;
    }

    public ContenidoMultimedia() {}

    @Override
    public boolean getBaja() {
        return baja;
    }

    @Override
    public void setBaja(boolean baja) {
        this.baja = baja;
    }

    public ClasificacionEdad getClasificacionEdad() {
        return clasificacionEdad;
    }

    public void setClasificacionEdad(ClasificacionEdad clasificacionEdad) {
        this.clasificacionEdad = clasificacionEdad;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "ContenidoMultimedia{" +
                "Clasificaion=" + clasificacionEdad +
                ", DuracionMinutos=" + duracionMinutos +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    /**
     * Dos contenidos son iguales si son de la misma clase y tienen el mismo id propio (una
     * película 1 y una serie 1 son distintas).
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((ContenidoMultimedia) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }

    /**
     * Orden natural: por id propio.
     */
    @Override
    public int compareTo(ContenidoMultimedia o) {
        return Integer.compare(this.id, o.getId());
    }
}