package UI;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Modelo.Director;
import Modelo.Pelicula;
import Service.DirectorService;
import Service.PeliculaService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuPeliculas {

    private final PeliculaService peliculas;
    private final DirectorService directores;
    private final Scanner entrada;

    public MenuPeliculas(PeliculaService peliculas, DirectorService directores, Scanner entrada) {
        this.peliculas = peliculas;
        this.directores = directores;
        this.entrada = entrada;
    }

    public void mostrar() {
        while (true) {
            System.out.println("\n--- Películas ---");
            System.out.println("1. Listar");
            System.out.println("2. Consultar por ID");
            System.out.println("3. Agregar");
            System.out.println("4. Modificar");
            System.out.println("5. Dar de baja o reactivar");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar();
                    case "2" -> mostrarPelicula(pedirPelicula());
                    case "3" -> agregar();
                    case "4" -> modificar();
                    case "5" -> cambiarEstado();
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.err.println("No se pudo guardar el cambio. " + e.getMessage());
            }
        }
    }

    private void listar() {
        ArrayList<Pelicula> lista = peliculas.leerTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay películas cargadas.");
            return;
        }

        for (Pelicula pelicula : lista) {
            mostrarPelicula(pelicula);
        }
    }

    private void mostrarPelicula(Pelicula pelicula) {
        String estado = pelicula.getBaja() ? "De baja" : "Activa";
        System.out.println("ID " + pelicula.getId() + " | " + estado + " | " + pelicula);
    }

    private Pelicula pedirPelicula() {
        int id = leerEnteroPositivo("ID de la película");
        Pelicula pelicula = peliculas.buscarPorId(id);
        if (pelicula == null) {
            throw new IllegalArgumentException("No existe una película con ese ID.");
        }
        return pelicula;
    }

    private void agregar() throws SQLException {
        Pelicula pelicula = leerDatos(0, false);
        if (peliculas.agregar(pelicula)) {
            System.out.println("Película agregada con ID " + pelicula.getId() + ".");
        } else {
            System.out.println("No se pudo agregar la película.");
        }
    }

    private void modificar() throws SQLException {
        Pelicula actual = pedirPelicula();
        mostrarPelicula(actual);
        Pelicula modificada = leerDatos(actual.getId(), actual.getBaja());

        if (peliculas.modificar(modificada)) {
            System.out.println("Película modificada.");
        } else {
            System.out.println("No se pudo modificar la película.");
        }
    }

    private void cambiarEstado() throws SQLException {
        Pelicula pelicula = pedirPelicula();
        boolean estabaDeBaja = pelicula.getBaja();

        if (peliculas.darDeBaja(pelicula.getId())) {
            System.out.println(estabaDeBaja ? "Película reactivada." : "Película dada de baja.");
        } else {
            System.out.println("No se pudo cambiar el estado de la película.");
        }
    }

    private Pelicula leerDatos(int id, boolean baja) {
        ArrayList<Director> listaDirectores = directores.leerTodos();
        if (listaDirectores.isEmpty()) {
            throw new IllegalArgumentException("Primero cargá un director.");
        }

        String nombre = leerTexto("Nombre", 100);
        int duracion = leerEnteroPositivo("Duración en minutos");
        ClasificacionEdad clasificacion = elegir("Clasificación", ClasificacionEdad.values());
        GeneroAudiovisual genero = elegir("Género", GeneroAudiovisual.values());

        System.out.println("Directores disponibles");
        for (Director director : listaDirectores) {
            System.out.println(director.getId() + ". " + director.getNombre() + " " + director.getApellido());
        }

        int idDirector = leerEnteroPositivo("ID del director");
        if (directores.buscarPorId(idDirector) == null) {
            throw new IllegalArgumentException("El director indicado no existe.");
        }

        String fechaEstreno = leerTexto("Fecha de estreno", 10);
        return new Pelicula(baja, clasificacion, duracion, nombre, genero, idDirector, fechaEstreno, id);
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