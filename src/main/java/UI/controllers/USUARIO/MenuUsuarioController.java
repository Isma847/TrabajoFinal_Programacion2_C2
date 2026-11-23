package UI.controllers.USUARIO;

import Service.GestionCuenta;
import Service.PeliculaService;
import Service.SerieService;
import UI.controllers.LoginController;
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
    private SerieService serieService;

    public void setSerieService(SerieService serieService) {
        this.serieService = serieService;
    }

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
            controller.setSerieService(serieService);
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
            controller.setSerieService(serieService);
            controller.setPeliculaService(peliculaService);

            Stage stage = (Stage) btncerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al volver al login: " + e.getMessage());
        }
    }



    @FXML
    private void abrirSeries() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/series-usuario.fxml")
            );

            Parent root = loader.load();

            SeriesUsuarioController controller = loader.getController();

            // Primero configuramos el controlador
            controller.setSerieService(serieService);
            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);

            // Después cambiamos la ventana
            Stage stage = (Stage) btnPeliculas.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir series: " + e.getMessage());
        }
    }


}