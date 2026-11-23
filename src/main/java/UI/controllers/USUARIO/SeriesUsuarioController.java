package UI.controllers.USUARIO;

import Modelo.Serie;
import Service.*;
import UI.controllers.ControladorBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import UI.controllers.USUARIO.SeriesUsuarioController;

public class SeriesUsuarioController extends ControladorBase {

    private SerieService serieService;

    private PeliculaService peliculaService;

    private VideojuegoService videojuegoService;

    private ResenaService resenaService;

    @FXML
    private TableView<Serie> tablaSeries;

    @FXML
    private TableColumn<Serie,String>colNombre;

    @FXML
    private TableColumn<Serie,Integer>colDuracion;

    @FXML
    private TableColumn<Serie,String>colDescripcion;

    @FXML
    private TableColumn<Serie,String>colClasificacion;

    @FXML
    private TableColumn<Serie,String>colGenero;

    @FXML
    private TableColumn<Serie,Integer>colCapitulos;

    @FXML
    private TableColumn<Serie,Integer>colTemporadas;

    private GestionCuenta gestionCuenta;

    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
    }

    public void setSerieService(SerieService serieService) {
        this.serieService = serieService;
        configurarColumna();
        cargarSeries();
    }

    public void setPeliculaService(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    public void setVideojuegoService(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    public void setResenaService(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    //METODOS DE SERIES

    private void configurarColumna(){
        this.colNombre.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getNombre()));

        this.colDuracion.setCellValueFactory(datos->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDuracionMinutos()));

        this.colDescripcion.setCellValueFactory(datos->
                new SimpleStringProperty(datos.getValue().getDescripcion()));

        this.colClasificacion.setCellValueFactory(datos->
                new SimpleStringProperty(String.valueOf(datos.getValue().getClasificacionEdad())));

        this.colGenero.setCellValueFactory(datos->
                new SimpleStringProperty(String.valueOf(datos.getValue().getGenero())));

        this.colCapitulos.setCellValueFactory(datos->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCantidadCapitulos()));

        this.colTemporadas.setCellValueFactory(datos->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCantidadTemporadas()));
    }

    public void cargarSeries(){
        tablaSeries.setItems(
                FXCollections.observableArrayList(serieService.getLista(false))
        );
    }

    public void volverMenu() {
        volverAlMenuUsuario(
                tablaSeries,
                gestionCuenta,
                peliculaService,
                serieService,
                videojuegoService,
                resenaService
        );
    }


}
