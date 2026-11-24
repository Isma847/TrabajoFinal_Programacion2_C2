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

public class ResenaService {

    private final ResenaDao dao = new ResenaDao();

    // Clave: idContenidoGeneral:idUsuario
    private final Map<String, Resena> coleccion = new LinkedHashMap<>();

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

    private String codificar(int idContenido, int idUsuario) {
        validarId(idContenido, "contenido");
        validarId(idUsuario, "usuario");

        return idContenido + ":" + idUsuario;
    }

    // Posición 0: ID general del contenido.
    // Posición 1: ID del usuario.
    // Se utiliza únicamente con claves creadas por codificar().
    private int[] decodificar(String clave) {
        String[] partes = clave.split(":");

        return new int[] {
                Integer.parseInt(partes[0]),
                Integer.parseInt(partes[1])
        };
    }

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

    public Resena buscarPorContenidoYUsuario(int idContenido, int idUsuario) {
        String clave = codificar(idContenido, idUsuario);
        Resena resena = coleccion.get(clave);

        return resena == null ? null : copiar(resena);
    }

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

    public Resena buscarPorId(int idResena) {
        for (Resena resena : coleccion.values()) {
            if (resena.getId() == idResena) {
                return copiar(resena);
            }
        }

        return null;
    }

    public ArrayList<Resena> leerTodos() {
        ArrayList<Resena> resultado = new ArrayList<>();

        for (Resena resena : coleccion.values()) {
            resultado.add(copiar(resena));
        }

        return resultado;
    }

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

    public boolean eliminar(int idResena) throws SQLException {
        Resena resena = buscarPorId(idResena);

        if (resena == null) {
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

    private void validarId(int id, String campo) {
        if (id <= 0) {
            throw new DatoInvalidoException(
                    "El ID del " + campo + " debe ser mayor que cero."
            );
        }
    }

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