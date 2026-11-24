package UI.controllers.ADMIN;

import Enumerados.ClasificacionEdad;
import Enumerados.GeneroVideojuegos;
import Excepciones.DatoInvalidoException;
import Modelo.ContenidoMultimedia;
import Modelo.Videojuego;
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

import java.sql.SQLException;

public class AdministrarVideojuegosController extends ControladorBase {

    @FXML private Button btnVolver;
    @FXML private TableView<Videojuego> tablaVideojuegos;
    @FXML private TextField txtBuscarId;
    @FXML private TableColumn<Videojuego, Integer> colId;
    @FXML private TableColumn<Videojuego, String> colNombre;
    @FXML private TableColumn<Videojuego, Integer> colDuracion;
    @FXML private TableColumn<Videojuego, String> colGenero;
    @FXML private TableColumn<Videojuego, String> colClasificacion;
    @FXML private TableColumn<Videojuego, String> colBaja;
    @FXML private ComboBox<String> cmbFiltroBaja;
    @FXML private ComboBox<ClasificacionEdad> cmbClasificacion;
    @FXML private ComboBox<GeneroVideojuegos> cmbGenero;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextArea txtDescripcion;
    @FXML private TableColumn<Videojuego, Integer> colDificultad;
    @FXML private TextField txtDificultad;

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
        colDificultad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDificultad()));

        cmbClasificacion.setItems(FXCollections.observableArrayList(ClasificacionEdad.values()));
        cmbGenero.setItems(FXCollections.observableArrayList(GeneroVideojuegos.values()));
        cmbFiltroBaja.setItems(FXCollections.observableArrayList("Todos", "Activos", "De baja"));
        cmbFiltroBaja.getSelectionModel().selectFirst();
        cmbFiltroBaja.setOnAction(event -> cargarDatos());

        tablaVideojuegos.getSelectionModel().selectedItemProperty().addListener(
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

        ObservableList<Videojuego> lista = FXCollections.observableArrayList();
        for (ContenidoMultimedia contenido : contenidoService.leerTodos()) {
            if (!(contenido instanceof Videojuego videojuego)) {
                continue;
            }
            if ("Activos".equals(cmbFiltroBaja.getValue()) && contenido.getBaja()) {
                continue;
            }
            if ("De baja".equals(cmbFiltroBaja.getValue()) && !contenido.getBaja()) {
                continue;
            }
            lista.add(videojuego);
        }
        tablaVideojuegos.setItems(lista);
        limpiarFormulario();
    }

    @FXML
    private void agregarVideojuego() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Videojuego contenido = obtenerDatosFormulario(0, false);
            if (contenidoService.agregar(contenido)) {
                int idGeneral = contenidoService.buscarIdContenido(contenido);
                cargarDatos();
                mostrarMensaje("Videojuegos", "Se agregó el contenido con ID general " + idGeneral + ".");
            } else {
                mostrarMensaje("Videojuegos", "No se pudo agregar el contenido.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void modificarVideojuego() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Videojuego seleccionada = pedirSeleccion();
            Videojuego modificada = obtenerDatosFormulario(
                    seleccionada.getId(), seleccionada.getBaja());
            if (contenidoService.modificar(modificada)) {
                cargarDatos();
                mostrarMensaje("Videojuegos", "Se guardaron los cambios.");
            } else {
                mostrarMensaje("Videojuegos", "No se pudieron guardar los cambios.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void cambiarEstadoVideojuego() {
        if (!comprobarAdministrador()) {
            return;
        }
        try {
            Videojuego seleccionada = pedirSeleccion();
            boolean estabaDeBaja = seleccionada.getBaja();
            if (contenidoService.darDeBaja(seleccionada)) {
                cargarDatos();
                mostrarMensaje("Videojuegos",
                        estabaDeBaja ? "Contenido reactivado." : "Contenido dado de baja.");
            } else {
                mostrarMensaje("Videojuegos", "No se pudo cambiar el estado.");
            }
        } catch (IllegalArgumentException | DatoInvalidoException e) {
            mostrarMensaje("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar", e.getMessage());
        }
    }

    private Videojuego pedirSeleccion() {
        Videojuego seleccionada = tablaVideojuegos.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            throw new DatoInvalidoException("Seleccioná un videojuego de la tabla.");
        }
        return seleccionada;
    }

    private Videojuego obtenerDatosFormulario(int id, boolean baja) {
        String nombre = leerTextoObligatorio(txtNombre, "Nombre", 100);
        int duracion = leerEnteroPositivo(txtDuracion, "Duración en minutos");
        if (cmbClasificacion.getValue() == null || cmbGenero.getValue() == null) {
            throw new DatoInvalidoException("Seleccioná una clasificación y un género.");
        }
        int dificultad = leerEnteroPositivo(txtDificultad, "Dificultad");
        String descripcion = leerDescripcion(txtDescripcion);
        return new Videojuego(baja, cmbClasificacion.getValue(), duracion, nombre,
                id, dificultad, cmbGenero.getValue(), descripcion);
    }

    private void mostrarFormulario(Videojuego contenido) {
        txtNombre.setText(contenido.getNombre());
        txtDuracion.setText(String.valueOf(contenido.getDuracionMinutos()));
        cmbClasificacion.setValue(contenido.getClasificacionEdad());
        cmbGenero.setValue(contenido.getGenero());
        txtDescripcion.setText(contenido.getDescripcion() == null ? "" : contenido.getDescripcion());
        txtDificultad.setText(String.valueOf(contenido.getDificultad()));
    }

    @FXML
    private void nuevoVideojuego() {
        if (comprobarAdministrador()) {
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        tablaVideojuegos.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtDuracion.clear();
        txtDescripcion.clear();
        cmbClasificacion.getSelectionModel().clearSelection();
        cmbGenero.getSelectionModel().clearSelection();
        txtDificultad.clear();
    }

    @FXML
    private void buscarVideojuego() {
        if (!comprobarAdministrador()) {
            return;
        }

        try {
            int id = Integer.parseInt(txtBuscarId.getText());

            ContenidoMultimedia contenido = contenidoService.buscarPorId(id);

            if (contenido == null) {
                mostrarMensaje("Búsqueda", "No se encontró ningún contenido con ese ID.");
                return;
            }

            if (!(contenido instanceof Videojuego)) {
                mostrarMensaje("Búsqueda", "El ID corresponde a otro tipo de contenido.");
                return;
            }

            Videojuego videojuego = (Videojuego) contenido;

            String filtro = cmbFiltroBaja.getValue();

            if (("Activos".equals(filtro) && videojuego.getBaja())
                    || ("De baja".equals(filtro) && !videojuego.getBaja())) {
                mostrarMensaje("Búsqueda", "El videojuego no coincide con el filtro seleccionado.");
                return;
            }

            tablaVideojuegos.getItems().setAll(videojuego);

        } catch (NumberFormatException e) {
            mostrarMensaje("Búsqueda", "Ingresá un ID numérico válido.");
        }
    }

    @FXML
    private void mostrarTodosLosVideojuegos() {
        cargarDatos();
    }

    @FXML
    private void volver() {
        cambiarVista(btnVolver, "/vistas/gestion-admin.fxml", "Gestión administrativa");
    }
}
