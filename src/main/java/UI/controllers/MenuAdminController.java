package UI.controllers;

import Service.GestionCuenta;
import Service.PeliculaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuAdminController {

    private GestionCuenta gestionCuenta;
    private PeliculaService peliculaService;

    @FXML
    private Button btncerrarSesion;



    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
    }

    public void setPeliculaService(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }


    @FXML
    private void abrirPeliculas() {
        System.out.println("Abrir películas");
    }

    @FXML
    private void abrirSeries() {
        System.out.println("Abrir series");
    }

    @FXML
    private void abrirVideojuegos() {
        System.out.println("Abrir videojuegos");
    }

    @FXML
    private void abrirResenas() {
        System.out.println("Abrir reseñas");
    }

    @FXML
    private void cerrarSesion() {
        gestionCuenta.cerrarSesion();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/login.fxml")
            );

            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);

            Stage stage = (Stage) btncerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al volver al login: " + e.getMessage());
        }
    }
}