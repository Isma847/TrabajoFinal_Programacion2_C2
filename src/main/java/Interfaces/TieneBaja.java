package Interfaces;

/// Se implementa en las clases POJO a las que se les puede realizar una baja lógica,
///  y por lo tanto requieren un ID.

public interface TieneBaja {
    int getId();
    void setId(int id);
    boolean getBaja();
    void setBaja(boolean baja);
}
