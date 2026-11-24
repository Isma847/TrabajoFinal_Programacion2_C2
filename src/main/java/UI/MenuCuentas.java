package UI;

import Enumerados.Rol;
import Excepciones.DatoInvalidoException;
import Modelo.Usuario;
import Service.UsuarioService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuCuentas {

    private final UsuarioService usuarios;
    private final Scanner entrada;

    public MenuCuentas(UsuarioService usuarios, Scanner entrada) {
        this.usuarios = usuarios;
        this.entrada = entrada;
    }

    // Devuelve true si se modificó la cuenta de la sesión actual.
    public boolean mostrar(Usuario administrador) {
        if (administrador == null || administrador.getRol() != Rol.ADMINISTRADOR) {
            throw new DatoInvalidoException(
                    "Se requiere una cuenta administradora."
            );
        }

        while (true) {
            System.out.println("\n--- Gestionar cuentas ---");
            System.out.println("1. Listar");
            System.out.println("2. Consultar por ID");
            System.out.println("3. Crear cuenta");
            System.out.println("4. Modificar un campo");
            System.out.println("5. Dar de baja o reactivar");
            System.out.println("6. Asignar rol administrador por ID");
            System.out.println("7. Quitar rol administrador por ID");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar();
                    case "2" -> mostrarCuenta(pedirCuenta());
                    case "3" -> crear();
                    case "4" -> {
                        if (modificar(administrador.getId())) {
                            return true;
                        }
                    }
                    case "5" -> {
                        if (cambiarEstado(administrador.getId())) {
                            return true;
                        }
                    }
                    case "6" -> {
                        if (asignarRolAdministrador(administrador.getId())) {
                            return true;
                        }
                    }
                    case "7" -> {
                        if (quitarRolAdministrador(administrador.getId())) {
                            return true;
                        }
                    }
                    case "0" -> {
                        return false;
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException | DatoInvalidoException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.err.println(
                        "No se pudo guardar el cambio. " + e.getMessage()
                );
            }
        }
    }

    private void listar() {
        ArrayList<Usuario> lista = usuarios.leerTodos();

        if (lista.isEmpty()) {
            System.out.println("No hay cuentas cargadas.");
            return;
        }

        System.out.println("\nID | Usuario | Rol | Estado");

        for (Usuario usuario : lista) {
            System.out.printf("%d | %s | %s | %s%n",
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getRol(),
                    usuario.getBaja() ? "De baja" : "Activa");
        }
    }

    private void mostrarCuenta(Usuario usuario) {
        System.out.println("\n--- Cuenta " + usuario.getId() + " ---");
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Apellido: " + usuario.getApellido());
        System.out.println("Usuario: " + usuario.getUsername());
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Edad: "
                + (usuario.getEdad() == null ? "Sin informar" : usuario.getEdad()));
        System.out.println("Rol: " + usuario.getRol());
        System.out.println("Permiso de eliminación: "
                + (usuario.isPuedeEliminar() ? "Sí" : "No"));
        System.out.println("Estado: "
                + (usuario.getBaja() ? "De baja" : "Activa"));
    }

    private Usuario pedirCuenta() {
        int id = leerEntero("ID de la cuenta");
        Usuario usuario = usuarios.buscarPorId(id);

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "No existe una cuenta con ese ID."
            );
        }

        return usuario;
    }

    private void crear() throws SQLException {
        String nombre = leerTexto("Nombre");
        String apellido = leerTexto("Apellido");
        String username = leerTexto("Nombre de usuario");
        String email = leerTexto("Email");
        String password = leerTexto("Contraseña");
        Rol rol = leerRol();
        Integer edad = leerEdad(rol);
        boolean puedeEliminar = rol == Rol.ADMINISTRADOR && leerPermiso();

        Usuario usuario = new Usuario(
                apellido, nombre, email, password, username,
                edad, rol, puedeEliminar
        );

        if (usuarios.agregar(usuario)) {
            System.out.println(
                    "Cuenta creada con ID " + usuario.getId() + "."
            );
        } else {
            System.out.println("No se pudo crear la cuenta.");
        }
    }

    private boolean modificar(int idAdministrador) throws SQLException {
        Usuario usuario = pedirCuenta();
        mostrarCuenta(usuario);

        System.out.println("\n¿Qué querés modificar?");
        System.out.println("1. Nombre");
        System.out.println("2. Apellido");
        System.out.println("3. Nombre de usuario");
        System.out.println("4. Email");
        System.out.println("5. Contraseña");
        System.out.println("6. Edad");
        System.out.println("7. Rol");
        System.out.println("8. Permiso de eliminación");
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        switch (entrada.nextLine().trim()) {
            case "1" -> usuario.setNombre(leerTexto("Nuevo nombre"));
            case "2" -> usuario.setApellido(leerTexto("Nuevo apellido"));
            case "3" -> usuario.setUsername(
                    leerTexto("Nuevo nombre de usuario")
            );
            case "4" -> usuario.setEmail(leerTexto("Nuevo email"));
            case "5" -> usuario.setPassword(leerTexto("Nueva contraseña"));
            case "6" -> usuario.setEdad(leerEdad(usuario.getRol()));
            case "7" -> cambiarRol(usuario);
            case "8" -> {
                if (usuario.getRol() != Rol.ADMINISTRADOR) {
                    throw new DatoInvalidoException(
                            "El permiso corresponde al rol ADMINISTRADOR."
                    );
                }

                usuario.setPuedeEliminar(leerPermiso());
            }
            case "0" -> {
                return false;
            }
            default -> throw new IllegalArgumentException("Opción inválida.");
        }

        return guardarCambio(usuario, idAdministrador);
    }

    private void cambiarRol(Usuario usuario) {
        Rol rol = leerRol();
        usuario.setRol(rol);

        if (rol == Rol.USUARIO) {
            usuario.setPuedeEliminar(false);

            if (usuario.getEdad() == null) {
                usuario.setEdad(leerEdad(rol));
            }
        }
    }

    private boolean quitarRolAdministrador(int idAdministrador)
            throws SQLException {
        Usuario usuario = pedirCuenta();

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            System.out.println("La cuenta no tiene el rol ADMINISTRADOR.");
            return false;
        }

        mostrarCuenta(usuario);

        usuario.setRol(Rol.USUARIO);
        usuario.setPuedeEliminar(false);

        if (usuario.getEdad() == null) {
            usuario.setEdad(leerEdad(Rol.USUARIO));
        }

        return guardarCambio(usuario, idAdministrador);
    }

    private boolean cambiarEstado(int idAdministrador) throws SQLException {
        Usuario usuario = pedirCuenta();

        if (usuario.getId() == idAdministrador && !usuario.getBaja()) {
            throw new DatoInvalidoException(
                    "No podés dar de baja tu propia cuenta."
            );
        }

        mostrarCuenta(usuario);
        usuario.setBaja(!usuario.getBaja());

        return guardarCambio(usuario, idAdministrador);
    }

    private boolean asignarRolAdministrador(int idAdministrador)
            throws SQLException {
        Usuario usuario = pedirCuenta();

        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            System.out.println("La cuenta ya tiene el rol ADMINISTRADOR.");
            return false;
        }

        usuario.setRol(Rol.ADMINISTRADOR);

        return guardarCambio(usuario, idAdministrador);
    }

    private boolean guardarCambio(Usuario usuario, int idAdministrador)
            throws SQLException {
        if (usuario.getId() == idAdministrador && usuario.getBaja()) {
            throw new DatoInvalidoException(
                    "No podés dar de baja tu propia cuenta."
            );
        }

        if (!usuarios.modificar(usuario)) {
            System.out.println("No se pudo modificar la cuenta.");
            return false;
        }

        System.out.println("Cuenta actualizada.");

        return usuario.getId() == idAdministrador;
    }

    private Rol leerRol() {
        System.out.println("1. Usuario");
        System.out.println("2. Administrador");

        return switch (leerEntero("Rol")) {
            case 1 -> Rol.USUARIO;
            case 2 -> Rol.ADMINISTRADOR;
            default -> throw new IllegalArgumentException("Rol inválido.");
        };
    }

    private Integer leerEdad(Rol rol) {
        String campo = rol == Rol.ADMINISTRADOR
                ? "Edad (Enter para no informar)" : "Edad";

        String texto = leerTexto(campo).trim();

        if (rol == Rol.ADMINISTRADOR && texto.isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "La edad debe ser un número entero."
            );
        }
    }

    private boolean leerPermiso() {
        System.out.println("¿Tiene permiso para eliminar definitivamente?");
        System.out.println("1. Sí");
        System.out.println("2. No");

        return switch (leerEntero("Opción")) {
            case 1 -> true;
            case 2 -> false;
            default -> throw new IllegalArgumentException("Opción inválida.");
        };
    }

    private String leerTexto(String campo) {
        System.out.print(campo + ": ");
        return entrada.nextLine();
    }

    private int leerEntero(String campo) {
        try {
            return Integer.parseInt(leerTexto(campo).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    campo + " debe ser un número entero."
            );
        }
    }
}