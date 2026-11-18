package UI;

import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Favorito;
import Modelo.Pelicula;
import Modelo.Serie;
import Modelo.Usuario;
import Service.FavoritoService;
import Service.PeliculaService;
import Service.SerieService;
import Service.UsuarioService;
import Service.VideojuegoService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuFavoritos {

    private final FavoritoService favoritos;
    private final PeliculaService peliculas;
    private final SerieService series;
    private final VideojuegoService videojuegos;
    private final UsuarioService usuarios;
    private final Scanner entrada;

    public MenuFavoritos(FavoritoService favoritos, PeliculaService peliculas,
                         SerieService series, VideojuegoService videojuegos,
                         UsuarioService usuarios, Scanner entrada) {
        this.favoritos = favoritos;
        this.peliculas = peliculas;
        this.series = series;
        this.videojuegos = videojuegos;
        this.usuarios = usuarios;
        this.entrada = entrada;
    }

    public void mostrarAdmin() {
        while (true) {
            System.out.println("\n--- Estadísticas de favoritos ---");
            System.out.println("1. Cantidad de favoritos por tipo");
            System.out.println("2. Contenidos y cantidad de favoritos");
            System.out.println("3. Los 5 contenidos más favoritos");
            System.out.println("4. Cuenta con más favoritos");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> mostrarTotales();
                    case "2" -> mostrarPopularidad(false, false);
                    case "3" -> mostrarPopularidad(false, true);
                    case "4" -> mostrarCuentas();
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

    public void mostrarUsuario(Usuario usuario) {
        while (true) {
            System.out.println("\n--- Mis favoritos ---");
            System.out.println("1. Ver mis favoritos");
            System.out.println("2. Agregar un favorito");
            System.out.println("3. Quitar un favorito");
            System.out.println("4. Los 5 contenidos más populares");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listarPropios(usuario.getId());
                    case "2" -> agregar(usuario.getId());
                    case "3" -> quitar(usuario.getId());
                    case "4" -> mostrarPopularidad(true, true);
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

    private void mostrarTotales() {
        System.out.println("\nTipo | Favoritos");
        System.out.println("PELICULA | " + favoritos.obtenerEstadisticaPorTipo("PELICULA"));
        System.out.println("SERIE | " + favoritos.obtenerEstadisticaPorTipo("SERIE"));
        System.out.println("JUEGO | " + favoritos.obtenerEstadisticaPorTipo("JUEGO"));
    }

    private void mostrarPopularidad(boolean soloActivos, boolean topCinco) {
        String tipo = elegirTipo(true);
        Map<ContenidoMultimedia, Integer> cantidades = new LinkedHashMap<>();
        for (ContenidoMultimedia contenido : catalogo(tipo, soloActivos)) {
            int cantidad = favoritos.obtenerEstadisticaPorContenido(contenido.getId(), tipoDe(contenido));
            if (!topCinco || cantidad > 0) {
                cantidades.put(contenido, cantidad);
            }
        }

        ArrayList<Map.Entry<ContenidoMultimedia, Integer>> lista = new ArrayList<>(cantidades.entrySet());
        lista.sort(Comparator.<Map.Entry<ContenidoMultimedia, Integer>>comparingInt(Map.Entry::getValue)
                .reversed()
                .thenComparing(item -> tipoDe(item.getKey()))
                .thenComparingInt(item -> item.getKey().getId()));

        if (lista.isEmpty()) {
            System.out.println("No hay contenidos para mostrar en esta consulta.");
            return;
        }

        int limite = topCinco ? Math.min(5, lista.size()) : lista.size();
        System.out.println("\nTipo | ID | Nombre | Favoritos | Estado");
        for (int i = 0; i < limite; i++) {
            Map.Entry<ContenidoMultimedia, Integer> item = lista.get(i);
            ContenidoMultimedia contenido = item.getKey();
            System.out.printf("%s | %d | %s | %d | %s%n", tipoDe(contenido), contenido.getId(),
                    contenido.getNombre(), item.getValue(), contenido.getBaja() ? "De baja" : "Activo");
        }
    }

    private void mostrarCuentas() {
        Map<Integer, Integer> cantidades = favoritos.contarPorUsuario();
        int maximo = cantidades.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        if (maximo == 0) {
            System.out.println("Todavía no hay cuentas con favoritos.");
            return;
        }

        ArrayList<Integer> ids = new ArrayList<>(cantidades.keySet());
        ids.sort(Integer::compareTo);
        System.out.println("\nCuentas con más favoritos");
        for (int id : ids) {
            if (cantidades.get(id) == maximo) {
                Usuario usuario = usuarios.buscarPorId(id);
                String nombre = usuario == null ? "Cuenta no disponible" : usuario.getUsername();
                System.out.printf("ID %d | %s | %d favoritos%n", id, nombre, maximo);
            }
        }
    }

    private ArrayList<Favorito> listarPropios(int idUsuario) {
        ArrayList<Favorito> lista = new ArrayList<>(favoritos.leerPorUsuario(idUsuario));
        lista.sort(Comparator.comparing(Favorito::getTipo)
                .thenComparingInt(Favorito::getId_contenido)
                .thenComparingInt(Favorito::getId));

        ArrayList<Favorito> unicos = new ArrayList<>();
        HashSet<String> mostrados = new HashSet<>();
        for (Favorito favorito : lista) {
            if (mostrados.add(favorito.getTipo() + ":" + favorito.getId_contenido())) {
                unicos.add(favorito);
            }
        }

        if (unicos.isEmpty()) {
            System.out.println("Todavía no tenés favoritos.");
            return unicos;
        }

        System.out.println("\nOpción | Tipo | ID del contenido | Nombre | Estado");
        for (int i = 0; i < unicos.size(); i++) {
            Favorito favorito = unicos.get(i);
            ContenidoMultimedia contenido = buscarContenido(favorito.getTipo(), favorito.getId_contenido());
            String nombre = contenido == null ? "Contenido eliminado" : contenido.getNombre();
            String estado = contenido == null ? "No disponible" : contenido.getBaja() ? "De baja" : "Activo";
            System.out.printf("%d | %s | %d | %s | %s%n", i + 1, favorito.getTipo(),
                    favorito.getId_contenido(), nombre, estado);
        }
        return unicos;
    }

    private void agregar(int idUsuario) throws SQLException {
        String tipo = elegirTipo(false);
        ArrayList<ContenidoMultimedia> contenidos = catalogo(tipo, true);
        if (contenidos.isEmpty()) {
            System.out.println("No hay contenidos activos de este tipo.");
            return;
        }

        System.out.println("\nID | Nombre");
        for (ContenidoMultimedia contenido : contenidos) {
            System.out.println(contenido.getId() + " | " + contenido.getNombre());
        }

        int id = leerEntero("ID del contenido (0 para volver)");
        if (id == 0) {
            return;
        }
        ContenidoMultimedia contenido = buscarContenido(tipo, id);
        if (contenido == null || contenido.getBaja()) {
            throw new IllegalArgumentException("No existe un contenido activo con ese ID.");
        }
        if (favoritos.contiene(idUsuario, id, tipo)) {
            System.out.println("Ese contenido ya está en tus favoritos.");
            return;
        }

        if (favoritos.crear(new Favorito(0, idUsuario, id, tipo))) {
            System.out.println("Contenido agregado a tus favoritos.");
        } else {
            System.out.println("No se pudo agregar el favorito.");
        }
    }

    private void quitar(int idUsuario) throws SQLException {
        ArrayList<Favorito> lista = listarPropios(idUsuario);
        if (lista.isEmpty()) {
            return;
        }

        int opcion = leerEntero("Opción del favorito (0 para volver)");
        if (opcion == 0) {
            return;
        }
        if (opcion < 1 || opcion > lista.size()) {
            throw new IllegalArgumentException("Opción inválida");
        }

        Favorito favorito = lista.get(opcion - 1);
        favoritos.quitarDeUsuario(idUsuario, favorito.getId_contenido(), favorito.getTipo());
        System.out.println("Contenido quitado de tus favoritos.");
    }

    private ArrayList<ContenidoMultimedia> catalogo(String tipo, boolean soloActivos) {
        ArrayList<ContenidoMultimedia> contenidos = new ArrayList<>();
        if ("TODOS".equals(tipo) || "PELICULA".equals(tipo)) {
            contenidos.addAll(peliculas.leerTodos());
        }
        if ("TODOS".equals(tipo) || "SERIE".equals(tipo)) {
            contenidos.addAll(series.leerTodos());
        }
        if ("TODOS".equals(tipo) || "JUEGO".equals(tipo)) {
            contenidos.addAll(videojuegos.leerTodos());
        }
        contenidos.removeIf(contenido -> soloActivos && contenido.getBaja());
        return contenidos;
    }

    private ContenidoMultimedia buscarContenido(String tipo, int id) {
        return switch (tipo) {
            case "PELICULA" -> peliculas.buscarPorId(id);
            case "SERIE" -> series.buscarPorId(id);
            case "JUEGO" -> videojuegos.buscarPorId(id);
            default -> null;
        };
    }

    private String tipoDe(ContenidoMultimedia contenido) {
        if (contenido instanceof Pelicula) {
            return "PELICULA";
        }
        if (contenido instanceof Serie) {
            return "SERIE";
        }
        return "JUEGO";
    }

    private String elegirTipo(boolean incluirTodos) {
        System.out.println("\n1. Películas");
        System.out.println("2. Series");
        System.out.println("3. Videojuegos");
        if (incluirTodos) {
            System.out.println("4. Todos los tipos");
        }
        System.out.print("Tipo: ");

        return switch (entrada.nextLine().trim()) {
            case "1" -> "PELICULA";
            case "2" -> "SERIE";
            case "3" -> "JUEGO";
            case "4" -> {
                if (!incluirTodos) {
                    throw new IllegalArgumentException("Opción inválida");
                }
                yield "TODOS";
            }
            default -> throw new IllegalArgumentException("Opción inválida");
        };
    }

    private int leerEntero(String campo) {
        System.out.print(campo + ": ");
        try {
            return Integer.parseInt(entrada.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
    }
}

