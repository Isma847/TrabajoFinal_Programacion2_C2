package UI.controllers;

import Excepciones.DatoInvalidoException;
import Modelo.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Optional;

public class MiCuentaController extends ControladorBase {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtEdad;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblRol;
    @FXML private Button btnGuardar;
    @FXML private Button btnDarDeBaja;
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        // Enter en cualquier campo guarda los cambios.
        txtNombre.setOnAction(evento -> guardar());
        txtApellido.setOnAction(evento -> guardar());
        txtUsuario.setOnAction(evento -> guardar());
        txtEdad.setOnAction(evento -> guardar());
        txtContrasena.setOnAction(evento -> guardar());
    }

    @Override
    protected void cargarDatos() {
        Usuario usuario = usuarioActual();
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtEmail.setText(usuario.getEmail());
        txtUsuario.setText(usuario.getUsername());
        txtEdad.setText(usuario.getEdad() == null ? "" : String.valueOf(usuario.getEdad()));
        lblRol.setText("Rol: " + usuario.getRol());
    }

    @FXML
    private void guardar() {
        try {
            Usuario cambios = gestionCuenta.consultarMiCuenta();

            cambios.setNombre(leerTextoObligatorio(txtNombre, "El nombre", 80));
            cambios.setApellido(leerTextoObligatorio(txtApellido, "El apellido", 80));
            cambios.setUsername(leerTextoObligatorio(txtUsuario, "El nombre de usuario", 60));

            int edad = leerEnteroPositivo(txtEdad, "La edad");
            if (edad > 120) {
                throw new DatoInvalidoException("La edad debe estar entre 1 y 120.");
            }
            cambios.setEdad(edad);

            String contrasena = txtContrasena.getText();
            if (!contrasena.isEmpty()) {
                if (contrasena.length() < 6 || contrasena.length() > 30) {
                    throw new DatoInvalidoException("La contrasena debe tener entre 6 y 30 caracteres.");
                }
                cambios.setPassword(contrasena);
            }

            if (gestionCuenta.modificarMiCuenta(cambios)) {
                mostrarMensaje("Mi cuenta", "Datos actualizados.");
                cargarDatos();
            } else {
                mostrarMensaje("Mi cuenta", "No se pudieron actualizar los datos.");
            }

        } catch (DatoInvalidoException e) {
            mostrarMensaje("Mi cuenta", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Mi cuenta", "No se pudo guardar el cambio: " + e.getMessage());
        }
    }

    @FXML
    private void darDeBaja() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Dar de baja");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("Tu cuenta se dara de baja y se cerrara la sesion. ¿Continuar?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) return;

        try {
            if (gestionCuenta.darDeBajaMiCuenta()) {
                mostrarMensaje("Mi cuenta", "Tu cuenta fue dada de baja. La sesion se cerro.");
                cambiarVista(btnDarDeBaja, "/vistas/login.fxml", "Temuflix - Iniciar sesion");
            } else {
                mostrarMensaje("Mi cuenta", "No se pudo dar de baja la cuenta.");
            }
        } catch (DatoInvalidoException e) {
            mostrarMensaje("Mi cuenta", e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje("Mi cuenta", "No se pudo dar de baja la cuenta: " + e.getMessage());
        }
    }

    @FXML
    private void volverAlMenu() {
        cambiarVista(btnVolver, "/vistas/menu-usuario.fxml", "Temuflix - Catalogo");
    }
}