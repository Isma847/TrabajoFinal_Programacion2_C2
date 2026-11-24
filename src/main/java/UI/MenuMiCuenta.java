package UI;

import Enumerados.Rol;
import Excepciones.DatoInvalidoException;
import Modelo.Favorito;
import Modelo.Resena;
import Modelo.Usuario;
import Service.FavoritoService;
import Service.GestionCuenta;
import Service.ResenaService;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import Service.ContenidoService;

public class MenuMiCuenta {

    private final GestionCuenta gestionCuenta;
    private final ResenaService resenas;
    private final FavoritoService favoritos;
    private final Scanner entrada;
    private final ContenidoService contenidos;

    public MenuMiCuenta(GestionCuenta gestionCuenta, ResenaService resenas,
                        FavoritoService favoritos, ContenidoService contenidos,
                        Scanner entrada) {
        this.gestionCuenta = gestionCuenta;
        this.resenas = resenas;
        this.favoritos = favoritos;
        this.contenidos = contenidos;
        this.entrada = entrada;
    }

    public void mostrar() {
        while (gestionCuenta.getUsuarioActual() != null) {
            if (gestionCuenta.getUsuarioActual().getRol() != Rol.USUARIO) {
                return;
            }

            System.out.println("\n--- Mi cuenta ---");
            System.out.println("1. Ver mi cuenta");
            System.out.println("2. Ver mis estadísticas");
            System.out.println("3. Modificar un dato personal");
            System.out.println("4. Darme de baja y cerrar sesión");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> mostrarCuenta();
                    case "2" -> mostrarEstadisticas();
                    case "3" -> modificar();
                    case "4" -> darDeBaja();
                    case "0" -> {
                        return;
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

    private void mostrarCuenta() {
        Usuario usuario = gestionCuenta.consultarMiCuenta();

        System.out.println("\n--- Datos de mi cuenta ---");
        System.out.println("ID: " + usuario.getId());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Apellido: " + usuario.getApellido());
        System.out.println("Nombre de usuario: " + usuario.getUsername());
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Edad: "
                + (usuario.getEdad() == null ? "Sin informar" : usuario.getEdad()));
        System.out.println("Rol: " + usuario.getRol());
        System.out.println("presione enter para continuar...");
        entrada.nextLine();
    }

    private void mostrarEstadisticas() {
        int idUsuario = gestionCuenta.consultarMiCuenta().getId();

        int favoritosPeliculas = 0;
        int favoritosSeries = 0;
        int favoritosJuegos = 0;

        HashSet<Integer> contenidosFavoritos = new HashSet<>();

        for (Favorito favorito : favoritos.leerPorUsuario(idUsuario)) {
            if (!contenidosFavoritos.add(favorito.getId_contenido())) {
                continue;
            }

            switch (contenidos.getTipo(favorito.getId_contenido()).name()) {
                case "PELICULA" -> favoritosPeliculas++;
                case "SERIE" -> favoritosSeries++;
                case "JUEGO" -> favoritosJuegos++;
            }
        }


        int totalResenas = 0;
        int resenasPeliculas = 0;
        int resenasSeries = 0;
        int resenasJuegos = 0;
        long sumaCalificaciones = 0;

        for (Resena resena : resenas.leerTodos()) {
            if (resena.getIdUsuario() != idUsuario) {
                continue;
            }

            totalResenas++;
            sumaCalificaciones += resena.getCalificacion();

            switch (contenidos.getTipo(resena.getIdContenido()).name()) {
                case "PELICULA" -> resenasPeliculas++;
                case "SERIE" -> resenasSeries++;
                case "JUEGO" -> resenasJuegos++;
            }
        }

        System.out.println("\n--- Mis estadísticas ---");
        System.out.println("Películas favoritas: " + favoritosPeliculas);
        System.out.println("Series favoritas: " + favoritosSeries);
        System.out.println("Videojuegos favoritos: " + favoritosJuegos);
        System.out.println("Total de favoritos: " + contenidosFavoritos.size());

        System.out.println("Reseñas de películas: " + resenasPeliculas);
        System.out.println("Reseñas de series: " + resenasSeries);
        System.out.println("Reseñas de videojuegos: " + resenasJuegos);
        System.out.println("Total de reseñas publicadas: " + totalResenas);

        if (totalResenas == 0) {
            System.out.println("Promedio de tus calificaciones: sin reseñas.");
        } else {
            double promedio = (double) sumaCalificaciones / totalResenas;
            System.out.printf(
                    "Promedio de tus calificaciones: %.2f%n", promedio
            );
        }
        System.out.println("presione enter para continuar...");
        entrada.nextLine();
    }

    private void modificar() throws SQLException {
        Usuario cambios = gestionCuenta.consultarMiCuenta();

        System.out.println("\n¿Qué dato querés modificar?");
        System.out.println("1. Nombre");
        System.out.println("2. Apellido");
        System.out.println("3. Nombre de usuario");
        System.out.println("4. Email");
        System.out.println("5. Contraseña");
        System.out.println("6. Edad");
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        switch (entrada.nextLine().trim()) {
            case "1" -> cambios.setNombre(leerTexto("Nuevo nombre").trim());
            case "2" -> cambios.setApellido(leerTexto("Nuevo apellido").trim());
            case "3" -> cambios.setUsername(
                    leerTexto("Nuevo nombre de usuario").trim()
            );
            case "4" -> cambios.setEmail(leerTexto("Nuevo email").trim());
            case "5" -> cambios.setPassword(leerTexto("Nueva contraseña"));
            case "6" -> cambios.setEdad(leerEntero("Nueva edad"));
            case "0" -> {
                return;
            }
            default -> throw new IllegalArgumentException("Opción inválida.");
        }

        if (gestionCuenta.modificarMiCuenta(cambios)) {
            System.out.println("Datos actualizados.");
        } else {
            System.out.println("No se pudieron actualizar los datos.");
        }
    }

    private void darDeBaja() throws SQLException {
        if (gestionCuenta.darDeBajaMiCuenta()) {
            System.out.println("Tu cuenta fue dada de baja. La sesión se cerró.");
        } else {
            System.out.println("No se pudo dar de baja la cuenta.");
        }
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