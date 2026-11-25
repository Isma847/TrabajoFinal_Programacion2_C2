package Service;

import DAO.FavoritoDao;
import Excepciones.DatoInvalidoException;
import Modelo.Favorito;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service de favoritos (relación usuario - contenido). No extiende {@code Service<T>} porque el
 * favorito no tiene baja lógica: se agrega o se quita. Sigue la Alternativa B (carga única y
 * escritura primero en la base).
 *
 * Es independiente de ContenidoService: no comprueba que el contenido exista. Eso lo hacen los
 * menús y controladores, que son los que conocen ambos services.
 */
public class FavoritoService {

    private final FavoritoDao dao;

    /**
     * Colección en memoria. Se eligió un Set porque un favorito es solo una asociación que no
     * puede repetirse y no tiene un orden que importe: lo único que se hace es recorrerlo para
     * filtrar o contar. Favorito define equals y hashCode por id, así que el Set evita guardar
     * dos veces el mismo registro. Que un usuario no repita el mismo contenido se controla con
     * contiene() y, en última instancia, con el UNIQUE de la base.
     */
    private final HashSet<Favorito> coleccion = new HashSet<>();

    /**
     * Recibe el DAO y carga los favoritos desde la base una sola vez.
     */
    public FavoritoService(FavoritoDao dao) throws SQLException {
        this.dao = dao;
        cargar();
    }

    /**
     * Vacía la colección y la llena con lo que hay en la base.
     */
    private void cargar() throws SQLException {
        coleccion.clear();
        for (Favorito favorito : dao.leerTodos()) {
            coleccion.add(favorito);
        }
    }

    /**
     * Agrega un favorito: valida los ids, rechaza el que ya trae id o el que ya existe para ese
     * usuario y contenido, lo guarda en la base y, si salió bien, guarda una copia en la
     * colección. Devuelve false si no se pudo agregar.
     */
    public boolean crear(Favorito favorito) throws SQLException {
        if (favorito == null) {
            throw new DatoInvalidoException("El favorito es obligatorio");
        }
        validarReferencia(favorito.getId_usaurio(), favorito.getId_contenido());
        if (favorito.getId() != 0
                || contiene(favorito.getId_usaurio(), favorito.getId_contenido())) {
            return false;
        }

        int id = dao.crear(favorito);
        if (id <= 0) {
            return false;
        }

        favorito.setId(id);
        coleccion.add(copiar(favorito));
        return true;
    }

    /**
     * Indica si el usuario ya tiene ese contenido como favorito.
     */
    public boolean contiene(int idUsuario, int idContenido) {
        for (Favorito favorito : coleccion) {
            if (favorito.getId_usaurio() == idUsuario
                    && favorito.getId_contenido() == idContenido) {
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina un favorito por su id: primero en la base y, si salió bien, en la colección. Se
     * recorre con Iterator porque es la forma segura de quitar un elemento mientras se recorre.
     */
    public boolean eliminar(int id) throws SQLException {
        Iterator<Favorito> it = coleccion.iterator();
        while (it.hasNext()) {
            Favorito favorito = it.next();
            if (favorito.getId() == id) {
                if (dao.eliminar(id) <= 0) {
                    return false;
                }
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve un conjunto nuevo con copias de los favoritos de un usuario.
     */
    public HashSet<Favorito> leerPorUsuario(int idUsuario) {
        HashSet<Favorito> resultado = new HashSet<>();
        for (Favorito favorito : coleccion) {
            if (favorito.getId_usaurio() == idUsuario) {
                resultado.add(copiar(favorito));
            }
        }
        return resultado;
    }

    /**
     * Elimina todos los favoritos de un usuario, en la base y en la colección. Sirve para limpiar
     * la memoria cuando se elimina una cuenta (la base ya los borra por cascada).
     */
    public void eliminarPorUsuario(int idUsuario) throws SQLException {
        dao.eliminarPorUsuario(idUsuario);
        Iterator<Favorito> it = coleccion.iterator();
        while (it.hasNext()) {
            if (it.next().getId_usaurio() == idUsuario) {
                it.remove();
            }
        }
    }

    /**
     * Quita un contenido de los favoritos de un usuario, en la base y en la colección. Devuelve
     * cuántos registros se borraron (0 si no lo tenía).
     */
    public int quitarDeUsuario(int idUsuario, int idContenido) throws SQLException {
        validarReferencia(idUsuario, idContenido);
        int eliminados = dao.quitarDeUsuario(idUsuario, idContenido);
        Iterator<Favorito> it = coleccion.iterator();
        while (it.hasNext()) {
            Favorito favorito = it.next();
            if (favorito.getId_usaurio() == idUsuario && favorito.getId_contenido() == idContenido) {
                it.remove();
            }
        }
        return eliminados;
    }

    /**
     * Cuenta cuántos usuarios tienen ese contenido como favorito.
     */
    public int obtenerEstadisticaPorContenido(int idContenido) {
        int cantidad = 0;
        for (Favorito favorito : coleccion) {
            if (favorito.getId_contenido() == idContenido) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /**
     * Cuenta los favoritos de cada usuario. Se devuelve un Map porque cada usuario (clave) lleva
     * su propio contador (valor); cada vez que aparece el mismo usuario se le suma 1.
     */
    public Map<Integer, Integer> contarPorUsuario() {
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (Favorito favorito : coleccion) {
            Integer actual = cantidades.get(favorito.getId_usaurio());
            if (actual == null) {
                cantidades.put(favorito.getId_usaurio(), 1);
            } else {
                cantidades.put(favorito.getId_usaurio(), actual + 1);
            }
        }
        return cantidades;
    }

    /**
     * Devuelve la colección interna, no una copia. Usar solo para leer: modificarla desde afuera
     * dejaría la memoria distinta de la base.
     */
    public HashSet<Favorito> getColeccion() {
        return coleccion;
    }

    /**
     * Verifica que el id de usuario y el de contenido sean mayores que cero.
     */
    private void validarReferencia(int idUsuario, int idContenido) {
        if (idUsuario <= 0 || idContenido <= 0) {
            throw new DatoInvalidoException("El usuario y el contenido deben tener un ID válido");
        }
    }

    /**
     * Crea un favorito nuevo con los mismos datos, para no exponer el objeto guardado.
     */
    private Favorito copiar(Favorito favorito) {
        return new Favorito(favorito.getId(), favorito.getId_usaurio(), favorito.getId_contenido());
    }
}