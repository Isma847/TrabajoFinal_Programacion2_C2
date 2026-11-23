package UI.controllers.USUARIO;

import Service.*;
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
    private VideojuegoService videojuegoService;
    private ResenaService resenaService;

    public void setSerieService(SerieService serieService) {
        this.serieService = serieService;
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
            controller.setVideojuegoService(videojuegoService);
            controller.setResenaService(resenaService);
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
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/videojuegos-usuario.fxml")
            );

            Parent root = loader.load();

            VideojuegosUsuarioController controller = loader.getController();

            controller.setVideojuegoService(videojuegoService);
            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);
            controller.setSerieService(serieService);
            controller.setResenaService(resenaService);

            Stage stage = (Stage) btnPeliculas.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir videojuegos: " + e.getMessage());
        }
    }

    @FXML
    private void abrirResenas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/resenas-usuario.fxml")
            );

            Parent root = loader.load();

            ResenasUsuarioController controller = loader.getController();

            controller.setResenaService(resenaService);
            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);
            controller.setSerieService(serieService);
            controller.setVideojuegoService(videojuegoService);

            Stage stage = (Stage) btnPeliculas.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir reseñas: " + e.getMessage());
        }
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
            controller.setVideojuegoService(videojuegoService);

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
            controller.setVideojuegoService(videojuegoService);
            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);
            controller.setSerieService(serieService);
            controller.setResenaService(resenaService);

            // Después cambiamos la ventana
            Stage stage = (Stage) btnPeliculas.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir series: " + e.getMessage());
        }
    }


}