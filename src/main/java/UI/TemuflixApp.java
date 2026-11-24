
package UI;

import DAO.FavoritoDao;
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
        PeliculaService peliculas = new PeliculaService();
        SerieService serieService=new SerieService();
        VideojuegoService videojuegoService=new VideojuegoService();
        ResenaService resenaService=new ResenaService();
        FavoritoService favoritoService=new FavoritoService(new FavoritoDao());

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
        controller.setVideojuegoService(videojuegoService);
        controller.setResenaService(resenaService);
        controller.setFavoritoService(favoritoService);

        Scene escena = new Scene(root, 800, 500);

        stage.setTitle("Temuflix");
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
