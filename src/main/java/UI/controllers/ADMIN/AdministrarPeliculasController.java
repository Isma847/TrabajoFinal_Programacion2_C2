
package UI.controllers.ADMIN;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Modelo.Director;
import Modelo.Pelicula;
import Service.*;

import UI.controllers.ControladorBase;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class AdministrarPeliculasController extends ControladorBase {

    private GestionCuenta gestionCuenta;
    private PeliculaService peliculaService;
    private SerieService serieService;
    private VideojuegoService videojuegoService;
    private ResenaService resenaService;
    private FavoritoService favoritoService;
    private DirectorService directorService;

    @FXML private Button btnVolver;

    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, String> colClasificacion;
    @FXML private TableColumn<Pelicula, String> colFecha;
    @FXML private TableColumn<Pelicula, Boolean> colBaja;

    @FXML private ComboBox<String> cmbFiltroBaja;
    @FXML private ComboBox<ClasificacionEdad> cmbClasificacion;
    @FXML private ComboBox<GeneroAudiovisual> cmbGenero;
    @FXML private ComboBox<Director> cmbDirector;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtFecha;
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

        colFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFechaEstreno()
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
    private void filtrarPeliculas() {
        if (peliculaService == null) return;

        boolean baja = "Dadas de baja".equals(cmbFiltroBaja.getValue());

        tablaPeliculas.getItems().setAll(
                peliculaService.getLista(baja)
        );
    }

    @FXML
    private void agregarPelicula() {
        try {
            Pelicula pelicula = obtenerDatosFormulario();

            if (peliculaService.agregar(pelicula)) {
                mostrarMensaje("Éxito", "Película agregada correctamente.");
                filtrarPeliculas();
            } else {
                mostrarMensaje("Error", "No se pudo agregar la película.");
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La duración debe ser un número entero.");
        } catch (Exception e) {
            mostrarMensaje("Error", e.getMessage());
        }
    }

    private Pelicula obtenerDatosFormulario() {
        if (txtNombre.getText().isBlank()
                || txtDuracion.getText().isBlank()
                || txtFecha.getText().isBlank()
                || txtDescripcion.getText().isBlank()
                || cmbClasificacion.getValue() == null
                || cmbGenero.getValue() == null
                || cmbDirector.getValue() == null) {

            throw new IllegalArgumentException(
                    "Completá todos los campos."
            );
        }

        int duracion = Integer.parseInt(txtDuracion.getText());

        if (duracion <= 0) {
            throw new IllegalArgumentException(
                    "La duración debe ser mayor a cero."
            );
        }

        Director director = cmbDirector.getValue();

        return new Pelicula(
                false,
                cmbClasificacion.getValue(),
                duracion,
                txtNombre.getText().trim(),
                cmbGenero.getValue(),
                director.getId(),
                txtFecha.getText().trim(),
                0,
                txtDescripcion.getText().trim()
        );
    }

    @FXML
    private void modificarPelicula() {
        Pelicula seleccionada = tablaPeliculas.getSelectionModel()
                .getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Atención", "Seleccioná una película.");
            return;
        }

        try {
            Pelicula modificada = obtenerDatosFormulario();

            modificada.setId(seleccionada.getId());
            modificada.setBaja(seleccionada.getBaja());

            if (peliculaService.modificar(modificada)) {
                mostrarMensaje("Éxito", "Película modificada correctamente.");

                filtrarPeliculas();
            } else {
                mostrarMensaje("Error", "No se pudo modificar la película.");
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La duración debe ser un número entero.");
        } catch (Exception e) {
            mostrarMensaje("Error", e.getMessage());
        }
    }

    @FXML
    private void cambiarEstadoPelicula() {
        Pelicula seleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Atención", "Seleccioná una película.");
            return;
        }

        try {
            if (peliculaService.darDeBaja(seleccionada.getId())) {
                mostrarMensaje("Éxito", "Estado de la película actualizado.");

                filtrarPeliculas();
            } else {
                mostrarMensaje("Error", "No se pudo cambiar el estado.");
            }

        } catch (SQLException e) {
            mostrarMensaje("Error", e.getMessage());
        }
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
