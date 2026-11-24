package UI;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Excepciones.DatoInvalidoException;
import Modelo.Director;
import Modelo.Serie;
import Service.ContenidoService;
import Service.DirectorService;
import Service.SerieService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuSeries {

    private final SerieService series;
    private final DirectorService directores;
    private final Scanner entrada;
    private final ContenidoService contenidos;

    public MenuSeries(ContenidoService contenidos, DirectorService directores, Scanner entrada) {
        this.contenidos = contenidos;
        this.series = contenidos.getSeries();
        this.directores = directores;
        this.entrada = entrada;
    }

    public void mostrar() {
        while (true) {
            System.out.println("\n--- Series ---");
            System.out.println("1. Listar");
            System.out.println("2. Consultar por ID");
            System.out.println("3. Agregar");
            System.out.println("4. Modificar un campo");
            System.out.println("5. Dar de baja o reactivar");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar(series.leerTodos());
                    case "2" -> mostrarSerie(pedirSerie(false));
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
            System.out.println("\n--- Catálogo de series ---");
            System.out.println("1. Listar series activas");
            System.out.println("2. Consultar por ID");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar(series.getLista(false));
                    case "2" -> mostrarSerie(pedirSerie(true));
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

    private void listar(List<Serie> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay series cargadas.");
            return;
        }

        System.out.println("ID | Nombre | Temporadas | Capítulos | Estado | Descripción");
        for (Serie serie : lista) {
            System.out.printf("%d | %s | %d | %d | %s | %s%n",
                    serie.getId(), serie.getNombre(), serie.getCantidadTemporadas(),
                    serie.getCantidadCapitulos(), estado(serie), descripcionVisible(serie.getDescripcion()));
        }
        System.out.println("presione enter para continuar...");
        entrada.nextLine();
    }

    private void mostrarSerie(Serie serie) {
        System.out.println("\n--- Serie " + serie.getId() + " ---");
        System.out.println("Nombre: " + serie.getNombre());
        System.out.println("Duración: " + serie.getDuracionMinutos() + " min");
        System.out.println("Clasificación: " + serie.getClasificacionEdad());
        System.out.println("Género: " + serie.getGenero());
        System.out.println("Director: " + nombreDirector(serie.getIdDirector()));
        System.out.println("Capítulos: " + serie.getCantidadCapitulos());
        System.out.println("Temporadas: " + serie.getCantidadTemporadas());
        System.out.println("Descripción: " + descripcionVisible(serie.getDescripcion()));
        System.out.println("Estado: " + estado(serie));
        System.out.println("presione enter para continuar...");
        entrada.nextLine();
    }

    private String descripcionVisible(String descripcion) {
        return descripcion == null || descripcion.isBlank() ? "Sin descripción" : descripcion;
    }

    private String estado(Serie serie) {
        return serie.getBaja() ? "De baja" : "Activa";
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

    private Serie pedirSerie(boolean soloActivas) {
        int id = leerEnteroPositivo("ID de la serie");
        Serie serie = series.buscarPorId(id);
        if (serie == null || (soloActivas && serie.getBaja())) {
            throw new IllegalArgumentException("No existe una serie con ese ID.");
        }
        return serie;
    }

    private void agregar() throws SQLException {
        String nombre = leerTexto("Nombre", 100);
        int duracion = leerEnteroPositivo("Duración en minutos");
        ClasificacionEdad clasificacion = elegir("Clasificación", ClasificacionEdad.values());
        GeneroAudiovisual genero = elegir("Género", GeneroAudiovisual.values());
        int idDirector = leerDirector();
        int capitulos = leerEnteroPositivo("Cantidad de capítulos");
        int temporadas = leerEnteroPositivo("Cantidad de temporadas");
        String descripcion = leerTexto("Descripción", 600);

        Serie serie = new Serie(false, clasificacion, duracion, nombre, genero,
                idDirector, 0, capitulos, temporadas, descripcion);
        if (contenidos.agregar(serie)) {
            System.out.println("Serie agregada con ID " + serie.getId() + ".");
        } else {
            System.out.println("No se pudo agregar la serie.");
        }
    }

    private void modificar() throws SQLException {
        Serie actual = pedirSerie(false);
        mostrarSerie(actual);

        System.out.println("\n¿Qué querés modificar?");
        System.out.println("1. Nombre");
        System.out.println("2. Duración");
        System.out.println("3. Clasificación");
        System.out.println("4. Género");
        System.out.println("5. Director");
        System.out.println("6. Cantidad de capítulos");
        System.out.println("7. Cantidad de temporadas");
        System.out.println("8. Descripción");
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        Serie modificada = copiarSerie(actual);
        switch (entrada.nextLine().trim()) {
            case "1" -> modificada.setNombre(leerTexto("Nuevo nombre", 100));
            case "2" -> modificada.setDuracionMinutos(leerEnteroPositivo("Nueva duración en minutos"));
            case "3" -> modificada.setClasificacionEdad(elegir("Nueva clasificación", ClasificacionEdad.values()));
            case "4" -> modificada.setGenero(elegir("Nuevo género", GeneroAudiovisual.values()));
            case "5" -> modificada.setIdDirector(leerDirector());
            case "6" -> modificada.setCantidadCapitulos(leerEnteroPositivo("Nueva cantidad de capítulos"));
            case "7" -> modificada.setCantidadTemporadas(leerEnteroPositivo("Nueva cantidad de temporadas"));
            case "8" -> modificada.setDescripcion(leerTexto("Descripción", 600));
            case "0" -> {
                return;
            }
            default -> {
                System.out.println("Opción inválida");
                return;
            }
        }

        if (contenidos.modificar(modificada)) {
            System.out.println("Serie modificada.");
        } else {
            System.out.println("No se pudo modificar la serie.");
        }
    }

    private Serie copiarSerie(Serie serie) {
        return new Serie(
                serie.getBaja(),
                serie.getClasificacionEdad(),
                serie.getDuracionMinutos(),
                serie.getNombre(),
                serie.getGenero(),
                serie.getIdDirector(),
                serie.getId(),
                serie.getCantidadCapitulos(),
                serie.getCantidadTemporadas(),
                serie.getDescripcion()
        );
    }

    private void cambiarEstado() throws SQLException {
        Serie serie = pedirSerie(false);
        boolean estabaDeBaja = serie.getBaja();

        if (contenidos.darDeBaja(serie)) {
            System.out.println(estabaDeBaja ? "Serie reactivada." : "Serie dada de baja.");
        } else {
            System.out.println("No se pudo cambiar el estado de la serie.");
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
