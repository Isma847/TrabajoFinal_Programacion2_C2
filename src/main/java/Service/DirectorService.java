package Service;

import DAO.DirectorDao;
import Excepciones.DatoInvalidoException;
import Modelo.Director;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;

/**
 * Service de directores. No extiende {@code Service<T>} porque el director no tiene baja lógica
 * (no implementa TieneBaja): solo se puede agregar, buscar, modificar, listar y eliminar. Sigue
 * igual la Alternativa B: carga la colección una vez y escribe primero en la base.
 *
 * Devuelve y guarda copias de los directores (método copiar), así que modificar un objeto
 * obtenido de acá no cambia la colección: para que un cambio quede hay que pasar por modificar,
 * que valida y guarda en la base.
 */
public class DirectorService {

    private final DirectorDao dao = new DirectorDao();

    /**
     * Colección en memoria con el id como clave. Se usa un Map porque la búsqueda, modificación y
     * eliminación son por id (acceso directo), y LinkedHashMap para conservar el orden de la base
     * en los listados.
     */
    private final Map<Integer, Director> coleccion = new LinkedHashMap<>();

    /**
     * Carga todos los directores desde la base una sola vez.
     */
    public DirectorService() throws SQLException {
        for (Director director : dao.leerTodos()) {
            coleccion.put(director.getId(), director);
        }
    }

    /**
     * Valida, rechaza al director que ya trae un id (no es nuevo), lo guarda en la base y, si
     * salió bien, guarda una copia en la colección.
     */
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

    /**
     * Devuelve una copia del director, o null si no existe.
     */
    public Director buscarPorId(int id) {
        Director director = coleccion.get(id);
        if (director == null) {
            return null;
        }
        return copiar(director);
    }

    /**
     * Valida y actualiza en la base; si salió bien reemplaza la entrada de la colección por una
     * copia. Devuelve false si el id no existe.
     */
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

    /**
     * Devuelve una lista nueva con copias de todos los directores.
     */
    public ArrayList<Director> leerTodos() {
        ArrayList<Director> directores = new ArrayList<>();
        for (Director director : coleccion.values()) {
            directores.add(copiar(director));
        }
        return directores;
    }

    /**
     * Nombre y apellido obligatorios, de hasta 80 caracteres (el largo de la columna). Lanza
     * DatoInvalidoException si no se cumple.
     */
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

    /**
     * Crea un director nuevo con los mismos datos, para no exponer el objeto guardado en la
     * colección.
     */
    private Director copiar(Director director) {
        Director copia = new Director(director.getApellido(), director.getNombre(), director.getDescripcion());
        copia.setId(director.getId());
        return copia;
    }

    /**
     * Borrado definitivo del director. La base deja sin director a sus películas y series (SET
     * NULL), pero la memoria no se actualiza sola: después de llamarlo hay que llamar a
     * PeliculaService.quitarDirector y SerieService.quitarDirector.
     */
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

    /**
     * Busca directores cuyo "nombre apellido" contenga el texto, sin distinguir mayúsculas
     * (Locale.ROOT evita diferencias por idioma). Lanza DatoInvalidoException si el texto está
     * vacío. Devuelve copias.
     */
    public ArrayList<Director> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new DatoInvalidoException(
                    "Ingresá un nombre o apellido para buscar."
            );
        }

        String consulta = texto.trim().toLowerCase(Locale.ROOT);
        ArrayList<Director> resultado = new ArrayList<>();

        for (Director director : coleccion.values()) {
            String nombreCompleto =
                    director.getNombre() + " " + director.getApellido();

            if (nombreCompleto.toLowerCase(Locale.ROOT).contains(consulta)) {
                resultado.add(copiar(director));
            }
        }

        return resultado;
    }


}