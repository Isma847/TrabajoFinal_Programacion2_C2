package UI;

import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Favorito;
import Modelo.Usuario;
import Service.ContenidoService;
import Service.FavoritoService;
import Service.UsuarioService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class MenuFavoritos {

    /// Un contenido con su id general y su tipo, que ya no se guarda en el favorito.
    private record Item(int idGeneral, TipoContenido tipo, ContenidoMultimedia contenido) {}

    private final FavoritoService favoritos;
    private final ContenidoService contenidos;
    private final UsuarioService usuarios;
    private final Scanner entrada;

    public MenuFavoritos(FavoritoService favoritos, ContenidoService contenidos,
                         UsuarioService usuarios, Scanner entrada) {
        this.favoritos = favoritos;
        this.contenidos = contenidos;
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
        // El favorito no guarda el tipo: se suman los favoritos de cada contenido de ese tipo.
        for (TipoContenido tipo : TipoContenido.values()) {
            int total = 0;
            for (Item item : catalogo(tipo, false)) {
                total += favoritos.obtenerEstadisticaPorContenido(item.idGeneral());
            }
            System.out.println(tipo + " | " + total);
        }
    }

    private void mostrarPopularidad(boolean soloActivos, boolean topCinco) {
        TipoContenido tipo = elegirTipo(true);
        ArrayList<Item> lista = new ArrayList<>();
        ArrayList<Integer> cantidades = new ArrayList<>();
        for (Item item : catalogo(tipo, soloActivos)) {
            int cantidad = favoritos.obtenerEstadisticaPorContenido(item.idGeneral());
            if (!topCinco || cantidad > 0) {
                lista.add(item);
                cantidades.add(cantidad);
            }
        }

        if (lista.isEmpty()) {
            System.out.println("No hay contenidos para mostrar en esta consulta.");
            return;
        }

        ArrayList<Integer> orden = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            orden.add(i);
        }
        orden.sort(Comparator.<Integer>comparingInt(cantidades::get).reversed()
                .thenComparing(i -> lista.get(i).tipo())
                .thenComparingInt(i -> lista.get(i).idGeneral()));

        int limite = topCinco ? Math.min(5, orden.size()) : orden.size();
        System.out.println("\nTipo | ID | Nombre | Favoritos | Estado");
        for (int i = 0; i < limite; i++) {
            Item item = lista.get(orden.get(i));
            System.out.printf("%s | %d | %s | %d | %s%n", item.tipo(), item.idGeneral(),
                    item.contenido().getNombre(), cantidades.get(orden.get(i)),
                    item.contenido().getBaja() ? "De baja" : "Activo");
        }
    }

    private void mostrarCuentas() {
        java.util.Map<Integer, Integer> cantidades = favoritos.contarPorUsuario();
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
        // El tipo se deriva del contenido: se ordena por tipo y luego por id general.
        lista.sort(Comparator.<Favorito, Integer>comparing(f -> tipoOrden(f.getId_contenido()))
                .thenComparingInt(Favorito::getId_contenido));

        if (lista.isEmpty()) {
            System.out.println("Todavía no tenés favoritos.");
            return lista;
        }

        System.out.println("\nOpción | Tipo | ID del contenido | Nombre | Estado");
        for (int i = 0; i < lista.size(); i++) {
            Favorito favorito = lista.get(i);
            ContenidoMultimedia contenido = contenidos.buscarPorId(favorito.getId_contenido());
            String tipo = contenido == null ? "-" : contenidos.getTipo(favorito.getId_contenido()).toString();
            String nombre = contenido == null ? "Contenido eliminado" : contenido.getNombre();
            String estado = contenido == null ? "No disponible" : contenido.getBaja() ? "De baja" : "Activo";
            System.out.printf("%d | %s | %d | %s | %s%n", i + 1, tipo,
                    favorito.getId_contenido(), nombre, estado);
        }
        return lista;
    }

    private void agregar(int idUsuario) throws SQLException {
        // El tipo sigue sirviendo para filtrar el listado; el favorito guarda solo el id general.
        TipoContenido tipo = elegirTipo(false);
        ArrayList<Item> items = catalogo(tipo, true);
        if (items.isEmpty()) {
            System.out.println("No hay contenidos activos de este tipo.");
            return;
        }

        System.out.println("\nID | Nombre");
        for (Item item : items) {
            System.out.println(item.idGeneral() + " | " + item.contenido().getNombre());
        }

        int id = leerEntero("ID del contenido (0 para volver)");
        if (id == 0) {
            return;
        }
        // El service de favoritos no conoce los contenidos: la existencia se valida acá.
        boolean enLista = items.stream().anyMatch(item -> item.idGeneral() == id);
        if (!enLista) {
            throw new IllegalArgumentException("No existe un contenido activo de ese tipo con ese ID.");
        }
        if (favoritos.contiene(idUsuario, id)) {
            System.out.println("Ese contenido ya está en tus favoritos.");
            return;
        }

        if (favoritos.crear(new Favorito(0, idUsuario, id))) {
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
        favoritos.quitarDeUsuario(idUsuario, favorito.getId_contenido());
        System.out.println("Contenido quitado de tus favoritos.");
    }

    /// Contenidos del registro general, filtrados por tipo (null = todos) y por baja.
    private ArrayList<Item> catalogo(TipoContenido tipo, boolean soloActivos) {
        ArrayList<Item> items = new ArrayList<>();
        for (ContenidoMultimedia contenido : contenidos.leerTodos()) {
            int idGeneral = contenidos.buscarIdContenido(contenido);
            if (idGeneral == -1) {
                continue;
            }
            TipoContenido tipoContenido = contenidos.getTipo(idGeneral);
            if ((tipo == null || tipoContenido == tipo) && !(soloActivos && contenido.getBaja())) {
                items.add(new Item(idGeneral, tipoContenido, contenido));
            }
        }
        items.sort(Comparator.comparingInt(Item::idGeneral));
        return items;
    }

    private int tipoOrden(int idGeneral) {
        return contenidos.buscarPorId(idGeneral) == null
                ? Integer.MAX_VALUE : contenidos.getTipo(idGeneral).ordinal();
    }

    /// Devuelve null cuando se eligen todos los tipos.
    private TipoContenido elegirTipo(boolean incluirTodos) {
        System.out.println("\n1. Películas");
        System.out.println("2. Series");
        System.out.println("3. Videojuegos");
        if (incluirTodos) {
            System.out.println("4. Todos los tipos");
        }
        System.out.print("Tipo: ");

        return switch (entrada.nextLine().trim()) {
            case "1" -> TipoContenido.PELICULA;
            case "2" -> TipoContenido.SERIE;
            case "3" -> TipoContenido.JUEGO;
            case "4" -> {
                if (!incluirTodos) {
                    throw new IllegalArgumentException("Opción inválida");
                }
                yield null;
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