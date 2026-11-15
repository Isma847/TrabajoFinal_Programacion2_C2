package Interfaces;

import java.sql.SQLException;

public interface BajaLogicaEnDao<T> extends CRUDBase<T> {
    public int bajaLogica(int id, boolean baja) throws SQLException;
}
