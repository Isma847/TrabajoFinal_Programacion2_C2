package UI.controllers.ADMIN;

import UI.controllers.ControladorBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MenuAdminController extends ControladorBase {

    @FXML private Label lblCuenta;
    @FXML private Button btnCerrarSesion;

    @Override
    protected void cargarDatos() {
        if (comprobarAdministrador()) {
            lblCuenta.setText("Cuenta: " + usuarioActual().getUsername());
        }
    }

    @FXML
    private void abrirMenuUsuario() {
        if (comprobarAdministrador()) {
            cambiarVista(btnCerrarSesion, "/vistas/menu-usuario.fxml",
                    "Temuflix - Catalogo");
        }
    }

    @FXML
    private void abrirGestionAdmin() {
        if (comprobarAdministrador()) {
            cambiarVista(btnCerrarSesion, "/vistas/gestion-admin.fxml",
                    "Temuflix - Gestion administrativa");
        }
    }

    @FXML
    private void cerrarSesion() {
        gestionCuenta.cerrarSesion();
        cambiarVista(btnCerrarSesion, "/vistas/login.fxml",
                "Temuflix - Iniciar sesion");
    }
}

