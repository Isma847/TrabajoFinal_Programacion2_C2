package Service;

import DAO.ContenidoDao;
import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Coordina los tres services de contenido (películas, series y videojuegos) y les da un id
 * general, el de la tabla pivote contenidoMultimedia. Con ese id las reseñas y los favoritos
 * pueden apuntar a cualquier tipo de contenido.
 *
 * No extiende {@code Service<T>}: se compone de PeliculaService, SerieService, VideojuegoService
 * y ContenidoDao, que recibe por constructor. Cada contenido tiene dos ids: el propio (el de su
 * tabla) y el general (el del pivote).
 *
 * Todas las altas, modificaciones y bajas de contenido deben hacerse por esta clase y no por el
 * service de cada tipo, porque si no el pivote y el registro quedan desactualizados. En cada
 * operación primero se actúa sobre el service del tipo y, si sale bien, recién ahí se actualiza
 * el registro.
 */
public final class ContenidoService {

    private final PeliculaService peliculas;
    private final SerieService series;
    private final VideojuegoService videojuegos;

    private final ContenidoDao dao;

    /**
     * Registro de todos los contenidos, con el id general como clave. Se eligió un Map porque
     * reseñas y favoritos buscan por id general (acceso directo) y LinkedHashMap para conservar
     * el orden de los listados. Guarda las mismas instancias que los services de cada tipo, no
     * copias; por eso, cuando un service reemplaza un objeto (modificar), acá hay que reemplazar
     * la entrada también.
     */
    private final Map<Integer, ContenidoMultimedia> registro = new LinkedHashMap<>();

    /**
     * Recibe los tres services y el DAO del pivote y arma el registro.
     */
    public ContenidoService(PeliculaService peliculas, SerieService series, VideojuegoService videojuegos, ContenidoDao dao) throws SQLException {
        this.peliculas = peliculas;
        this.series = series;
        this.videojuegos = videojuegos;
        this.dao = dao;
        cargar();
    }

    /**
     * Da de alta un contenido nuevo (película, serie o videojuego) en dos pasos: 1) lo agrega en
     * el service de su tipo, que lo valida y lo guarda en la base; 2) crea su fila en el pivote,
     * que genera el id general, y lo guarda en el registro.
     *
     * Si falla el paso 2 se deshace el paso 1 (se elimina el contenido recién creado) y se
     * relanza el error, para no dejar un contenido sin id general. Lanza DatoInvalidoException si
     * el contenido ya trae un id propio distinto de cero.
     */
    public boolean agregar(ContenidoMultimedia contenido) throws SQLException {
        TipoContenido tipo = tipoDe(contenido);

        if (contenido.getId() != 0) {
            throw new DatoInvalidoException(
                    "Un contenido nuevo debe tener ID propio igual a cero."
            );
        }

        if (!agregarEnService(tipo, contenido)) {
            return false;
        }

        try {
            registrar(tipo, contenido);
        } catch (SQLException error) {
            try {
                if (eliminarEnService(tipo, contenido.getId())) {
                    contenido.setId(0);
                } else {
                    error.addSuppressed(new SQLException(
                            "No se pudo deshacer el alta del contenido."
                    ));
                }
            } catch (SQLException errorDeshacer) {
                error.addSuppressed(errorDeshacer);
            }

            throw error;
        }

        return true;
    }

    /**
     * Modifica un contenido existente delegando en el service de su tipo. Como ese service
     * reemplaza su instancia por la recibida, se reemplaza también la entrada del registro. Lanza
     * DatoInvalidoException si el contenido no tiene id general.
     */
    public boolean modificar(ContenidoMultimedia contenido) throws SQLException {
        TipoContenido tipo = tipoDe(contenido);
        int idGeneral = buscarIdContenido(tipo, contenido.getId());

        if (idGeneral == -1) {
            throw new DatoInvalidoException(
                    "El contenido no tiene un registro general."
            );
        }

        if (!modificarEnService(tipo, contenido)) {
            return false;
        }

        registro.put(idGeneral, buscarEnService(tipo, contenido.getId()));
        return true;
    }

    /**
     * Alterna la baja lógica (da de baja o reactiva) de un contenido identificado por tipo e id
     * propio, y refresca el registro.
     */
    public boolean darDeBaja(TipoContenido tipo, int idPropio)
            throws SQLException {
        int idGeneral = buscarIdContenido(tipo, idPropio);

        if (idGeneral == -1) {
            throw new DatoInvalidoException(
                    "El contenido no tiene un registro general."
            );
        }

        if (!darDeBajaEnService(tipo, idPropio)) {
            return false;
        }

        registro.put(idGeneral, buscarEnService(tipo, idPropio));
        return true;
    }

