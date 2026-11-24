package Service;

import DAO.ContenidoDao;
import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ContenidoService {

    private PeliculaService peliculas;
    private SerieService series;
    private VideojuegoService videojuegos;

    private ContenidoDao dao;
    private final Map<Integer, ContenidoMultimedia> registro = new LinkedHashMap<>();

    public ContenidoService(PeliculaService peliculas, SerieService series, VideojuegoService videojuegos, ContenidoDao dao) throws SQLException {
        this.peliculas = peliculas;
        this.series = series;
        this.videojuegos = videojuegos;
        this.dao = dao;
        cargar();
    }

    public ContenidoService() {}

    /// Primero se realiza la operación en su respectivo service y si sale bien se realizan
    /// los cambios en el listado general (registro).

    /*
    Para realizar las acciones CRUD para los diferentes contenidos (Peliculas, series y videojuegos)
    se tienen que realizar mediante esta clase, que se ocupa de subir la llamada al service correspondiente y
    si la operación sale bien entonces hace algo en el listado, por ejemplo, para crear un contenido nuevo
    se lo pasa a su service, si ese service da el positivo entonces agrega ese contenido al listado en la bdd
    y luego con el id que devuelve lo agrega al listado local.
     */

    public boolean agregar(ContenidoMultimedia contenido) throws SQLException {
        TipoContenido tipo = tipoDe(contenido);
        if (!agregarEnService(tipo, contenido)) return false;

        try {
            registrar(tipo, contenido);
        } catch (SQLException e) {
            try {
                eliminarEnService(tipo, contenido.getId());
            } catch (SQLException deshacer) {
                e.addSuppressed(deshacer);
            }
            throw e;
        }
        return true;
    }

    public boolean modificar(ContenidoMultimedia contenido) throws SQLException {
        TipoContenido tipo = tipoDe(contenido);
        int idGeneral = buscarIdContenido(contenido);

        if(!modificarEnService(tipo, contenido)) return false;
        if (idGeneral != -1) registro.put(idGeneral, contenido);

        return true;
    }

    /// Alternar la baja
    public boolean darDeBaja(TipoContenido tipo, int idPropio) throws SQLException {
        return darDeBajaEnService(tipo, idPropio);
    }

    public boolean eliminar(TipoContenido tipo, int idPropio) throws SQLException {
        int idGeneral = buscarIdContenido(tipo, idPropio);

        if (!eliminarEnService(tipo, idPropio)) return false;
        if (idGeneral != -1) registro.remove(idGeneral);
        return true;
    }

    public boolean eliminarPorID(int idGeneral) throws SQLException {
        ContenidoMultimedia contenido = registro.get(idGeneral);
        if (contenido == null) return false;
        return eliminar(tipoDe(contenido), contenido.getId());
    }

    public ContenidoMultimedia buscarPorId(int idGeneral) {
        return registro.get(idGeneral);
    }

    /// Los métodos que buscan id devuelven -1 si no se encontro
    /// Busca el id general según el objeto
    public int buscarIdContenido(ContenidoMultimedia contenido) {
        for (Map.Entry<Integer, ContenidoMultimedia> entrada : registro.entrySet()) {
            if (entrada.getValue().equals(contenido)) {
                return entrada.getKey();
            }
        }
        return -1;
    }

    /// Busca el id general según el tipo e id de tipo
    public int buscarIdContenido(TipoContenido tipo, int idPropio) {
        for (Map.Entry<Integer, ContenidoMultimedia> entrada : registro.entrySet()) {
            ContenidoMultimedia contenido = entrada.getValue();
            if (contenido.getId() == idPropio && tipoDe(contenido) == tipo) {
                return entrada.getKey();
            }
        }
        return -1;
    }

    /// Devuelve el tipo de contenido del objeto que posee dicha ID
    public TipoContenido getTipo(int idGeneral) throws DatoInvalidoException{
        ContenidoMultimedia contenido = registro.get(idGeneral);
        if (contenido == null) throw new DatoInvalidoException("No existe ningun contenido con ese id. Introduzca el id de contenido.");
        return tipoDe(contenido);
    }

    public ArrayList<ContenidoMultimedia> leerTodos() {
        return new ArrayList<>(registro.values());
    }

    public ArrayList<ContenidoMultimedia> getLista(boolean baja) {
        ArrayList<ContenidoMultimedia> lista = new ArrayList<>();
        for (ContenidoMultimedia contenido : registro.values()) {
            if (contenido.getBaja() == baja) {
                lista.add(contenido);
            }
        }
        return lista;
    }

    private void cargar() throws SQLException {
        for (RegistroContenido fila : dao.leerTodos()) {
            ContenidoMultimedia contenido = buscarEnService(fila.getTipo(), fila.getIdPropio());
            if (contenido != null) {
                registro.put(fila.getId(), contenido);
            }
        }
    }

    /// Crea una fila para guardar el contenido
    private void registrar(TipoContenido tipo, ContenidoMultimedia contenido) throws SQLException {
        int idGeneral = dao.crear(tipo, contenido.getId());
        if (idGeneral <= 0) {
            throw new SQLException("No se pudo registrar el contenido");
        }
        registro.put(idGeneral, contenido);
    }

    /// Verifica y devuelve el tip de contenido multimedia (Enum)
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

    /// Agrega el contenido a su correspondiente service.
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

    /// Modifica en su correspondiente service.
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

    /// Da de baja en su correspondiente service.
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

    /// Elimina en su correspondiente service.
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

    /// Busca por id en su correspondiente service
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

    /* Getters de los service */
    // SOLO USAR PARA LEER DATOS
    public PeliculaService getPeliculas() {
        return peliculas;
    }

    public SerieService getSeries() {
        return series;
    }

    public VideojuegoService getVideojuegos() {
        return videojuegos;
    }
}