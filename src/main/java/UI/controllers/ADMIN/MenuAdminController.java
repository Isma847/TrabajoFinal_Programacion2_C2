
package UI.controllers.ADMIN;

import Service.*;
import UI.controllers.ControladorBase;
import UI.controllers.LoginController;
import UI.controllers.USUARIO.MenuUsuarioController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuAdminController extends ControladorBase {

    private GestionCuenta gestionCuenta;
    private PeliculaService peliculaService;
    private SerieService serieService;
    private VideojuegoService videojuegoService;
    private ResenaService resenaService;
    private FavoritoService favoritoService;
    private DirectorService directorService;

    @FXML
    private Button btnCerrarSesion;

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
    private void abrirMenuUsuario() {
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


            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir el menú de usuario: " + e.getMessage());
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

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al volver al login: " + e.getMessage());
        }
    }

    @FXML
    private void abrirGestionAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/gestion-admin.fxml")
            );

            Parent root = loader.load();

            GestionAdminController controller = loader.getController();

            controller.setGestionCuenta(gestionCuenta);
            controller.setPeliculaService(peliculaService);
            controller.setSerieService(serieService);
            controller.setVideojuegoService(videojuegoService);
            controller.setResenaService(resenaService);
            controller.setFavoritoService(favoritoService);
            controller.setDirectorService(directorService);

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir gestión administrativa: " + e.getMessage());
        }
    }

}
