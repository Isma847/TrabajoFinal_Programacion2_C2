package UI;

import Excepciones.DatoInvalidoException;
import Excepciones.SesionInvalidaException;
import Enumerados.Rol;
import Modelo.Usuario;
import Service.GestionCuenta;

import java.util.Scanner;

public class Sistema {

    private final GestionCuenta gestionCuenta;
    private final Scanner entrada;
    private final MenuPeliculas menuPeliculas;
    private final MenuSeries menuSeries;
    private final MenuVideojuegos menuVideojuegos;
    private final MenuResenas menuResenas;
    private final MenuFavoritos menuFavoritos;
    private final MenuDirectores menuDirectores;
    private final MenuCuentas menuCuentas;
    private final MenuMiCuenta menuMiCuenta;

    public Sistema(GestionCuenta gestionCuenta, Scanner entrada,
                   MenuPeliculas menuPeliculas, MenuSeries menuSeries,
                   MenuVideojuegos menuVideojuegos, MenuResenas menuResenas,
                   MenuFavoritos menuFavoritos, MenuDirectores menuDirectores,
                   MenuCuentas menuCuentas, MenuMiCuenta menuMiCuenta) {
        this.gestionCuenta = gestionCuenta;
        this.entrada = entrada;
        this.menuPeliculas = menuPeliculas;
        this.menuSeries = menuSeries;
        this.menuVideojuegos = menuVideojuegos;
        this.menuResenas = menuResenas;
        this.menuFavoritos = menuFavoritos;
        this.menuDirectores = menuDirectores;
        this.menuCuentas = menuCuentas;
        this.menuMiCuenta = menuMiCuenta;
    }


    public void mostrarMenuInicio() {
        while (true) {
            System.out.println("\n--- Temuflix ---");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Crear cuenta");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> iniciarSesion();
                case "2" -> crearCuenta();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void iniciarSesion() {
        System.out.print("Usuario: ");
        String username = entrada.nextLine();

        System.out.print("Contraseña: ");
        String password = entrada.nextLine();

        try {
            gestionCuenta.iniciarSesion(username, password);
        } catch (SesionInvalidaException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (gestionCuenta.getUsuarioActual().getRol() == Rol.ADMINISTRADOR) {
            mostrarMenuAdmin();
        } else {
            mostrarMenuUsuario();
        }
    }

    public void mostrarMenuAdmin() {
        Usuario administrador = gestionCuenta.getUsuarioActual();

        if (administrador == null || administrador.getRol() != Rol.ADMINISTRADOR) {
            return;
        }

        while (true) {
            System.out.println("\n--- Menú administrador ---");
            System.out.println("1. Gestionar películas");
            System.out.println("2. Gestionar series");
            System.out.println("3. Gestionar videojuegos");
            System.out.println("4. Gestionar reseñas");
            System.out.println("5. Estadísticas de favoritos");
            System.out.println("6. Gestionar directores");
            System.out.println("7. Gestionar cuentas");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> menuPeliculas.mostrar();
                case "2" -> menuSeries.mostrar();
                case "3" -> menuVideojuegos.mostrar();
                case "4" -> menuResenas.mostrarAdmin(administrador);
                case "5" -> menuFavoritos.mostrarAdmin();
                case "6" -> menuDirectores.mostrar(administrador);
                case "7" -> {
                    if (menuCuentas.mostrar(administrador)) {
                        gestionCuenta.cerrarSesion();
                        System.out.println(
                                "Tu cuenta fue modificada. Iniciá sesión nuevamente."
                        );
                        return;
                    }
                }
                case "0" -> {
                    gestionCuenta.cerrarSesion();
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    public void mostrarMenuUsuario() {
        while (true) {
            Usuario usuario = gestionCuenta.getUsuarioActual();

            if (usuario == null || usuario.getRol() != Rol.USUARIO) {
                return;
            }

            System.out.println("\n--- Menú usuario ---");
            System.out.println("1. Consultar videojuegos");
            System.out.println("2. Reseñas");
            System.out.println("3. Consultar películas");
            System.out.println("4. Consultar series");
            System.out.println("5. Favoritos");
            System.out.println("6. Consultar directores y películas");
            System.out.println("7. Mi cuenta y estadísticas");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> menuVideojuegos.mostrarUsuario();
                case "2" -> menuResenas.mostrarUsuario(usuario);
                case "3" -> menuPeliculas.mostrarUsuario();
                case "4" -> menuSeries.mostrarUsuario();
                case "5" -> menuFavoritos.mostrarUsuario(usuario);
                case "6" -> menuDirectores.mostrarUsuario();
                case "7" -> menuMiCuenta.mostrar();
                case "0" -> {
                    gestionCuenta.cerrarSesion();
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }


    // Lee una linea y la convierte a entero; devuelve -1 si no es un numero
    private int leerEntero() {
        try {
            return Integer.parseInt(entrada.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void crearCuenta() {
        boolean reintentar = true;
        System.out.println("\n--- Crear cuenta ---");
        System.out.println("1. Usuario");
        System.out.println("2. Administrador");
        System.out.println("0. Volver");
        System.out.print("Opción: ");

        String opcion = entrada.nextLine().trim();
        if ("0".equals(opcion)) {
            return;
        }
        if (!"1".equals(opcion) && !"2".equals(opcion)) {
            System.out.println("Opción inválida");
            return;
        }

        Rol rol = "1".equals(opcion) ? Rol.USUARIO : Rol.ADMINISTRADOR;
        while (reintentar) {
            System.out.print("Nombre: ");
            String nombre = entrada.nextLine();
            System.out.print("Apellido: ");
            String apellido = entrada.nextLine();

            Integer edad = null;
            if (rol == Rol.USUARIO) {
                System.out.print("Edad: ");
                edad = leerEntero();
            }

            System.out.print("Email: ");
            String email = entrada.nextLine();
            System.out.print("Contraseña: ");
            String password = entrada.nextLine();
            System.out.print("Nombre de usuario: ");
            String username = entrada.nextLine();

            boolean puedeEliminar = false;
            if (rol == Rol.ADMINISTRADOR) {
                System.out.println("¿Tiene permiso para eliminar definitivamente?");
                System.out.println("1. Sí");
                System.out.println("2. No");
                System.out.print("Opción: ");
                int permiso = leerEntero();
                if (permiso != 1 && permiso != 2) {
                    System.out.println("Opción inválida");
                    return;
                }
                puedeEliminar = permiso == 1;
            }

            Usuario usuario = new Usuario(apellido, nombre, email, password, username,
                    edad, rol, puedeEliminar);
            try {
                if (gestionCuenta.agregarUsuario(usuario)) {
                    System.out.println("Cuenta creada. Ya podés iniciar sesión.");
                }
                reintentar = false;
            } catch (DatoInvalidoException e) {
                System.out.println(e.getMessage());
                System.out.println("desea volver a intentar crear una cuenta?");
                System.out.println("1. reintentar");
                System.out.println("2. volver al menu inicial");
                int opcionDatoinvalido = leerEntero();
                if (opcionDatoinvalido == 1) {
                    reintentar = true;
                } else if (opcionDatoinvalido == 2) {
                    reintentar = false;
                } else {
                    System.out.println("opcion invalida, volviendo al menu inicial");
                    reintentar = false;
                }
            }
        }
    }
}
