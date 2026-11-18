package UI.controllers;

import Modelo.Pelicula;
import Service.GestionCuenta;
import Service.PeliculaService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import java.io.IOException;

public class PeliculasUsuarioController {

    private PeliculaService peliculaService;

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

    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
    }
    
    

    public void setPeliculaService(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
        configurarColumnas();
        cargarPeliculas();
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

    @FXML
    private void volverMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/menu-usuario.fxml")
            );

            Parent root = loader.load();

            // Le devolvemos los servicios al menú para no perder la sesión ni los datos
            MenuUsuarioController controller = loader.getController();
            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);

            // Usamos tablaPeliculas para obtener la ventana actual
            Stage stage = (Stage) tablaPeliculas.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al volver al menú: " + e.getMessage());
        }
    }}