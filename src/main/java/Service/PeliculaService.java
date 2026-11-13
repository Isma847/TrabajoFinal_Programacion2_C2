package Service;

import DAO.PeliculaDao;
import Modelo.Pelicula;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class PeliculaService {

    private PeliculaDao dao = new PeliculaDao();
    private LinkedHashSet<Pelicula> peliculas = new LinkedHashSet<>();

    public PeliculaService() throws SQLException, ClassNotFoundException {
        leerTodos();
    }

    public boolean agregar(Pelicula p) throws SQLException, ClassNotFoundException {
        int id = dao.crear(p);
        if (id > 0) {
            p.setId(id);
            peliculas.add(p);
            return true;
        }
        return false;
    }

    public Pelicula buscar_x_id(int id) throws SQLException, ClassNotFoundException {
        for (Pelicula p : peliculas) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void modificar(Pelicula p) throws SQLException, ClassNotFoundException {
        int re = dao.actualizar(p);
        if (re > 0) {
            peliculas.remove(p);
            peliculas.add(p);
        }
    }

    public void darDeBaja(int id) throws SQLException, ClassNotFoundException {
        Pelicula p = buscar_x_id(id);
        int re = dao.eliminar(id, !p.getBaja());
        if (re > 0) {
            p.setBaja(!p.getBaja());
        }
    }

    public LinkedHashSet<Pelicula> leerTodos() throws SQLException, ClassNotFoundException {
        ArrayList<Pelicula> arr = (ArrayList<Pelicula>) dao.leer();
        peliculas.clear();
        for (Pelicula p : arr) {
            peliculas.add(p);
        }
        return peliculas;
    }
}