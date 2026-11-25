package UI.controllers.ADMIN;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroAudiovisual;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Director;
import Modelo.Pelicula;
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
import java.util.ArrayList;

public class AdministrarPeliculasController extends ControladorBase {

    @FXML private Button btnVolver;
    @FXML private TextField txtBuscarNombre;
    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, String> colClasificacion;
    @FXML private TableColumn<Pelicula, String> colBaja;
    @FXML private ComboBox<String> cmbFiltroBaja;
    @FXML private ComboBox<ClasificacionEdad> cmbClasificacion;
    @FXML private ComboBox<GeneroAudiovisual> cmbGenero;
    @FXML private ComboBox<Director> cmbDirector;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextArea txtDescripcion;
    @FXML private TableColumn<Pelicula, String> colFecha;
    @FXML private TextField txtFecha;

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
        colFecha.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getFechaEstreno()));

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

        tablaPeliculas.getSelectionModel().selectedItemProperty().addListener(
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
        ObservableList<Pelicula> lista = FXCollections.observableArrayList();
        for (ContenidoMultimedia contenido : contenidoService.leerTodos()) {
            if (!(contenido instanceof Pelicula pelicula)) {
                continue;
            }
            if ("Activos".equals(cmbFiltroBaja.getValue()) && contenido.getBaja()) {
                continue;
            }
            if ("De baja".equals(cmbFiltroBaja.getValue()) && !contenido.getBaja()) {
                continue;
            }
            lista.add(pelicula);
        }
        tablaPeliculas.setItems(lista);
        limpiarFormulario();
    }

    @FXML
    private void agregarPelicula() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Pelicula contenido = obtenerDatosFormulario(0, false);
            if (contenidoService.agregar(contenido)) {
                int idGeneral = contenidoService.buscarIdContenido(contenido);
                cargarDatos();
                mostrarMensaje("Películas", "Se agregó el contenido con ID general " + idGeneral + ".");
            } else {
                mostrarMensaje("Películas", "No se pudo agregar el contenido.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void modificarPelicula() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Pelicula seleccionada = pedirSeleccion();
            Pelicula modificada = obtenerDatosFormulario(
                    seleccionada.getId(), seleccionada.getBaja());
            if (contenidoService.modificar(modificada)) {
                cargarDatos();
                mostrarMensaje("Películas", "Se guardaron los cambios.");
            } else {
                mostrarMensaje("Películas", "No se pudieron guardar los cambios.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void cambiarEstadoPelicula() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Pelicula seleccionada = pedirSeleccion();
            boolean estabaDeBaja = seleccionada.getBaja();
            if (contenidoService.darDeBaja(seleccionada)) {
                cargarDatos();
                mostrarMensaje("Películas",
                        estabaDeBaja ? "Contenido reactivado." : "Contenido dado de baja.");
            } else {
                mostrarMensaje("Películas", "No se pudo cambiar el estado.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    private Pelicula pedirSeleccion() {
        Pelicula seleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            throw new DatoInvalidoException("Seleccioná una película de la tabla.");
        }
        return seleccionada;
    }

    private Pelicula obtenerDatosFormulario(int id, boolean baja) {
        String nombre = leerTextoObligatorio(txtNombre, "Nombre", 100);
        int duracion = leerEnteroPositivo(txtDuracion, "Duración en minutos");
        if (cmbClasificacion.getValue() == null || cmbGenero.getValue() == null) {
            throw new DatoInvalidoException("Seleccioná una clasificación y un género.");
        }
        Director director = cmbDirector.getValue();
        int idDirector = director == null ? 0 : director.getId();
        String fecha = leerTextoObligatorio(txtFecha, "Fecha de estreno", 10);
        String descripcion = leerDescripcion(txtDescripcion);
        return new Pelicula(baja, cmbClasificacion.getValue(), duracion, nombre,
                cmbGenero.getValue(), idDirector, fecha, id, descripcion);
    }

    private void mostrarFormulario(Pelicula contenido) {
        txtNombre.setText(contenido.getNombre());
        txtDuracion.setText(String.valueOf(contenido.getDuracionMinutos()));
        cmbClasificacion.setValue(contenido.getClasificacionEdad());
        cmbGenero.setValue(contenido.getGenero());
        txtDescripcion.setText(contenido.getDescripcion() == null ? "" : contenido.getDescripcion());
        txtFecha.setText(contenido.getFechaEstreno());
        cmbDirector.getSelectionModel().clearSelection();
        for (Director director : cmbDirector.getItems()) {
            if (director != null && director.getId() == contenido.getIdDirector()) {
                cmbDirector.getSelectionModel().select(director);
                break;
            }
        }
    }

    @FXML
    private void nuevaPelicula() {
        if (comprobarAdministrador()) {
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        tablaPeliculas.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtDuracion.clear();
        txtDescripcion.clear();
        cmbClasificacion.getSelectionModel().clearSelection();
        cmbGenero.getSelectionModel().clearSelection();
        cmbDirector.getSelectionModel().clearSelection();
        txtFecha.clear();
    }

    @FXML
    private void buscarPelicula() {
        String nombre = txtBuscarNombre.getText().trim();

        ArrayList<Pelicula> resultado = new ArrayList<>();

        for (ContenidoMultimedia contenido : contenidoService.buscarPorNombre(nombre)) {
            if (contenido instanceof Pelicula pelicula) {
                if (coincideConFiltro(pelicula, cmbFiltroBaja.getValue())) {
                    resultado.add(pelicula);
                }
            }
        }

        tablaPeliculas.setItems(FXCollections.observableArrayList(resultado));
    }


    @FXML
    private void mostrarTodasLasPeliculas() {
        cargarDatos();
    }

    @FXML
    private void volver() {
        cambiarVista(btnVolver, "/vistas/gestion-admin.fxml", "Gestión administrativa");
    }
}
