package UI.controllers.USUARIO;

import Excepciones.DatoInvalidoException;
import Modelo.ContenidoAudiovisual;
import Modelo.ContenidoMultimedia;
import Modelo.Director;
import UI.controllers.ControladorBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.Collection;

public class DirectoresUsuarioController extends ControladorBase {
    @FXML
    private TableView<Director> tablaPeliculas;
    @FXML private TableColumn<Director, Integer> colId;
    @FXML private TableColumn<Director, String> colNombre;
    @FXML private TableColumn<Director, String> colApellido;
    @FXML private TableColumn<Director, String> colProducciones;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblCantidad;

    @FXML
    private void initialize() {
        tablaPeliculas.setPlaceholder(new Label("No hay directores disponibles."));

        colId.setCellValueFactory(datos ->
                new SimpleIntegerProperty(datos.getValue().getId()).asObject());

        colNombre.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getNombre()));

        colApellido.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getApellido()));

        colProducciones.setCellValueFactory(datos ->
                new SimpleStringProperty(agruparProducciones(datos.getValue().getId())));

        tablaPeliculas.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, seleccionada) ->
                        txtDescripcion.setText(seleccionada == null
                                ? "" : descripcionVisible(seleccionada.getDescripcion())));
    }


    private String agruparProducciones(int idDirector) {
        String producciones = "";
        for(ContenidoMultimedia c : contenidoService.getLista(false)){
            if (c instanceof ContenidoAudiovisual) {
                if (((ContenidoAudiovisual) c).getIdDirector() == idDirector) {
                    producciones += ((ContenidoAudiovisual) c).getNombre() + ", ";
                }
            }
        }
        if (producciones.equals("")) {
            producciones = "Ninguna produccion...";
        }else{
            producciones = producciones.substring(0, producciones.length() - 2)+".";
        }
        return producciones;
    }

    @Override
    protected void cargarDatos() {
        try {
            usuarioActual();
            Collection<Director> disponibles = new ArrayList<>();

            for (Director director : directorService.leerTodos()) {
                disponibles.add(director);
            }

            tablaPeliculas.setItems(FXCollections.observableArrayList(disponibles));
            txtDescripcion.clear();
            lblCantidad.setText("Disponibles: " + disponibles.size());

        } catch (DatoInvalidoException e) {
            tablaPeliculas.getItems().clear();
            txtDescripcion.clear();
            lblCantidad.setText("Disponibles: 0");
            mostrarMensaje("Atención", e.getMessage());
        }
    }

    @FXML
    private void volverMenu() {
        cambiarVista(tablaPeliculas, "/vistas/menu-usuario.fxml", "Temuflix");
    }
}
