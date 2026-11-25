package Service;

import DAO.ResenaDao;
import Excepciones.DatoInvalidoException;
import Modelo.Resena;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service de reseñas. No extiende {@code Service<T>} porque la reseña no tiene baja lógica (se
 * borra de verdad) y porque su colección tiene otra clave. Sigue la Alternativa B (carga única y
 * escritura primero en la base) y devuelve copias de las reseñas.
 *
 * Regla principal: un usuario solo puede hacer una reseña por contenido, igual que el UNIQUE de
 * la base.
 */
public class ResenaService {

    private final ResenaDao dao = new ResenaDao();

    /**
     * Colección en memoria. Se eligió un Map con clave compuesta "idContenido:idUsuario" porque
     * la regla del negocio es una reseña por usuario y contenido: con esa clave, saber si ya
     * existe o buscar la reseña de un usuario sobre un contenido es un acceso directo
     * (containsKey / get) en lugar de recorrer todo. Es LinkedHashMap para conservar el orden de
     * la base. La clave se arma y se descompone con codificar y decodificar.
     */
    private final Map<String, Resena> coleccion = new LinkedHashMap<>();

    /**
     * Carga las reseñas desde la base. Si encuentra dos para el mismo usuario y contenido lanza
     * SQLException, porque la base estaría inconsistente.
     */
    public ResenaService() throws SQLException {
        for (Resena resena : dao.leerTodos()) {
            String clave = codificar(
                    resena.getIdContenido(),
                    resena.getIdUsuario()
            );

            if (coleccion.putIfAbsent(clave, copiar(resena)) != null) {
                throw new SQLException(
                        "Hay reseñas repetidas para la pareja contenido:usuario "
                                + clave
                );
            }
        }
    }

    /**
     * Arma la clave "idContenido:idUsuario" (y valida que ambos ids sean mayores que cero).
     */
    private String codificar(int idContenido, int idUsuario) {
        validarId(idContenido, "contenido");
        validarId(idUsuario, "usuario");

        return idContenido + ":" + idUsuario;
    }

    /**
     * Hace lo inverso de codificar: separa la clave en [idContenido, idUsuario]. Solo se usa con
     * claves creadas por codificar.
     */
    private int[] decodificar(String clave) {
        String[] partes = clave.split(":");

        return new int[] {
                Integer.parseInt(partes[0]),
                Integer.parseInt(partes[1])
        };
    }

    /**
     * Agrega una reseña nueva: la valida, rechaza la que ya trae id y lanza DatoInvalidoException
     * si el usuario ya reseñó ese contenido. Se guarda en la base y, si salió bien, una copia en
     * la colección.
     */
    public boolean agregar(Resena resena) throws SQLException {
        validarDatos(resena);

        if (resena.getId() != 0) {
            return false;
        }

        String clave = codificar(
                resena.getIdContenido(),
                resena.getIdUsuario()
        );

        if (coleccion.containsKey(clave)) {
            throw new DatoInvalidoException(
                    "Ya publicaste una reseña sobre este contenido. "
                            + "Podés modificar la existente."
            );
        }

        int id = dao.crear(resena);

        if (id <= 0) {
            return false;
        }

        resena.setId(id);
        coleccion.put(clave, copiar(resena));

        return true;
    }

    /**
     * Devuelve la reseña de un usuario sobre un contenido (copia), o null si no existe. Es la
     * búsqueda directa por clave.
     */
    public Resena buscarPorContenidoYUsuario(int idContenido, int idUsuario) {
        String clave = codificar(idContenido, idUsuario);
        Resena resena = coleccion.get(clave);

        return resena == null ? null : copiar(resena);
    }

    /**
     * Devuelve las reseñas de un usuario. Como la clave empieza por el contenido, hay que
     * recorrer y decodificar cada clave.
     */
    public ArrayList<Resena> leerPorUsuario(int idUsuario) {
        validarId(idUsuario, "usuario");

        ArrayList<Resena> resultado = new ArrayList<>();

        for (Map.Entry<String, Resena> entrada : coleccion.entrySet()) {
            int[] ids = decodificar(entrada.getKey());

            if (ids[1] == idUsuario) {
                resultado.add(copiar(entrada.getValue()));
            }
        }

        return resultado;
    }

