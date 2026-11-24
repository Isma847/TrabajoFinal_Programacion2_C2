package UI.controllers.USUARIO;

import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Favorito;
import Modelo.Usuario;
import UI.controllers.ControladorBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

public class FavoritosUsuarioController extends ControladorBase {

    @FXML private ComboBox<TipoContenido> comboTipo;
    @FXML private ComboBox<ContenidoMultimedia> comboContenido;
    @FXML private TableView<Favorito> tablaFavoritos;
    @FXML private TableColumn<Favorito, Integer> colId;
    @FXML private TableColumn<Favorito, String> colTipo;
    @FXML private TableColumn<Favorito, String> colContenido;
    @FXML private TableColumn<Favorito, String> colEstado;
    @FXML private Label lblCantidad;

    @FXML
    private void initialize() {
        comboTipo.setItems(FXCollections.observableArrayList(TipoContenido.values()));
        comboTipo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoContenido tipo) {
                if (tipo == null) return "";
                return switch (tipo) {
                    case PELICULA -> "Películas";
                    case SERIE -> "Series";
                    case JUEGO -> "Videojuegos";
                };
            }

            @Override
            public TipoContenido fromString(String texto) {
                return null;
            }
        });
        comboContenido.setConverter(new StringConverter<>() {
            @Override
            public String toString(ContenidoMultimedia contenido) {
                return contenido == null ? ""
                        : contenido.getNombre() + " · ID "
                                + contenidoService.buscarIdContenido(contenido);
            }

            @Override
            public ContenidoMultimedia fromString(String texto) {
                return null;
            }
        });
        comboTipo.setOnAction(evento -> cargarContenidos());

        colId.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getId_contenido()));
        colTipo.setCellValueFactory(datos -> {
            int idGeneral = datos.getValue().getId_contenido();
            String tipo = contenidoService.buscarPorId(idGeneral) == null
                    ? "No disponible"
                    : switch (contenidoService.getTipo(idGeneral)) {
                        case PELICULA -> "Película";
                        case SERIE -> "Serie";
                        case JUEGO -> "Videojuego";
                    };
            return new SimpleStringProperty(tipo);
        });
        colContenido.setCellValueFactory(datos ->
                new SimpleStringProperty(nombreContenido(datos.getValue())));
        colEstado.setCellValueFactory(datos -> {
            ContenidoMultimedia contenido = contenidoService.buscarPorId(
                    datos.getValue().getId_contenido());
            String estado = contenido == null ? "No disponible"
                    : contenido.getBaja() ? "De baja" : "Activo";
            return new SimpleStringProperty(estado);
        });
        tablaFavoritos.setPlaceholder(new Label("Todavía no agregaste favoritos."));
    }

    @Override
    protected void cargarDatos() {
        try {
            usuarioActual();
            cargarFavoritos();
            if (comboTipo.getValue() == null) {
                comboTipo.getSelectionModel().selectFirst();
            } else {
                cargarContenidos();
            }
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    private String nombreContenido(Favorito favorito) {
        ContenidoMultimedia contenido = contenidoService.buscarPorId(
                favorito.getId_contenido());
        return contenido == null ? "Contenido no disponible" : contenido.getNombre();
    }

    private void cargarFavoritos() {
        Usuario usuario = usuarioActual();
        ArrayList<Favorito> favoritos = new ArrayList<>(
                favoritoService.leerPorUsuario(usuario.getId()));
        favoritos.sort(Comparator.comparing(
                        this::nombreContenido, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Favorito::getId_contenido));
        tablaFavoritos.setItems(FXCollections.observableArrayList(favoritos));
        lblCantidad.setText("Mis favoritos: " + favoritos.size());
    }

    private void cargarContenidos() {
        comboContenido.getSelectionModel().clearSelection();
        comboContenido.getItems().clear();
        if (comboTipo.getValue() == null) return;

        try {
            usuarioActual();
            ArrayList<ContenidoMultimedia> disponibles =
                    contenidoService.filtrarPorTipo(comboTipo.getValue(), false);
            disponibles.sort(Comparator.comparing(
                    ContenidoMultimedia::getNombre, String.CASE_INSENSITIVE_ORDER));
            comboContenido.setItems(FXCollections.observableArrayList(disponibles));
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    private int idContenidoSeleccionado() {
        ContenidoMultimedia seleccionado = comboContenido.getValue();
        if (seleccionado == null) {
            throw new DatoInvalidoException("Seleccioná un contenido para agregar.");
        }

        int idGeneral = contenidoService.buscarIdContenido(seleccionado);
        ContenidoMultimedia actual = contenidoService.buscarPorId(idGeneral);
        if (idGeneral <= 0 || actual == null || actual.getBaja()) {
            throw new DatoInvalidoException(
                    "El contenido seleccionado no está disponible.");
        }
        return idGeneral;
    }

    @FXML
    private void agregarFavorito() {
        try {
            Usuario usuario = usuarioActual();
            int idGeneral = idContenidoSeleccionado();

            if (favoritoService.contiene(usuario.getId(), idGeneral)) {
                mostrarMensaje("Favoritos", "Este contenido ya está en tus favoritos.");
                return;
            }

            Favorito nuevo = new Favorito(0, usuario.getId(), idGeneral);
            if (favoritoService.crear(nuevo)) {
                cargarFavoritos();
                mostrarMensaje("Favoritos", "Se agregó el contenido a tus favoritos.");
            } else {
                mostrarMensaje("Favoritos", "No se pudo agregar el favorito.");
            }
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error", "No se pudo guardar el favorito: " + e.getMessage());
        }
    }

    @FXML
    private void quitarFavorito() {
        try {
            Usuario usuario = usuarioActual();
            Favorito seleccionado = tablaFavoritos.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                throw new DatoInvalidoException("Seleccioná un favorito para quitar.");
            }
            if (seleccionado.getId_usaurio() != usuario.getId()) {
                throw new DatoInvalidoException(
                        "Solo podés quitar favoritos de tu propia cuenta.");
            }

            int eliminados = favoritoService.quitarDeUsuario(
                    usuario.getId(), seleccionado.getId_contenido());
            cargarFavoritos();

            if (eliminados > 0) {
                mostrarMensaje("Favoritos", "Se quitó el contenido de tus favoritos.");
            } else {
                mostrarMensaje("Favoritos", "El favorito ya no estaba guardado.");
            }
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error", "No se pudo quitar el favorito: " + e.getMessage());
        }
    }

    @FXML
    private void volverAlMenu() {
        cambiarVista(tablaFavoritos, "/vistas/menu-usuario.fxml", "Temuflix");
    }
}

