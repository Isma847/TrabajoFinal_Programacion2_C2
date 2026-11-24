
package UI.controllers.ADMIN;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroVideojuegos;
import Modelo.Videojuego;
import Service.*;

import UI.controllers.ControladorBase;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class AdministrarVideojuegosController extends ControladorBase {

    private GestionCuenta gestionCuenta;
    private PeliculaService peliculaService;
    private SerieService serieService;
    private VideojuegoService videojuegoService;
    private ResenaService resenaService;
    private FavoritoService favoritoService;
    private DirectorService directorService;

    @FXML private Button btnVolver;

    @FXML private TableView<Videojuego> tablaVideojuegos;
    @FXML private TableColumn<Videojuego, Integer> colId;
    @FXML private TableColumn<Videojuego, String> colNombre;
    @FXML private TableColumn<Videojuego, String> colGenero;
    @FXML private TableColumn<Videojuego, String> colClasificacion;
    @FXML private TableColumn<Videojuego, Integer> colDificultad;
    @FXML private TableColumn<Videojuego, Boolean> colBaja;

    @FXML private ComboBox<String> cmbFiltroBaja;
    @FXML private ComboBox<ClasificacionEdad> cmbClasificacion;
    @FXML private ComboBox<GeneroVideojuegos> cmbGenero;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtDificultad;
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
        filtrarVideojuegos();
    }

    public void setResenaService(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    public void setFavoritoService(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    public void setDirectorService(DirectorService directorService) {
        this.directorService = directorService;
    }

    @FXML
    private void initialize() {
        cmbClasificacion.getItems().setAll(ClasificacionEdad.values());
        cmbGenero.getItems().setAll(GeneroVideojuegos.values());

        cmbFiltroBaja.getItems().setAll(
                "Activos",
                "Dados de baja"
        );
        cmbFiltroBaja.setValue("Activos");

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

        colDificultad.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        data.getValue().getDificultad()
                ));

        colBaja.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        data.getValue().getBaja()
                ));
    }

    @FXML
    private void filtrarVideojuegos() {
        if (videojuegoService == null || cmbFiltroBaja == null) {
            return;
        }

        boolean baja = "Dados de baja".equals(cmbFiltroBaja.getValue());

        tablaVideojuegos.getItems().setAll(
                videojuegoService.getLista(baja)
        );
    }

    @FXML
    private void agregarVideojuego() {
        try {
            Videojuego videojuego = obtenerDatosFormulario();

            if (videojuegoService.agregar(videojuego)) {
                mostrarMensaje("Éxito", "Videojuego agregado correctamente.");
                filtrarVideojuegos();
                limpiarFormulario();
            } else {
                mostrarMensaje("Error", "No se pudo agregar el videojuego.");
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La duración y la dificultad deben ser números enteros.");
        } catch (Exception e) {
            mostrarMensaje("Error", e.getMessage());
        }
    }

    private Videojuego obtenerDatosFormulario() {
        if (txtNombre.getText().isBlank()
                || txtDuracion.getText().isBlank()
                || txtDificultad.getText().isBlank()
                || txtDescripcion.getText().isBlank()
                || cmbClasificacion.getValue() == null
                || cmbGenero.getValue() == null) {

            throw new IllegalArgumentException(
                    "Completá todos los campos."
            );
        }

        int duracion = Integer.parseInt(txtDuracion.getText());
        int dificultad = Integer.parseInt(txtDificultad.getText());

        if (duracion <= 0) {
            throw new IllegalArgumentException(
                    "La duración debe ser mayor a cero."
            );
        }

        if (dificultad <= 0) {
            throw new IllegalArgumentException(
                    "La dificultad debe ser mayor a cero."
            );
        }

        return new Videojuego(
                false,
                cmbClasificacion.getValue(),
                duracion,
                txtNombre.getText().trim(),
                0,
                dificultad,
                cmbGenero.getValue(),
                txtDescripcion.getText().trim()
        );
    }

    @FXML
    private void modificarVideojuego() {
        Videojuego seleccionada = tablaVideojuegos.getSelectionModel()
                .getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Atención", "Seleccioná un videojuego.");
            return;
        }

        try {
            Videojuego modificado = obtenerDatosFormulario();

            modificado.setId(seleccionada.getId());
            modificado.setBaja(seleccionada.getBaja());

            if (videojuegoService.modificar(modificado)) {
                mostrarMensaje("Éxito", "Videojuego modificado correctamente.");
                filtrarVideojuegos();
                limpiarFormulario();
            } else {
                mostrarMensaje("Error", "No se pudo modificar el videojuego.");
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La duración y la dificultad deben ser números enteros.");
        } catch (Exception e) {
            mostrarMensaje("Error", e.getMessage());
        }
    }

    @FXML
    private void cambiarEstadoVideojuego() {
        Videojuego seleccionada = tablaVideojuegos.getSelectionModel()
                .getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Atención", "Seleccioná un videojuego.");
            return;
        }

        try {
            if (videojuegoService.darDeBaja(seleccionada.getId())) {
                mostrarMensaje("Éxito", "Estado del videojuego actualizado.");
                filtrarVideojuegos();
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
        txtDificultad.clear();
        txtDescripcion.clear();
        cmbClasificacion.setValue(null);
        cmbGenero.setValue(null);
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
