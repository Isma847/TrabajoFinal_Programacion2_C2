package UI.controllers.USUARIO;

import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Videojuego;
import UI.controllers.ControladorBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.Comparator;

public class VideojuegosUsuarioController extends ControladorBase {

    @FXML private TableView<Videojuego> tablaVideojuegos;
    @FXML private TableColumn<Videojuego, Integer> colId;
    @FXML private TableColumn<Videojuego, String> colNombre;
    @FXML private TableColumn<Videojuego, String> colDescripcion;
    @FXML private TableColumn<Videojuego, String> colGenero;
    @FXML private TableColumn<Videojuego, String> colClasificacion;
    @FXML private TableColumn<Videojuego, Integer> colDuracion;
    @FXML private TableColumn<Videojuego, Integer> colDificultad;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblCantidad;

    @FXML
    private void initialize() {
        tablaVideojuegos.setPlaceholder(new Label("No hay videojuegos disponibles."));
        colId.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        contenidoService.buscarIdContenido(datos.getValue())));
        colNombre.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getNombre()));
        colDescripcion.setCellValueFactory(datos ->
                new SimpleStringProperty(descripcionVisible(
                        datos.getValue().getDescripcion())));
        colGenero.setCellValueFactory(datos ->
                new SimpleStringProperty(String.valueOf(datos.getValue().getGenero())));
        colClasificacion.setCellValueFactory(datos ->
                new SimpleStringProperty(String.valueOf(
                        datos.getValue().getClasificacionEdad())));
        colDuracion.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDuracionMinutos()));
        colDificultad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDificultad()));

        tablaVideojuegos.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, seleccionada) ->
                        txtDescripcion.setText(seleccionada == null
                                ? "" : descripcionVisible(seleccionada.getDescripcion())));
    }

    @Override
    protected void cargarDatos() {
        try {
            usuarioActual();
            ArrayList<Videojuego> disponibles = new ArrayList<>();

            for (ContenidoMultimedia contenido :
                    contenidoService.filtrarPorTipo(TipoContenido.JUEGO, false)) {
                disponibles.add((Videojuego) contenido);
            }

            disponibles.sort(Comparator.comparing(
                    Videojuego::getNombre, String.CASE_INSENSITIVE_ORDER));
            tablaVideojuegos.setItems(FXCollections.observableArrayList(disponibles));
            txtDescripcion.clear();
            lblCantidad.setText("Disponibles: " + disponibles.size());

        } catch (DatoInvalidoException e) {
            tablaVideojuegos.getItems().clear();
            txtDescripcion.clear();
            lblCantidad.setText("Disponibles: 0");
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    @FXML
    private void volverMenu() {
        cambiarVista(tablaVideojuegos, "/vistas/menu-usuario.fxml", "Temuflix");
    }
}

