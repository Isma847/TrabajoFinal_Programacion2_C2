package Service;

import DAO.FavoritoDao;
import Excepciones.DatoInvalidoException;
import Modelo.Favorito;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FavoritoService {

    private final FavoritoDao dao;
    private final HashSet<Favorito> coleccion = new HashSet<>();

    public FavoritoService(FavoritoDao dao) throws SQLException {
        this.dao = dao;
        cargar();
    }

    private void cargar() throws SQLException {
        coleccion.clear();
        for (Favorito favorito : dao.leerTodos()) {
            coleccion.add(favorito);
        }
    }

    public boolean crear(Favorito favorito) throws SQLException {
        if (favorito == null) {
            throw new DatoInvalidoException("El favorito es obligatorio");
        }
        validarReferencia(favorito.getId_usaurio(), favorito.getId_contenido(), favorito.getTipo());
        if (favorito.getId() != 0
                || contiene(favorito.getId_usaurio(), favorito.getId_contenido(), favorito.getTipo())) {
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

    public boolean contiene(int idUsuario, int idContenido, String tipo) {
        for (Favorito favorito : coleccion) {
            if (favorito.getId_usaurio() == idUsuario
                    && favorito.getId_contenido() == idContenido && favorito.getTipo().equals(tipo)) {
                return true;
            }
        }
        return false;
    }

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

    public HashSet<Favorito> leerPorUsuario(int idUsuario) {
        HashSet<Favorito> resultado = new HashSet<>();
        for (Favorito favorito : coleccion) {
            if (favorito.getId_usaurio() == idUsuario) {
                resultado.add(copiar(favorito));
            }
        }
        return resultado;
    }

    public void eliminarPorUsuario(int idUsuario) throws SQLException {
        dao.eliminarPorUsuario(idUsuario);
        coleccion.removeIf(favorito -> favorito.getId_usaurio() == idUsuario);
    }

    public int quitarDeUsuario(int idUsuario, int idContenido, String tipo) throws SQLException {
        validarReferencia(idUsuario, idContenido, tipo);
        int eliminados = dao.quitarDeUsuario(idUsuario, idContenido, tipo);
        coleccion.removeIf(favorito -> favorito.getId_usaurio() == idUsuario
                && favorito.getId_contenido() == idContenido && favorito.getTipo().equals(tipo));
        return eliminados;
    }

    public int obtenerEstadisticaPorContenido(int idContenido, String tipo) {
        HashSet<Integer> usuarios = new HashSet<>();
        for (Favorito favorito : coleccion) {
            if (favorito.getId_contenido() == idContenido && favorito.getTipo().equals(tipo)) {
                usuarios.add(favorito.getId_usaurio());
            }
        }
        return usuarios.size();
    }

    public int obtenerEstadisticaPorTipo(String tipo) {
        HashSet<String> favoritos = new HashSet<>();
        for (Favorito favorito : coleccion) {
            if (favorito.getTipo().equals(tipo)) {
                favoritos.add(favorito.getId_usaurio() + ":" + favorito.getId_contenido());
            }
        }
        return favoritos.size();
    }

    public Map<Integer, Integer> contarPorUsuario() {
        Map<Integer, HashSet<String>> contenidos = new LinkedHashMap<>();
        for (Favorito favorito : coleccion) {
            contenidos.computeIfAbsent(favorito.getId_usaurio(), id -> new HashSet<>())
                    .add(favorito.getTipo() + ":" + favorito.getId_contenido());
        }

        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (Map.Entry<Integer, HashSet<String>> entrada : contenidos.entrySet()) {
            cantidades.put(entrada.getKey(), entrada.getValue().size());
        }
        return cantidades;
    }

    public HashSet<Favorito> getColeccion() {
        return coleccion;
    }

    private void validarReferencia(int idUsuario, int idContenido, String tipo) {
        if (idUsuario <= 0 || idContenido <= 0) {
            throw new DatoInvalidoException("El usuario y el contenido deben tener un ID válido");
        }
        if (!"PELICULA".equals(tipo) && !"SERIE".equals(tipo) && !"JUEGO".equals(tipo)) {
            throw new DatoInvalidoException("El tipo debe ser PELICULA, SERIE o JUEGO");
        }
    }

    private Favorito copiar(Favorito favorito) {
        return new Favorito(favorito.getId(), favorito.getId_usaurio(),
                favorito.getId_contenido(), favorito.getTipo());
    }
}

