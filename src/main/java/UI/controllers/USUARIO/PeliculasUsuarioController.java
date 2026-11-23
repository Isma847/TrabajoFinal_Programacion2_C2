package UI.controllers.USUARIO;

import Modelo.Pelicula;
import Service.*;
import UI.controllers.ControladorBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class PeliculasUsuarioController extends ControladorBase {

    //ATRIBUTOS

    private PeliculaService peliculaService;

    private SerieService serieService;

    private VideojuegoService videojuegoService;

    private ResenaService resenaService;

    private FavoritoService favoritoService;

    //FXML

    @FXML
    private TableView<Pelicula> tablaPeliculas;

    @FXML
    private TableColumn<Pelicula, String> colNombre;

    @FXML
    private TableColumn<Pelicula, String> colGenero;

    @FXML
    private TableColumn<Pelicula, Integer> colDuracion;

    @FXML
    private TableColumn<Pelicula, String> colClasificacion;

    @FXML
    private TableColumn<Pelicula, String> colFechaEstreno;

    @FXML
    private TableColumn<Pelicula, String> colDescripcion;
    private GestionCuenta gestionCuenta;

    //SETTERS

    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
    }

    public void setPeliculaService(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
        configurarColumnas();
        cargarPeliculas();
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

    //METODOS DE PELICULAS

    private void configurarColumnas() {
        colNombre.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getNombre()));

        colGenero.setCellValueFactory(datos ->
                new SimpleStringProperty(String.valueOf(datos.getValue().getGenero())));

        colDuracion.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDuracionMinutos()));

        colClasificacion.setCellValueFactory(datos ->
                new SimpleStringProperty(String.valueOf(datos.getValue().getClasificacionEdad())));

        colFechaEstreno.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getFechaEstreno()));

        colDescripcion.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getDescripcion()));
    }

    private void cargarPeliculas() {
        tablaPeliculas.setItems(
                FXCollections.observableArrayList(peliculaService.getLista(false))
        );
    }

    public void volverMenu() {
        volverAlMenuUsuario(
                tablaPeliculas,
                gestionCuenta,
                peliculaService,
                serieService,
                videojuegoService,
                resenaService,
                favoritoService
        );
    }

   }