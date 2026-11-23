
package UI.controllers.USUARIO;

import Modelo.Videojuego;
import Service.*;
import UI.controllers.ControladorBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class VideojuegosUsuarioController extends ControladorBase {

    private VideojuegoService videojuegoService;
    private GestionCuenta gestionCuenta;
    private PeliculaService peliculaService;
    private SerieService serieService;
    private ResenaService resenaService;


    @FXML
    private TableView<Videojuego> tablaVideojuegos;

    @FXML
    private TableColumn<Videojuego, String> colNombre;

    @FXML
    private TableColumn<Videojuego, String> colGenero;

    @FXML
    private TableColumn<Videojuego, Integer> colDuracion;

    @FXML
    private TableColumn<Videojuego, String> colClasificacion;

    @FXML
    private TableColumn<Videojuego, Integer> colDificultad;

    @FXML
    private TableColumn<Videojuego, String> colDescripcion;

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
        configurarColumnas();
        cargarVideojuegos();
    }

    public void setResenaService(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getNombre()));

        colGenero.setCellValueFactory(datos ->
                new SimpleStringProperty(String.valueOf(datos.getValue().getGenero())));

        colDuracion.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDuracionMinutos()));

        colClasificacion.setCellValueFactory(datos ->
                new SimpleStringProperty(String.valueOf(datos.getValue().getClasificacionEdad())));

        colDificultad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDificultad()));

        colDescripcion.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getDescripcion()));
    }

    private void cargarVideojuegos() {
        tablaVideojuegos.setItems(
                FXCollections.observableArrayList(videojuegoService.getLista(false))
        );
    }

    public void volverMenu() {
        volverAlMenuUsuario(
                tablaVideojuegos,
                gestionCuenta,
                peliculaService,
                serieService,
                videojuegoService,
                resenaService

        );
    }
}
