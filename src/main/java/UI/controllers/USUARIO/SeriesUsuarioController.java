package UI.controllers.USUARIO;

import Enumerados.TipoContenido;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Serie;
import UI.controllers.ControladorBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Comparator;

public class SeriesUsuarioController extends ControladorBase {

    @FXML private TableView<Serie> tablaSeries;
    @FXML private TableColumn<Serie, Integer> colId;
    @FXML private TableColumn<Serie, String> colNombre;
    @FXML private TableColumn<Serie, String> colDescripcion;
    @FXML private TableColumn<Serie, String> colGenero;
    @FXML private TableColumn<Serie, String> colClasificacion;
    @FXML private TableColumn<Serie, Integer> colDuracion;
    @FXML private TableColumn<Serie, Integer> colCapitulos;
    @FXML private TableColumn<Serie, Integer> colTemporadas;
    @FXML private TableColumn<Serie, String> colDirector;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtBuscarNombre;
    @FXML private Label lblCantidad;

    @FXML
    private void initialize() {
        tablaSeries.setPlaceholder(new Label("No hay series disponibles."));
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
        colCapitulos.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCantidadCapitulos()));
        colTemporadas.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCantidadTemporadas()));
        colDirector.setCellValueFactory(datos ->
                new SimpleStringProperty(nombreDirector(datos.getValue().getIdDirector())));

        tablaSeries.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, seleccionada) ->
                        txtDescripcion.setText(seleccionada == null
                                ? "" : descripcionVisible(seleccionada.getDescripcion())));
    }

    @Override
    protected void cargarDatos() {
        try {
            usuarioActual();
            ArrayList<Serie> disponibles = new ArrayList<>();

            for (ContenidoMultimedia contenido :
                    contenidoService.filtrarPorTipo(TipoContenido.SERIE, false)) {
                disponibles.add((Serie) contenido);
            }

            disponibles.sort(Comparator.comparing(
                    Serie::getNombre, String.CASE_INSENSITIVE_ORDER));
            tablaSeries.setItems(FXCollections.observableArrayList(disponibles));
            txtDescripcion.clear();
            lblCantidad.setText("Disponibles: " + disponibles.size());

        } catch (DatoInvalidoException e) {
            tablaSeries.getItems().clear();
            txtDescripcion.clear();
            lblCantidad.setText("Disponibles: 0");
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    @FXML
    private void buscarSerie() {
        String nombre = txtBuscarNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarMensaje("Búsqueda", "Ingresá el nombre de una serie.");
            return;
        }

        ArrayList<Serie> resultado = new ArrayList<>();

        for (ContenidoMultimedia contenido : contenidoService.buscarPorNombre(nombre)) {
            if (contenido instanceof Serie serie && !serie.getBaja()) {
                resultado.add(serie);
            }
        }

        resultado.sort(Comparator.comparing(
                Serie::getNombre, String.CASE_INSENSITIVE_ORDER));

        tablaSeries.setItems(FXCollections.observableArrayList(resultado));

        tablaSeries.getSelectionModel().clearSelection();
        txtDescripcion.clear();

        lblCantidad.setText("Resultados: " + resultado.size());

        if (resultado.isEmpty()) {
            mostrarMensaje("Búsqueda", "No se encontraron series con ese nombre.");
        }
    }

    @FXML
    private void mostrarTodasLasSeries() {
        cargarDatos();
    }

    @FXML
    private void volverMenu() {
        cambiarVista(tablaSeries, "/vistas/menu-usuario.fxml", "Temuflix");
    }
}

