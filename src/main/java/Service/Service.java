package Service;

import Interfaces.BajaLogicaEnDao;
import Interfaces.TieneBaja;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service genérico y abstracto del que heredan los services de contenido (películas, series y
 * videojuegos). Concentra las operaciones comunes: agregar, buscar, modificar, dar de baja,
 * eliminar y listar.
 *
 * Sigue la "Alternativa B" de la cátedra: la colección en memoria vive en el Service y se carga
 * una sola vez desde la base al construirlo. En cada alta, modificación o baja se escribe primero
 * en la base y, solo si salió bien, se actualiza la colección (nunca se recarga todo).
 *
 * {@code T extends TieneBaja} garantiza que cualquier T tenga id y baja, que es lo que necesita
 * esta clase. Cada service hijo solo define sus validaciones (validar) y sus filtros propios.
 */
public abstract class Service<T extends TieneBaja> {

    /**
     * DAO de la entidad. Se declara con la interfaz BajaLogicaEnDao (y no con un DAO concreto)
     * para que esta clase sirva para cualquier entidad con baja.
     */
    protected final BajaLogicaEnDao<T> dao;

    /**
     * Colección en memoria, con el id como clave. Se eligió un Map porque buscarPorId, modificar,
     * darDeBaja y eliminar son accesos directos por id, sin recorrer todo. Se usa LinkedHashMap
     * (y no HashMap) porque conserva el orden de inserción: los listados salen en el mismo orden
     * en que vinieron de la base.
     */
    protected final Map<Integer, T> coleccion = new LinkedHashMap<>();

    /**
     * Guarda el DAO y carga la colección desde la base. Es protected porque solo lo llaman los
     * services hijos.
     */
    protected Service(BajaLogicaEnDao<T> dao) throws SQLException {
        this.dao = dao;
        cargar();
    }

    /// Lee la base y arma el Map. Se llama una sola vez, desde el constructor.
    private void cargar() throws SQLException {
        coleccion.clear();
        for (T obj : dao.leerTodos()) {
            coleccion.put(obj.getId(), obj);
        }
    }

    /**
     * Gancho que cada service hijo implementa con sus reglas. agregar y modificar lo llaman antes
     * de tocar la base; si el dato es inválido debe lanzar DatoInvalidoException.
     */
    protected abstract void validar(T obj);

    /**
     * Da de alta un objeto nuevo: lo valida, lo guarda en la base y, si la base devolvió un id,
     * se lo asigna y lo agrega a la colección. Devuelve false si no se pudo guardar.
     */
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

    /**
     * Busca por id. Devuelve null si no existe. No verifica la baja (devuelve también los dados
     * de baja). Devuelve la misma instancia que está en la colección, no una copia.
     */
    public T buscarPorId(int id) {
        return coleccion.get(id);
    }

    /**
     * Modifica un objeto existente: lo valida, conserva el estado de baja que ya tenía, actualiza
     * la base y recién ahí reemplaza la instancia en la colección. Devuelve false si el id no
     * existe o la base no actualizó nada.
     */
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

    /**
     * Alterna el estado de baja lógica: si estaba activo lo da de baja y si estaba de baja lo
     * reactiva. Primero cambia la base y después el objeto en memoria.
     */
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

    /**
     * Borrado definitivo. Es una acción excepcional: lo habitual es la baja lógica. La
     * base borra por cascada lo que dependa del objeto, pero las otras colecciones en memoria no
     * se enteran; quien orquesta la eliminación debe limpiarlas.
     */
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

    /**
     * Devuelve todos los objetos en una lista nueva. Se devuelve una copia de la lista, y no la
     * colección interna, para que quien la use no pueda agregar ni quitar elementos sin pasar por
     * el Service. Se eligió ArrayList porque el resultado solo se recorre y se muestra en orden.
     */
    public ArrayList<T> leerTodos() {
        return new ArrayList<>(coleccion.values());
    }

    /**
     * Devuelve una lista nueva con los objetos cuyo estado de baja coincide con el parámetro
     * (false = activos, true = dados de baja). Los filtros de los services hijos parten de acá.
     */
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
