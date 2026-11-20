package Service;

import DAO.AdministradorDao;
import Excepciones.DatoInvalidoException;
import Modelo.Administrador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdministradorService {

    private final AdministradorDao dao = new AdministradorDao();
    private final Map<Integer, Administrador> coleccion = new LinkedHashMap<>();

    public AdministradorService() throws SQLException {
        for (Administrador administrador : dao.leerTodos()) {
            coleccion.put(administrador.getId(), administrador);
        }
    }

    public boolean agregar(Administrador administrador) throws SQLException {
        validar(administrador);
        if (administrador.getId() != 0) {
            return false;
        }

        int id = dao.crear(administrador);
        if (id <= 0) {
            return false;
        }

        administrador.setId(id);
        coleccion.put(id, copiar(administrador));
        return true;
    }

    public Administrador buscarPorId(int id) {
        Administrador administrador = coleccion.get(id);
        if (administrador == null) {
            return null;
        }
        return copiar(administrador);
    }

    public boolean modificar(Administrador administrador) throws SQLException {
        validar(administrador);
        int id = administrador.getId();
        if (!coleccion.containsKey(id)) {
            return false;
        }

        if (dao.actualizar(administrador) <= 0) {
            return false;
        }

        coleccion.put(id, copiar(administrador));
        return true;
    }

    public ArrayList<Administrador> leerTodos() {
        ArrayList<Administrador> administradores = new ArrayList<>();
        for (Administrador administrador : coleccion.values()) {
            administradores.add(copiar(administrador));
        }
        return administradores;
    }

    private void validar(Administrador administrador) {
        if (administrador == null) {
            throw new DatoInvalidoException("El administrador es obligatorio");
        }

        validarTexto(administrador.getNombre(), "nombre", 80);
        validarTexto(administrador.getApellido(), "apellido", 80);
        validarTexto(administrador.getUsername(), "username", 60);
        validarTexto(administrador.getPassword(), "contraseña", 30);
        validarTexto(administrador.getEmail(), "email", 160);
    }

    private void validarTexto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank() || valor.codePointCount(0, valor.length()) > maximo) {
            throw new DatoInvalidoException("El campo " + campo + " debe tener entre 1 y "
                    + maximo + " caracteres");
        }
    }

    private Administrador copiar(Administrador administrador) {
        Administrador copia = new Administrador(
                administrador.getApellido(),
                administrador.getNombre(),
                administrador.getEmail(),
                administrador.getPassword(),
                administrador.getUsername(),
                administrador.isPuedeEliminar()
        );
        copia.setId(administrador.getId());
        copia.setBaja(administrador.getBaja());
        return copia;
    }
}