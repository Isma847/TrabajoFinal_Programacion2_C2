
package UI;

import Service.*;
import UI.controllers.LoginController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TemuflixApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        UsuarioService usuarios = new UsuarioService();
        AdministradorService administradores = new AdministradorService();
        PeliculaService peliculas = new PeliculaService();
        SerieService serieService=new SerieService();
        GestionCuenta gestionCuenta =
                new GestionCuenta(usuarios);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/vistas/login.fxml")
        );

        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setGestionCuenta(gestionCuenta);
        controller.setPeliculaService(peliculas);
        controller.setSerieService(serieService);

        Scene escena = new Scene(root, 800, 500);

        stage.setTitle("Temuflix");
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
