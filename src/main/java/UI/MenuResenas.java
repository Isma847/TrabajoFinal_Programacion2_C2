package UI;

import Enumerados.Rol;
import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Resena;
import Modelo.Usuario;
import Service.ContenidoService;
import Service.ResenaService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class MenuResenas {

    /// Un contenido con su id general (el que guardan las reseñas).
    private record Item(int idGeneral, ContenidoMultimedia contenido) {}

    private final ResenaService resenas;
    private final ContenidoService contenidos;
    private final Scanner entrada;

    public MenuResenas(ResenaService resenas, ContenidoService contenidos, Scanner entrada) {
        this.resenas = resenas;
        this.contenidos = contenidos;
        this.entrada = entrada;
    }

    public void mostrarAdmin(Usuario administrador) {
        if (administrador == null || administrador.getRol() != Rol.ADMINISTRADOR) {
            throw new DatoInvalidoException("Se requiere una cuenta administradora");
        }

        while (true) {
            System.out.println("\n--- Gestionar reseñas ---");
            System.out.println("1. Listar todas");
            System.out.println("2. Consultar por ID");
            System.out.println("3. Eliminar reseña");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar(resenas.leerTodos());
                    case "2" -> mostrarResena(pedirResena());
                    case "3" -> {
                        if (!administrador.isPuedeEliminar()) {
                            throw new DatoInvalidoException("Tu cuenta requiere permiso de eliminación");
                        }
                        eliminar();
                    }
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida");
                }
            } catch (IllegalArgumentException | DatoInvalidoException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.err.println("No se pudo eliminar la reseña. " + e.getMessage());
            }
        }
    }

    public void mostrarUsuario(Usuario usuario) {
        while (true) {
            System.out.println("\n--- Reseñas ---");
            System.out.println("1. Películas");
            System.out.println("2. Series");
            System.out.println("3. Videojuegos");
            System.out.println("4. Ver mis reseñas");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> elegirContenido(usuario, TipoContenido.PELICULA);
                    case "2" -> elegirContenido(usuario, TipoContenido.SERIE);
                    case "3" -> elegirContenido(usuario, TipoContenido.JUEGO);
                    case "4" -> listar(resenas.leerPorUsuario(usuario.getId()));
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida");
                }
            } catch (IllegalArgumentException | DatoInvalidoException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void elegirContenido(Usuario usuario, TipoContenido tipo) {
        ArrayList<Item> items = activosDeTipo(tipo);
        if (items.isEmpty()) {
            System.out.println("No hay títulos activos de este tipo.");
            return;
        }

        System.out.println("\nID | Nombre");
        for (Item item : items) {
            System.out.printf("%d | %s%n", item.idGeneral(), item.contenido().getNombre());
        }

        int id = leerEntero("ID del contenido (0 para volver)");
        if (id == 0) {
            return;
        }

        for (Item item : items) {
            if (item.idGeneral() == id) {
                mostrarPorContenido(usuario, item);
                return;
            }
        }

        System.out.println("No hay un título activo de ese tipo con ese ID.");
    }

    /// Contenidos activos de un tipo, con su id general.
    private ArrayList<Item> activosDeTipo(TipoContenido tipo) {
        ArrayList<Item> items = new ArrayList<>();
        for (ContenidoMultimedia contenido : contenidos.leerTodos()) {
            int idGeneral = contenidos.buscarIdContenido(contenido);
            if (idGeneral != -1 && !contenido.getBaja() && contenidos.getTipo(idGeneral) == tipo) {
                items.add(new Item(idGeneral, contenido));
            }
        }
        items.sort(Comparator.comparingInt(Item::idGeneral));
        return items;
    }

    private void mostrarPorContenido(Usuario usuario, Item item) {
        ContenidoMultimedia contenido = item.contenido();
        int idContenido = item.idGeneral();
        while (true) {
            System.out.println("\n--- Reseñas de " + contenido.getNombre() + " ---");
            System.out.println("1. Ver reseñas");
            System.out.println("2. Publicar reseña");
            System.out.println("3. Editar una reseña propia");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            try {
                switch (entrada.nextLine().trim()) {
                    case "1" -> listar(resenas.filtrarPorContenido(idContenido));
                    case "2" -> agregar(usuario.getId(), idContenido);
                    case "3" -> modificar(usuario.getId(), idContenido);
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida");
                }
            } catch (IllegalArgumentException | DatoInvalidoException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.err.println("No se pudo guardar la reseña. " + e.getMessage());
            }
        }
    }

    private void listar(List<Resena> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay reseñas para mostrar.");
            return;
        }

        for (Resena resena : lista) {
            mostrarResena(resena);
        }
    }

    private void mostrarResena(Resena resena) {
        String comentario = resena.getComentario();
        if (comentario == null || comentario.isBlank()) {
            comentario = "Sin comentario";
        }

        // El tipo no se guarda en la reseña: se obtiene del contenido.
        int idContenido = resena.getIdContenido();
        ContenidoMultimedia contenido = contenidos.buscarPorId(idContenido);
        String titulo = contenido == null
                ? "Contenido " + idContenido
                : contenidos.getTipo(idContenido) + " " + idContenido + " (" + contenido.getNombre() + ")";

        System.out.printf("%nReseña %d | %s | Usuario %d%n",
                resena.getId(), titulo, resena.getIdUsuario());
        System.out.println("Calificación: " + resena.getCalificacion());
        System.out.println("Comentario: " + comentario);
        System.out.println("Fecha: " + resena.getFecha());
    }

    private Resena pedirResena() {
        int id = leerEntero("ID de la reseña");
        Resena resena = resenas.buscarPorId(id);
        if (resena == null) {
            throw new IllegalArgumentException("No existe una reseña con ese ID.");
        }
        return resena;
    }

    private void agregar(int idUsuario, int idContenido) throws SQLException {
        int calificacion = leerEntero("Calificación");
        String comentario = leerComentario();
        String fecha = LocalDate.now().toString();

        Resena resena = new Resena(idContenido, idUsuario, calificacion, comentario, fecha);
        if (resenas.agregar(resena)) {
            System.out.println("Reseña publicada con ID " + resena.getId() + ".");
        } else {
            System.out.println("No se pudo publicar la reseña.");
        }
    }

    private void modificar(int idUsuario, int idContenido) throws SQLException {
        Resena resena = resenas.buscarPorContenidoYUsuario(idContenido, idUsuario);

        if (resena == null) {
            System.out.println("Todavía no publicaste una reseña de este título.");
            return;
        }

        mostrarResena(resena);
        System.out.println("\n¿Qué querés modificar?");
        System.out.println("1. Calificación");
        System.out.println("2. Comentario");
        System.out.println("0. Cancelar");
        System.out.print("Opción: ");

        switch (entrada.nextLine().trim()) {
            case "1" -> resena.setCalificacion(leerEntero("Nueva calificación"));
            case "2" -> resena.setComentario(leerComentario());
            case "0" -> {
                return;
            }
            default -> {
                System.out.println("Opción inválida");
                return;
            }
        }

        if (resenas.modificar(resena)) {
            System.out.println("Reseña modificada.");
        } else {
            System.out.println("No se pudo modificar la reseña.");
        }
    }

    private void eliminar() throws SQLException {
        Resena resena = pedirResena();
        if (resenas.eliminar(resena.getId())) {
            System.out.println("Reseña eliminada.");
        } else {
            System.out.println("No se pudo eliminar la reseña.");
        }
    }

    private String leerComentario() {
        System.out.print("Comentario (Enter para dejarlo vacío): ");
        String comentario = entrada.nextLine().trim();
        return comentario.isEmpty() ? null : comentario;
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