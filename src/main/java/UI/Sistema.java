package UI;

import Excepciones.SesionInvalidaException;
import Modelo.Administrador;
import Service.GestionCuenta;

import java.util.Scanner;

public class Sistema {

    private final GestionCuenta gestionCuenta;
    private final Scanner entrada;
    private final MenuSeries menuSeries;
    private final MenuPeliculas menuPeliculas;



    public Sistema(GestionCuenta gestionCuenta, Scanner entrada, MenuSeries menuSeries, MenuPeliculas menuPeliculas) {
        this.gestionCuenta = gestionCuenta;
        this.entrada = entrada;
        this.menuSeries = menuSeries;
        this.menuPeliculas = menuPeliculas;
    }


    public void mostrarMenuAdmin() {
        while (true) {
            System.out.println("\n--- Menú administrador ---");
            System.out.println("1. Gestionar películas");
            System.out.println("2. Gestionar series");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            switch (entrada.nextLine().trim()) {
                case "2" -> menuSeries.mostrar();
                case "1" -> menuPeliculas.mostrar();
                case "0" -> {
                    gestionCuenta.cerrarSesion();
                    return;
                }
                default -> System.out.println("Opción inválida");
            }
        }
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


    public void mostrarMenuUsuario() {
        mostrarMenuSesion("Menú usuario");
    }

    private void mostrarMenuSesion(String titulo) {
        while (true) {
            System.out.println("\n--- " + titulo + " ---");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            if ("0".equals(entrada.nextLine().trim())) {
                gestionCuenta.cerrarSesion();
                return;
            }

            System.out.println("Opción inválida");
        }
    }


    public Sistema(GestionCuenta gestionCuenta, Scanner entrada,
                   MenuPeliculas menuPeliculas, MenuSeries menuSeries) {
        this.gestionCuenta = gestionCuenta;
        this.entrada = entrada;
        this.menuPeliculas = menuPeliculas;
        this.menuSeries = menuSeries;
    }

}