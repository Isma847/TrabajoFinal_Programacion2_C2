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

public class MenuUsuarioController {

    private GestionCuenta gestionCuenta;

    @FXML
    private Button btnPeliculas;

    private PeliculaService peliculaService;

    public void setPeliculaService(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    @FXML
    private void abrirPeliculas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/peliculas-usuario.fxml")
            );

            Parent root = loader.load();

            PeliculasUsuarioController controller = loader.getController();
            controller.setPeliculaService(peliculaService);
            controller.setGestionCuenta(gestionCuenta); // <-- ¡Muy importante!

            Stage stage = (Stage) btnPeliculas.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir películas: " + e.getMessage());
        }
    }


    @FXML
    private Button btncerrarSesion;

    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
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

            Stage stage = (Stage) btncerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al volver al login: " + e.getMessage());
        }
    }


    @FXML
    private void abrirSeries() {
        System.out.println("Abrir series");
    }




}