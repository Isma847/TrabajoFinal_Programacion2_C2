package Interfaces;

import java.sql.SQLException;
import java.util.Collection;

/// Contrato que define el funcionamiento de los CRUD principales.

public interface CRUDBase <T> {
    public int crear(T obj) throws SQLException;
    public Collection<T> leerTodos() throws SQLException;
    public int actualizar(T obj) throws SQLException;
    public int eliminar(int id) throws SQLException;
}
