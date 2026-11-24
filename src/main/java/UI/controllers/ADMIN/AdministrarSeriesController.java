package UI.controllers.ADMIN;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Director;
import Modelo.Serie;
import UI.controllers.ControladorBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.sql.SQLException;

public class AdministrarSeriesController extends ControladorBase {

    @FXML private Button btnVolver;
    @FXML private TableView<Serie> tablaSeries;
    @FXML private TableColumn<Serie, Integer> colId;
    @FXML private TableColumn<Serie, String> colNombre;
    @FXML private TableColumn<Serie, Integer> colDuracion;
    @FXML private TableColumn<Serie, String> colGenero;
    @FXML private TableColumn<Serie, String> colClasificacion;
    @FXML private TableColumn<Serie, String> colBaja;
    @FXML private ComboBox<String> cmbFiltroBaja;
    @FXML private ComboBox<ClasificacionEdad> cmbClasificacion;
    @FXML private ComboBox<GeneroAudiovisual> cmbGenero;
    @FXML private ComboBox<Director> cmbDirector;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextArea txtDescripcion;
    @FXML private TableColumn<Serie, Integer> colCapitulos;
    @FXML private TableColumn<Serie, Integer> colTemporadas;
    @FXML private TextField txtCapitulos;
    @FXML private TextField txtTemporadas;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(contenidoService.buscarIdContenido(datos.getValue())));
        colNombre.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getNombre()));
        colDuracion.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDuracionMinutos()));
        colGenero.setCellValueFactory(datos ->
                new SimpleStringProperty(String.valueOf(datos.getValue().getGenero())));
        colClasificacion.setCellValueFactory(datos ->
                new SimpleStringProperty(String.valueOf(datos.getValue().getClasificacionEdad())));
        colBaja.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getBaja() ? "De baja" : "Activo"));
        colCapitulos.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCantidadCapitulos()));
        colTemporadas.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCantidadTemporadas()));

        cmbClasificacion.setItems(FXCollections.observableArrayList(ClasificacionEdad.values()));
        cmbGenero.setItems(FXCollections.observableArrayList(GeneroAudiovisual.values()));
        cmbFiltroBaja.setItems(FXCollections.observableArrayList("Todos", "Activos", "De baja"));
        cmbFiltroBaja.getSelectionModel().selectFirst();
        cmbFiltroBaja.setOnAction(event -> cargarDatos());

        cmbDirector.setConverter(new StringConverter<>() {
            @Override
            public String toString(Director director) {
                if (director == null) {
                    return "Sin director";
                }
                return director.getNombre() + " " + director.getApellido()
                        + " (ID " + director.getId() + ")";
            }

            @Override
            public Director fromString(String texto) {
                return null;
            }
        });
        cmbDirector.setPromptText("Sin director");

        tablaSeries.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, actual) -> {
                    if (actual != null) {
                        mostrarFormulario(actual);
                    }
                });
    }

    @Override
    protected void cargarDatos() {
        if (!comprobarAdministrador()) {
            return;
        }

        cmbDirector.getItems().clear();
        cmbDirector.getItems().add(null);
        cmbDirector.getItems().addAll(directorService.leerTodos());
        ObservableList<Serie> lista = FXCollections.observableArrayList();
        for (ContenidoMultimedia contenido : contenidoService.leerTodos()) {
            if (!(contenido instanceof Serie serie)) {
                continue;
            }
            if ("Activos".equals(cmbFiltroBaja.getValue()) && contenido.getBaja()) {
                continue;
            }
            if ("De baja".equals(cmbFiltroBaja.getValue()) && !contenido.getBaja()) {
                continue;
            }
            lista.add(serie);
        }
        tablaSeries.setItems(lista);
        limpiarFormulario();
    }

    @FXML
    private void agregarSerie() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Serie contenido = obtenerDatosFormulario(0, false);
            if (contenidoService.agregar(contenido)) {
                int idGeneral = contenidoService.buscarIdContenido(contenido);
                cargarDatos();
                mostrarMensaje("Series", "Se agregó el contenido con ID general " + idGeneral + ".");
            } else {
                mostrarMensaje("Series", "No se pudo agregar el contenido.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void modificarSerie() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Serie seleccionada = pedirSeleccion();
            Serie modificada = obtenerDatosFormulario(
                    seleccionada.getId(), seleccionada.getBaja());
            if (contenidoService.modificar(modificada)) {
                cargarDatos();
                mostrarMensaje("Series", "Se guardaron los cambios.");
            } else {
                mostrarMensaje("Series", "No se pudieron guardar los cambios.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void cambiarEstadoSerie() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Serie seleccionada = pedirSeleccion();
            boolean estabaDeBaja = seleccionada.getBaja();
            if (contenidoService.darDeBaja(seleccionada)) {
                cargarDatos();
                mostrarMensaje("Series",
                        estabaDeBaja ? "Contenido reactivado." : "Contenido dado de baja.");
            } else {
                mostrarMensaje("Series", "No se pudo cambiar el estado.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    private Serie pedirSeleccion() {
        Serie seleccionada = tablaSeries.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            throw new DatoInvalidoException("Seleccioná una serie de la tabla.");
        }
        return seleccionada;
    }

    private Serie obtenerDatosFormulario(int id, boolean baja) {
        String nombre = leerTextoObligatorio(txtNombre, "Nombre", 100);
        int duracion = leerEnteroPositivo(txtDuracion, "Duración en minutos");
        if (cmbClasificacion.getValue() == null || cmbGenero.getValue() == null) {
            throw new DatoInvalidoException("Seleccioná una clasificación y un género.");
        }
        Director director = cmbDirector.getValue();
        int idDirector = director == null ? 0 : director.getId();
        int capitulos = leerEnteroPositivo(txtCapitulos, "Cantidad de capítulos");
        int temporadas = leerEnteroPositivo(txtTemporadas, "Cantidad de temporadas");
        String descripcion = leerDescripcion(txtDescripcion);
        return new Serie(baja, cmbClasificacion.getValue(), duracion, nombre,
                cmbGenero.getValue(), idDirector, id, capitulos, temporadas, descripcion);
    }

    private void mostrarFormulario(Serie contenido) {
        txtNombre.setText(contenido.getNombre());
        txtDuracion.setText(String.valueOf(contenido.getDuracionMinutos()));
        cmbClasificacion.setValue(contenido.getClasificacionEdad());
        cmbGenero.setValue(contenido.getGenero());
        txtDescripcion.setText(contenido.getDescripcion() == null ? "" : contenido.getDescripcion());
        txtCapitulos.setText(String.valueOf(contenido.getCantidadCapitulos()));
        txtTemporadas.setText(String.valueOf(contenido.getCantidadTemporadas()));
        cmbDirector.getSelectionModel().clearSelection();
        for (Director director : cmbDirector.getItems()) {
            if (director != null && director.getId() == contenido.getIdDirector()) {
                cmbDirector.getSelectionModel().select(director);
                break;
            }
        }
    }

    @FXML
    private void nuevaSerie() {
        if (comprobarAdministrador()) {
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        tablaSeries.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtDuracion.clear();
        txtDescripcion.clear();
        cmbClasificacion.getSelectionModel().clearSelection();
        cmbGenero.getSelectionModel().clearSelection();
        cmbDirector.getSelectionModel().clearSelection();
        txtCapitulos.clear();
        txtTemporadas.clear();
    }

    @FXML
    private void volver() {
        cambiarVista(btnVolver, "/vistas/gestion-admin.fxml", "Gestión administrativa");
    }
}
