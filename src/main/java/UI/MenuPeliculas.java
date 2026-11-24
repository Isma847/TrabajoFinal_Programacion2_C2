package UI;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Excepciones.DatoInvalidoException;
import Modelo.Director;
import Modelo.Pelicula;
import Service.DirectorService;
import Service.PeliculaService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Service.ContenidoService;

public class MenuPeliculas {

    private final PeliculaService peliculas;
    private final DirectorService directores;
    private final Scanner entrada;
    private final ContenidoService contenidos;

    public MenuPeliculas(ContenidoService contenidos,
                         DirectorService directores, Scanner entrada) {
        this.contenidos = contenidos;
        this.peliculas = contenidos.getPeliculas();
        this.directores = directores;
        this.entrada = entrada;
    }

    public void mostrar() {
        while (true) {
            System.out.println("\n--- Películas ---");
            System.out.println("1. Listar");
            System.out.println("2. Consultar por ID");
            System.out.println("3. Agregar");
            System.out.println("4. Modificar un campo");
            System.out.println("5. Dar de baja o reactivar");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar(peliculas.leerTodos());
                    case "2" -> mostrarPelicula(pedirPelicula(false));
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
            System.out.println("\n--- Catálogo de películas ---");
            System.out.println("1. Listar películas activas");
            System.out.println("2. Consultar por ID");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar(peliculas.getLista(false));
                    case "2" -> mostrarPelicula(pedirPelicula(true));
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

    private void listar(List<Pelicula> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay películas cargadas.");
            return;
        }

        System.out.println("ID | Nombre | Duración | Estado | Descripción");
        for (Pelicula pelicula : lista) {
            System.out.printf("%d | %s | %d min | %s | %s%n",
                    pelicula.getId(), pelicula.getNombre(), pelicula.getDuracionMinutos(), estado(pelicula), descripcionVisible(pelicula.getDescripcion()));
        }
    }

    private void mostrarPelicula(Pelicula pelicula) {
        System.out.println("\n--- Película " + pelicula.getId() + " ---");
        System.out.println("Nombre: " + pelicula.getNombre());
        System.out.println("Duración: " + pelicula.getDuracionMinutos() + " min");
        System.out.println("Clasificación: " + pelicula.getClasificacionEdad());
        System.out.println("Género: " + pelicula.getGenero());
        System.out.println("Director: " + nombreDirector(pelicula.getIdDirector()));
        System.out.println("Fecha de estreno: " + pelicula.getFechaEstreno());
        System.out.println("Descripción: " + descripcionVisible(pelicula.getDescripcion()));
        System.out.println("Estado: " + estado(pelicula));
    }

    private String descripcionVisible(String descripcion) {
        return descripcion == null || descripcion.isBlank() ? "Sin descripción" : descripcion;
    }

    private String estado(Pelicula pelicula) {
        return pelicula.getBaja() ? "De baja" : "Activa";
    }

    private String nombreDirector(int id) {
        if (id <= 0) {
            return "Sin director";
        }
        Director director = directores.buscarPorId(id);
        if (director == null) {
            return "ID " + id;
        }
        return director.getNombre() + " " + director.getApellido() + " (ID " + id + ")";
    }

    private Pelicula pedirPelicula(boolean soloActivas) {
        int id = leerEnteroPositivo("ID de la película");
        Pelicula pelicula = peliculas.buscarPorId(id);
        if (pelicula == null || (soloActivas && pelicula.getBaja())) {
            throw new IllegalArgumentException("No existe una película con ese ID.");
        }
        return pelicula;
    }

    private void agregar() throws SQLException {
        String nombre = leerTexto("Nombre", 100);
        int duracion = leerEnteroPositivo("Duración en minutos");
        ClasificacionEdad clasificacion = elegir("Clasificación", ClasificacionEdad.values());
        GeneroAudiovisual genero = elegir("Género", GeneroAudiovisual.values());
        String descripcion =  leerTexto("Descripcion", 600);
        int idDirector = leerDirector();
        String fechaEstreno = leerTexto("Fecha de estreno", 10);

        Pelicula pelicula = new Pelicula(false, clasificacion, duracion, nombre, genero,
                idDirector, fechaEstreno, 0, descripcion);
        if (contenidos.agregar(pelicula)) {
            System.out.println("Película agregada con ID " + pelicula.getId() + ".");
        } else {
            System.out.println("No se pudo agregar la película.");
        }
    }

    private void modificar() throws SQLException {
        Pelicula actual = pedirPelicula(false);
        mostrarPelicula(actual);

        System.out.println("\n¿Qué querés modificar?");
        System.out.println("1. Nombre");
        System.out.println("2. Duración");
        System.out.println("3. Clasificación");
        System.out.println("4. Género");
        System.out.println("5. Director");
        System.out.println("6. Fecha de estreno");
        System.out.println("7. Descripción");
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        Pelicula modificada = copiarPelicula(actual);
        switch (entrada.nextLine().trim()) {
            case "1" -> modificada.setNombre(leerTexto("Nuevo nombre", 100));
            case "2" -> modificada.setDuracionMinutos(leerEnteroPositivo("Nueva duración en minutos"));
            case "3" -> modificada.setClasificacionEdad(elegir("Nueva clasificación", ClasificacionEdad.values()));
            case "4" -> modificada.setGenero(elegir("Nuevo género", GeneroAudiovisual.values()));
            case "5" -> modificada.setIdDirector(leerDirector());
            case "6" -> modificada.setFechaEstreno(leerTexto("Nueva fecha de estreno", 10));
            case "7" -> modificada.setDescripcion(leerTexto("Nueva descripcion", 600));
            case "0" -> {
                return;
            }
            default -> {
                System.out.println("Opción inválida");
                return;
            }
        }

        if (contenidos.modificar(modificada)) {
            System.out.println("Película modificada.");
        } else {
            System.out.println("No se pudo modificar la película.");
        }
    }

    private Pelicula copiarPelicula(Pelicula pelicula) {
        return new Pelicula(
                pelicula.getBaja(),
                pelicula.getClasificacionEdad(),
                pelicula.getDuracionMinutos(),
                pelicula.getNombre(),
                pelicula.getGenero(),
                pelicula.getIdDirector(),
                pelicula.getFechaEstreno(),
                pelicula.getId(),
                pelicula.getDescripcion()
        );
    }

    private void cambiarEstado() throws SQLException {
        Pelicula pelicula = pedirPelicula(false);
        boolean estabaDeBaja = pelicula.getBaja();

        if (contenidos.darDeBaja(pelicula)) {
            System.out.println(estabaDeBaja ? "Película reactivada." : "Película dada de baja.");
        } else {
            System.out.println("No se pudo cambiar el estado de la película.");
        }
    }

    private int leerDirector() {
        ArrayList<Director> listaDirectores = directores.leerTodos();
        if (listaDirectores.isEmpty()) {
            throw new IllegalArgumentException("Primero cargá un director.");
        }

        System.out.println("Directores disponibles");
        for (Director director : listaDirectores) {
            System.out.println(director.getId() + ". " + director.getNombre() + " " + director.getApellido());
        }

        int idDirector = leerEnteroPositivo("ID del director");
        if (directores.buscarPorId(idDirector) == null) {
            throw new IllegalArgumentException("El director indicado no existe.");
        }
        return idDirector;
    }

    private String leerTexto(String campo, int maximo) {
        System.out.print(campo + ": ");
        String texto = entrada.nextLine().trim();
        if (texto.isEmpty() || texto.codePointCount(0, texto.length()) > maximo) {
            throw new IllegalArgumentException(campo + " debe tener entre 1 y " + maximo + " caracteres.");
        }
        return texto;
    }

    private int leerEnteroPositivo(String campo) {
        System.out.print(campo + ": ");
        int numero;
        try {
            numero = Integer.parseInt(entrada.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
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
