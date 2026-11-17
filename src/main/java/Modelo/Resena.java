package Modelo;

public class Resena {

    private int id;
    private int idContenido;
    private int idUsuario;
    private String tipo;
    private int calificacion;
    private String comentario;
    private String fecha;

    public Resena(int idContenido, int idUsuario, String tipo,
                  int calificacion, String comentario, String fecha) {
        this(calificacion, comentario, fecha);
        this.idContenido = idContenido;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
    }

    public Resena(int calificacion, String comentario, String fecha) {
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public Resena() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdContenido() {
        return idContenido;
    }

    public void setIdContenido(int idContenido) {
        this.idContenido = idContenido;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Resena{" +
                "id=" + id +
                ", idContenido=" + idContenido +
                ", idUsuario=" + idUsuario +
                ", tipo='" + tipo + '\'' +
                ", calificacion=" + calificacion +
                ", comentario='" + comentario + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}