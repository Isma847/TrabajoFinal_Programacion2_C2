package Service;

import DAO.ResenaDao;
import Excepciones.DatoInvalidoException;
import Modelo.Resena;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResenaService {

    private final ResenaDao dao = new ResenaDao();
    private final Map<Integer, Resena> coleccion = new LinkedHashMap<>();

    public ResenaService() throws SQLException {
        for (Resena resena : dao.leerTodos()) {
            coleccion.put(resena.getId(), resena);
        }
    }

    public boolean agregar(Resena resena) throws SQLException {
        validarDatos(resena);
        if (resena.getId() != 0) {
            return false;
        }
        if (resena.getIdUsuario() <= 0) {
            throw new DatoInvalidoException("El ID del usuario es obligatorio");
        }
        validarContenido(resena.getTipo(), resena.getIdContenido());

        int id = dao.crear(resena);
        if (id <= 0) {
            return false;
        }

        resena.setId(id);
        coleccion.put(id, copiar(resena));
        return true;
    }

    public Resena buscarPorId(int id) {
        Resena resena = coleccion.get(id);
        if (resena == null) {
            return null;
        }
        return copiar(resena);
    }

    public boolean modificar(Resena resena) throws SQLException {
        validarDatos(resena);
        Resena actual = coleccion.get(resena.getId());
        if (actual == null) {
            return false;
        }

        Resena modificada = copiar(actual);
        modificada.setCalificacion(resena.getCalificacion());
        modificada.setComentario(resena.getComentario());
        modificada.setFecha(resena.getFecha());

        if (dao.actualizar(modificada) <= 0) {
            return false;
        }

        coleccion.put(modificada.getId(), modificada);
        return true;
    }

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

    public ArrayList<Resena> leerTodos() {
        ArrayList<Resena> resenas = new ArrayList<>();
        for (Resena resena : coleccion.values()) {
            resenas.add(copiar(resena));
        }
        return resenas;
    }

    public ArrayList<Resena> filtrarPorContenido(String tipo, int idContenido) {
        validarContenido(tipo, idContenido);
        ArrayList<Resena> resenas = new ArrayList<>();
        for (Resena resena : coleccion.values()) {
            if (resena.getIdContenido() == idContenido && tipo.equals(resena.getTipo())) {
                resenas.add(copiar(resena));
            }
        }
        return resenas;
    }

    private void validarDatos(Resena resena) {
        if (resena == null) {
            throw new DatoInvalidoException("La reseña es obligatoria");
        }

        String fecha = resena.getFecha();
        if (fecha == null || fecha.codePointCount(0, fecha.length()) > 10) {
            throw new DatoInvalidoException("La fecha es obligatoria y admite hasta 10 caracteres");
        }
    }

    private void validarContenido(String tipo, int idContenido) {
        if (!"PELICULA".equals(tipo) && !"SERIE".equals(tipo) && !"JUEGO".equals(tipo)) {
            throw new DatoInvalidoException("El tipo de contenido debe ser PELICULA, SERIE o JUEGO");
        }
        if (idContenido <= 0) {
            throw new DatoInvalidoException("El ID del contenido es obligatorio");
        }
    }

    private Resena copiar(Resena resena) {
        Resena copia = new Resena(
                resena.getIdContenido(),
                resena.getIdUsuario(),
                resena.getTipo(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFecha()
        );
        copia.setId(resena.getId());
        return copia;
    }
}
