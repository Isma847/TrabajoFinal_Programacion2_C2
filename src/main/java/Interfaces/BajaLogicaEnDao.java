package Interfaces;

import java.sql.SQLException;

/// Se implementa en los DAO que manipulen tablas con baja logica. Esta interfaz no condiciona
/// a que el DAO que la implemente no pueda realizar una baja definitiva.

public interface BajaLogicaEnDao<T> extends CRUDBase<T> {
    public int bajaLogica(int id, boolean baja) throws SQLException;
}
