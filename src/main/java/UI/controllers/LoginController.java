package UI.controllers;

import Enumerados.Rol;
import Excepciones.DatoInvalidoException;
import Excepciones.SesionInvalidaException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends ControladorBase {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnIniciarSesion;

    @FXML
    private void initialize() {
        txtContrasena.setOnAction(evento -> iniciarSesion());
    }

    @FXML
    private void iniciarSesion() {
        String nombreUsuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (nombreUsuario.isBlank() || contrasena.isBlank()) {
            mostrarMensaje("Iniciar sesion", "Completa el usuario y la contrasena.");
            return;
        }

        try {
            gestionCuenta.iniciarSesion(nombreUsuario, contrasena);

            if (usuarioActual().getRol() == Rol.ADMINISTRADOR) {
                cambiarVista(btnIniciarSesion, "/vistas/menu-admin.fxml",
                        "Temuflix - Administracion");
            } else {
                cambiarVista(btnIniciarSesion, "/vistas/menu-usuario.fxml",
                        "Temuflix - Catalogo");
            }

        } catch (SesionInvalidaException | DatoInvalidoException e) {
            mostrarMensaje("Iniciar sesion", e.getMessage());
        }
    }

    @FXML
    private void abrirRegistro() {
        cambiarVista(btnIniciarSesion, "/vistas/registro.fxml",
                "Temuflix - Crear cuenta");
    }
}
