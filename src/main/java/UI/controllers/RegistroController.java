package UI.controllers;

import Excepciones.DatoInvalidoException;
import Modelo.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController extends ControladorBase {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtEdad;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnRegistrar;

    @FXML
    private void initialize() {
        txtContrasena.setOnAction(evento -> registrar());
        btnRegistrar.setOnAction(evento -> registrar());
    }

    @FXML
    private void registrar() {
        try {
            String nombre = leerTextoObligatorio(txtNombre, "El nombre", 80);
            String apellido = leerTextoObligatorio(txtApellido, "El apellido", 80);
            String email = leerTextoObligatorio(txtEmail, "El email", 160);
            String username = leerTextoObligatorio(txtUsuario, "El nombre de usuario", 60);
            int edad = leerEnteroPositivo(txtEdad, "La edad");

            if (edad > 120) {
                throw new DatoInvalidoException("La edad debe estar entre 1 y 120.");
            }

            String contrasena = txtContrasena.getText();
            if (contrasena.length() > 30 || contrasena.length() < 6) {
                throw new DatoInvalidoException(
                        "La contrasena debe tener entre 6 y 30 caracteres.");
            }

            Usuario nuevo = new Usuario(apellido, nombre, email, contrasena, username, edad);
            if (gestionCuenta.agregarUsuario(nuevo)) {
                mostrarMensaje("Crear cuenta",
                        "Cuenta creada con exito. Ya podes iniciar sesion.");
                volverLogin();
            } else {
                mostrarMensaje("Crear cuenta",
                        "No se pudo crear la cuenta. Intenta nuevamente.");
            }

        } catch (DatoInvalidoException e) {
            mostrarMensaje("Crear cuenta", e.getMessage());
        }
    }

    @FXML
    private void volverLogin() {
        cambiarVista(btnRegistrar, "/vistas/login.fxml", "Temuflix - Iniciar sesion");
    }
}
