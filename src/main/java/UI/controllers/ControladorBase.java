package UI.controllers;

import Service.*;
import UI.controllers.USUARIO.MenuUsuarioController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorBase {

    protected void cambiarDeVentana(Node componente, String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(ruta)
            );

            Parent root = loader.load();

            Stage stage = (Stage) componente.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al cambiar de vista: " + e.getMessage());
        }
    }


    protected ContenidoService contenidoService;

    public void setContenidoService(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    protected void volverAlMenuUsuario(
            Node componente,
            GestionCuenta gestionCuenta,
            PeliculaService peliculaService,
            SerieService serieService,
            VideojuegoService videojuegoService,
            ResenaService resenaService,
            FavoritoService favoritoService) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/menu-usuario.fxml")
            );

            Parent root = loader.load();

            MenuUsuarioController controller = loader.getController();


            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);
            controller.setSerieService(serieService);
            controller.setVideojuegoService(videojuegoService);
            controller.setResenaService(resenaService);
            controller.setFavoritoService(favoritoService);


            Stage stage = (Stage) componente.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al volver al menú: " + e.getMessage());
        }
    }

}
