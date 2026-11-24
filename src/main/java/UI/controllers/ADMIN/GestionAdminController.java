
package UI.controllers.ADMIN;

import Service.*;
import UI.controllers.ControladorBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionAdminController extends ControladorBase {

    private GestionCuenta gestionCuenta;
    private PeliculaService peliculaService;
    private SerieService serieService;
    private VideojuegoService videojuegoService;
    private ResenaService resenaService;
    private FavoritoService favoritoService;
    private DirectorService directorService;

    @FXML
    private Button btnVolver;

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

    public void setFavoritoService(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    public void setDirectorService(DirectorService directorService) {
        this.directorService = directorService;
    }

    @FXML
    private void abrirGestionPeliculas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/administrar-peliculas.fxml")
            );

            Parent root = loader.load();

            AdministrarPeliculasController controller = loader.getController();

            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);
            controller.setSerieService(serieService);
            controller.setVideojuegoService(videojuegoService);
            controller.setResenaService(resenaService);
            controller.setFavoritoService(favoritoService);
            controller.setDirectorService(directorService);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir gestión de películas: " + e.getMessage());
        }
    }

    @FXML
    public void abrirGestionSeries() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/vistas/administrar-series.fxml")
        );

        Parent root = loader.load();

        AdministrarSeriesController controller = loader.getController();

        controller.setGestionCuenta(gestionCuenta);
        controller.setPeliculaService(peliculaService);
        controller.setSerieService(serieService);
        controller.setVideojuegoService(videojuegoService);
        controller.setResenaService(resenaService);
        controller.setFavoritoService(favoritoService);
        controller.setDirectorService(directorService);

        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML
    public void abrirGestionVideojuegos() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/vistas/administrar-videojuegos.fxml")
        );

        Parent root = loader.load();

        AdministrarVideojuegosController controller = loader.getController();

        controller.setGestionCuenta(gestionCuenta);
        controller.setPeliculaService(peliculaService);
        controller.setSerieService(serieService);
        controller.setVideojuegoService(videojuegoService);
        controller.setResenaService(resenaService);
        controller.setFavoritoService(favoritoService);
        controller.setDirectorService(directorService);

        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML
    private void volver() {
        volverAlMenuAdministrador(
                btnVolver,
                gestionCuenta,
                peliculaService,
                serieService,
                videojuegoService,
                resenaService,
                favoritoService,
                directorService
        );
    }

}
