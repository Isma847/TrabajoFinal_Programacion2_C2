package UI.controllers.USUARIO;

import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Resena;
import Modelo.Usuario;
import UI.controllers.ControladorBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class ResenasUsuarioController extends ControladorBase {

    @FXML private ComboBox<TipoContenido> comboTipo;
    @FXML private ComboBox<ContenidoMultimedia> comboContenido;
    @FXML private TableView<Resena> tablaResenas;
    @FXML private TableColumn<Resena, Integer> colAutor;
    @FXML private TableColumn<Resena, Integer> colCalificacion;
    @FXML private TableColumn<Resena, String> colComentario;
    @FXML private TableColumn<Resena, String> colFecha;
    @FXML private TextField txtCalificacion;
    @FXML private TextArea txtComentario;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Label lblResumen;

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

        colAutor.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getIdUsuario()));
        colCalificacion.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCalificacion()));
        colComentario.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getComentario() == null
                        || datos.getValue().getComentario().isBlank()
                        ? "Sin comentario" : datos.getValue().getComentario()));
        colComentario.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String comentario, boolean vacia) {
                super.updateItem(comentario, vacia);
                setText(vacia ? null : comentario);
                setWrapText(true);
            }
        });
        colFecha.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getFecha()));
        tablaResenas.setPlaceholder(new Label(
                "Elegí un contenido para consultar sus reseñas."));

        comboTipo.setOnAction(evento -> cargarContenidos());
        comboContenido.setOnAction(evento -> cargarResenas());
        tablaResenas.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, seleccionada) ->
                        seleccionarResena(seleccionada));
        btnModificar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    @Override
    protected void cargarDatos() {
        try {
            usuarioActual();
            if (comboTipo.getValue() == null) {
                comboTipo.getSelectionModel().selectFirst();
            } else {
                cargarContenidos();
            }
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    private void cargarContenidos() {
        comboContenido.getSelectionModel().clearSelection();
        comboContenido.getItems().clear();
        tablaResenas.getItems().clear();
        limpiarCampos();
        lblResumen.setText("Seleccioná un contenido.");

        if (comboTipo.getValue() == null) return;

        try {
            usuarioActual();
            ArrayList<ContenidoMultimedia> disponibles =
                    contenidoService.filtrarPorTipo(comboTipo.getValue(), false);
            disponibles.sort(Comparator.comparing(
                    ContenidoMultimedia::getNombre, String.CASE_INSENSITIVE_ORDER));
            comboContenido.setItems(FXCollections.observableArrayList(disponibles));

            if (disponibles.isEmpty()) {
                lblResumen.setText("No hay contenidos disponibles de este tipo.");
            }
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    private void cargarResenas() {
        tablaResenas.getItems().clear();
        limpiarCampos();

        if (comboContenido.getValue() == null) {
            lblResumen.setText("Seleccioná un contenido.");
            return;
        }

        try {
            usuarioActual();
            int idGeneral = idContenidoSeleccionado();
            ArrayList<Resena> resenas = resenaService.filtrarPorContenido(idGeneral);
            resenas.sort(Comparator.comparing(Resena::getFecha).reversed()
                    .thenComparingInt(Resena::getIdUsuario));
            tablaResenas.setItems(FXCollections.observableArrayList(resenas));
            lblResumen.setText("Reseñas de la comunidad: " + resenas.size());
        } catch (DatoInvalidoException e) {
            lblResumen.setText("El contenido no está disponible.");
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    private int idContenidoSeleccionado() {
        ContenidoMultimedia seleccionado = comboContenido.getValue();
        if (seleccionado == null) {
            throw new DatoInvalidoException("Seleccioná un contenido.");
        }

        int idGeneral = contenidoService.buscarIdContenido(seleccionado);
        ContenidoMultimedia actual = contenidoService.buscarPorId(idGeneral);
        if (idGeneral <= 0 || actual == null || actual.getBaja()) {
            throw new DatoInvalidoException(
                    "El contenido seleccionado no está disponible.");
        }
        return idGeneral;
    }

    private int leerCalificacion() {
        int calificacion = leerEnteroPositivo(txtCalificacion, "La calificación");
        if (calificacion > 10) {
            throw new DatoInvalidoException(
                    "La calificación debe estar entre 1 y 10.");
        }
        return calificacion;
    }

    private String comentarioIngresado() {
        String comentario = txtComentario.getText();
        return comentario == null || comentario.isBlank() ? null : comentario.trim();
    }

    @FXML
    private void publicarResena() {
        try {
            Usuario usuario = usuarioActual();
            int idGeneral = idContenidoSeleccionado();

            if (resenaService.buscarPorContenidoYUsuario(
                    idGeneral, usuario.getId()) != null) {
                mostrarMensaje("Reseñas",
                        "Ya publicaste una reseña sobre este contenido. "
                                + "Seleccioná la tuya en la tabla para modificarla.");
                return;
            }

            Resena nueva = new Resena(
                    idGeneral,
                    usuario.getId(),
                    leerCalificacion(),
                    comentarioIngresado(),
                    LocalDate.now().toString()
            );

            if (resenaService.agregar(nueva)) {
                cargarResenas();
                mostrarMensaje("Reseñas", "La reseña se publicó correctamente.");
            } else {
                mostrarMensaje("Reseñas", "No se pudo publicar la reseña.");
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Atención", "Ingresá una calificación entera entre 1 y 10.");
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error", "No se pudo guardar la reseña: " + e.getMessage());
        }
    }

    @FXML
    private void modificarResena() {
        try {
            Usuario usuario = usuarioActual();
            int idGeneral = idContenidoSeleccionado();
            Resena seleccionada = tablaResenas.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                throw new DatoInvalidoException("Seleccioná tu reseña en la tabla.");
            }

            Resena cambios = resenaService.buscarPorContenidoYUsuario(
                    idGeneral, usuario.getId());
            if (seleccionada.getIdUsuario() != usuario.getId()
                    || seleccionada.getIdContenido() != idGeneral
                    || cambios == null
                    || cambios.getId() != seleccionada.getId()) {
                throw new DatoInvalidoException(
                        "Solo podés modificar tu propia reseña del contenido seleccionado.");
            }

            // El servicio devuelve una copia: la tabla conserva sus datos hasta guardar.
            cambios.setCalificacion(leerCalificacion());
            cambios.setComentario(comentarioIngresado());

            if (resenaService.modificar(cambios)) {
                cargarResenas();
                mostrarMensaje("Reseñas", "La reseña se modificó correctamente.");
            } else {
                mostrarMensaje("Reseñas", "No se pudo modificar la reseña.");
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Atención", "Ingresá una calificación entera entre 1 y 10.");
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error", "No se pudo modificar la reseña: " + e.getMessage());
        }
    }

    private void seleccionarResena(Resena seleccionada) {
        limpiarCampos();
        if (seleccionada == null) return;

        try {
            if (seleccionada.getIdUsuario() == usuarioActual().getId()) {
                txtCalificacion.setText(String.valueOf(seleccionada.getCalificacion()));
                txtComentario.setText(seleccionada.getComentario() == null
                        ? "" : seleccionada.getComentario());
                btnModificar.setDisable(false);
                btnEliminar.setDisable(false);
            }
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    @FXML
    private void eliminarResena() {
        try {
            Usuario usuario = usuarioActual();

            Resena seleccionada = tablaResenas.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                throw new DatoInvalidoException("Seleccioná tu reseña para eliminarla.");
            }

            if (seleccionada.getIdUsuario() != usuario.getId()) {
                throw new DatoInvalidoException(
                        "Solamente podés eliminar tus propias reseñas.");
            }

            if (resenaService.eliminar(seleccionada.getId(), usuario.getId())) {
                cargarResenas();
                mostrarMensaje("Reseñas", "Tu reseña se eliminó correctamente.");
            } else {
                mostrarMensaje("Reseñas", "No se pudo eliminar la reseña.");
            }

        } catch (DatoInvalidoException e) {
            mostrarMensaje("Atención", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error", "No se pudo eliminar la reseña: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtCalificacion.clear();
        txtComentario.clear();
        btnModificar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    @FXML
    private void volverMenu() {
        cambiarVista(tablaResenas, "/vistas/menu-usuario.fxml", "Temuflix");
    }
}

