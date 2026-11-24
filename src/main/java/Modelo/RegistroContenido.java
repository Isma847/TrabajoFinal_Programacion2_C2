package Modelo;

import Enumerados.TipoContenido;

/**
 * Una fila de la tabla pivote contenidoMultimedia: id general, tipo e id propio del contenido.
 * Solo sirve para traer esos datos desde la base.
 */
public class RegistroContenido {

    private int id;
    private TipoContenido tipo;
    private int idPropio;

    public RegistroContenido(int id, TipoContenido tipo, int idPropio) {
        this.id = id;
        this.tipo = tipo;
        this.idPropio = idPropio;
    }

    public RegistroContenido() {}

    public int getId() {
        return id;
    }

    public TipoContenido getTipo() {
        return tipo;
    }

    public int getIdPropio() {
        return idPropio;
    }
}
