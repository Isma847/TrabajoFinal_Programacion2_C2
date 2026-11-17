package UI;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroVideojuegos;
import Excepciones.DatoInvalidoException;
import Modelo.Videojuego;
import Service.VideojuegoService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MenuVideojuegos {

    private final VideojuegoService videojuegos;
    private final Scanner entrada;

    public MenuVideojuegos(VideojuegoService videojuegos, Scanner entrada) {
        this.videojuegos = videojuegos;
        this.entrada = entrada;
    }

    public void mostrar() {
        while (true) {
            System.out.println("\n--- Gestionar videojuegos ---");
            System.out.println("1. Listar");
            System.out.println("2. Consultar por ID");
            System.out.println("3. Agregar");
            System.out.println("4. Modificar un campo");
            System.out.println("5. Dar de baja o reactivar");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar(videojuegos.leerTodos());
                    case "2" -> mostrarVideojuego(pedirVideojuego(false));
                    case "3" -> agregar();
                    case "4" -> modificar();
                    case "5" -> cambiarEstado();
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida");
                }
            } catch (IllegalArgumentException | DatoInvalidoException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.err.println("No se pudo guardar el cambio. " + e.getMessage());
            }
        }
    }

    public void mostrarUsuario() {
        while (true) {
            System.out.println("\n--- Catálogo de videojuegos ---");
            System.out.println("1. Listar videojuegos activos");
            System.out.println("2. Consultar por ID");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar(videojuegos.getLista(false));
                    case "2" -> mostrarVideojuego(pedirVideojuego(true));
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void listar(List<Videojuego> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay videojuegos para mostrar.");
            return;
        }

        System.out.println("ID | Nombre | Género | Dificultad | Estado");
        for (Videojuego videojuego : lista) {
            System.out.printf("%d | %s | %s | %d | %s%n", videojuego.getId(),
                    videojuego.getNombre(), videojuego.getGenero(), videojuego.getDificultad(),
                    estado(videojuego));
        }
    }

    private void mostrarVideojuego(Videojuego videojuego) {
        String descripcion = videojuego.getDescripcion();
        if (descripcion == null || descripcion.isBlank()) {
            descripcion = "Sin descripción";
        }

        System.out.println("\n--- Videojuego " + videojuego.getId() + " ---");
        System.out.println("Nombre: " + videojuego.getNombre());
        System.out.println("Duración: " + videojuego.getDuracionMinutos() + " min");
        System.out.println("Clasificación: " + videojuego.getClasificacionEdad());
        System.out.println("Género: " + videojuego.getGenero());
        System.out.println("Dificultad: " + videojuego.getDificultad());
        System.out.println("Descripción: " + descripcion);
        System.out.println("Estado: " + estado(videojuego));
    }

    private String estado(Videojuego videojuego) {
        return videojuego.getBaja() ? "De baja" : "Activo";
    }

    private Videojuego pedirVideojuego(boolean soloActivos) {
        int id = leerEnteroPositivo("ID del videojuego");
        Videojuego videojuego = videojuegos.buscarPorId(id);
        if (videojuego == null || (soloActivos && videojuego.getBaja())) {
            throw new IllegalArgumentException("No se encontró un videojuego con ese ID.");
        }
        return videojuego;
    }

    private void agregar() throws SQLException {
        String nombre = leerNombre("Nombre");
        int duracion = leerEnteroPositivo("Duración en minutos");
        ClasificacionEdad clasificacion = elegir("Clasificación", ClasificacionEdad.values());
        GeneroVideojuegos genero = elegir("Género", GeneroVideojuegos.values());
        int dificultad = leerEntero("Dificultad");
        String descripcion = leerDescripcion();

        Videojuego videojuego = new Videojuego(false, clasificacion, duracion, nombre,
                0, dificultad, genero, descripcion);
        if (videojuegos.agregar(videojuego)) {
            System.out.println("Videojuego agregado con ID " + videojuego.getId() + ".");
        } else {
            System.out.println("No se pudo agregar el videojuego.");
        }
    }

    private void modificar() throws SQLException {
        Videojuego actual = pedirVideojuego(false);
        mostrarVideojuego(actual);

        System.out.println("\n¿Qué querés modificar?");
        System.out.println("1. Nombre");
        System.out.println("2. Duración");
        System.out.println("3. Clasificación");
        System.out.println("4. Género");
        System.out.println("5. Dificultad");
        System.out.println("6. Descripción");
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        Videojuego modificado = copiarVideojuego(actual);
        switch (entrada.nextLine().trim()) {
            case "1" -> modificado.setNombre(leerNombre("Nuevo nombre"));
            case "2" -> modificado.setDuracionMinutos(leerEnteroPositivo("Nueva duración en minutos"));
            case "3" -> modificado.setClasificacionEdad(elegir("Nueva clasificación", ClasificacionEdad.values()));
            case "4" -> modificado.setGenero(elegir("Nuevo género", GeneroVideojuegos.values()));
            case "5" -> modificado.setDificultad(leerEntero("Nueva dificultad"));
            case "6" -> modificado.setDescripcion(leerDescripcion());
            case "0" -> {
                return;
            }
            default -> {
                System.out.println("Opción inválida");
                return;
            }
        }

        if (videojuegos.modificar(modificado)) {
            System.out.println("Videojuego modificado.");
        } else {
            System.out.println("No se pudo modificar el videojuego.");
        }
    }

    private Videojuego copiarVideojuego(Videojuego videojuego) {
        return new Videojuego(
                videojuego.getBaja(),
                videojuego.getClasificacionEdad(),
                videojuego.getDuracionMinutos(),
                videojuego.getNombre(),
                videojuego.getId(),
                videojuego.getDificultad(),
                videojuego.getGenero(),
                videojuego.getDescripcion()
        );
    }

    private void cambiarEstado() throws SQLException {
        Videojuego videojuego = pedirVideojuego(false);
        boolean estabaDeBaja = videojuego.getBaja();

        if (videojuegos.darDeBaja(videojuego.getId())) {
            System.out.println(estabaDeBaja ? "Videojuego reactivado." : "Videojuego dado de baja.");
        } else {
            System.out.println("No se pudo cambiar el estado del videojuego.");
        }
    }

    private String leerNombre(String campo) {
        System.out.print(campo + ": ");
        String nombre = entrada.nextLine().trim();
        if (nombre.isEmpty() || nombre.codePointCount(0, nombre.length()) > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 1 y 100 caracteres.");
        }
        return nombre;
    }

    private String leerDescripcion() {
        System.out.print("Descripción (Enter para dejarla vacía): ");
        String descripcion = entrada.nextLine().trim();
        if (descripcion.codePointCount(0, descripcion.length()) > 600) {
            throw new IllegalArgumentException("La descripción admite hasta 600 caracteres.");
        }
        return descripcion.isEmpty() ? null : descripcion;
    }

    private int leerEntero(String campo) {
        System.out.print(campo + ": ");
        try {
            return Integer.parseInt(entrada.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
    }

    private int leerEnteroPositivo(String campo) {
        int numero = leerEntero(campo);
        if (numero <= 0) {
            throw new IllegalArgumentException(campo + " debe ser mayor que cero.");
        }
        return numero;
    }

    private <T> T elegir(String campo, T[] opciones) {
        System.out.println(campo);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ". " + opciones[i]);
        }

        int opcion = leerEnteroPositivo("Opción");
        if (opcion > opciones.length) {
            throw new IllegalArgumentException("Opción inválida para " + campo + ".");
        }
        return opciones[opcion - 1];
    }
}
