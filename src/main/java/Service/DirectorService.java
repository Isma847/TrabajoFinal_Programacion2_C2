package Service;

import DAO.DirectorDao;
import Excepciones.DatoInvalidoException;
import Modelo.Director;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DirectorService {

    private final DirectorDao dao = new DirectorDao();
    private final Map<Integer, Director> coleccion = new LinkedHashMap<>();

    public DirectorService() throws SQLException {
        for (Director director : dao.leerTodos()) {
            coleccion.put(director.getId(), director);
        }
    }

    public boolean agregar(Director director) throws SQLException {
        validar(director);
        if (director.getId() != 0) {
            return false;
        }

        int id = dao.crear(director);
        if (id <= 0) {
            return false;
        }

        director.setId(id);
        coleccion.put(id, copiar(director));
        return true;
    }

    public Director buscarPorId(int id) {
        Director director = coleccion.get(id);
        if (director == null) {
            return null;
        }
        return copiar(director);
    }

    public boolean modificar(Director director) throws SQLException {
        validar(director);
        int id = director.getId();
        if (!coleccion.containsKey(id)) {
            return false;
        }

        if (dao.actualizar(director) <= 0) {
            return false;
        }

        coleccion.put(id, copiar(director));
        return true;
    }

    public ArrayList<Director> leerTodos() {
        ArrayList<Director> directores = new ArrayList<>();
        for (Director director : coleccion.values()) {
            directores.add(copiar(director));
        }
        return directores;
    }

    private void validar(Director director) {
        if (director == null) {
            throw new DatoInvalidoException("El director es obligatorio");
        }

        String nombre = director.getNombre();
        if (nombre == null || nombre.isBlank() || nombre.codePointCount(0, nombre.length()) > 80) {
            throw new DatoInvalidoException("El nombre debe tener entre 1 y 80 caracteres");
        }

        String apellido = director.getApellido();
        if (apellido == null || apellido.isBlank() || apellido.codePointCount(0, apellido.length()) > 80) {
            throw new DatoInvalidoException("El apellido debe tener entre 1 y 80 caracteres");
        }
    }

    private Director copiar(Director director) {
        Director copia = new Director(director.getApellido(), director.getNombre(), director.getDescripcion());
        copia.setId(director.getId());
        return copia;
    }

    // Llamar a PeliculaService.quitarDirector(id) y SerieService.quitarDirector(id) despues de usarlo
    public boolean eliminar(int id) throws SQLException {
        if (!coleccion.containsKey(id)) {
            return false;
        }
        if (dao.eliminar(id) <= 0) {
            return false;
        }
        coleccion.remove(id);
        return true;
    }
}