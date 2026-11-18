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
    private final MenuFavoritos menuFavoritos;

    public Sistema(GestionCuenta gestionCuenta, Scanner entrada, MenuPeliculas menuPeliculas,
                   MenuSeries menuSeries, MenuVideojuegos menuVideojuegos,
                   MenuResenas menuResenas, MenuFavoritos menuFavoritos) {
        this.gestionCuenta = gestionCuenta;
        this.entrada = entrada;
        this.menuPeliculas = menuPeliculas;
        this.menuSeries = menuSeries;
        this.menuVideojuegos = menuVideojuegos;
        this.menuResenas = menuResenas;
        this.menuFavoritos = menuFavoritos;
    }

    public void mostrarMenuInicio() {
        while (true) {
            System.out.println("\n--- Temuflix ---");
            System.out.println("1. Iniciar sesión");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> iniciarSesion();
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
            System.out.println("5. Estadísticas de favoritos");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> menuPeliculas.mostrar();
                case "2" -> menuSeries.mostrar();
                case "3" -> menuVideojuegos.mostrar();
                case "4" -> menuResenas.mostrarAdmin();
                case "5" -> menuFavoritos.mostrarAdmin();
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
            System.out.println("1. Consultar videojuegos");
            System.out.println("2. Reseñas");
            System.out.println("3. Consultar películas");
            System.out.println("4. Consultar series");
            System.out.println("5. Favoritos");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "1" -> menuVideojuegos.mostrarUsuario();
                case "2" -> menuResenas.mostrarUsuario(usuario);
                case "3" -> menuPeliculas.mostrarUsuario();
                case "4" -> menuSeries.mostrarUsuario();
                case "5" -> menuFavoritos.mostrarUsuario(usuario);
                case "0" -> {
                    gestionCuenta.cerrarSesion();
                    return;
                }
                default -> System.out.println("Opción inválida");
            }
        }
    }
}
