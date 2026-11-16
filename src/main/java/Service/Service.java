package Service;

import Interfaces.BajaLogicaEnDao;
import Interfaces.TieneBaja;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Service<T extends TieneBaja> {

    protected final BajaLogicaEnDao<T> dao;
    protected final Map<Integer, T> coleccion = new LinkedHashMap<>();

    protected Service(BajaLogicaEnDao<T> dao) throws SQLException {
        this.dao = dao;
        cargar();
    }

    private void cargar() throws SQLException {
        coleccion.clear();
        for (T obj : dao.leerTodos()) {
            coleccion.put(obj.getId(), obj);
        }
    }

    // Sirve para validar que los datos del objeto T sean validos
    protected abstract void validar(T obj);

    public boolean agregar(T obj) throws SQLException {
        validar(obj);
        int id = dao.crear(obj);
        if(id <= 0) {
            return false;
        }
        obj.setId(id);
        coleccion.put(id, obj);
        return true;
    }

    // No verifica si esta de baja
    public T buscarPorId(int id) {
        return coleccion.get(id);
    }

    public boolean modificar(T obj) throws SQLException {
        T actual = coleccion.get(obj.getId());
        if(actual == null) {
            return false;
        }
        validar(obj);
        obj.setBaja(actual.getBaja());
        if(dao.actualizar(obj) > 0) {
            coleccion.put(obj.getId(), obj);
            return true;
        }
        return false;
    }

    // Alterna el estado de baja
    public boolean darDeBaja(int id) throws SQLException {
        T obj = coleccion.get(id);
        if(obj == null) {
            return false;
        }
        boolean nuevaBaja = !obj.getBaja();
        if(dao.bajaLogica(id, nuevaBaja) > 0) {
            obj.setBaja(nuevaBaja);
            return true;
        }
        return false;
    }

    public boolean eliminar(int id) throws SQLException {
        T obj = coleccion.get(id);
        if(obj == null) {
            return false;
        }
        if(dao.eliminar(id) > 0) {
            coleccion.remove(id);
            return true;
        }
        return false;
    }

    public ArrayList<T> leerTodos() {
        return new ArrayList<>(coleccion.values());
    }

    public ArrayList<T> getLista(boolean baja) {
        ArrayList<T> lista = new ArrayList<>();
        for(T obj : coleccion.values()) {
            if (obj.getBaja() == baja) {
                lista.add(obj);
            }
        }
        return lista;
    }
}
