
package UI.controllers.ADMIN;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Modelo.Director;
import Modelo.Serie;
import Service.*;

import UI.controllers.ControladorBase;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class AdministrarSeriesController extends ControladorBase {

    private GestionCuenta gestionCuenta;
    private PeliculaService peliculaService;
    private SerieService serieService;
    private VideojuegoService videojuegoService;
    private ResenaService resenaService;
    private FavoritoService favoritoService;
    private DirectorService directorService;

    @FXML private Button btnVolver;

    @FXML private TableView<Serie> tablaSeries;
    @FXML private TableColumn<Serie, Integer> colId;
    @FXML private TableColumn<Serie, String> colNombre;
    @FXML private TableColumn<Serie, String> colGenero;
    @FXML private TableColumn<Serie, String> colClasificacion;
    @FXML private TableColumn<Serie, Integer> colCapitulos;
    @FXML private TableColumn<Serie, Integer> colTemporadas;
    @FXML private TableColumn<Serie, Boolean> colBaja;

    @FXML private ComboBox<String> cmbFiltroBaja;
    @FXML private ComboBox<ClasificacionEdad> cmbClasificacion;
    @FXML private ComboBox<GeneroAudiovisual> cmbGenero;
    @FXML private ComboBox<Director> cmbDirector;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtCapitulos;
    @FXML private TextField txtTemporadas;
    @FXML private TextArea txtDescripcion;

    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
    }

    public void setPeliculaService(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    public void setSerieService(SerieService serieService) {
        this.serieService = serieService;
    }

    public void setVideojuegoService(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    public void setResenaService(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    public void setFavoritoService(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    public void setDirectorService(DirectorService directorService) {
        this.directorService = directorService;
        cargarDirectores();
    }

    @FXML
    private void initialize() {
        cmbClasificacion.getItems().setAll(ClasificacionEdad.values());
        cmbGenero.getItems().setAll(GeneroAudiovisual.values());

        cmbFiltroBaja.getItems().setAll(
                "Activas",
                "Dadas de baja"
        );

        cmbFiltroBaja.setValue("Activas");

        configurarTabla();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        data.getValue().getId()
                ));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNombre()
                ));

        colGenero.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getGenero().toString()
                ));

        colClasificacion.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getClasificacionEdad().toString()
                ));

        colCapitulos.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        data.getValue().getCantidadCapitulos()
                ));

        colTemporadas.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        data.getValue().getCantidadTemporadas()
                ));

        colBaja.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        data.getValue().getBaja()
                ));
    }

    private void cargarDirectores() {
        cmbDirector.getItems().setAll(directorService.leerTodos());

        cmbDirector.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Director director, boolean empty) {
                super.updateItem(director, empty);

                if (empty || director == null) {
                    setText(null);
                } else {
                    setText(director.getNombre() + " " + director.getApellido());
                }
            }
        });

        cmbDirector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Director director, boolean empty) {
                super.updateItem(director, empty);

                if (empty || director == null) {
                    setText(null);
                } else {
                    setText(director.getNombre() + " " + director.getApellido());
                }
            }
        });
    }

    @FXML
    private void filtrarSeries() {
        if (serieService == null || cmbFiltroBaja == null) return;

        boolean baja = "Dadas de baja".equals(cmbFiltroBaja.getValue());

        tablaSeries.getItems().setAll(
                serieService.getLista(baja)
        );
    }

    @FXML
    private void agregarSerie() {
        try {
            Serie serie = obtenerDatosFormulario();

            if (serieService.agregar(serie)) {
                mostrarMensaje("Éxito", "Serie agregada correctamente.");
                filtrarSeries();
                limpiarFormulario();
            } else {
                mostrarMensaje("Error", "No se pudo agregar la serie.");
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La duración, los capítulos y las temporadas deben ser números enteros.");
        } catch (Exception e) {
            mostrarMensaje("Error", e.getMessage());
        }
    }

    private Serie obtenerDatosFormulario() {
        if (txtNombre.getText().isBlank()
                || txtDuracion.getText().isBlank()
                || txtCapitulos.getText().isBlank()
                || txtTemporadas.getText().isBlank()
                || txtDescripcion.getText().isBlank()
                || cmbClasificacion.getValue() == null
                || cmbGenero.getValue() == null
                || cmbDirector.getValue() == null) {

            throw new IllegalArgumentException(
                    "Completá todos los campos."
            );
        }

        int duracion = Integer.parseInt(txtDuracion.getText());
        int capitulos = Integer.parseInt(txtCapitulos.getText());
        int temporadas = Integer.parseInt(txtTemporadas.getText());

        if (duracion <= 0 || capitulos <= 0 || temporadas <= 0) {
            throw new IllegalArgumentException(
                    "La duración, los capítulos y las temporadas deben ser mayores a cero."
            );
        }

        Director director = cmbDirector.getValue();

        return new Serie(
                false,
                cmbClasificacion.getValue(),
                duracion,
                txtNombre.getText().trim(),
                cmbGenero.getValue(),
                director.getId(),
                0,
                capitulos,
                temporadas,
                txtDescripcion.getText().trim()
        );
    }

    @FXML
    private void modificarSerie() {
        Serie seleccionada = tablaSeries.getSelectionModel()
                .getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Atención", "Seleccioná una serie.");
            return;
        }

        try {
            Serie modificada = obtenerDatosFormulario();

            modificada.setId(seleccionada.getId());
            modificada.setBaja(seleccionada.getBaja());

            if (serieService.modificar(modificada)) {
                mostrarMensaje("Éxito", "Serie modificada correctamente.");
                filtrarSeries();
                limpiarFormulario();
            } else {
                mostrarMensaje("Error", "No se pudo modificar la serie.");
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La duración, los capítulos y las temporadas deben ser números enteros.");
        } catch (Exception e) {
            mostrarMensaje("Error", e.getMessage());
        }
    }

    @FXML
    private void cambiarEstadoSerie() {
        Serie seleccionada = tablaSeries.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Atención", "Seleccioná una serie.");
            return;
        }

        try {
            if (serieService.darDeBaja(seleccionada.getId())) {
                mostrarMensaje("Éxito", "Estado de la serie actualizado.");
                filtrarSeries();
            } else {
                mostrarMensaje("Error", "No se pudo cambiar el estado.");
            }

        } catch (SQLException e) {
            mostrarMensaje("Error", e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDuracion.clear();
        txtCapitulos.clear();
        txtTemporadas.clear();
        txtDescripcion.clear();

        cmbClasificacion.setValue(null);
        cmbGenero.setValue(null);
        cmbDirector.setValue(null);
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void volver() {
        volverAlMenuAdministrador(
                btnVolver,
                gestionCuenta,
                peliculaService,
                serieService,
                videojuegoService,
                resenaService,
                favoritoService,
                directorService
        );
    }
}
