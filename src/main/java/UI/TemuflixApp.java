package UI;

import DAO.ContenidoDao;
import DAO.FavoritoDao;
import Service.ContenidoService;
import Service.DirectorService;
import Service.FavoritoService;
import Service.GestionCuenta;
import Service.PeliculaService;
import Service.ResenaService;
import Service.SerieService;
import Service.UsuarioService;
import Service.VideojuegoService;
import UI.controllers.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class TemuflixApp extends Application {

    @Override
    public void start(Stage ventana) {
        try {
            UsuarioService usuarios = new UsuarioService();
            GestionCuenta gestionCuenta = new GestionCuenta(usuarios);
            DirectorService directores = new DirectorService();

            PeliculaService peliculas = new PeliculaService();
            SerieService series = new SerieService();
            VideojuegoService videojuegos = new VideojuegoService();

            ContenidoService contenidos = new ContenidoService(
                    peliculas, series, videojuegos, new ContenidoDao()
            );

            ResenaService resenas = new ResenaService();
            FavoritoService favoritos = new FavoritoService(new FavoritoDao());

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/login.fxml")
            );
            Parent raiz = loader.load();
            LoginController controlador = loader.getController();
            controlador.configurarServicios(
                    gestionCuenta, contenidos, directores, resenas, favoritos
            );

            ventana.setTitle("Temuflix - Iniciar sesion");
            ventana.setMinWidth(720);
            ventana.setMinHeight(500);
            ventana.setScene(new Scene(raiz));
            ventana.show();

        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("No se pudo iniciar Temuflix");
            alerta.setHeaderText("Revisa la conexion y los scripts de la base de datos.");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

