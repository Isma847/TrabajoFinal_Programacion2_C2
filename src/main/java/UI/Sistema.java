package UI;

import Excepciones.SesionInvalidaException;
import Modelo.Administrador;
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

    public Sistema(GestionCuenta gestionCuenta, Scanner entrada, MenuPeliculas menuPeliculas,
                   MenuSeries menuSeries, MenuVideojuegos menuVideojuegos, MenuResenas menuResenas) {
        this.gestionCuenta = gestionCuenta;
        this.entrada = entrada;
        this.menuPeliculas = menuPeliculas;
        this.menuSeries = menuSeries;
        this.menuVideojuegos = menuVideojuegos;
        this.menuResenas = menuResenas;
    }

    public void mostrarMenuInicio() {
        while (true) {
            System.out.println("\n--- Temuflix ---");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. crear cuenta.");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> iniciarSesion();
                case "2" -> CrearCuenta();
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

        if (gestionCuenta.getUsuarioActual() instanceof Administrador) {
            mostrarMenuAdmin();
        } else {
            mostrarMenuUsuario();
        }
    }

    public void mostrarMenuAdmin() {
        while (true) {
            System.out.println("\n--- Menú administrador ---");
            System.out.println("1. Gestionar películas");
            System.out.println("2. Gestionar series");
            System.out.println("3. Gestionar videojuegos");
            System.out.println("4. Gestionar reseñas");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> menuPeliculas.mostrar();
                case "2" -> menuSeries.mostrar();
                case "3" -> menuVideojuegos.mostrar();
                case "4" -> menuResenas.mostrarAdmin();
                case "0" -> {
                    gestionCuenta.cerrarSesion();
                    return;
                }
                default -> System.out.println("Opción inválida");
            }
        }
    }

    public void mostrarMenuUsuario() {
        if (!(gestionCuenta.getUsuarioActual() instanceof Usuario usuario)) {
            return;
        }

        while (true) {
            System.out.println("\n--- Menú usuario ---");
            System.out.println("1. jugar videojuegos");
            System.out.println("2. Reseñas");
            System.out.println("3. Consultar películas");
            System.out.println("4. Consultar series");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> menuVideojuegos.mostrarUsuario();
                case "2" -> menuResenas.mostrarUsuario(usuario);
                case "3" -> menuPeliculas.mostrarUsuario();
                case "4" -> menuSeries.mostrarUsuario();
                case "0" -> {
                    gestionCuenta.cerrarSesion();
                    return;
                }
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void CrearCuenta (){
        System.out.println("quiere crear una cuenta de usuario o administrador?");
        System.out.println("1. usuario");
        System.out.println("2. administradoer");
        System.out.println("0. volver a la pantalla anterior");

        switch (entrada.nextLine().trim()) {
            case "1":
                System.out.println("ingrese su nombre.");
                String nombreUser = entrada.nextLine();
                System.out.println("ingrese su apellido.");
                String apellidoUser = entrada.nextLine();
                System.out.println("ingrese su edad.");
                int edad = entrada.nextInt();
                entrada.nextLine();
                System.out.println("ingrese su email.");
                String emailUSer = entrada.nextLine();
                System.out.println("ingrese su password.");
                String passwordUser = entrada.nextLine();
                System.out.println("ingrese su nombre de usuario");
                String usernameUSer = entrada.nextLine();

                gestionCuenta.agregarUsuario(new Usuario (apellidoUser, nombreUser, emailUSer, passwordUser, usernameUSer, edad));
                break;
            case  "2":
                System.out.println("ingrese su nombre");
                String nombreAdmin = entrada.nextLine();
                System.out.println("ingrese su apellido");
                String apellidoAdmin = entrada.nextLine();
                System.out.println("ingrese su email");
                String emailAdmin = entrada.nextLine();
                System.out.println("ingrese su password");
                String passwordAdmin = entrada.nextLine();
                System.out.println("ingrese su nombre de usuario");
                String usernameAdmin = entrada.nextLine();
                System.out.println("tiene permitido eliminar definitivamente?");
                System.out.println("1. si");
                System.out.println("2. no");
                int eliminar =  entrada.nextInt();
                entrada.nextLine();
                if (eliminar == 1) {
                    gestionCuenta.agregarAdministrador(new Administrador(apellidoAdmin, nombreAdmin, emailAdmin, passwordAdmin, usernameAdmin, true));
                } else if (eliminar == 2) {
                    gestionCuenta.agregarAdministrador(new Administrador(apellidoAdmin, nombreAdmin, emailAdmin, passwordAdmin, usernameAdmin, false));
                } else {
                    System.out.println("comando no valido, volviendo al menu anterior.");
                }
                break;
            case "0":
                break;
            default:
                System.out.println("comando invalido.");
                break;
        }


    }
}
