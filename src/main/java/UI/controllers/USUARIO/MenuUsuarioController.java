package UI.controllers.USUARIO;

import Enumerados.Rol;
import Modelo.Usuario;
import UI.controllers.ControladorBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MenuUsuarioController extends ControladorBase {

    @FXML private Label lblCuenta;
    @FXML private Button btnPeliculas;
    @FXML private Button btnAdministracion;
    @FXML private Button btnMiCuenta;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnDirectores;

    @Override
    protected void cargarDatos() {
        Usuario usuario = usuarioActual();
        lblCuenta.setText("Cuenta: " + usuario.getUsername());

        boolean administrador = usuario.getRol() == Rol.ADMINISTRADOR;
        btnAdministracion.setVisible(administrador);
        btnAdministracion.setManaged(administrador);
    }

    @FXML
    private void abrirPeliculas() {
        cambiarVista(btnPeliculas, "/vistas/peliculas-usuario.fxml", "Temuflix - Peliculas");
    }

    @FXML
    private void abrirSeries() {
        cambiarVista(btnPeliculas, "/vistas/series-usuario.fxml", "Temuflix - Series");
    }

    @FXML
    private void abrirVideojuegos() {
        cambiarVista(btnPeliculas, "/vistas/videojuegos-usuario.fxml", "Temuflix - Videojuegos");
    }

    @FXML
    private void abrirResenas() {
        cambiarVista(btnPeliculas, "/vistas/resenas-usuario.fxml", "Temuflix - Resenas");
    }

    @FXML
    private void abrirFavoritos() {
        cambiarVista(btnPeliculas, "/vistas/favoritos-usuario.fxml", "Temuflix - Favoritos");
    }

    @FXML
    private void abrirMiCuenta() {
        cambiarVista(btnMiCuenta, "/vistas/gestion-mi-cuenta.fxml", "Temuflix - Mi cuenta");
    }

    @FXML
    private void abrirDirectores() {
        cambiarVista(btnDirectores, "/vistas/directores-usuario.fxml", "Temuflix - Directores");
    }

    @FXML
    private void volverAdministracion() {
        if (comprobarAdministrador()) {
            cambiarVista(btnAdministracion, "/vistas/menu-admin.fxml", "Temuflix - Administracion");
        }
    }

    @FXML
    private void cerrarSesion() {
        gestionCuenta.cerrarSesion();
        cambiarVista(btnCerrarSesion, "/vistas/login.fxml", "Temuflix - Iniciar sesion");
    }
}