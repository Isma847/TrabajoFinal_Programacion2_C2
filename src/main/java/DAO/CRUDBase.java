package DAO;

import java.sql.SQLException;
import java.util.Collection;

public abstract class CRUDBase <T> {
    abstract public int crear(T obj) throws SQLException;
    abstract public Collection<T> leer() throws SQLException;
    abstract public int actualizar(T obj) throws SQLException;
    abstract public int eliminar(int id, boolean baja) throws SQLException;
}
