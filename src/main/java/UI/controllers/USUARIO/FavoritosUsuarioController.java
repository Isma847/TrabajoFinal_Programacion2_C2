
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

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

    @FXML
    public void agregarFavorito() {

        // Primer diálogo: elegir el tipo de contenido
        ChoiceDialog<String> dialogTipo = new ChoiceDialog<>(
                "Películas",
                "Películas",
                "Series",
                "Videojuegos"
        );

        dialogTipo.setTitle("Agregar favorito");
        dialogTipo.setHeaderText("Seleccioná el tipo de contenido");
        dialogTipo.setContentText("Tipo:");

        Optional<String> tipoElegido = dialogTipo.showAndWait();

        if (tipoElegido.isEmpty()) {
            return;
        }

        // Segundo diálogo: elegir el contenido
        switch (tipoElegido.get()) {

            case "Películas":
                agregarPelicula();
                break;

            case "Series":
                agregarSerie();
                break;

            case "Videojuegos":
                agregarVideojuego();
                break;
        }
    }

    private void agregarPelicula() {

        ArrayList<Pelicula> peliculas = peliculaService.getLista(false);

        if (peliculas.isEmpty()) {
            mostrarMensaje("ATENCION","No hay películas disponibles");
            return;
        }

        ChoiceDialog<Pelicula> dialog = new ChoiceDialog<>(
                peliculas.get(0),
                peliculas
        );

        dialog.setTitle("Agregar película");
        dialog.setHeaderText("Seleccioná una película");

        Optional<Pelicula> seleccion = dialog.showAndWait();

        seleccion.ifPresent(pelicula -> guardarFavorito(
                pelicula.getId(), "PELICULA"
        ));
    }

    private void agregarSerie() {

        ArrayList<Serie> series = serieService.getLista(false);

        if (series.isEmpty()) {
            mostrarMensaje("ATENCION","No hay series disponibles.");
            return;
        }

        ChoiceDialog<Serie> dialog = new ChoiceDialog<>(
                series.get(0),
                series
        );

        dialog.setTitle("Agregar serie");
        dialog.setHeaderText("Seleccioná una serie");

        Optional<Serie> seleccion = dialog.showAndWait();

        seleccion.ifPresent(serie -> guardarFavorito(
                serie.getId(), "SERIE"
        ));
    }

    private void agregarVideojuego() {

        ArrayList<Videojuego> videojuegos = videojuegoService.getLista(false);

        if (videojuegos.isEmpty()) {
            mostrarMensaje("ATENCION","No hay videojuegos disponibles.");
            return;
        }

        ChoiceDialog<Videojuego> dialog = new ChoiceDialog<>(
                videojuegos.get(0),
                videojuegos
        );

        dialog.setTitle("Agregar videojuego");
        dialog.setHeaderText("Seleccioná un videojuego");

        Optional<Videojuego> seleccion = dialog.showAndWait();

        seleccion.ifPresent(videojuego -> guardarFavorito(
                videojuego.getId(), "JUEGO"
        ));
    }


    private void guardarFavorito(int idContenido, String tipo) {

        Cuenta cuenta = gestionCuenta.getUsuarioActual();
        int idUsuario = cuenta.getId();

        if (favoritoService.contiene(idUsuario, idContenido, tipo)) {
            mostrarMensaje("ATENCION","Este contenido ya está en tus favoritos.");
            return;
        }

        Favorito favorito = new Favorito(
                0,
                idUsuario,
                idContenido,
                tipo
        );

        try {
            if (favoritoService.crear(favorito)) {
                mostrarMensaje("ATENCION","¡Contenido agregado a favoritos!");
                cargarFavoritos();
            } else {
                mostrarMensaje("ATENCION","No se pudo agregar el favorito.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