    /**
     * Devuelve todas las reseñas de un contenido (por su id general).
     */
    public ArrayList<Resena> filtrarPorContenido(int idContenido) {
        validarId(idContenido, "contenido");

        ArrayList<Resena> resultado = new ArrayList<>();

        for (Map.Entry<String, Resena> entrada : coleccion.entrySet()) {
            int[] ids = decodificar(entrada.getKey());

            if (ids[0] == idContenido) {
                resultado.add(copiar(entrada.getValue()));
            }
        }

        return resultado;
    }

    /**
     * Busca por el id propio de la reseña. Se recorre porque la clave del Map no incluye ese id.
     * Devuelve una copia o null.
     */
    public Resena buscarPorId(int idResena) {
        for (Resena resena : coleccion.values()) {
            if (resena.getId() == idResena) {
                return copiar(resena);
            }
        }

        return null;
    }

    /**
     * Devuelve una lista nueva con copias de todas las reseñas.
     */
    public ArrayList<Resena> leerTodos() {
        ArrayList<Resena> resultado = new ArrayList<>();

        for (Resena resena : coleccion.values()) {
            resultado.add(copiar(resena));
        }

        return resultado;
    }

    /**
     * Modifica calificación, comentario y fecha de una reseña existente (se identifica por su
     * id). El usuario y el contenido originales se conservan. Se actualiza la base y recién ahí
     * la colección.
     */
    public boolean modificar(Resena cambios) throws SQLException {
        validarDatos(cambios);

        Resena actual = buscarPorId(cambios.getId());

        if (actual == null) {
            return false;
        }

        // Se conservan el ID, el usuario y el contenido originales.
        Resena modificada = copiar(actual);
        modificada.setCalificacion(cambios.getCalificacion());
        modificada.setComentario(cambios.getComentario());
        modificada.setFecha(cambios.getFecha());

        if (dao.actualizar(modificada) <= 0) {
            return false;
        }

        String clave = codificar(
                actual.getIdContenido(),
                actual.getIdUsuario()
        );

        coleccion.put(clave, modificada);

        return true;
    }

    /**
     * Elimina la reseña por su id, primero en la base y luego en la colección.
     */
    public boolean eliminar(int idResena, int idUsuario) throws SQLException {
        Resena resena = buscarPorId(idResena);

        if (resena == null) {
            return false;
        }

        if (resena.getIdUsuario() != idUsuario) {
            return false;
        }

        if (dao.eliminar(idResena) <= 0) {
            return false;
        }

        String clave = codificar(
                resena.getIdContenido(),
                resena.getIdUsuario()
        );

        coleccion.remove(clave);

        return true;
    }

    /**
     * Reglas de la reseña: calificación entre 1 y 10 y fecha real con formato AAAA-MM-DD. Lanza
     * DatoInvalidoException si no se cumplen.
     */
    private void validarDatos(Resena resena) {
        if (resena == null) {
            throw new DatoInvalidoException("La reseña es obligatoria.");
        }

        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 10) {
            throw new DatoInvalidoException(
                    "La calificación debe estar entre 1 y 10."
            );
        }

        String fecha = resena.getFecha();

        if (fecha == null || fecha.length() != 10) {
            throw new DatoInvalidoException(
                    "La fecha debe tener el formato AAAA-MM-DD."
            );
        }

        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException("La fecha de la reseña no es válida.");
        }
    }

    /**
     * Verifica que un id sea mayor que cero.
     */
    private void validarId(int id, String campo) {
        if (id <= 0) {
            throw new DatoInvalidoException(
                    "El ID del " + campo + " debe ser mayor que cero."
            );
        }
    }

    /**
     * Crea una reseña nueva con los mismos datos, para no exponer el objeto guardado.
     */
    private Resena copiar(Resena resena) {
        Resena copia = new Resena(
                resena.getIdContenido(),
                resena.getIdUsuario(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFecha()
        );

        copia.setId(resena.getId());

        return copia;
    }
}