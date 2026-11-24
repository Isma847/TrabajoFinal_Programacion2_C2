package UI.controllers.USUARIO;

import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Pelicula;
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

public class PeliculasUsuarioController extends ControladorBase {

    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, String> colDescripcion;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, String> colClasificacion;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colFecha;
    @FXML private TableColumn<Pelicula, String> colDirector;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblCantidad;

    @FXML
    private void initialize() {
        tablaPeliculas.setPlaceholder(new Label("No hay películas disponibles."));
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
        colFecha.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getFechaEstreno()));
        colDirector.setCellValueFactory(datos ->
                new SimpleStringProperty(nombreDirector(datos.getValue().getIdDirector())));

        tablaPeliculas.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, seleccionada) ->
                        txtDescripcion.setText(seleccionada == null
                                ? "" : descripcionVisible(seleccionada.getDescripcion())));
    }

    @Override
    protected void cargarDatos() {
        try {
            usuarioActual();
            ArrayList<Pelicula> disponibles = new ArrayList<>();

            for (ContenidoMultimedia contenido :
                    contenidoService.filtrarPorTipo(TipoContenido.PELICULA, false)) {
                disponibles.add((Pelicula) contenido);
            }

            disponibles.sort(Comparator.comparing(
                    Pelicula::getNombre, String.CASE_INSENSITIVE_ORDER));
            tablaPeliculas.setItems(FXCollections.observableArrayList(disponibles));
            txtDescripcion.clear();
            lblCantidad.setText("Disponibles: " + disponibles.size());

        } catch (DatoInvalidoException e) {
            tablaPeliculas.getItems().clear();
            txtDescripcion.clear();
            lblCantidad.setText("Disponibles: 0");
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    @FXML
    private void volverMenu() {
        cambiarVista(tablaPeliculas, "/vistas/menu-usuario.fxml", "Temuflix");
    }
}

