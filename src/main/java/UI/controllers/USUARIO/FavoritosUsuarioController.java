
package UI.controllers.USUARIO;

import Modelo.Favorito;
import Modelo.Cuenta;
import Modelo.Pelicula;
import Modelo.Serie;
import Modelo.Videojuego;

import Service.*;

import UI.controllers.ControladorBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

public class FavoritosUsuarioController extends ControladorBase {

    @FXML
    private TableView<Favorito> tablaFavoritos;

    @FXML
    private TableColumn<Favorito, String> colTipo;

    @FXML
    private TableColumn<Favorito, String> colContenido;

    @FXML
    private TableColumn<Favorito, Integer> colId;

    @FXML
    private Button btnQuitar;

    @FXML
    private Button btnVolver;

    private FavoritoService favoritoService;
    private GestionCuenta gestionCuenta;
    private PeliculaService peliculaService;
    private SerieService serieService;
    private VideojuegoService videojuegoService;
    private ResenaService resenaService;

    public void setFavoritoService(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

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

    public void inicializarPantalla() {

        colTipo.setCellValueFactory(
                new PropertyValueFactory<>("tipo")
        );

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id_contenido")
        );

        colContenido.setCellValueFactory(cellData -> {
            Favorito favorito = cellData.getValue();

            String nombre = obtenerNombreContenido(favorito);

            return new SimpleStringProperty(nombre);
        });

        cargarFavoritos();
    }

    private void cargarFavoritos() {

        try {
            Cuenta cuenta = gestionCuenta.getUsuarioActual();

            int idUsuario = cuenta.getId();

            HashSet<Favorito> favoritos =
                    favoritoService.leerPorUsuario(idUsuario);

            ObservableList<Favorito> lista =
                    FXCollections.observableArrayList(favoritos);

            tablaFavoritos.setItems(lista);

        } catch (Exception e) {
            mostrarMensaje("Error", "No se pudieron cargar los favoritos: " + e.getMessage());
        }
    }

    private String obtenerNombreContenido(Favorito favorito) {

        int id = favorito.getId_contenido();

        switch (favorito.getTipo()) {

            case "PELICULA":
                Pelicula pelicula = peliculaService.buscarPorId(id);
                return pelicula != null ? pelicula.getNombre() : "Contenido no disponible";

            case "SERIE":
                Serie serie = serieService.buscarPorId(id);
                return serie != null ? serie.getNombre() : "Contenido no disponible";

            case "JUEGO":
                Videojuego videojuego = videojuegoService.buscarPorId(id);
                return videojuego != null ? videojuego.getNombre() : "Contenido no disponible";

            default:
                return "Desconocido";
        }
    }

    @FXML
    private void quitarFavorito() {

        Favorito seleccionado = tablaFavoritos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje("Atención", "Seleccioná un favorito primero.");
            return;
        }

        try {
            int idUsuario = gestionCuenta.getUsuarioActual().getId();

            favoritoService.quitarDeUsuario(
                    idUsuario,
                    seleccionado.getId_contenido(),
                    seleccionado.getTipo()
            );

            cargarFavoritos();

            mostrarMensaje("Favoritos", "Se quitó el contenido de favoritos.");

        } catch (SQLException e) {
            mostrarMensaje("Error", "No se pudo quitar el favorito: " + e.getMessage());
        }
    }

    @FXML
    private void volverAlMenu() {
        volverAlMenuUsuario(
                btnVolver,
                gestionCuenta,
                peliculaService,
                serieService,
                videojuegoService,
                resenaService,
                favoritoService
        );
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
