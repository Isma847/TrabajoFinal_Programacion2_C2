package Modelo;

import java.util.Objects;

/**
 * Contenido marcado como favorito por un usuario. Apunta al contenido por su id general.
 */
public class Favorito {
    private int id;
    private int id_usaurio;
    private int id_contenido;

    public Favorito(int id, int id_usaurio, int id_contenido) {
        this.id = id;
        this.id_usaurio = id_usaurio;
        this.id_contenido = id_contenido;
    }

    public Favorito() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_usaurio() {
        return id_usaurio;
    }

    public void setId_usaurio(int id_usaurio) {
        this.id_usaurio = id_usaurio;
    }

    public int getId_contenido() {
        return id_contenido;
    }

    public void setId_contenido(int id_contenido) {
        this.id_contenido = id_contenido;
    }
    
    /**
     * Dos favoritos son iguales si tienen el mismo id.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Favorito favorito = (Favorito) o;
        return getId() == favorito.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
