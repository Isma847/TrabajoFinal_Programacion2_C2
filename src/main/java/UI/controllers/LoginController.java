package UI.controllers;

import Service.GestionCuenta;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnIniciarSesion;


    private GestionCuenta gestionCuenta;

    public void setGestionCuenta(GestionCuenta gestionCuenta) {
        this.gestionCuenta = gestionCuenta;
    }

    //Metodo que conecta el boton con el controlador

    @FXML
    private void iniciarSesion() {

        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        System.out.println("Intentando iniciar sesión...");

        // Aquí conectaremos la autenticación.
    }
}

