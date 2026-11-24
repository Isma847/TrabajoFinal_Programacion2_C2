package UI.controllers.USUARIO;

import Modelo.ContenidoMultimedia;
import Modelo.Resena;
import Service.*;
import UI.controllers.ControladorBase;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ResenasUsuarioController extends ControladorBase {

    private ResenaService resenaService;
    private PeliculaService peliculaService;
    private SerieService serieService;
    private VideojuegoService videojuegoService;
    private FavoritoService favoritoService;
    private GestionCuenta gestionCuenta;


    @FXML
    private ComboBox<String> comboTipo;

    @FXML
    private ComboBox<ContenidoMultimedia> comboContenido;

    @FXML
    private TableView<Resena> tablaResenas;

    @FXML
    private TableColumn<Resena, Integer> colCalificacion;

    @FXML
    private TableColumn<Resena, String> colComentario;

    @FXML
    private TableColumn<Resena, String> colFecha;

    @FXML
    private TextField txtCalificacion;

    @FXML
    private TextArea txtComentario;

    public void setResenaService(ResenaService resenaService) {
        this.resenaService = resenaService;
        configurarColumnas();
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

    public void setFavoritoService(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
    }

    @FXML
    public void initialize() {
        comboTipo.setItems(FXCollections.observableArrayList(
                "PELICULA", "SERIE", "JUEGO"
        ));

        comboTipo.setOnAction(event -> cargarContenidos());

        comboContenido.setOnAction(event -> cargarResenas());
    }

    private void configurarColumnas() {
        colCalificacion.setCellValueFactory(datos ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        datos.getValue().getCalificacion()
                ));

        colComentario.setCellValueFactory(datos ->
                new javafx.beans.property.SimpleStringProperty(
                        datos.getValue().getComentario()
                ));

        colFecha.setCellValueFactory(datos ->
                new javafx.beans.property.SimpleStringProperty(
                        datos.getValue().getFecha()
                ));
    }

    private void cargarContenidos() {
        comboContenido.getItems().clear();
        tablaResenas.getItems().clear();

        String tipo = comboTipo.getValue();

        if (tipo == null) {
            return;
        }

        List<? extends ContenidoMultimedia> contenidos;

        switch (tipo) {
            case "PELICULA" -> contenidos = peliculaService.getLista(false);
            case "SERIE" -> contenidos = serieService.getLista(false);
            case "JUEGO" -> contenidos = videojuegoService.getLista(false);
            default -> {
                return;
            }
        }

        comboContenido.setItems(
                FXCollections.observableArrayList(contenidos)
        );
    }

    private void cargarResenas() {
        ContenidoMultimedia contenido = comboContenido.getValue();

        if (contenido == null) {
            return;
        }

        tablaResenas.setItems(
                FXCollections.observableArrayList(
                        resenaService.filtrarPorContenido(contenido.getId())
                )
        );
    }

    @FXML
    private void publicarResena() {
        try {
            ContenidoMultimedia contenido = comboContenido.getValue();

            if (contenido == null) {
                mostrarMensaje("Seleccioná un contenido.");
                return;
            }

            int calificacion = Integer.parseInt(txtCalificacion.getText());

            if (calificacion < 1 || calificacion > 10) {
                mostrarMensaje("La calificación debe estar entre 1 y 10.");
                return;
            }

            String comentario = txtComentario.getText();

            Resena resena = new Resena(
                    contenido.getId(),
                    gestionCuenta.getUsuarioActual().getId(),
                    calificacion,
                    comentario == null || comentario.isBlank()
                            ? null : comentario.trim(),
                    LocalDate.now().toString()
            );

            if (resenaService.agregar(resena)) {
                mostrarMensaje("Reseña publicada correctamente.");
                limpiarCampos();
                cargarResenas();
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresá una calificación válida.");
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar la reseña: " + e.getMessage());
        }
    }

    @FXML
    private void modificarResena() {
        Resena seleccionada = tablaResenas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Seleccioná una reseña de la tabla.");
            return;
        }

        if (seleccionada.getIdUsuario()
                != gestionCuenta.getUsuarioActual().getId()) {
            mostrarMensaje("Solo podés modificar tus propias reseñas.");
            return;
        }

        try {
            int calificacion = Integer.parseInt(txtCalificacion.getText());

            if (calificacion < 1 || calificacion > 10) {
                mostrarMensaje("La calificación debe estar entre 1 y 10.");
                return;
            }

            seleccionada.setCalificacion(calificacion);

            String comentario = txtComentario.getText();
            seleccionada.setComentario(
                    comentario == null || comentario.isBlank()
                            ? null : comentario.trim()
            );

            if (resenaService.modificar(seleccionada)) {
                mostrarMensaje("Reseña modificada correctamente.");
                limpiarCampos();
                cargarResenas();
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresá una calificación válida.");
        } catch (SQLException e) {
            mostrarMensaje("Error al modificar la reseña: " + e.getMessage());
        }
    }

    @FXML
    private void seleccionarResena() {
        Resena seleccionada = tablaResenas.getSelectionModel().getSelectedItem();

        if (seleccionada != null) {
            txtCalificacion.setText(
                    String.valueOf(seleccionada.getCalificacion())
            );
            txtComentario.setText(seleccionada.getComentario());
        }
    }

    private void limpiarCampos() {
        txtCalificacion.clear();
        txtComentario.clear();
    }

    private void mostrarMensaje(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void volverMenu() {
        volverAlMenuUsuario(
                tablaResenas,
                gestionCuenta,
                peliculaService,
                serieService,
                videojuegoService,
                resenaService,
                favoritoService
        );
    }
}