    /**
     * Igual que el anterior, pero recibiendo el objeto, para que los menús y pantallas no tengan
     * que conocer su tipo ni su id propio.
     */
    public boolean darDeBaja(ContenidoMultimedia contenido) throws SQLException {
        return darDeBaja(tipoDe(contenido), contenido.getId());
    }

    /**
     * Contenidos de un tipo (PELICULA, SERIE o JUEGO) según su estado de baja (false = activos).
     */
    public ArrayList<ContenidoMultimedia> filtrarPorTipo(
            TipoContenido tipo, boolean baja) {
        ArrayList<ContenidoMultimedia> resultado = new ArrayList<>();

        for (ContenidoMultimedia contenido : registro.values()) {
            if (contenido.getBaja() == baja && tipoDe(contenido) == tipo) {
                resultado.add(contenido);
            }
        }

        return resultado;
    }


    /**
     * Borrado definitivo de un contenido, por tipo e id propio. Se elimina en el service del tipo
     * y la base borra por cascada su fila del pivote y sus reseñas, favoritos e historial; acá se
     * quita la entrada del registro. Las colecciones en memoria de reseñas y favoritos no se
     * enteran: quien llame a este método debe limpiarlas. Es una acción excepcional, lo habitual
     * es la baja lógica.
     */
    public boolean eliminar(TipoContenido tipo, int idPropio) throws SQLException {
        int idGeneral = buscarIdContenido(tipo, idPropio);

        if (!eliminarEnService(tipo, idPropio)) return false;
        if (idGeneral != -1) registro.remove(idGeneral);
        return true;
    }

    /**
     * Igual que eliminar, pero a partir del id general.
     */
    public boolean eliminarPorID(int idGeneral) throws SQLException {
        ContenidoMultimedia contenido = registro.get(idGeneral);
        if (contenido == null) return false;
        return eliminar(tipoDe(contenido), contenido.getId());
    }

    /**
     * Busca un contenido por id general. Devuelve la instancia real (no una copia) o null si no
     * existe.
     */
    public ContenidoMultimedia buscarPorId(int idGeneral) {
        return registro.get(idGeneral);
    }

    /**
     * Devuelve el id general de un contenido comparándolo con equals (clase e id propio). Los
     * métodos que buscan id devuelven -1 si no se encontró.
     */
    public int buscarIdContenido(ContenidoMultimedia contenido) {
        for (Map.Entry<Integer, ContenidoMultimedia> entrada : registro.entrySet()) {
            if (entrada.getValue().equals(contenido)) {
                return entrada.getKey();
            }
        }
        return -1;
    }

    /**
     * Devuelve el id general a partir del tipo y el id propio, o -1 si no existe.
     */
    public int buscarIdContenido(TipoContenido tipo, int idPropio) {
        for (Map.Entry<Integer, ContenidoMultimedia> entrada : registro.entrySet()) {
            ContenidoMultimedia contenido = entrada.getValue();
            if (contenido.getId() == idPropio && tipoDe(contenido) == tipo) {
                return entrada.getKey();
            }
        }
        return -1;
    }

    /**
     * Devuelve el tipo del contenido que tiene ese id general. Lanza DatoInvalidoException si el
     * id no existe.
     */
    public TipoContenido getTipo(int idGeneral) throws DatoInvalidoException{
        ContenidoMultimedia contenido = registro.get(idGeneral);
        if (contenido == null) throw new DatoInvalidoException("No existe ningun contenido con ese id. Introduzca el id de contenido.");
        return tipoDe(contenido);
    }

    /**
     * Devuelve una lista nueva con todos los contenidos. La lista es una copia (agregarle o
     * quitarle elementos no afecta al registro), pero los contenidos son los mismos objetos.
     */
    public ArrayList<ContenidoMultimedia> leerTodos() {
        return new ArrayList<>(registro.values());
    }

    /**
     * Contenidos de todos los tipos según su estado de baja (false = activos).
     */
    public ArrayList<ContenidoMultimedia> getLista(boolean baja) {
        ArrayList<ContenidoMultimedia> lista = new ArrayList<>();
        for (ContenidoMultimedia contenido : registro.values()) {
            if (contenido.getBaja() == baja) {
                lista.add(contenido);
            }
        }
        return lista;
    }

    /**
     * Carga inicial: lee el pivote y, por cada fila, busca el contenido en el service de su tipo
     * y lo guarda bajo su id general. Si una fila apunta a un contenido que ya no existe, se
     * ignora.
     */
    private void cargar() throws SQLException {
        for (RegistroContenido fila : dao.leerTodos()) {
            ContenidoMultimedia contenido = buscarEnService(fila.getTipo(), fila.getIdPropio());
            if (contenido != null) {
                registro.put(fila.getId(), contenido);
            }
        }
    }

