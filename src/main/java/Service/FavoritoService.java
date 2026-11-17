package Service;

import DAO.FavoritoDao;
import Modelo.Favorito;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

public class FavoritoService {
    private FavoritoDao dao;
    private final HashSet<Favorito> coleccion = new HashSet<>();

    public FavoritoService(FavoritoDao dao) throws SQLException {
        this.dao = dao;
        cargar();
    }

    private void cargar() throws SQLException {
        coleccion.clear();
        for (Favorito obj : dao.leerTodos()) {
            coleccion.add(obj);
        }
    }

    public boolean crear(Favorito favorito) throws SQLException {
        int id = dao.crear(favorito);
        if (id > 0) {
            favorito.setId(id);
            coleccion.add(favorito);
            return true;
        }
        return false;
    }

    public boolean eliminar(int id) throws SQLException {
        for (Favorito obj : coleccion) {
            if (obj.getId() == id) {
                if (dao.eliminar(id) > 0) {
                    coleccion.remove(obj);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public HashSet<Favorito> leerPorUsuario(int id_usuario) {
        HashSet<Favorito> resultado = new HashSet<>();
        for (Favorito obj : coleccion) {
            if (obj.getId_usaurio() == id_usuario) {
                resultado.add(obj);
            }
        }
        return resultado;
    }

    public void eliminarPorUsuario(int id_usuario) throws SQLException {
        Iterator<Favorito> it = coleccion.iterator();
        while (it.hasNext()) {
            Favorito obj = it.next();

            if (obj.getId_usaurio() == id_usuario) {
                if (dao.eliminar(obj.getId()) > 0) {
                    it.remove();
                }
            }
        }
    }
}
