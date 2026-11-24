package UI;

import Enumerados.Rol;
import Excepciones.DatoInvalidoException;
import Modelo.Director;
import Modelo.Usuario;
import Service.DirectorService;
import Service.PeliculaService;
import Service.SerieService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import Modelo.Pelicula;

public class MenuDirectores {

    private final DirectorService directores;
    private final PeliculaService peliculas;
    private final SerieService series;
    private final Scanner entrada;

    public MenuDirectores(DirectorService directores, PeliculaService peliculas,
                          SerieService series, Scanner entrada) {
        this.directores = directores;
        this.peliculas = peliculas;
        this.series = series;
        this.entrada = entrada;
    }

    public void mostrar(Usuario administrador) {
        if (administrador == null || administrador.getRol() != Rol.ADMINISTRADOR) {
            throw new DatoInvalidoException("Se requiere una cuenta administradora.");
        }

        while (true) {
            System.out.println("\n--- Gestionar directores ---");
            System.out.println("1. Listar");
            System.out.println("2. Consultar por ID");
            System.out.println("3. Agregar");
            System.out.println("4. Modificar un campo");
            System.out.println("5. Eliminar");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar();
                    case "2" -> mostrarDirector(pedirDirector());
                    case "3" -> agregar();
                    case "4" -> modificar();
                    case "5" -> {
                        if (!administrador.isPuedeEliminar()) {
                            throw new DatoInvalidoException(
                                    "Tu cuenta requiere permiso de eliminación."
                            );
                        }
                        eliminar();
                    }
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException | DatoInvalidoException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.err.println("No se pudo guardar el cambio. " + e.getMessage());
            }
        }
    }

    private void listar() {
        ArrayList<Director> lista = directores.leerTodos();

        if (lista.isEmpty()) {
            System.out.println("No hay directores cargados.");
            return;
        }

        System.out.println("\nID | Nombre | Apellido");
        for (Director director : lista) {
            System.out.printf("%d | %s | %s%n",
                    director.getId(), director.getNombre(), director.getApellido());
        }
    }

    private void mostrarDirector(Director director) {
        String descripcion = director.getDescripcion();
        if (descripcion == null || descripcion.isBlank()) {
            descripcion = "Sin descripción";
        }

        System.out.println("\n--- Director " + director.getId() + " ---");
        System.out.println("Nombre: " + director.getNombre());
        System.out.println("Apellido: " + director.getApellido());
        System.out.println("Descripción: " + descripcion);
    }

    private Director pedirDirector() {
        int id = leerEntero("ID del director");
        Director director = directores.buscarPorId(id);

        if (director == null) {
            throw new IllegalArgumentException("No existe un director con ese ID.");
        }

        return director;
    }

    private void agregar() throws SQLException {
        String nombre = leerTexto("Nombre", 80);
        String apellido = leerTexto("Apellido", 80);
        String descripcion = leerDescripcion();

        Director director = new Director(apellido, nombre, descripcion);

        if (directores.agregar(director)) {
            System.out.println("Director agregado con ID " + director.getId() + ".");
        } else {
            System.out.println("No se pudo agregar el director.");
        }
    }

    private void modificar() throws SQLException {
        Director actual = pedirDirector();
        mostrarDirector(actual);

        System.out.println("\n¿Qué querés modificar?");
        System.out.println("1. Nombre");
        System.out.println("2. Apellido");
        System.out.println("3. Descripción");
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        String nombre = actual.getNombre();
        String apellido = actual.getApellido();
        String descripcion = actual.getDescripcion();

        switch (entrada.nextLine().trim()) {
            case "1" -> nombre = leerTexto("Nuevo nombre", 80);
            case "2" -> apellido = leerTexto("Nuevo apellido", 80);
            case "3" -> descripcion = leerDescripcion();
            case "0" -> {
                return;
            }
            default -> throw new IllegalArgumentException("Opción inválida.");
        }

        Director modificado = new Director(apellido, nombre, descripcion);
        modificado.setId(actual.getId());

        if (directores.modificar(modificado)) {
            System.out.println("Director modificado.");
        } else {
            System.out.println("No se pudo modificar el director.");
        }
    }

    private void eliminar() throws SQLException {
        Director director = pedirDirector();
        mostrarDirector(director);

        if (directores.eliminar(director.getId())) {
            peliculas.quitarDirector(director.getId());
            series.quitarDirector(director.getId());

            System.out.println("Director eliminado.");
            System.out.println("Los contenidos asociados quedaron sin director.");
        } else {
            System.out.println("No se pudo eliminar el director.");
        }
    }

    private String leerDescripcion() {
        System.out.print("Descripción (Enter para dejarla vacía): ");
        String descripcion = entrada.nextLine().trim();
        return descripcion.isEmpty() ? null : descripcion;
    }

    private String leerTexto(String campo, int maximo) {
        System.out.print(campo + ": ");
        String texto = entrada.nextLine().trim();

        if (texto.isEmpty() || texto.codePointCount(0, texto.length()) > maximo) {
            throw new IllegalArgumentException(
                    campo + " debe tener entre 1 y " + maximo + " caracteres."
            );
        }

        return texto;
    }


    private int leerEntero(String campo) {
        System.out.print(campo + ": ");

        try {
            return Integer.parseInt(entrada.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
    }

    //Interfaz Usuario
    public void mostrarUsuario() {
        while (true) {
            System.out.println("\n--- Consultar directores ---");
            System.out.println("1. Listar directores");
            System.out.println("2. Consultar director y películas por ID");
            System.out.println("3. Buscar por nombre o apellido");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar();
                    case "2" -> mostrarDirectorConPeliculas(pedirDirector());
                    case "3" -> buscarDirectoresUsuario();
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException | DatoInvalidoException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void buscarDirectoresUsuario() {
        String texto = leerTexto("Nombre o apellido", 80);
        ArrayList<Director> resultado = directores.buscarPorNombre(texto);

        if (resultado.isEmpty()) {
            System.out.println("No se encontraron directores.");
            return;
        }

        for (Director director : resultado) {
            mostrarDirectorConPeliculas(director);
        }
    }

    private void mostrarDirectorConPeliculas(Director director) {
        mostrarDirector(director);

        ArrayList<Pelicula> resultado =
                peliculas.filtrarPorDirector(director.getId(), false);

        if (resultado.isEmpty()) {
            System.out.println("Este director no tiene películas activas cargadas.");
            return;
        }

        System.out.println("\nID | Película | Duración | Género");

        for (Pelicula pelicula : resultado) {
            System.out.printf("%d | %s | %d min | %s%n",
                    pelicula.getId(),
                    pelicula.getNombre(),
                    pelicula.getDuracionMinutos(),
                    pelicula.getGenero());
        }
    }

}