    /**
     * Crea la fila del pivote de un contenido ya guardado en su service y lo agrega al registro
     * con el id general que devuelve la base. Lanza SQLException si no se pudo registrar.
     */
    private void registrar(TipoContenido tipo, ContenidoMultimedia contenido) throws SQLException {
        int idGeneral = dao.crear(tipo, contenido.getId());
        if (idGeneral <= 0) {
            throw new SQLException("No se pudo registrar el contenido");
        }
        registro.put(idGeneral, contenido);
    }

    /**
     * Determina el tipo (enum) de un contenido según su clase, con instanceof. Lanza
     * DatoInvalidoException si no es película, serie ni videojuego.
     */
    private TipoContenido tipoDe(ContenidoMultimedia contenido) {
        if (contenido instanceof Pelicula) {
            return TipoContenido.PELICULA;
        }
        if (contenido instanceof Serie) {
            return TipoContenido.SERIE;
        }
        if (contenido instanceof Videojuego) {
            return TipoContenido.JUEGO;
        }
        throw new DatoInvalidoException("El contenido posee un tipo erroneo.");
    }

    /* Métodos para gestionar por service */

    /**
     * Agrega el contenido en el service de su tipo. Los cinco métodos "...EnService" hacen lo
     * mismo: según el tipo eligen a qué service delegar (y castean el contenido a su clase
     * concreta). Así el switch queda en un solo lugar y el resto de la clase no depende del tipo.
     */
    private boolean agregarEnService(TipoContenido tipo, ContenidoMultimedia contenido) throws SQLException {
        switch (tipo) {
            case PELICULA:
                return peliculas.agregar((Pelicula) contenido);
            case SERIE:
                return series.agregar((Serie) contenido);
            case JUEGO:
                return videojuegos.agregar((Videojuego) contenido);
        }
        return false;
    }

    /**
     * Modifica el contenido en el service de su tipo.
     */
    private boolean modificarEnService(TipoContenido tipo, ContenidoMultimedia contenido) throws SQLException {
        switch (tipo) {
            case PELICULA:
                return peliculas.modificar((Pelicula) contenido);
            case SERIE:
                return series.modificar((Serie) contenido);
            case JUEGO:
                return videojuegos.modificar((Videojuego) contenido);
        }
        return false;
    }

    /**
     * Alterna la baja en el service de su tipo.
     */
    private boolean darDeBajaEnService(TipoContenido tipo, int idPropio) throws SQLException {
        switch (tipo) {
            case PELICULA:
                return peliculas.darDeBaja(idPropio);
            case SERIE:
                return series.darDeBaja(idPropio);
            case JUEGO:
                return videojuegos.darDeBaja(idPropio);
        }
        return false;
    }

    /**
     * Elimina el contenido en el service de su tipo.
     */
    private boolean eliminarEnService(TipoContenido tipo, int idPropio) throws SQLException {
        switch (tipo) {
            case PELICULA:
                return peliculas.eliminar(idPropio);
            case SERIE:
                return series.eliminar(idPropio);
            case JUEGO:
                return videojuegos.eliminar(idPropio);
        }
        return false;
    }

    /**
     * Busca el contenido por id propio en el service de su tipo.
     */
    private ContenidoMultimedia buscarEnService(TipoContenido tipo, int idPropio) {
        switch (tipo) {
            case PELICULA:
                return peliculas.buscarPorId(idPropio);
            case SERIE:
                return series.buscarPorId(idPropio);
            case JUEGO:
                return videojuegos.buscarPorId(idPropio);
        }
        return null;
    }

    ///Busca por el nombre de la pelicula
    public ArrayList<ContenidoMultimedia> buscarPorNombre(String nombre) {
        ArrayList<ContenidoMultimedia> resultado = new ArrayList<>();

        if (nombre == null || nombre.trim().isEmpty()) {
            return resultado;
        }

        String nombreBuscado = nombre.trim().toLowerCase();

        for (ContenidoMultimedia contenido : registro.values()) {
            if (contenido.getNombre() != null &&
                    contenido.getNombre().toLowerCase().contains(nombreBuscado)) {
                resultado.add(contenido);
            }
        }

        return resultado;
    }

    /* Getters de los service */

    /**
     * Acceso al service de películas, para consultar datos (por ejemplo sus filtros). No usarlo
     * para agregar, modificar ni dar de baja: eso debe hacerse por esta clase.
     */
    public PeliculaService getPeliculas() {
        return peliculas;
    }

    /**
     * Acceso al service de series. Solo para consultar.
     */
    public SerieService getSeries() {
        return series;
    }

    /**
     * Acceso al service de videojuegos. Solo para consultar.
     */
    public VideojuegoService getVideojuegos() {
        return videojuegos;
    }
}