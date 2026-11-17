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
        if (administrador.getBaja()) {
            throw new DatoInvalidoException("El esquema actual solo permite guardar administradores activos");
        }

        validarTexto(administrador.getNombre(), "nombre");
        validarTexto(administrador.getApellido(), "apellido");
        validarTexto(administrador.getUsername(), "username");
        validarTexto(administrador.getPassword(), "contraseña");
        validarTexto(administrador.getEmail(), "email");
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DatoInvalidoException("El campo " + campo + " es obligatorio");
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
        return copia;
    }
}