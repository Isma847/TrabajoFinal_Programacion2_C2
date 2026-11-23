package Modelo;

import Enumerados.TipoContenido;

// Se utiliza para traer los registros de contenido al local.
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
