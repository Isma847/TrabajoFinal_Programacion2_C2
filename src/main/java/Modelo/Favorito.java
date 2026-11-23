package Modelo;

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
}
